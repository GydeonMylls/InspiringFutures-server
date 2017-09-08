Adding a course
---------------
1. Add the course name to programmes.txt
2. Add the course's hashed password to passwords.txt
3. Add a .txt file with the same name as the course containing the course's daily questionnaire

Courses and Passwords
---------------------
Courses and their passwords in programmes.txt. Passwords are stored in secure SHA256 hashed form. You can add a course and its password using the add_password script, e.g.
    $ ./addpassword "Course name6" "password"
To change passwords you can either:
    a) delete the relevant line from programmes.txt and add it again with the new password (preferred method)
    b) hash the new password yourself and change the hash in programmes.txt, you can hash passwords using:
        i)  the provided java class, e.g. java SHA256Hasher "password" (preferred method)
        ii) a variety of websites (such as [this one](http://www.miraclesalad.com/webtools/sha256.php)), search for 'SHA256 hash generator'

Daily questionnaires
--------------------
Each course has a questionnaire stored in CourseName.json (spaces in the course name are replaced with underscores) that is shown to users every day. This is only downloaded to the user's device when they join the course, so if you change it it will only affect new users. When you add a course a default questionnaire is loaded into this file.