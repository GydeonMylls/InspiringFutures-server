/*
 * Copyright 2017 Gideon Mills
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.cam.crim.inspiringfutures.server;

import org.json.JSONException;
import uk.ac.cam.crim.inspiringfutures.JSONWrappers.JSONResponseWrapper;
import uk.ac.cam.crim.inspiringfutures.JSONWrappers.JSONResponsesWrapper;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 *     Broker for PostgreSQL database; constructs tables as necessary and inserts data, handling special types properly.
 *     The main method takes four arguments: device id, questionnaire id, timestamp, responses (a string representation of a JSONResponsesWrapper)
 *     The questionnaire id is used as the table name.
 * </p>
 * <p> Created by  Gideon Mills on 21/08/2017 for InspiringFutures. </p>
 */

public class RemoteDatabaseHelper {

//    public static final int SQLITE_MAX_LENGTH = 1000000000;
    public static final String CONFIG_FILE = "database.cfg";
    public static final String QUESTION_COLUMN_NAME = "question";

    public static final String KEY_HOSTNAME = "hostname";
    public static final String KEY_DATABASE_NAME = "database_name";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public static final String DEFAULT_HOSTNAME = "localhost";
    public static final String DEFAULT_DATABASE_NAME = "inspiringfutures";
    public static final String DEFAULT_USERNAME = "postgres";
    public static final String DEFAULT_PASSWORD = "postgres";

//    public static final String TAG = "RemoteDatabaseHelper";

    private static final String TABLE_BASE_COLUMNS = RemoteDatabaseSchema.Columns.DEVICE_ID + " TEXT NOT NULL    , " +
                                                     RemoteDatabaseSchema.Columns.TIMESTAMP + " TIMESTAMP   NOT NULL      ";

    private final String mDeviceId;
    private final String mQuestionnaireId;
    private final String mTimestamp;
    private final String mTableName;
    private final JSONResponseWrapper[] mResponses;

    private final String mHostname;
    private final String mDatabaseName;
    private final String mUsername;
    private final String mPassword;

    /**
     * Mapping of names of class to the PostgreSQL datatypes that should be used to store them.
     * This could be stored in a file but this class needs to be modified anyway to add insertion behaviour of new types so might as well have the two in the same place.
     */
    private static final Map<String,String> sDatatypesMap = new HashMap<String,String>();
    static {
        sDatatypesMap.put(Boolean.class.getCanonicalName(),     "BOOLEAN"   );
        sDatatypesMap.put(Byte.class.getCanonicalName(),        "SMALLINT"  );
        sDatatypesMap.put(Double.class.getCanonicalName(),      "FLOAT8"    );
        sDatatypesMap.put(Float.class.getCanonicalName(),       "FLOAT4"    );
        sDatatypesMap.put(Integer.class.getCanonicalName(),     "INTEGER"   );
        sDatatypesMap.put(Long.class.getCanonicalName(),        "BIGINT"    );
        sDatatypesMap.put(Short.class.getCanonicalName(),       "SMALLINT"  );
        sDatatypesMap.put(String.class.getCanonicalName(),      "TEXT"      );
    }

    private Connection mDatabaseConnection;


    public RemoteDatabaseHelper(String deviceId, String questionnaireId, String timestamp, String responsesString) throws JSONException {
        mDeviceId = deviceId;
        mQuestionnaireId = questionnaireId;
        mTimestamp = timestamp;
        mTableName = mQuestionnaireId.replace(' ','_');

        JSONResponsesWrapper responsesJSONResponsesWrapper = new JSONResponsesWrapper(responsesString);
        mResponses = new JSONResponseWrapper[ responsesJSONResponsesWrapper.length() ];
        for (int i = 0; i< responsesJSONResponsesWrapper.length(); i++) {
            mResponses[i] = (JSONResponseWrapper) responsesJSONResponsesWrapper.get(i);
        }

        // Read config file for host, name, user & password
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            File configFile = new File(CONFIG_FILE);

            if (configFile.createNewFile()) {
                // Config file doesn't exist, create with default values
                Properties defaultProperties = new Properties();
                defaultProperties.setProperty(KEY_HOSTNAME, DEFAULT_HOSTNAME);
                defaultProperties.setProperty(KEY_DATABASE_NAME, DEFAULT_DATABASE_NAME);
                defaultProperties.setProperty(KEY_USERNAME, DEFAULT_USERNAME);
                defaultProperties.setProperty(KEY_PASSWORD, DEFAULT_PASSWORD);
                outStream = new FileOutputStream(configFile);
                defaultProperties.store(outStream, "Inspiring Futures database configuration");
                // Attempt to use default properties
                mHostname = DEFAULT_HOSTNAME;
                mDatabaseName = DEFAULT_DATABASE_NAME;
                mUsername = DEFAULT_USERNAME;
                mPassword =DEFAULT_PASSWORD;
            } else {
                // Config file exists, load properties
                inStream = new FileInputStream(configFile);
                Properties dbProperties = new Properties();
                dbProperties.load(inStream);

                mHostname = dbProperties.getProperty(KEY_HOSTNAME);
                mDatabaseName = dbProperties.getProperty(KEY_DATABASE_NAME);
                mUsername = dbProperties.getProperty(KEY_USERNAME);
                mPassword = dbProperties.getProperty(KEY_PASSWORD);
            }
        } catch (IOException e) {
//            e.printStackTrace();
            throw new RuntimeException("Error while reading configuration file");
        } finally {
            if (null != inStream) {
                try { inStream.close(); } catch (IOException e) { e.printStackTrace(); }        // Exception should happen
            }
            if (null != outStream) {
                try { outStream.close(); } catch (IOException e) { e.printStackTrace(); }        // Exception should happen
            }
        }

        // Load PostgreSQL driver
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Database driver not found");
        }
    }

    private void openDatabase() throws SQLException {
        mDatabaseConnection = DriverManager.getConnection(
                "jdbc:postgresql://" + mHostname + "/" + mDatabaseName,
                mUsername,
                mPassword
        );
    }

    private void closeDatabse() throws SQLException {
        if (null != mDatabaseConnection) {
            mDatabaseConnection.close();
        }
    }

    private void createTable() throws SQLException, JSONException {
        if (mDatabaseConnection.getMetaData().getTables(null, null, mQuestionnaireId, null).next()) {
            // Table exists, do nothing
            return;
        } else {
            // Create table, inferring column types from responses
            StringBuilder createStatement = new StringBuilder("CREATE TABLE " + mTableName + "(" + TABLE_BASE_COLUMNS);
            int num = 1;
            for (JSONResponseWrapper sampleKVPair : mResponses) {
                String questionColumn = ", " + QUESTION_COLUMN_NAME + num++ + " " + sDatatypesMap.get(sampleKVPair.getValueType());
//                Object sampleResponse = sampleKVPair.getValue();
//                // byte[], Boolean, Byte, Double, Float, Integer, Long, Short, String
//                if (sampleResponse instanceof byte[]) {
//                    // TODO Wrap byte[] in a JSON holding filetype, save and file on server with filename in database
//                    questionColumn += "BYTEA";
//                } else if (sampleResponse instanceof Boolean) {
//                    questionColumn += "BOOLEAN";
//                } else if (sampleResponse instanceof Byte) {
//                    questionColumn += "SMALLINT";
//                } else if (sampleResponse instanceof Double) {
//                    questionColumn += "FLOAT8";
//                } else if (sampleResponse instanceof Float) {
//                    questionColumn += "FLOAT4";
//                } else if (sampleResponse instanceof Integer) {
//                    questionColumn += "INTEGER";
//                } else if (sampleResponse instanceof Long) {
//                    questionColumn += "BIGINT";
//                } else if (sampleResponse instanceof Short) {
//                    questionColumn += "SMALLINT";
//                } else if (sampleResponse instanceof String) {
//                    questionColumn += "TEXT";
//                }
                createStatement.append(questionColumn);
            }
            createStatement.append(")");
            Statement statement = null;
            try{
                statement = mDatabaseConnection.createStatement();
                statement.executeUpdate(createStatement.toString());
            } finally {
                if (null != statement) {
                    statement.close();
                }
            }
        }
    }

    private PreparedStatement createInsertStatement() throws JSONException, SQLException {
        String insertStatement = "INSERT INTO " + mTableName + "(" +
                RemoteDatabaseSchema.Columns.DEVICE_ID + ", " +
                RemoteDatabaseSchema.Columns.TIMESTAMP;
        String valuesString = " VALUES(" +
                "'" + mDeviceId + "'" + ", " +
                "TO_TIMESTAMP(" + mTimestamp + ")";
        int num = 1;
        for (JSONResponseWrapper response : mResponses) {
            insertStatement += ", " + QUESTION_COLUMN_NAME + num++;
            valuesString += ", ?";
        }
        insertStatement += ")" + valuesString + ")";
        return mDatabaseConnection.prepareStatement( insertStatement );
    }

    private void insert(PreparedStatement insertStatement) throws JSONException, SQLException {
        for (int i=0; i<mResponses.length; i++) {
            Serializable value = mResponses[i].getValue();
            if (value instanceof Boolean) {
                insertStatement.setBoolean(i+1, (Boolean) value);
            } else if (value instanceof Byte) {
                insertStatement.setByte(i+1, (Byte) value);
            } else if (value instanceof Double) {
                insertStatement.setDouble(i+1, (Double) value);
            } else if (value instanceof Float) {
                insertStatement.setFloat(i+1, (Float) value);
            } else if (value instanceof Integer) {
                insertStatement.setInt(i+1, (Integer) value);
            } else if (value instanceof Long) {
                insertStatement.setLong(i+1, (Long) value);
            } else if (value instanceof Short) {
                insertStatement.setShort(i+1, (Short) value);
            } else if (value instanceof String) {
                insertStatement.setString(i+1, (String) value);
            }
        }
        insertStatement.execute();
    }

    public class RemoteDatabaseSchema {
        public class Columns {
            public static final String DEVICE_ID = "device_id";
            public static final String TIMESTAMP = "timestamp";
        }
    }

    public static void main(String[] args) {
        if (4 == args.length) {
            RemoteDatabaseHelper helper = null;
            try {
                String deviceId = args[0];
                String questionnaireId = args[1];
                String timestamp = args[2];
                String responses = args[3];
                helper = new RemoteDatabaseHelper(deviceId, questionnaireId, timestamp, responses);
                helper.openDatabase();
                helper.createTable();
                helper.insert(
                    helper.createInsertStatement()
                );
            } catch (SQLException | JSONException e) {
                // TODO Do something here?
                e.printStackTrace();
            } finally {
                if (null != helper) {
                    try {
                        helper.closeDatabse();
                    } catch (SQLException e) {
                        // Shouldn't happen
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
