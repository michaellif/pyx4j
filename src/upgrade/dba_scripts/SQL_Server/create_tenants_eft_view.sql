/**
***     ======================================================================================================================
***
***              @version $Revision$ ($Author$) $Date$
***
***             View to export PAP from Yardi 
*** 
***     =======================================================================================================================
**/

-- Adjust for the database in use
USE sl_0411;

DROP VIEW tenant_EFT_charges;

CREATE VIEW tenant_EFT_charges AS
(SELECT	ct.SCODE +' '+CAST(c.hmy AS VARCHAR(50))AS charge_id,
		CAST(c.destimated AS NUMERIC(18,2)) AS estimated_charge,
		ISNULL(a.dPercentAllocated ,100.00) AS percentage,
		CASE WHEN r.ucode IS NOT NULL THEN r.UCODE ELSE t.scode END AS tenant_id,
		p.scode AS property_id,
		e.HBANK,
		SUBSTRING (e.STRANSIT,2,3) AS bank_id,
		SUBSTRING (e.STRANSIT,5,LEN(e.STRANSIT)) AS transit_number,
		REPLACE(e.SACCT,'-','') AS SACCT,
		ISNULL(t.SFIRSTNAME, '') + ' ' + t.SLASTNAME AS tenant,
		ISNULL(r.SFIRSTNAME, '') + ' ' + r.ULASTNAME AS roommate,
		e.SNAME AS bank_account_holder,
		t.SRENT,
		t.DTotalCharges,
		t.SUNITCODE,
		t.SADDR1,
		t.SCITY,
		t.SSTATE,
		t.SZIPCODE,
		pl.SADDR1 AS property_list  
FROM CAMRULE C 
JOIN chargtyp ct ON (ct.hmy = c.HCHARGECODE)
JOIN TENANT t ON (c.HTENANT = t.HMYPERSON)
JOIN PROPERTY p ON (t.HPROPERTY = p.HMY)
JOIN	ACHDATA e ON (t.HMYPERSON = e.HPERSON)
LEFT JOIN ACHAllocation a ON (a.hCamRule = c.HMY AND a.hACHData = e.Hmy)
JOIN	Lease_History	lh ON (t.HMYPERSON = hTent)
LEFT JOIN	LISTPROP lp ON (p.HMY = lp.HPROPERTY)
LEFT JOIN	PROPERTY pl ON (lp.HPROPLIST = pl.HMY)
LEFT JOIN PERSON r ON (e.hRoommate = r.HMY)
WHERE	(c.dtto > CURRENT_TIMESTAMP AND		c.DTFROM < CURRENT_TIMESTAMP)
AND		lh.sStatus = 'Current'
AND		pl.SADDR1 LIKE '%Vista%'
AND		e.SACCT NOT IN ('0','1') 
AND		e.bRecur = -1
AND		c.DTACHPOSTED IS NOT NULL
AND		c.ICTPERMO = 1 );

