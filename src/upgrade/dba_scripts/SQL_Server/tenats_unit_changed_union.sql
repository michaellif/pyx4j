/**
***     ======================================================================================================================
***
***              @version $Revision$ ($Author$) $Date$
***
***             SQL to find tenants that move from unit to unit - one file to find them 
***
***     =======================================================================================================================
**/

SELECT	DISTINCT t1.scode,
		t0.dbname AS old_db,
		t1.dbname AS new_db,
		t0.hmyperson as old_id,
		t1.hmyperson AS new_id,
		CASE WHEN t0.hmyperson != t1.hmyperson THEN t2.scode ELSE NULL END AS new_lease_id,
		t0.name AS old_name,		
		t1.name AS new_name,
		t0.sunitcode AS old_unit,
		t1.sunitcode AS new_unit,
		t0.pscode AS old_property,
		t1.pscode AS new_property,
		CAST(t0.dtleasefrom AS DATE) AS old_lease_from,
		CAST(t1.dtleasefrom AS DATE) AS new_lease_from,
		CAST(t0.dtleaseto AS DATE) AS old_lease_to,
		CAST(t1.dtleaseto AS DATE) AS new_lease_to,
		t0.status AS old_status,
		t1.status AS new_status,
		t1.saddr1 AS proplist
FROM	
		(SELECT DISTINCT t.scode,
				'0322' AS dbname,
				t.hmyperson,
				t.sfirstname+' '+t.slastname AS name,		
				t.sunitcode ,
				p.scode AS pscode,
				t.dtleasefrom, 
				t.dtleaseto ,			
				s.status,
				pl.saddr1 
		FROM	sl_0322.dbo.tenant t
		JOIN	sl_0322.dbo.tenstatus s ON (t.istatus = s.istatus)
		JOIN	sl_0322.dbo.property p ON (t.HPROPERTY = p.HMY)
		LEFT JOIN	sl_0322.dbo.listprop lp ON (p.HMY = lp.HPROPERTY)
		LEFT JOIN	sl_0322.dbo.property pl ON (lp.HPROPLIST = pl.HMY )
		/*WHERE  pl.saddr1 LIKE '%VISTA%'*/) AS t0
FULL OUTER JOIN 
		(SELECT DISTINCT t.scode,
				'0422' AS dbname,
				t.hmyperson,
				t.sfirstname+' '+t.slastname AS name,		
				t.sunitcode ,
				p.scode AS pscode,
				t.dtleasefrom, 
				t.dtleaseto ,			
				s.status,
				pl.saddr1 
		FROM	sl_0422.dbo.tenant t
		JOIN	sl_0422.dbo.tenstatus s ON (t.istatus = s.istatus)
		JOIN	sl_0422.dbo.property p ON (t.HPROPERTY = p.HMY)
		LEFT JOIN	sl_0422.dbo.listprop lp ON (p.HMY = lp.HPROPERTY)
		LEFT JOIN	sl_0422.dbo.property pl ON (lp.HPROPLIST = pl.HMY )
		/*WHERE  pl.saddr1 LIKE '%VISTA%'*/) AS t1
		ON (t0.scode = t1.scode)
JOIN sl_0422.dbo.tenant AS t2 ON (t0.hmyperson = t2.hmyperson)
WHERE	t0.sunitcode != t1.sunitcode
AND		t1.saddr1 LIKE '%vista%'
UNION 
SELECT	DISTINCT t1.scode,
		t0.dbname AS old_db,
		t1.dbname AS new_db,
		t0.hmyperson as old_id,
		t1.hmyperson AS new_id,
		CASE WHEN t0.hmyperson != t1.hmyperson THEN t2.scode ELSE NULL END AS new_lease_id,
		t0.name AS old_name,		
		t1.name AS new_name,
		t0.sunitcode AS old_unit,
		t1.sunitcode AS new_unit,
		t0.pscode AS old_property,
		t1.pscode AS new_property,
		CAST(t0.dtleasefrom AS DATE) AS old_lease_from,
		CAST(t1.dtleasefrom AS DATE) AS new_lease_from,
		CAST(t0.dtleaseto AS DATE) AS old_lease_to,
		CAST(t1.dtleaseto AS DATE) AS new_lease_to,
		t0.status AS old_status,
		t1.status AS new_status,
		t1.saddr1 AS proplist
FROM	
		(SELECT DISTINCT t.scode,
				'0422' AS dbname,
				t.hmyperson,
				t.sfirstname+' '+t.slastname AS name,		
				t.sunitcode ,
				p.scode AS pscode,
				t.dtleasefrom, 
				t.dtleaseto ,			
				s.status,
				pl.saddr1 
		FROM	sl_0422.dbo.tenant t
		JOIN	sl_0422.dbo.tenstatus s ON (t.istatus = s.istatus)
		JOIN	sl_0422.dbo.property p ON (t.HPROPERTY = p.HMY)
		LEFT JOIN	sl_0422.dbo.listprop lp ON (p.HMY = lp.HPROPERTY)
		LEFT JOIN	sl_0422.dbo.property pl ON (lp.HPROPLIST = pl.HMY )
		/*WHERE  pl.saddr1 LIKE '%VISTA%'*/) AS t0
FULL OUTER JOIN 
		(SELECT DISTINCT t.scode,
				'0530' AS dbname,
				t.hmyperson,
				t.sfirstname+' '+t.slastname AS name,		
				t.sunitcode ,
				p.scode AS pscode,
				t.dtleasefrom, 
				t.dtleaseto ,			
				s.status,
				pl.saddr1 
		FROM	sl_0530.dbo.tenant t
		JOIN	sl_0530.dbo.tenstatus s ON (t.istatus = s.istatus)
		JOIN	sl_0530.dbo.property p ON (t.HPROPERTY = p.HMY)
		LEFT JOIN	sl_0530.dbo.listprop lp ON (p.HMY = lp.HPROPERTY)
		LEFT JOIN	sl_0530.dbo.property pl ON (lp.HPROPLIST = pl.HMY )
		/*WHERE  pl.saddr1 LIKE '%VISTA%'*/) AS t1
		ON (t0.scode = t1.scode)
JOIN sl_0530.dbo.tenant AS t2 ON (t0.hmyperson = t2.hmyperson)
WHERE	t0.sunitcode != t1.sunitcode
AND		t1.saddr1 LIKE '%vista%'
UNION  
SELECT	DISTINCT t1.scode,
		t0.dbname AS old_db,
		t1.dbname AS new_db,
		t0.hmyperson as old_id,
		t1.hmyperson AS new_id,
		CASE WHEN t0.hmyperson != t1.hmyperson THEN t2.scode ELSE NULL END AS new_lease_id,
		t0.name AS old_name,		
		t1.name AS new_name,
		t0.sunitcode AS old_unit,
		t1.sunitcode AS new_unit,
		t0.pscode AS old_property,
		t1.pscode AS new_property,
		CAST(t0.dtleasefrom AS DATE) AS old_lease_from,
		CAST(t1.dtleasefrom AS DATE) AS new_lease_from,
		CAST(t0.dtleaseto AS DATE) AS old_lease_to,
		CAST(t1.dtleaseto AS DATE) AS new_lease_to,
		t0.status AS old_status,
		t1.status AS new_status,
		t1.saddr1 AS proplist
FROM	
		(SELECT DISTINCT t.scode,
				'0530' AS dbname,
				t.hmyperson,
				t.sfirstname+' '+t.slastname AS name,		
				t.sunitcode ,
				p.scode AS pscode,
				t.dtleasefrom, 
				t.dtleaseto ,			
				s.status,
				pl.saddr1 
		FROM	sl_0530.dbo.tenant t
		JOIN	sl_0530.dbo.tenstatus s ON (t.istatus = s.istatus)
		JOIN	sl_0530.dbo.property p ON (t.HPROPERTY = p.HMY)
		LEFT JOIN	sl_0530.dbo.listprop lp ON (p.HMY = lp.HPROPERTY)
		LEFT JOIN	sl_0530.dbo.property pl ON (lp.HPROPLIST = pl.HMY )
		/*WHERE  pl.saddr1 LIKE '%VISTA%'*/) AS t0
FULL OUTER JOIN 
		(SELECT DISTINCT t.scode,
				'0628' AS dbname,
				t.hmyperson,
				t.sfirstname+' '+t.slastname AS name,		
				t.sunitcode ,
				p.scode AS pscode,
				t.dtleasefrom, 
				t.dtleaseto ,			
				s.status,
				pl.saddr1 
		FROM	sl_0628.dbo.tenant t
		JOIN	sl_0628.dbo.tenstatus s ON (t.istatus = s.istatus)
		JOIN	sl_0628.dbo.property p ON (t.HPROPERTY = p.HMY)
		LEFT JOIN	sl_0628.dbo.listprop lp ON (p.HMY = lp.HPROPERTY)
		LEFT JOIN	sl_0628.dbo.property pl ON (lp.HPROPLIST = pl.HMY )
		/*WHERE  pl.saddr1 LIKE '%VISTA%'*/) AS t1
		ON (t0.scode = t1.scode)
JOIN sl_0628.dbo.tenant AS t2 ON (t0.hmyperson = t2.hmyperson)
WHERE	t0.sunitcode != t1.sunitcode
AND		t1.saddr1 LIKE '%vista%'
UNION 
SELECT	DISTINCT t1.scode,
		t0.dbname AS old_db,
		t1.dbname AS new_db,
		t0.hmyperson as old_id,
		t1.hmyperson AS new_id,
		CASE WHEN t0.hmyperson != t1.hmyperson THEN t2.scode ELSE NULL END AS new_lease_id,
		t0.name AS old_name,		
		t1.name AS new_name,
		t0.sunitcode AS old_unit,
		t1.sunitcode AS new_unit,
		t0.pscode AS old_property,
		t1.pscode AS new_property,
		CAST(t0.dtleasefrom AS DATE) AS old_lease_from,
		CAST(t1.dtleasefrom AS DATE) AS new_lease_from,
		CAST(t0.dtleaseto AS DATE) AS old_lease_to,
		CAST(t1.dtleaseto AS DATE) AS new_lease_to,
		t0.status AS old_status,
		t1.status AS new_status,
		t1.saddr1 AS proplist
FROM	
		(SELECT DISTINCT t.scode,
				'0628' AS dbname,
				t.hmyperson,
				t.sfirstname+' '+t.slastname AS name,		
				t.sunitcode ,
				p.scode AS pscode,
				t.dtleasefrom, 
				t.dtleaseto ,			
				s.status,
				pl.saddr1 
		FROM	sl_0628.dbo.tenant t
		JOIN	sl_0628.dbo.tenstatus s ON (t.istatus = s.istatus)
		JOIN	sl_0628.dbo.property p ON (t.HPROPERTY = p.HMY)
		LEFT JOIN	sl_0628.dbo.listprop lp ON (p.HMY = lp.HPROPERTY)
		LEFT JOIN	sl_0628.dbo.property pl ON (lp.HPROPLIST = pl.HMY )
		/*WHERE  pl.saddr1 LIKE '%VISTA%'*/) AS t0
FULL OUTER JOIN 
		(SELECT DISTINCT t.scode,
				'0722' AS dbname,
				t.hmyperson,
				t.sfirstname+' '+t.slastname AS name,		
				t.sunitcode ,
				p.scode AS pscode,
				t.dtleasefrom, 
				t.dtleaseto ,			
				s.status,
				pl.saddr1 
		FROM	sl_0722.dbo.tenant t
		JOIN	sl_0722.dbo.tenstatus s ON (t.istatus = s.istatus)
		JOIN	sl_0722.dbo.property p ON (t.HPROPERTY = p.HMY)
		LEFT JOIN	sl_0722.dbo.listprop lp ON (p.HMY = lp.HPROPERTY)
		LEFT JOIN	sl_0722.dbo.property pl ON (lp.HPROPLIST = pl.HMY )
		/*WHERE  pl.saddr1 LIKE '%VISTA%'*/) AS t1
		ON (t0.scode = t1.scode)
JOIN sl_0722.dbo.tenant AS t2 ON (t0.hmyperson = t2.hmyperson)
WHERE	t0.sunitcode != t1.sunitcode
AND		t1.saddr1 LIKE '%vista%'
ORDER BY old_db,t1.saddr1;