-- password: testpass
INSERT INTO USERS (USERNAME, PASSWORD, ENABLED) VALUES ('testuser', '$2a$04$Tv3XL/Zme.cF3jbeUiX3r.kf3Myb5W6SlyD8UuwthZ5mtki2.kuw.', 1);

INSERT INTO AUTHORITIES (USERNAME, AUTHORITY) VALUES ('testuser', '*');

INSERT INTO PATIENT (BODY, DOB, GENDER, FAMILY, GIVEN, SUFFIX, PATIENT_ID) VALUES ('<html><head><link href="https://www.orionhealth.com/software/ccd/style/skin_v2.4.css" rel="stylesheet" type="text/css"></link><title>Continuity of Care Document</title></head><body><h2 align="center">Continuity of Care Document</h2><p align="center"><b>Created On: </b>March 13, 2017</p><hr></hr><div class="header"><div class="demographics sticky"><div class="bl"><div class="br"><div class="tr"><div class="person-name">HOOT, SCOOT </div><div class="sex-age"><span id="calculatedAge"></span></div><div class="id">AACJ-4531-1<span class="label">
 (ORION)
 </span><br></br><br></br></div></div></div></div></div></div><div class="section"><b>Electronically generated</b><b> by </b>Vanderbilt Health Affiliated Network<b> on </b>March 13, 2017</div><div class="section"><span><a name="N65879" href="#toc"><h2>Alerts, Allergies and Adverse Reactions</h2></a></span></div>

 <p>Allergies Unknown</p>
 <div class="section"><span><a name="N66021" href="#toc"><h2>Problems</h2></a></span></div>

 <p>Medical History Unknown</p>
 <div class="section"><span><a name="N66153" href="#toc"><h2>Medications</h2></a></span></div>

 <p>Drug Treatment Unknown</p>
 <div class="section"><span><a name="N66280" href="#toc"><h2>Procedures</h2></a></span></div>

 <p>No Known Procedure</p>
 <div class="section"><span><a name="N66362" href="#toc"><h2>Results</h2></a></span></div>

 <p>No Known Results</p>
 <div class="section"><span><a name="N66471" href="#toc"><h2>Encounters</h2></a></span></div>

 <p>No Known Encounter</p>
 </body><script language="JavaScript" type="text/javascript">
 var today = new Date();
 var age = 0;
 var xmlDob = '''';
 if (xmlDob.length > 0) {
 var dob = parseInt(xmlDob.substring(0, 8));
 //Script return month from 0 to 11 not from 1 to 12. Thats why the month has been incremented by 1.
 var todayMonth = (today.getMonth() + 1) + '''';
 if (todayMonth.length == 1) {
 todayMonth = "0" + todayMonth;
 }
 var todayDate = today.getDate() + '''';
 if (todayDate.length == 1) {
 todayDate = "0" + todayDate;
 }
 var today = parseInt('''' + today.getFullYear() + todayMonth + todayDate);
 age = Math.floor((today - dob) / 10000);
 }
 var ageWithSeparator='''';

 var gender = '''';

 // The forward slash depends on gender and age i.e age is greater than one year.
 if (gender.length != 0 && age != 0) {
 ageWithSeparator = "/";
 }
 if (age != 0) {
 ageWithSeparator = ageWithSeparator + age + ''y'';
 }
 //First inner condition: When the Gender is present and the DOB is greater than or equal to one year
 //Second inner condition: When the Gender is not present and the DOB greater than or equal to one year
 if ((xmlDob.length != 0 && gender.length != 0) || age != 0) {
 ageWithSeparator = ageWithSeparator + '', '';
 }
 document.getElementById(''calculatedAge'').innerHTML = ageWithSeparator;
 </script></html>', null, null, 'HOOT', 'SCOOT', null, 'AACJ-4531-1');
INSERT INTO PATIENT (BODY, DOB, GENDER, FAMILY, GIVEN, SUFFIX, PATIENT_ID) VALUES ('', TO_DATE('1967-04-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'M', 'BAGGINS', 'FRODO', null, '1000050');
INSERT INTO PATIENT (BODY, DOB, GENDER, FAMILY, GIVEN, SUFFIX, PATIENT_ID) VALUES ('<html><head><link href="http://www.orionhealth.com/software/ccd/style/skin_v2.4.css" rel="stylesheet" type="text/css"></link><title>Continuity of Care Document</title></head><body><h2 align="center">Continuity of Care Document</h2><p align="center"><b>Created On: </b>March 13, 2017</p><hr></hr><div class="header"><div class="demographics sticky"><div class="bl"><div class="br"><div class="tr"><div class="person-name">HOOT, SCOOT </div><div class="sex-age"><span id="calculatedAge"></span></div><div class="id">AACJ-4531-1<span class="label">
 (ORION)
 </span><br></br><br></br></div></div></div></div></div></div><div class="section"><b>Electronically generated</b><b> by </b>Vanderbilt Health Affiliated Network<b> on </b>March 13, 2017</div><div class="section"><span><a name="N65879" href="#toc"><h2>Alerts, Allergies and Adverse Reactions</h2></a></span></div>

 <p>Allergies Unknown</p>
 <div class="section"><span><a name="N66021" href="#toc"><h2>Problems</h2></a></span></div>

 <p>Medical History Unknown</p>
 <div class="section"><span><a name="N66153" href="#toc"><h2>Medications</h2></a></span></div>

 <p>Drug Treatment Unknown</p>
 <div class="section"><span><a name="N66280" href="#toc"><h2>Procedures</h2></a></span></div>

 <p>No Known Procedure</p>
 <div class="section"><span><a name="N66362" href="#toc"><h2>Results</h2></a></span></div>

 <p>No Known Results</p>
 <div class="section"><span><a name="N66471" href="#toc"><h2>Encounters</h2></a></span></div>

 <p>No Known Encounter</p>
 </body><script language="JavaScript" type="text/javascript">
 var today = new Date();
 var age = 0;
 var xmlDob = '''';
 if (xmlDob.length > 0) {
 var dob = parseInt(xmlDob.substring(0, 8));
 //Script return month from 0 to 11 not from 1 to 12. Thats why the month has been incremented by 1.
 var todayMonth = (today.getMonth() + 1) + '''';
 if (todayMonth.length == 1) {
 todayMonth = "0" + todayMonth;
 }
 var todayDate = today.getDate() + '''';
 if (todayDate.length == 1) {
 todayDate = "0" + todayDate;
 }
 var today = parseInt('''' + today.getFullYear() + todayMonth + todayDate);
 age = Math.floor((today - dob) / 10000);
 }
 var ageWithSeparator='''';

 var gender = '''';

 // The forward slash depends on gender and age i.e age is greater than one year.
 if (gender.length != 0 && age != 0) {
 ageWithSeparator = "/";
 }
 if (age != 0) {
 ageWithSeparator = ageWithSeparator + age + ''y'';
 }
 //First inner condition: When the Gender is present and the DOB is greater than or equal to one year
 //Second inner condition: When the Gender is not present and the DOB greater than or equal to one year
 if ((xmlDob.length != 0 && gender.length != 0) || age != 0) {
 ageWithSeparator = ageWithSeparator + '', '';
 }
 document.getElementById(''calculatedAge'').innerHTML = ageWithSeparator;
 </script></html>', null, null, 'HOOT', 'SCOOT', null, 'AACJ-4531-1');