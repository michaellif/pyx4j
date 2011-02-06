-- @version $Revision$ ($Author$) $Date$

---- Server Fine tuning
---- my.ini
---- # this adds 35% speed increase
---- innodb_flush_log_at_trx_commit=2
---- # To store big blobs
---- max_allowed_packet=200M

-- reset
DROP DATABASE vista;
CREATE DATABASE vista;

CREATE USER 'vista' IDENTIFIED BY 'vista';
GRANT ALL PRIVILEGES ON vista.* TO 'vista'@'%';

