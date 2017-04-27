create table message (id char(36) not null, headers clob not null, payload_class char(100) not null, payload clob not null, timestamp timestamp not null default CURRENT_TIMESTAMP, primary key (id));
create table message_receipt (server_node_identifier varchar(50) not null, message_id char(36) not null, primary key (message_id, server_node_identifier));
alter table message_receipt add constraint fk_message_receipt_message foreign key (message_id) references message;

-- noinspection SqlNoDataSourceInspectionForFiles
INSERT INTO PATIENT (ID, BODY, DOB, GENDER, FAMILY, GIVEN, SUFFIX, PATIENT_ID, CREATED) VALUES (nextval('PATIENT_SEQ'), '<html><head><link href="https://www.orionhealth.com/software/ccd/style/skin_v2.4.css" rel="stylesheet" type="text/css"></link><title>Continuity of Care Document</title></head><body><h2 align="center">Continuity of Care Document</h2><p align="center"><b>Created On: </b>March 13, 2017</p><hr></hr><div class="header"><div class="demographics sticky"><div class="bl"><div class="br"><div class="tr"><div class="person-name">HOOT, SCOOT </div><div class="sex-age"><span id="calculatedAge"></span></div><div class="id">AACJ-4531-1<span class="label">
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
 </script></html>', null, null, 'HOOT', 'SCOOT', null, 'AACJ-4531-1', current_timestamp-100);
INSERT INTO PATIENT (ID, BODY, DOB, GENDER, FAMILY, GIVEN, SUFFIX, PATIENT_ID, CREATED) VALUES (nextval('PATIENT_SEQ'), '', TO_DATE('1967-04-10 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'M', 'BAGGINS', 'FRODO', null, '1000050', current_timestamp-50);
INSERT INTO PATIENT (ID, BODY, DOB, GENDER, FAMILY, GIVEN, SUFFIX, PATIENT_ID, CREATED) VALUES (nextval('PATIENT_SEQ'), '<html><head><link href="http://www.orionhealth.com/software/ccd/style/skin_v2.4.css" rel="stylesheet" type="text/css"></link><title>Continuity of Care Document</title></head><body><h2 align="center">Continuity of Care Document</h2><p align="center"><b>Created On: </b>March 13, 2017</p><hr></hr><div class="header"><div class="demographics sticky"><div class="bl"><div class="br"><div class="tr"><div class="person-name">HOOT, SCOOT </div><div class="sex-age"><span id="calculatedAge"></span></div><div class="id">AACJ-4531-1<span class="label">
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
 </script></html>', null, null, 'HOOT', 'SCOOT', null, 'AACJ-4531-1', current_timestamp-.5);
INSERT INTO PATIENT (ID, BODY, DOB, GENDER, FAMILY, GIVEN, SUFFIX, PATIENT_ID, CREATED) VALUES (nextval('PATIENT_SEQ'), '', TO_DATE('1967-04-09', 'YYYY-MM-DD'), 'M', 'BAGGINS', 'BILBO', null, '1000060', current_timestamp-.4);
INSERT INTO PATIENT (ID, BODY, DOB, GENDER, FAMILY, GIVEN, SUFFIX, PATIENT_ID, CREATED) VALUES (nextval('PATIENT_SEQ'), '', TO_DATE('2017-04-10', 'YYYY-MM-DD'), 'F', 'FLINTSTONE', 'WILMA', null, '1000070', current_timestamp-.6);
INSERT INTO PATIENT (ID, BODY, DOB, GENDER, FAMILY, GIVEN, SUFFIX, PATIENT_ID, CREATED) VALUES (nextval('PATIENT_SEQ'), '', current_timestamp-2, 'F', 'FLINTSTONE', 'PEBBLES', null, '1000080', current_timestamp-.2);
INSERT INTO PATIENT (ID, BODY, DOB, GENDER, FAMILY, GIVEN, SUFFIX, PATIENT_ID, CREATED) VALUES (nextval('PATIENT_SEQ'), '', current_timestamp-59, 'F', 'RUBBLE', 'BARNEY', null, '1000085', current_timestamp);
INSERT INTO PATIENT (ID, BODY, DOB, GENDER, FAMILY, GIVEN, SUFFIX, PATIENT_ID, CREATED) VALUES (nextval('PATIENT_SEQ'), '', current_timestamp-1, 'U', 'FLINTSTONE', 'DINO', null, '1000090', current_timestamp-.7);
