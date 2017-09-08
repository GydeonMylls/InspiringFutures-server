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

import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 * <p> Created by  Gideon Mills on 21/08/2017 for InspiringFutures. </p>
 */

public class RemoteDatabaseHelper {

//    public static final int SQLITE_MAX_LENGTH = 1000000000;
    public static final String CONFIG_FILE = "database.cfg";

    public static final String KEY_HOST = "host";
    public static final String KEY_DATABASE_NAME = "database_name";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public static final String DEFAULT_HOST = "localhost";
    public static final String DEFAULT_DATABASE_NAME = "inspiringfutures";
    public static final String DEFAULT_USERNAME = "root";
    public static final String DEFAULT_PASSWORD = "";

//    public static final String TAG = "RemoteDatabaseHelper";

    private static final String TABLE_BASE_COLUMNS = RemoteDatabaseSchema.Columns.DEVICE_ID + " VARCHAR(20) NOT NULL    , " +
                                                     RemoteDatabaseSchema.Columns.TIMESTAMP + " TIMESTAMP   NOT NULL      ";

    private final String mDeviceId;
    private final String mQuestionnaireId;
    private final String mTimestamp;
    private final String mTableName;
    //    private final JSONContentValues mResponses;
    private final JSONKeyValuePair[] mResponses;

    private final String mHost;
    private final String mDatabaseName;
    private final String mUsername;
    private final String mPassword;

    private PreparedStatement mInsertStatement;

    private Connection mDatabaseConnection;


    public RemoteDatabaseHelper(String deviceId, String questionnaireId, String timestamp, String responsesString) throws JSONException {
        mDeviceId = deviceId;
        mQuestionnaireId = questionnaireId;
        mTimestamp = timestamp;
        mTableName = mQuestionnaireId.replace(' ','_');

        JSONContentValues responsesJSONContentValues = new JSONContentValues(responsesString);
        mResponses = new JSONKeyValuePair[ responsesJSONContentValues.length() ];
        for (int i=0; i<responsesJSONContentValues.length(); i++) {
            mResponses[i] = (JSONKeyValuePair) responsesJSONContentValues.get(i);
        }

        // Read config file for host, name, user & password
//        BufferedReader configReader = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            File configFile = new File(CONFIG_FILE);

            if (configFile.createNewFile()) {
                // Config file doesn't exist, create with default values
                Properties defaultProperties = new Properties();
                defaultProperties.setProperty(KEY_HOST, DEFAULT_HOST);
                defaultProperties.setProperty(KEY_DATABASE_NAME, DEFAULT_DATABASE_NAME);
                defaultProperties.setProperty(KEY_USERNAME, DEFAULT_USERNAME);
                defaultProperties.setProperty(KEY_PASSWORD, DEFAULT_PASSWORD);
                outStream = new FileOutputStream(configFile);
                defaultProperties.store(outStream, "Inspiring Futures database configuration");
                throw new RuntimeException("Configuration file not found, new file created with default properties");
            } else {
                // Config file exists, load properties
                inStream  = new FileInputStream(configFile);
                Properties dbProperties = new Properties();
                dbProperties.load(inStream);

                mHost = dbProperties.getProperty(KEY_HOST);
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

        // Load MySQL driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Database driver not found");
        }
    }

    private void openDatabase() throws SQLException {
        mDatabaseConnection = DriverManager.getConnection("jdbc:mysql://" + mHost
                + "/" + mDatabaseName + "?"
                + "user=" + mUsername
                + "&password=" + mPassword);
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
            for (JSONKeyValuePair sampleKVPair : mResponses) {
                String questionColumn = ", " + sampleKVPair.getKey() + " ";

                Object sampleResponse = sampleKVPair.getValue();
                // byte[], Boolean, Byte, Double, Float, Integer, Long, Short, String
                if (sampleResponse instanceof byte[]) {
                    questionColumn += "BLOB";
                } else if (sampleResponse instanceof Boolean) {
                    questionColumn += "BOOLEAN";
                } else if (sampleResponse instanceof Byte) {
                    questionColumn += "BYTE";
                } else if (sampleResponse instanceof Double) {
                    questionColumn += "DOUBLE";
                } else if (sampleResponse instanceof Float) {
                    questionColumn += "FLOAT";
                } else if (sampleResponse instanceof Integer) {
                    questionColumn += "INTEGER";
                } else if (sampleResponse instanceof Long) {
                    questionColumn += "BIGINT";
                } else if (sampleResponse instanceof Short) {
                    questionColumn += "SMALLINT";
                } else if (sampleResponse instanceof String) {
                    questionColumn += "TEXT";
                }
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
                "\"" + mDeviceId + "\"" + ", " +
                "FROM_UNIXTIME(" + mTimestamp + ")";
        for (JSONKeyValuePair response : mResponses) {
            insertStatement += ", " + response.getKey();
            valuesString += ", ?";
        }
        insertStatement += ")" + valuesString + ")";
        mInsertStatement = mDatabaseConnection.prepareStatement( insertStatement );

        return mInsertStatement;
    }

    private void insert() throws JSONException, SQLException {
        for (int i=0; i<mResponses.length; i++) {
            Object value = mResponses[i].getValue();
            if (value instanceof byte[]) {
                // TODO Don't know if this will work
//                mInsertStatement.setBytes(i+1, (byte[]) value);
                mInsertStatement.setBlob(i+1, new ByteArrayInputStream((byte[]) value) );
            } else if (value instanceof Boolean) {
                mInsertStatement.setBoolean(i+1, (Boolean) value);
            } else if (value instanceof Byte) {
                mInsertStatement.setByte(i+1, (Byte) value);
            } else if (value instanceof Double) {
                mInsertStatement.setDouble(i+1, (Double) value);
            } else if (value instanceof Float) {
                mInsertStatement.setFloat(i+1, (Float) value);
            } else if (value instanceof Integer) {
                mInsertStatement.setInt(i+1, (Integer) value);
            } else if (value instanceof Long) {
                mInsertStatement.setLong(i+1, (Long) value);
            } else if (value instanceof Short) {
                mInsertStatement.setShort(i+1, (Short) value);
            } else if (value instanceof String) {
                mInsertStatement.setString(i+1, "\"" + (String) value + "\"");
            }
        }
        mInsertStatement.execute();
    }

    public class RemoteDatabaseSchema {
        public class Columns {
            public static final String DEVICE_ID = "device_id";
            public static final String TIMESTAMP = "timestamp";
        }
    }

    public static void main(String[] args) throws JSONException, SQLException {
        // TODO IMORTANT Delete this if block, for troubleshooting only
        if (2 <= args.length) {
            try {
                File testfile = new File("test.txt");
                testfile.createNewFile();
                FileOutputStream outStream = new FileOutputStream(testfile);
                outStream.write(args[0].getBytes());
                outStream.write("\n".getBytes());
                outStream.write(args[1].getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

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
                helper.createInsertStatement();
                helper.insert();
//            } catch (SQLException e) {
//                // TODO
//                e.printStackTrace();
//            } catch (JSONException e) {
//                // TODO
//                e.printStackTrace();
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
