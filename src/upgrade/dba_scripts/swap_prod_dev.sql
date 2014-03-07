/**
***	=======================================================================
***	@version $Revision$ ($Author$) $Date$
***
***		Creates database vista with template vista_prod
***
***	=======================================================================
**/

ALTER USER vista NOLOGIN;
-- Default version
SELECT pg_terminate_backend(procpid) FROM pg_stat_activity WHERE datname = 'vista_prod' AND procpid != pg_backend_pid();
SELECT pg_terminate_backend(procpid) FROM pg_stat_activity WHERE datname = 'vista' AND procpid != pg_backend_pid();
-- Postgress 9.3 version
--SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'vista_prod' AND pid != pg_backend_pid();
--SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'vista' AND pid != pg_backend_pid();

DROP DATABASE IF EXISTS vista;
CREATE DATABASE vista TEMPLATE vista_prod OWNER vista;
ALTER USER vista LOGIN;
\c vista
CREATE SCHEMA _dba_;



