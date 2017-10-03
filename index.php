<?php
    $KEY_DEVICE_ID = 'device_id';
    $KEY_QUESTIONNAIRE_ID = 'questionnaire_id';
    $KEY_TIMESTAMP = 'timestamp';
    $KEY_RESPONSES = 'responses';
    $KEY_FILE = 'iffile';
    
    $JAR_FILE = 'broker.jar';
    $FILES_DIR = 'files\\';
    
    if (array_key_exists($KEY_DEVICE_ID, $_POST)) {
        // Only runs if device id is set (i.e. the expected post request is present)
        $deviceId = $_POST[$KEY_DEVICE_ID];
        $questionnaireId = $_POST[$KEY_QUESTIONNAIRE_ID];
        $timestamp = $_POST[$KEY_TIMESTAMP];
        $responses = $_POST[$KEY_RESPONSES];
        $escaped_responses = str_replace('"','\"',$responses);
        
        // Run server.jar with arguments from POST request
        exec("java -jar {$JAR_FILE} {$deviceId} {$questionnaireId} {$timestamp} \"{$escaped_responses}\""); //, $output);
    } else if (array_key_exists($KEY_FILE, $_FILES)) {
        $target = $FILES_DIR . basename($_FILES[$KEY_FILE]['name']);
        
        if (!file_exists($target)) {
            move_uploaded_file($_FILES[$KEY_FILE]['tmp_name'], $target);
            echo 'File uploaded successfully';
        }
    } else {
        // Makes curl debugging nicer
        echo '
<!--
Copyright 2017 Gideon Mills

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 -->
 
<html>
<head>
<link href="https://fonts.googleapis.com/css?family=Roboto:300" rel="stylesheet"> 
<link rel="stylesheet" type="text/css" href="assets/main.css">
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
</head>
<body>

<title>Inspiring Futures</title>

<div class="centre">
    <p>
    <object data="assets/ic_inspiringfutures_launcher_rs.svg" tye="image/svg+xml" width="150" height="150">
                <img src="assets/ic_inspiringfutures_launcher_rs.png" />
            </object></p>
    <h1><span ; style="color: #2da8ff;">Inspiring Futures</span> is a project by the University of Cambridge\'s <a href="http://www.crim.cam.ac.uk/" class="text">Institute of Criminology</a></h1>
    <p>
    <object data="assets/mark-github_rs.svg" type="image/svg+xml" width="75" height="75">
                <img src="assets/mark-github_rs.png" />
            </object>
    <hr style="max-width: 150px; border-top: 1px solid #686868; border-left: 1px solid #686868;">
    <p>
        <a href="https://github.com/GydeonMylls/InspiringFutures-client" class="svg">
            <object data="assets/ic_phone_android_black_48px_rs.svg" type="image/svg+xml" width="50" height="50">
                <img src="assets/ic_phone_android_black_48px_rs.png" />
            </object>
        </a><a href="https://github.com/GydeonMylls/InspiringFutures-server" class="svg">
            <object data="assets/server_rs.svg" type="image/svg+xml" width="50" height="50">
                <img src="assets/server_rs.png" />
            </object>
        </a>
</div>

<div class="bottom">
    <p>&copy; 2017 Gideon Mills</p>
</div>

</body>
</html>'
;
    }
?>
