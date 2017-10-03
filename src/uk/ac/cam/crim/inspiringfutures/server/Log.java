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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logging class, mainly so I don't have to keep uncommenting Log commands to compile this
 */
public class Log {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private static final String DEBUG = "Debug";
    private static final String ERROR = "Error";
    private static final String INFO = "Info";
    private static final String VERBOSE = "Verbose";
    private static final String WARNING  = "Warning";

    private static final String getTime() {
        return dateFormat.format(new Date());
    }

    private static void out(String type, String tag, String message) {
        System.out.println(getTime() + "  " + type + "/" + tag + ": " + message);
    }

    public static void d(String tag, String message) {
        out(DEBUG, tag,message);
    }
    public static void e(String tag, String message) {
        out(ERROR, tag,message);
    }
    public static void i(String tag, String message) {
        out(INFO, tag,message);
    }
    public static void v(String tag, String message) {
        out(VERBOSE, tag,message);
    }
    public static void w(String tag, String message) {
        out(WARNING, tag,message);
    }


}
