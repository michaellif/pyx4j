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
USE sl_0628;

DROP VIEW tenant_EFT_charges;

CREATE VIEW tenant_EFT_charges AS
(SELECT	        ct.SCODE AS charge_code,
                c.hmy AS charge_id,
                CASE WHEN ISNULL(c.BACH,-1) = -1  THEN 'true' ELSE 'false' END AS pap_applicable,
                CASE WHEN ISNULL(e.bRecur,-1) = -1 THEN 'true' ELSE 'false' END AS recurring_eft,
                CASE WHEN ISNULL(t.bach,0) = -1 THEN 'true' ELSE 'false' END AS tenant_eft,
		CAST(c.destimated AS NUMERIC(18,2)) AS estimated_charge,
		ISNULL(CAST(a.dPercentAllocated AS VARCHAR(50)),'') AS percentage,
		t.SCODE AS lease_id,
		ISNULL(r.UCODE,'') tenant_id,
		p.scode AS property_id,
		e.HBANK,
		SUBSTRING (e.STRANSIT,2,3) AS bank_id,
		SUBSTRING (e.STRANSIT,5,LEN(e.STRANSIT)) AS transit_number,
		REPLACE(e.SACCT,'-','') AS SACCT,
		e.bDefault "achDefault",
		e.HMY "achId",
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
JOIN tenstatus ts ON (t.ISTATUS = ts.istatus)
LEFT JOIN	LISTPROP lp ON (p.HMY = lp.HPROPERTY)
LEFT JOIN	PROPERTY pl ON (lp.HPROPLIST = pl.HMY)
LEFT JOIN PERSON r ON (e.hRoommate = r.HMY)
WHERE	(ISNULL(c.dtto,'01-JAN-2020') >= '01-JUL-2013' AND c.DTFROM <= '01-JUL-2013')
AND		ts.status IN ('Current','Notice')
AND		pl.SADDR1 LIKE '%Vista%'
AND		e.SACCT NOT IN ('0','1')
);

