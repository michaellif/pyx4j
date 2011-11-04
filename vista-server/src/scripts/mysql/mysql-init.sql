-- @version $Revision$ ($Author$) $Date$

---- Server Fine tuning
---- my.ini  [mysqld] section
---- # this adds 35% speed increase
---- innodb_flush_log_at_trx_commit=2
---- # To store big blobs
---- max_allowed_packet=200M

-- initialization
CREATE USER 'vista' IDENTIFIED BY 'vista';
CREATE DATABASE vista DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
GRANT ALL PRIVILEGES ON vista.* TO 'vista'@'%';


-- reset
DROP DATABASE vista;
CREATE DATABASE vista DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

----- if you get error:
----- ERROR 1396 (HY000) at line 3: Operation CREATE USER failed for 'vista'@'%'
----- then run the following commands:
-- DROP USER 'vista'@'localhost'
-- delete from mysql.user where user='vista';
-- delete from mysql.db where user='vista';
-- FLUSH PRIVILEGES;
