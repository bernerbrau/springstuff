CLEAR SCREEN
set pagesize 50
set linesize 80
set heading off
--set feedback off
set define off
SPOOL CONTINUUM_1_0_Installation.txt
PROMPT =======================================================================
PROMPT   Deployment Script: CONTINUUM_1_0_Installation.sql
PROMPT
WHENEVER SQLERROR EXIT


PROMPT EXECUTE DDL
@./v1.0/DDL/CONTINUUM_1_0_F1_DDL.sql

PROMPT EXECUTE DDL
@./v1.0/DDL/CONTINUUM_1_0_F2_DDL.sql

PROMPT EXECUTE DDL
@./v1.0/DDL/CONTINUUM_1_0_F3_DDL.sql

PROMPT EXECUTE DML
@./v1.0/DML/CONTINUUM_1_0_F5_DML.sql

PROMPT
PROMPT database deployment complete for release v1.0
TIMING STOP
SPOOL OFF
