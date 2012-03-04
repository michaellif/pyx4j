-- @version $Revision$ ($Author$) $Date$

-- reset
DROP DATABASE tst_entity;
CREATE DATABASE tst_entity;

CREATE USER tst_entity  WITH PASSWORD 'tst_entity';

CREATE DATABASE tst_entity;
GRANT ALL PRIVILEGES ON DATABASE tst_entity to tst_entity;


