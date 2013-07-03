SELECT          p.scode AS "Property Code",
                t.sunitcode AS "Unit",
                t.SCODE AS "Lease  Id",
                ISNULL(r.UCODE,'') AS "Tenant Id",
                e.SNAME AS "Name",
                SUBSTRING (e.STRANSIT,2,3) AS "Bank Id",
                SUBSTRING (e.STRANSIT,5,LEN(e.STRANSIT)) AS "Transit Number",
                REPLACE(e.SACCT,'-','') AS "Account Number",
                c.hmy AS "Charge Id",
                ct.SCODE AS "Charge Code",
                CASE WHEN ISNULL(c.BACH,-1) = -1  THEN 'true' ELSE 'false' END AS "PAP Applicable",
                CASE WHEN ISNULL(e.bRecur,-1) = -1 THEN 'true' ELSE 'false' END AS "Recurring EFT",
                CASE WHEN ISNULL(t.bach,0) = -1 THEN 'true' ELSE 'false' END AS "Tenant EFT",
		CAST(c.destimated AS NUMERIC(18,2)) AS "Estimated Charge",
		ISNULL(CAST(a.dPercentAllocated AS VARCHAR(50)),'') AS "Percentage"
FROM    CAMRULE C
JOIN    chargtyp ct ON (ct.hmy = c.HCHARGECODE)
JOIN    TENANT t ON (c.HTENANT = t.HMYPERSON)
JOIN    PROPERTY p ON (t.HPROPERTY = p.HMY)
JOIN	ACHDATA e ON (t.HMYPERSON = e.HPERSON)
LEFT JOIN       ACHAllocation a ON (a.hCamRule = c.HMY AND a.hACHData = e.Hmy)
JOIN    tenstatus ts ON (t.ISTATUS = ts.istatus)
LEFT JOIN	LISTPROP lp ON (p.HMY = lp.HPROPERTY)
LEFT JOIN	PROPERTY pl ON (lp.HPROPLIST = pl.HMY)
LEFT JOIN       PERSON r ON (e.hRoommate = r.HMY)
WHERE	(ISNULL(c.dtto,'01-JAN-2020') >= '01-AUG-2013' AND c.DTFROM <= '01-AUG-2013')
AND		ts.status IN ('Current','Notice')
AND		e.SACCT NOT IN ('0','1')
ORDER BY        p.scode,t.scode,e.bDefault,e.HMY,r.ucode,c.hmy;
