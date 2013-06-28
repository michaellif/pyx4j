/**
***     ======================================================================================================================
***
***              @version $Revision$ ($Author$) $Date$
***
***             Obfuscate account numbers in vista_dev
***
***     =======================================================================================================================
**/


USE vista_dev;


SELECT a.hmy,a.sacct,RIGHT(REPLICATE('0',12) +CAST(a.hmy AS VARCHAR),12) AS obf,
		at.SRECACCT  
FROM ACHDATA a
JOIN ACHTRANS at  ON (a.HPERSON = at.HPERSON AND a.STRANSIT = at.SRECTRANSIT)
WHERE	a.sacct != at.SRECACCT;

UPDATE	achtrans 
SET		SRECACCT = a.obf
FROM	(SELECT hperson,RIGHT(REPLICATE('0',12) +CAST(hmy AS VARCHAR),12) AS obf
		FROM ACHDATA) AS a, ACHTRANS at 
WHERE	at.hperson = a.hperson;

UPDATE ACHDATA
SET		SACCT = RIGHT(REPLICATE('0',12) +CAST(hmy AS VARCHAR),12);

UPDATE BANK
SET		SACCTNUM = RIGHT(REPLICATE('0',12) +CAST(hmy AS VARCHAR),12)
WHERE	SACCTNUM IS NOT NULL;