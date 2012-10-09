/**
***	=======================================================================
***	@version $Revision$ ($Author$) $Date$
***
***		Creates database vista with template vista_prod
***
***	=======================================================================
**/

ALTER USER vista NOLOGIN;
SELECT pg_terminate_backend(procpid) FROM pg_stat_activity WHERE datname = 'vista_prod';
SELECT pg_terminate_backend(procpid) FROM pg_stat_activity WHERE datname = 'vista';
DROP DATABASE IF EXISTS vista;
CREATE DATABASE vista TEMPLATE vista_prod OWNER vista;
ALTER USER vista LOGIN;
\c vista
CREATE SCHEMA _dba_;



