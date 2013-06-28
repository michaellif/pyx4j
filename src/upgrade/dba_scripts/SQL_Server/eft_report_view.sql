/**
***     ======================================================================================================================
***
***              @version $Revision$ ($Author$) $Date$
***
***             Emulation of Yardi EFT report 
*** 
***     =======================================================================================================================
**/


USE sl_0628;

DROP VIEW eft_trans_report;

CREATE VIEW eft_trans_report AS
(SELECT	at.DAMOUNT AS amount,
		SUBSTRING(at.srectransit,2,3) AS bank_id,
		SUBSTRING(at.SRECTRANSIT,5,LEN(at.srectransit)) AS transit_no,
		at.SRECTRANCODE AS transit_code,
		REPLACE(at.SRECACCT,'-','')AS account_no,
		at.srecname AS tenant_name,
		t.SCODE AS tenant_id,
		t.SUNITCODE AS unit_code,
		af.SUNIQUEID AS report_id
FROM	ACHTRANS at
JOIN	ACHFILE af ON (af.HMY = at.HFILE)
JOIN	TENANT t ON (at.HPERSON = t.HMYPERSON));



