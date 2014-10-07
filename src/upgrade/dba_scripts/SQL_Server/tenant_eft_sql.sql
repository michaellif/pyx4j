SELECT	        
                NULL AS "Ignore",
                p.scode AS "Property Code",
                t.SUNITCODE AS "Unit Number",
                t.SCODE AS "Lease Id",
                ISNULL(r.UCODE,'') AS "Tenant Id",
                e.SNAME AS "Bank Account Holder",
                SUBSTRING (e.STRANSIT,2,3) AS "Institution",
                SUBSTRING (e.STRANSIT,5,LEN(e.STRANSIT)) AS "Transit",
                REPLACE(e.SACCT,'-','') AS "Account",
                t.DTotalCharges AS "Amount",
                ISNULL(CAST(a.dPercentAllocated AS VARCHAR(50)),'') AS "Percentage",
                CAST(c.destimated AS NUMERIC(18,2)) AS "Yardi Lease Charge",
                ct.SCODE AS "Charge Code",
                CASE WHEN ISNULL(e.bRecur,-1) = -1 THEN 'true' ELSE 'false' END AS "Recurring EFT"
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
WHERE	(ISNULL(c.dtto,'01-JAN-2020') >= '01-NOV-2014' AND c.DTFROM <= '01-NOV-2014')
AND		ts.status IN ('Current','Notice')
AND		pl.SADDR1 LIKE '%Vista%'
AND		e.SACCT NOT IN ('0','1')
ORDER BY    p.scode,t.sunitcode;
