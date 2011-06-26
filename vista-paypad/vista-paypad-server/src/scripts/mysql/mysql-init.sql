-- @version $Revision$ ($Author$) $Date$

---- Server Fine tuning
---- my.ini  [mysqld] section
---- # this adds 35% speed increase
---- innodb_flush_log_at_trx_commit=2
---- # To store big blobs
---- max_allowed_packet=200M

-- initialization
CREATE USER 'paypad' IDENTIFIED BY 'paypad';
CREATE DATABASE paypad;
GRANT ALL PRIVILEGES ON paypad.* TO 'paypad'@'%';


