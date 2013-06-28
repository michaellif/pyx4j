/**
***     ======================================================================================================================
***
***              @version $Revision$ ($Author$) $Date$
***
***             SQL to manually restore vista_dev database from backup
***
***     =======================================================================================================================
**/


USE master;

ALTER DATABASE vista_dev SET single_user WITH ROLLBACK IMMEDIATE;
DROP DATABASE vista_dev;

RESTORE DATABASE vista_dev
FROM DISK = N'E:\vista_dev\vista_dev.bak'
WITH FILE = 1,
MOVE N'starlight' TO N'F:\Starlight_Yardi_Databases\vista_dev.mdf',
MOVE N'starlight_log' TO N'F:\Starlight_Yardi_Databases\vista_dev_1.ldf', NOUNLOAD, REPLACE, STATS = 10;

ALTER DATABASE vista_dev SET RECOVERY SIMPLE;
USE vista_dev;
DBCC SHRINKFILE ('starlight_log',0,TRUNCATEONLY);


