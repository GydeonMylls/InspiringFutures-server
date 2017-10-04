# Inspiring Futures - Server
See also the [app documentation](https://github.com/GydeonMylls/InspiringFutures-client).

## Course management
The list of courses and their passwords is stored in programmes.txt. Passwords are stored in secure SHA256 hashed form. You can add a course and its password using the add_password script, e.g.

    ./addpassword "Course name" "password"
To change passwords simply delete the relevant line from programmes.txt and add it again with the new password (the daily questionnaire will be preserved)

### Daily questionnaires
Each course has a questionnaire stored in Course_Name.json (spaces in the course name are replaced with underscores) that is shown to users every day. This is only downloaded to the user's device when they join the course, so if you change it it will only affect new users. When you add a course a default questionnaire is loaded into this file.

## Questionnaires
Questionnaires are stored in the format:

    {
        "questionnaire_id": "Questionnaire name",
        "questions_array": [
            ...
        ]
    }
The name of the questionnaire is stored in `questionnaire_id`, this determines which table in the database responses are stored in. NOTE: Questionnaires with the same name should be identical, otherwise responses will not be saved.  
The `questions_array` field holds the list of questions (separated by commas) in the order they are to be shown.
See appendix B for notes on editing questionnaires;

## Questions
Questions are in the format:

    {
        "esm_type": "uk.ac.cam.crim.inspiringfutures.ESM.ESM_Text",
        "question": "Example text question",
        "instructions": "Enter text"
    }
These fields are fairly self-explanatory and are common to all question types, other questions may have further fields. The types of question available as of 27/09/2017 are given below. A question may be marked as compulsory may starting the `question` field with an asterisk (`*`), see appendix A for an example.

### Text
Allows the user to enter text. E.g. 

    {
        "esm_type": "uk.ac.cam.crim.inspiringfutures.ESM.ESM_Text",
        "question": "Example text question",
        "instructions": "Enter text"
    }
  
### Multiple choice
Multiple choice questions have an `esm_options` options field that holds a list of options. If `Other` is given as an option the user will have the option to provide more details. This can be preceded with an asterisk (i.e. `*Other`) to require the user to provide further details if they select this option. See appendix A for examples of both of these.
The responses are stored in the database as a comma-separated list of options.

#### Radio buttons
The user can select only one option

    {
        "esm_type": "uk.ac.cam.crim.inspiringfutures.ESM.ESM_Radios",
        "question": "Example radio question",
        "instructions": "Select one option",
        "esm_options": [
            "Option 1",
            "Option 2",
            "Option 3"
        ]
    }

#### Checkboxes
The user can select multiple options. The `max_selection` field can be included to give a maximum number of selected options. Omitting this produces no limit.

    {
        "esm_type": "uk.ac.cam.crim.inspiringfutures.ESM.ESM_CheckBoxes",
        "question": "Example checkboxes question",
        "instructions": "Select up to three options",
        "max_selection": 3,
        "esm_options": [
            "Option 1",
            "Option 2",
            "Option 3"
        ]
    }
    
## Information
This is not in fact a question but can be used to provide information to the user. This might be, for example, and introduction to the questionnaire or a description of what the next section of the questionnaire is about.
The `question` field is displayed as a title as is normal questions. The `instructions` field can contain HTML tags, although not all tags will display properly on all devices. In the `instructions` field you can start a new line by typing`\n` and slashes (`/ `) must be preceded with a backslash (`\ `), see an example below.

    {
        "esm_type": "uk.ac.cam.crim.inspiringfutures.ESM.ESM_Info",
        "question": "Test info title",
        "instructions": "<h1>Heading 1<\/h1>\n<h1>Heading 2<\/h1>\n<h3>Heading 3<\/h3>\n<h4>Heading 4<\/h4>"
    }
A column will be created in the database table for information questions, but will be left blank.

### Photo
The user can take a picture with their phone camera, which will then be uploaded and saved in the `files` folder. Currently this cannot be compulsory.

            {
                "esm_type": "uk.ac.cam.crim.inspiringfutures.ESM.ESM_AudioVideo",
                "question": "Example photo question",
                "instructions": "Take a picture"
            }
The response stored in the database will be the name of the file the user submitted.

## Database
Users' responses to questionnaires are uploaded daily (when they are connected to WiFi) and stored in a PostgreSQL database. Access to this database must be configured in the `database.cfg` file (open with any text editor). For security purposes the database should be hosted on the same machine as the php script is and accessed via `localhost`or `127.0.0.1`.
For each questionnaire a table is created with the id of that questionnaire and the responses are stored therein, in columns `question1`, `question2`etc. No record is kept in the database of the actual questions asked.


## Appendix A: Example questionnaire

    {
        "questionnaire_id": "daily_test",
        "questions_array": [
            {
                "esm_type": "uk.ac.cam.crim.inspiringfutures.ESM.ESM_Info",
                "question": "Test info title",
                "instructions": "<p>You can put HTML text here<\/p>\n<h1>Heading 1<\/h1>\n<h1>Heading 2<\/h1>\n<h3>Heading 3<\/h3>\n<h4>Heading 4<\/h4>\n<p><strong>Bold<\/strong>, <em>italic<\/em> and <span style=\"text-decoration: underline;\">underlined<\/span> text<\/p>\n"
            },
            {
                "esm_type": "uk.ac.cam.crim.inspiringfutures.ESM.ESM_Text",
                "question": "Example text question",
                "instructions": "Enter text"
            },
            {
                "esm_type": "uk.ac.cam.crim.inspiringfutures.ESM.ESM_Radios",
                "esm_options": [
                    "Option 1",
                    "Option 2",
                    "Option 3",
                    "*Other"
                ],
                "question": "*Example radio question",
                "instructions": "Select one option"
            },
            {
                "esm_type": "uk.ac.cam.crim.inspiringfutures.ESM.ESM_CheckBoxes",
                "max_selection": 3,
                "esm_options": [
                    "Option 1",
                    "Option 2",
                    "Option 3",
                    "Other"
                ],
                "question": "Example checkboxes question",
                "instructions": "Select up to three options"
            },
            {
                "esm_type": "uk.ac.cam.crim.inspiringfutures.ESM.ESM_AudioVideo",
                "question": "Example photo question",
                "instructions": "Take a picture"
            }
        ]
    }

## Appendix B: Editing JSON files
Questions and questionnaires are stored as JSON files. These can be edited with any text editor. However to avoid mistakes such as missing commas or brackets you might prefer to use an online JSON editor such as this one: [http://jsoneditoronline.org/](http://jsoneditoronline.org/)

