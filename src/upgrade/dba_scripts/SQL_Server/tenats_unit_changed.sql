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
		t0.sfirstname+' '+t0.slastname AS "0628_name",		
		t1.sfirstname+' '+t1.slastname AS "0722_name",
		t0.sunitcode AS "0628_unit",
		t1.sunitcode AS "0722_unit",
		p0.scode AS "0628_property",
		p1.scode AS "0722_property",
		CAST(t0.dtleasefrom AS DATE) AS "0628_lease_from",
		CAST(t1.dtleasefrom AS DATE) AS "0722_lease_from",
		CAST(t0.dtleaseto AS DATE) AS "0628_lease_to",
		CAST(t1.dtleaseto AS DATE) AS "0722_lease_to",
		s0.status AS "0628_status",
		s1.status AS "0722_status",
		pl1.saddr1 AS proplist
FROM	sl_0628.dbo.tenant t0
JOIN	sl_0722.dbo.tenant t1 ON (t0.scode = t1.scode)
JOIN	sl_0628.dbo.tenstatus s0 ON (t0.istatus = s0.istatus)
JOIN	sl_0722.dbo.tenstatus s1 ON (t1.istatus = s1.istatus)
JOIN	sl_0628.dbo.property p0 ON (t0.HPROPERTY = p0.HMY)
JOIN	sl_0722.dbo.property p1 ON (t1.HPROPERTY = p1.HMY)
LEFT JOIN	sl_0628.dbo.listprop lp0 ON (p0.HMY = lp0.HPROPERTY)
LEFT JOIN	sl_0722.dbo.listprop lp1 ON (p1.HMY = lp1.HPROPERTY)
LEFT JOIN	sl_0628.dbo.property pl0 ON (lp0.HPROPLIST = pl0.HMY AND pl0.saddr1 LIKE '%VISTA%')
LEFT JOIN	sl_0722.dbo.property pl1 ON (lp1.HPROPLIST = pl1.HMY AND pl1.saddr1 LIKE '%VISTA%')
WHERE	t0.sunitcode != t1.sunitcode;

		