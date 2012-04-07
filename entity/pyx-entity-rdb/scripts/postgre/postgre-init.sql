-- @version $Revision$ ($Author$) $Date$

-- reset
DROP DATABASE tst_entity;
CREATE DATABASE tst_entity;

CREATE USER tst_entity  WITH PASSWORD 'tst_entity';

CREATE DATABASE tst_entity;
GRANT ALL PRIVILEGES ON DATABASE tst_entity to tst_entity;
ALTER DATABASE tst_entity OWNER TO tst_entity;
\c tst_entity
ALTER SCHEMA public OWNER TO tst_entity;


