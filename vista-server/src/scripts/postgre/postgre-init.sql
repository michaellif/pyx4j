-- @version $Revision$ ($Author$) $Date$

CREATE USER vista  WITH PASSWORD 'vista';
CREATE DATABASE vista OWNER vista;
GRANT ALL PRIVILEGES ON DATABASE vista to vista;
\c vista
ALTER SCHEMA public OWNER TO vista;

CREATE DATABASE vista_prod OWNER vista;
GRANT ALL PRIVILEGES ON DATABASE vista_prod to vista;
\c vista_prod
ALTER SCHEMA public OWNER TO vista;
