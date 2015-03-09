-- @version $Revision$ ($Author$) $Date$

CREATE USER vista  WITH PASSWORD 'vista';
CREATE DATABASE vista OWNER vista;
GRANT ALL PRIVILEGES ON DATABASE vista to vista;
\c vista
CREATE SCHEMA _dba_;
ALTER SCHEMA public OWNER TO vista;

CREATE DATABASE vista_prod OWNER vista;
GRANT ALL PRIVILEGES ON DATABASE vista_prod to vista;
\c vista_prod
CREATE SCHEMA _dba_;
ALTER SCHEMA public OWNER TO vista;

-- You can have many Databases for different branches; see  file development-branch.profile.off  in root

CREATE DATABASE vista1 OWNER vista;
GRANT ALL PRIVILEGES ON DATABASE vista1 to vista;
\c vista1
CREATE SCHEMA _dba_;
ALTER SCHEMA public OWNER TO vista;