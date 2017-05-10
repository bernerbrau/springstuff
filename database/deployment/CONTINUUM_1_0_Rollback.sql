CLEAR SCREEN
set pagesize 50
set linesize 80
set heading off
--set feedback off
set define off
SPOOL CONTINUUM_1_0_Rollback.txt
PROMPT =======================================================================
PROMPT   Rollback Script: CONTINUUM_1_0_Rollback.sql
PROMPT
WHENEVER SQLERROR EXIT


PROMPT EXECUTE DDL
@./v1.0/DDL/CONTINUUM_1_0_F1_ROLLBACK_DDL.sql

PROMPT EXECUTE DDL
@./v1.0/DDL/CONTINUUM_1_0_F2_ROLLBACK_DDL.sql

PROMPT
PROMPT database rollback complete for release v1.0
TIME STOP
SPOOL OFF
