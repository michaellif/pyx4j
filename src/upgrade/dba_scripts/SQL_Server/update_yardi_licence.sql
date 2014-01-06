/**
***	======================================================================
***
***		Update Yardi licence the sql way
***
***	======================================================================
**/


UPDATE	vista_dev.dbo.pmconfig 
SET		BACTIVEKEY = 0
WHERE	BACTIVEKEY = 2;

INSERT INTO vista_dev.dbo.PMCONFIG (SINSTALLKEY,SCOMPANYNAME,BACTIVEKEY)
(SELECT * FROM (SELECT  * FROM sl_1217.dbo.PMCONFIG
	EXCEPT
 SELECT  * FROM vista_dev.dbo.PMCONFIG) AS a
 WHERE bactivekey = 2);



