/**
***		Attempt to find units where sunitcode and saddr2 do not match
**/

-- All units related to Vista
SELECT	DISTINCT v.lease_id,p.saddr1,p.saddr2,v.sunitcode,v.property_list
FROM	(SELECT		saddr1,saddr2,ucode,
					LTRIM(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(saddr2,'APT',''),'#',''),'AP',''),'unit',''),'.',''),'Suite','')) AS ad2
		FROM		PERSON
		WHERE		ISNULL(SADDR2,'') != '') AS p		
JOIN	(SELECT		t.scode AS lease_id,sunitcode,
					SUBSTRING(sunitcode,PATINDEX('%[^0]%',sunitcode),LEN(sunitcode)) AS s2,
					pl.SADDR1 AS property_list
		FROM		tenant t
		JOIN PROPERTY p ON (t.HPROPERTY = p.HMY)
		LEFT JOIN	LISTPROP lp ON (p.HMY = lp.HPROPERTY)
		LEFT JOIN	PROPERTY pl ON (lp.HPROPLIST = pl.HMY)
		WHERE	pl.SADDR1 LIKE '%Vista%') AS v
	 ON (v.lease_id = p.UCODE)
WHERE	SUBSTRING(p.ad2,PATINDEX('%[^0]%',p.ad2),LEN(p.ad2)) != v.s2
ORDER BY property_list,SADDR1,lease_id;


-- Just the units that have current eft charges

SELECT	DISTINCT v.lease_id,p.saddr1,p.saddr2,v.sunitcode,v.property_list
FROM	(SELECT		saddr1,saddr2,ucode,
					LTRIM(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(saddr2,'APT',''),'#',''),'AP',''),'unit',''),'.',''),'Suite','')) AS ad2
		FROM		PERSON
		WHERE		ISNULL(SADDR2,'') != '') AS p		
JOIN	(SELECT		lease_id,sunitcode,property_list,
					SUBSTRING(sunitcode,PATINDEX('%[^0]%',sunitcode),LEN(sunitcode)) AS s2
		FROM		tenant_eft_charges) AS v
	 ON (v.lease_id = p.UCODE)
WHERE	SUBSTRING(p.ad2,PATINDEX('%[^0]%',p.ad2),LEN(p.ad2)) != v.s2
ORDER BY property_list,SADDR1,lease_id;
