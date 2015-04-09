-- @version $Revision$ ($Author$) $Date$

---- Server Fine tuning
---- my.ini
---- # this adds 35% speed increase
---- innodb_flush_log_at_trx_commit=2
---- # To store big blobs
---- max_allowed_packet=200M

-- reset
DROP DATABASE tst_entity;
CREATE DATABASE tst_entity;

CREATE USER 'tst_entity' IDENTIFIED BY 'tst_entity';
CREATE DATABASE tst_entity;
GRANT ALL PRIVILEGES ON tst_entity.* TO 'tst_entity'@'%';

