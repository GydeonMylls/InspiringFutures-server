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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a ContentValues in a JSONArray, allowing conversion to and from strings
 *
 * <p>Insertion and retrieval take linear time so shouldn't be used for large sets</p>
 *
 * Values must be one of byte[], Boolean, Byte, Double, Float, Integer, Long, Short, String
 *
 * <p> Created by  Gideon Mills on 09/08/2017 for InspiringFutures. </p>
 */

public class JSONContentValues extends JSONArray {

    public static final String INVALID_CONTENT_VALUES_TYPE = "Value provided is not valid for ContentValues";

    public JSONContentValues() {
        super();
    }
    public JSONContentValues(String json) throws JSONException {
        super();
        JSONArray jsonArray = new JSONArray(json);
        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String key = JSONKeyValuePair.getKey(jsonObject);
            String valueType = JSONKeyValuePair.getValueType(jsonObject);
            String valueString = JSONKeyValuePair.getValueString(jsonObject);
            Object value = JSONKeyValuePair.getValue(valueType, valueString);
            super.put(
                    i,
                    JSONKeyValuePair.getInstance(key, value)
            );
        }

    }

    @Override
    public String toString() {
        return super.toString();
    }

    public void put(String key, byte[] value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }
    public void put(String key,Boolean value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }
    public void put(String key,Byte value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }
    public void put(String key,Double value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }
    public void put(String key, Float value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }
    public void put(String key,Integer value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }
    public void put(String key,Long value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }
    public void put(String key,Short value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }
    public void put(String key,String value) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    super.put(i, new JSONKeyValuePair(key, value));
                    return;
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        super.put(
                new JSONKeyValuePair(key, value)
        );
    }

    public Object get(String key) {
        for (int i=0; i<super.length(); i++) {
            try {
                if ( ((JSONKeyValuePair) super.get(i)).getKey() == key ) {
                    return ((JSONKeyValuePair) super.get(i)).getValue();
                }
            } catch (JSONException e) {
                // Shouldn't ever happen
                e.printStackTrace();
            }
        }
        // Key doesn't already exist
        return null;
    }
    
//    public ContentValues toContentValues() {
//        ContentValues cv = new ContentValues();
//        JSONKeyValuePair kvp;
//        for (int i=0; i<super.length(); i++) {
//            try {
//                kvp = (JSONKeyValuePair) super.get(i);
//                try {
//                    putContentValues(cv, kvp.getKey(), kvp.getValue());
//                } catch (InvalidTypeException e) {
//                    // Shouldn't every happen but just in case
//                    e.printStackTrace();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return cv;
//    }

//    public static void putJSONContentValues(JSONContentValues values,String key,Object value) {
//        if (value instanceof Short) {
//            values.put(key, (Short) value);
//        } else if (value instanceof Long) {
//            values.put(key, (Long) value);
//        } else if (value instanceof Double) {
//            values.put(key, (Double) value);
//        } else if (value instanceof Integer) {
//            values.put(key, (Integer) value);
//        } else if (value instanceof String) {
//            values.put(key, (String) value);
//        } else if (value instanceof Boolean) {
//            values.put(key, (Boolean) value);
//        } else if (value instanceof Float) {
//            values.put(key, (Float) value);
//        } else if (value instanceof byte[]) {
//            values.put(key, (byte[]) value);
//        } else if (value instanceof Byte) {
//            values.put(key, (Byte) value);
//        } else {
//            throw new InvalidTypeException(JSONContentValues.INVALID_CONTENT_VALUES_TYPE);
//        }
//    }

//    /**
//     * Helper method to insert key-value pairs into a ContentValues object, where the value is of an unknown but valid type.
//     *
//     * <p>This only exists because ContentValues accepts a limited number of types and no more elegant solution exists</p>
//     *
//     * @param values    Object into which to insert
//     * @param key       Key
//     * @param value     Value, must be one of byte[], Boolean, Byte, Double, Float, Integer, Long, Short, String
//     */
//    public static void putContentValues(ContentValues values, String key, Object value ) {
//        if (value instanceof Short) {
//            values.put(key, (Short) value);
//        } else if (value instanceof Long) {
//            values.put(key, (Long) value);
//        } else if (value instanceof Double) {
//            values.put(key, (Double) value);
//        } else if (value instanceof Integer) {
//            values.put(key, (Integer) value);
//        } else if (value instanceof String) {
//            values.put(key, (String) value);
//        } else if (value instanceof Boolean) {
//            values.put(key, (Boolean) value);
//        } else if (value instanceof Float) {
//            values.put(key, (Float) value);
//        } else if (value instanceof byte[]) {
//            values.put(key, (byte[]) value);
//        } else if (value instanceof Byte) {
//            values.put(key, (Byte) value);
//        } else {
//            throw new InvalidTypeException(JSONContentValues.INVALID_CONTENT_VALUES_TYPE);
//        }
//    }

}
