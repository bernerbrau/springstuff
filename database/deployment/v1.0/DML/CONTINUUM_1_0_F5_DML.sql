Insert into CONTINUUMADM.USERS (USERNAME,PASSWORD,ENABLED) values ('continuumdev','0',1)
/

Insert into CONTINUUMADM.USERS (USERNAME,PASSWORD,ENABLED) values ('joshtest','$2a$11$crDwF8yE07nAPfbekXNanOZYIjUNDYsIg8X/WEayt0NSvXJcKIkyS',1)
/

Insert into CONTINUUMADM.USERS (USERNAME,PASSWORD,ENABLED) values ('vaadevmessaging.orionhealthcloud.com','0',1)
/

Insert into CONTINUUMADM.USERS (USERNAME,PASSWORD,ENABLED) values ('messaging.vaa.dev.orionhealth-saas-svcs.com','0',1)
/

Insert into CONTINUUMADM.USERS (USERNAME,PASSWORD,ENABLED) values ('blandr','$2a$11$q2Pao.bxpvv45.5V0TwjyuURcDmVPOjVzu8FNzDhbGkO6m.XLv4YG',1)
/

Insert into CONTINUUMADM.USERS (USERNAME,PASSWORD,ENABLED) values ('adamspl1','$2a$11$YY5vjkV6ls9kGV0DgNuNlua75QiAj.cCRks5cywdE2M3QB6w0ZwSK',1)
/

Insert into CONTINUUMADM.USERS (USERNAME,PASSWORD,ENABLED) values ('bernerdc','$2a$04$Tv3XL/Zme.cF3jbeUiX3r.kf3Myb5W6SlyD8UuwthZ5mtki2.kuw.',1)
/

Insert into CONTINUUMADM.AUTHORITIES (USERNAME,AUTHORITY) values ('continuumdev','authenticationbroker')
/

Insert into CONTINUUMADM.AUTHORITIES (USERNAME,AUTHORITY) values ('joshtest','provider')
/

Insert into CONTINUUMADM.AUTHORITIES (USERNAME,AUTHORITY) values ('joshtest','useradmin')
/

Insert into CONTINUUMADM.AUTHORITIES (USERNAME,AUTHORITY) values ('vaadevmessaging.orionhealthcloud.com','patientsource')
/

Insert into CONTINUUMADM.AUTHORITIES (USERNAME,AUTHORITY) values ('messaging.vaa.dev.orionhealth-saas-svcs.com','patientsource')
/

Insert into CONTINUUMADM.AUTHORITIES (USERNAME,AUTHORITY) values ('bernerdc','useradmin')
/

Insert into CONTINUUMADM.AUTHORITIES (USERNAME,AUTHORITY) values ('blandr','provider')
/

Insert into CONTINUUMADM.AUTHORITIES (USERNAME,AUTHORITY) values ('blandr','useradmin')
/

Insert into CONTINUUMADM.AUTHORITIES (USERNAME,AUTHORITY) values ('adamspl1','provider')
/

Insert into CONTINUUMADM.AUTHORITIES (USERNAME,AUTHORITY) values ('adamspl1','useradmin')
/

Insert into CONTINUUMADM.AUTHORITIES (USERNAME,AUTHORITY) values ('bernerdc','provider');
/

COMMIT
/