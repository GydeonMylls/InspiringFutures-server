<html>
<body>

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

<?php
    $KEY_DEVICE_ID = 'device_id';
    $KEY_QUESTIONNAIRE_ID = 'questionnaire_id';
    $KEY_TIMESTAMP = 'timestamp';
    $KEY_RESPONSES = 'responses';
    
    $JAR_FILE = 'server.jar';
?>

Landing page for the Inspiring Futures project by the University of Cambridge's <a href="http://www.crim.cam.ac.uk/">Institute of Criminology</a>

<?php
    if (array_key_exists($KEY_DEVICE_ID, $_POST)) {
        // Only runs if device id is set (i.e. the expected post request is present)
        $deviceId = $_POST[$KEY_DEVICE_ID];
        $questionnaireId = $_POST[$KEY_QUESTIONNAIRE_ID];
        $timestamp = $_POST[$KEY_TIMESTAMP];
        $responses = $_POST[$KEY_RESPONSES];
        $escaped_responses = str_replace('"','\"',$responses);
        
        // Run server.jar with arguments from POST request
        exec("java -jar {$JAR_FILE} {$deviceId} {$questionnaireId} {$timestamp} \"{$escaped_responses}\""); //, $output);
    }
?>

</body>
</html>