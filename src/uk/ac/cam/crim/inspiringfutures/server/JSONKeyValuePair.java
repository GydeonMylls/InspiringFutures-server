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
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Contains methods to represent a ContentValues-compatible key-value pair in a JSON object
 */
public class JSONKeyValuePair extends JSONObject {

    public static final String KEY = "KEY";
    public static final String VALUE_TYPE = "VALUE_TYPE";
    public static final String VALUE = "VALUE";

    public JSONKeyValuePair() { super(); }
    public JSONKeyValuePair(String string) throws JSONException { super(string); }

    // byte[], Boolean, Byte, Double, Float, Integer, Long, Short, String
    public JSONKeyValuePair(String key, byte[] value) {
        super();
        try {
            this.put(KEY, key)
                    .put(VALUE_TYPE, value.getClass().getName())
                    .put(VALUE, Arrays.toString(value));
        } catch (JSONException e) {
            // Should never happen
            e.printStackTrace();
        }
    }
    public JSONKeyValuePair(String key, Boolean value) {
        super();
        try {
            this.put(KEY, key)
                    .put(VALUE_TYPE, value.getClass().getName())
                    .put(VALUE, value.toString());
        } catch (JSONException e) {
            // Should never happen
            e.printStackTrace();
        }
    }
    public JSONKeyValuePair(String key, Byte value) {
        super();
        try {
            this.put(KEY, key)
                    .put(VALUE_TYPE, value.getClass().getName())
                    .put(VALUE, value.toString());
        } catch (JSONException e) {
            // Should never happen
            e.printStackTrace();
        }
    }
    public JSONKeyValuePair(String key, Double value) {
        super();
        try {
            this.put(KEY, key)
                    .put(VALUE_TYPE, value.getClass().getName())
                    .put(VALUE, value.toString());
        } catch (JSONException e) {
            // Should never happen
            e.printStackTrace();
        }
    }
    public JSONKeyValuePair(String key, Float value) {
        super();
        try {
            this.put(KEY, key)
                    .put(VALUE_TYPE, value.getClass().getName())
                    .put(VALUE, value.toString());
        } catch (JSONException e) {
            // Should never happen
            e.printStackTrace();
        }
    }
    public JSONKeyValuePair(String key, Integer value) {
        super();
        try {
            this.put(KEY, key)
                    .put(VALUE_TYPE, value.getClass().getName())
                    .put(VALUE, value.toString());
        } catch (JSONException e) {
            // Should never happen
            e.printStackTrace();
        }
    }
    public JSONKeyValuePair(String key, Long value) {
        super();
        try {
            this.put(KEY, key)
                    .put(VALUE_TYPE, value.getClass().getName())
                    .put(VALUE, value.toString());
        } catch (JSONException e) {
            // Should never happen
            e.printStackTrace();
        }
    }
    public JSONKeyValuePair(String key, Short value) {
        super();
        try {
            this.put(KEY, key)
                    .put(VALUE_TYPE, value.getClass().getName())
                    .put(VALUE, value.toString());
        } catch (JSONException e) {
            // Should never happen
            e.printStackTrace();
        }
    }
    public JSONKeyValuePair(String key, String value) {
        super();
        try {
            this.put(KEY, key)
                    .put(VALUE_TYPE, value.getClass().getName())
                    .put(VALUE, value);
        } catch (JSONException e) {
            // Should never happen
            e.printStackTrace();
        }
    }

    public static JSONKeyValuePair getInstance(String key, Object value) throws JSONException {
        if (value instanceof byte[]) {
            return new JSONKeyValuePair(key, (byte[]) value);
        } else if (value instanceof Boolean) {
            return new JSONKeyValuePair(key, (Boolean) value);
        } else if (value instanceof Byte) {
            return new JSONKeyValuePair(key, (Byte) value);
        } else if (value instanceof Double) {
            return new JSONKeyValuePair(key, (Double) value);
        } else if (value instanceof Float) {
            return new JSONKeyValuePair(key, (Float) value);
        } else if (value instanceof Integer) {
            return new JSONKeyValuePair(key, (Integer) value);
        } else if (value instanceof Long) {
            return new JSONKeyValuePair(key, (Long) value);
        } else if (value instanceof Short) {
            return new JSONKeyValuePair(key, (Short) value);
        } else if (value instanceof String) {
            return new JSONKeyValuePair(key, (String) value);
        } else {
            throw new JSONException("Unsupported type");
        }
    }

    public String getKey() throws JSONException {
        return this.getString(KEY);
    }

    public static String getKey(JSONObject json) throws JSONException {
        return json.getString(KEY);
    }
    public String getValueType() throws JSONException {
        return this.getString(VALUE_TYPE);
    }

    public static String getValueType(JSONObject json) throws JSONException {
        return json.getString(VALUE_TYPE);
    }
    public String getValueString() throws JSONException {
        return this.getString(VALUE);
    }

    public static String getValueString(JSONObject json) throws JSONException {
        return json.getString(VALUE);
    }

    public Object getValue() throws JSONException {
        String type = this.getValueType();
        String value = this.getValueString();
        return getValue(type, value);

    }
    
    public static Object getValue(String type, String value) throws JSONException {
        //    byte[], Boolean, Byte, Double, Float, Integer, Long, Short, String
        if (byte[].class.getName().equals(type)) {
            value = value.substring(1,value.length()-1);            // Drop square brackets
            String[] stringArray = value.split(", ");               // Separate items
            byte[] byteArray = new byte[ stringArray.length ];      // New byte[] of equal length
            for (int i=0; i<stringArray.length; i++) {
                byteArray[i] = Byte.parseByte( stringArray[i] );    // Parse strings as bytes
            }
            return byteArray;
        } else if (Boolean.class.getName().equals(type)) {
            return Boolean.parseBoolean(value);
        } else if (Byte.class.getName().equals(type)) {
            return Byte.parseByte(value);
        } else if (Double.class.getName().equals(type)) {
            return Double.parseDouble(value);
        } else if (Float.class.getName().equals(type)) {
            return Float.parseFloat(value);
        } else if (Integer.class.getName().equals(type)) {
            return Integer.parseInt(value);
        } else if (Long.class.getName().equals(type)) {
            return Long.parseLong(value);
        } else if (Short.class.getName().equals(type)) {
            return Short.parseShort(value);
        } else if (String.class.getName().equals(type)) {
            return value;
        } else {
            throw new JSONException("Unsupported type");
        }
    }

}
