/**
***	=======================================================================
***	@version $Revision$ ($Author$) $Date$
***
***		Creates database vista with template vista_prod
***
***	=======================================================================
**/

ALTER USER vista NOLOGIN;
SELECT pg_terminate_backend(procpid) FROM pg_stat_activity 
WHERE datname = 'vista_prod'
AND usename = 'vista';
DROP DATABASE IF EXISTS vista;
CREATE DATABASE vista TEMPLATE vista_prod OWNER vista;
ALTER USER vista LOGIN;




