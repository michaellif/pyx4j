/**
***     ======================================================================================================================
***
***              @version $Revision$ ($Author$) $Date$
***
***             SQL to find tenants that move from unit to unit
***
***     =======================================================================================================================
**/

SELECT	DISTINCT t1.scode,
		t0.name AS "0628_name",		
		t1.name AS "0722_name",
		t0.sunitcode AS "0628_unit",
		t1.sunitcode AS "0722_unit",
		t0.pscode AS "0628_property",
		t1.pscode AS "0722_property",
		CAST(t0.dtleasefrom AS DATE) AS "0628_lease_from",
		CAST(t1.dtleasefrom AS DATE) AS "0722_lease_from",
		CAST(t0.dtleaseto AS DATE) AS "0628_lease_to",
		CAST(t1.dtleaseto AS DATE) AS "0722_lease_to",
		t0.status AS "0628_status",
		t1.status AS "0722_status",
		t1.saddr1 AS proplist
FROM	
		(SELECT DISTINCT t.scode,
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
WHERE	t0.sunitcode != t1.sunitcode
AND		t1.saddr1 LIKE '%vista%'
ORDER BY t1.saddr1;


SELECT	DISTINCT t1.scode,
		t0.name AS "0322_name",		
		t1.name AS "0722_name",
		t0.sunitcode AS "0322_unit",
		t1.sunitcode AS "0722_unit",
		t0.pscode AS "0322_property",
		t1.pscode AS "0722_property",
		CAST(t0.dtleasefrom AS DATE) AS "0322_lease_from",
		CAST(t1.dtleasefrom AS DATE) AS "0722_lease_from",
		CAST(t0.dtleaseto AS DATE) AS "0322_lease_to",
		CAST(t1.dtleaseto AS DATE) AS "0722_lease_to",
		t0.status AS "0322_status",
		t1.status AS "0722_status",
		t1.saddr1 AS proplist
FROM	
		(SELECT DISTINCT t.scode,
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
WHERE	t0.sunitcode != t1.sunitcode
AND		t1.saddr1 LIKE '%vista%'
ORDER BY t1.saddr1;


SELECT	DISTINCT t1.scode,
		t0.name AS "0422_name",		
		t1.name AS "0722_name",
		t0.sunitcode AS "0422_unit",
		t1.sunitcode AS "0722_unit",
		t0.pscode AS "0422_property",
		t1.pscode AS "0722_property",
		CAST(t0.dtleasefrom AS DATE) AS "0422_lease_from",
		CAST(t1.dtleasefrom AS DATE) AS "0722_lease_from",
		CAST(t0.dtleaseto AS DATE) AS "0422_lease_to",
		CAST(t1.dtleaseto AS DATE) AS "0722_lease_to",
		t0.status AS "0422_status",
		t1.status AS "0722_status",
		t1.saddr1 AS proplist
FROM	
		(SELECT DISTINCT t.scode,
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
WHERE	t0.sunitcode != t1.sunitcode
AND		t1.saddr1 LIKE '%vista%'
ORDER BY t1.saddr1;

SELECT	DISTINCT t1.scode,
		t0.name AS "0530_name",		
		t1.name AS "0722_name",
		t0.sunitcode AS "0530_unit",
		t1.sunitcode AS "0722_unit",
		t0.pscode AS "0530_property",
		t1.pscode AS "0722_property",
		CAST(t0.dtleasefrom AS DATE) AS "0530_lease_from",
		CAST(t1.dtleasefrom AS DATE) AS "0722_lease_from",
		CAST(t0.dtleaseto AS DATE) AS "0530_lease_to",
		CAST(t1.dtleaseto AS DATE) AS "0722_lease_to",
		t0.status AS "0530_status",
		t1.status AS "0722_status",
		t1.saddr1 AS proplist
FROM	
		(SELECT DISTINCT t.scode,
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
WHERE	t0.sunitcode != t1.sunitcode
AND		t1.saddr1 LIKE '%vista%'
ORDER BY t1.saddr1;


		