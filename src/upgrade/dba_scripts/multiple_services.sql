SELECT  'cogir' AS pmc, l.lease_id, count(b.id) AS service_count
FROM    cogir.lease l
JOIN    cogir.lease_term lt ON (l.id = lt.lease AND l.current_term = lt.id)
JOIN    cogir.lease_term_v ltv ON (lt.id = ltv.holder)
JOIN    cogir.billable_item b ON (b.id = ltv.lease_products_service_item)
WHERE   l.status = 'Active' 
AND     ltv.to_date IS NULL
GROUP BY l.lease_id
HAVING  count(b.id) > 1
UNION
SELECT  'dms' AS pmc, l.lease_id, count(b.id) AS service_count
FROM    dms.lease l
JOIN    dms.lease_term lt ON (l.id = lt.lease AND l.current_term = lt.id)
JOIN    dms.lease_term_v ltv ON (lt.id = ltv.holder)
JOIN    dms.billable_item b ON (b.id = ltv.lease_products_service_item)
WHERE   l.status = 'Active' 
AND     ltv.to_date IS NULL
GROUP BY l.lease_id
HAVING  count(b.id) > 1
UNION
SELECT  'greenwin' AS pmc, l.lease_id, count(b.id) AS service_count
FROM    greenwin.lease l
JOIN    greenwin.lease_term lt ON (l.id = lt.lease AND l.current_term = lt.id)
JOIN    greenwin.lease_term_v ltv ON (lt.id = ltv.holder)
JOIN    greenwin.billable_item b ON (b.id = ltv.lease_products_service_item)
WHERE   l.status = 'Active' 
AND     ltv.to_date IS NULL
GROUP BY l.lease_id
HAVING  count(b.id) > 1
UNION
SELECT  'larlyn' AS pmc, l.lease_id, count(b.id) AS service_count
FROM    larlyn.lease l
JOIN    larlyn.lease_term lt ON (l.id = lt.lease AND l.current_term = lt.id)
JOIN    larlyn.lease_term_v ltv ON (lt.id = ltv.holder)
JOIN    larlyn.billable_item b ON (b.id = ltv.lease_products_service_item)
WHERE   l.status = 'Active' 
AND     ltv.to_date IS NULL
GROUP BY l.lease_id
HAVING  count(b.id) > 1
UNION
SELECT  'metcap' AS pmc, l.lease_id, count(b.id) AS service_count
FROM    metcap.lease l
JOIN    metcap.lease_term lt ON (l.id = lt.lease AND l.current_term = lt.id)
JOIN    metcap.lease_term_v ltv ON (lt.id = ltv.holder)
JOIN    metcap.billable_item b ON (b.id = ltv.lease_products_service_item)
WHERE   l.status = 'Active' 
AND     ltv.to_date IS NULL
GROUP BY l.lease_id
HAVING  count(b.id) > 1
UNION
SELECT  'realstar' AS pmc, l.lease_id, count(b.id) AS service_count
FROM    realstar.lease l
JOIN    realstar.lease_term lt ON (l.id = lt.lease AND l.current_term = lt.id)
JOIN    realstar.lease_term_v ltv ON (lt.id = ltv.holder)
JOIN    realstar.billable_item b ON (b.id = ltv.lease_products_service_item)
WHERE   l.status = 'Active' 
AND     ltv.to_date IS NULL
GROUP BY l.lease_id
HAVING  count(b.id) > 1
UNION
SELECT  'sterling' AS pmc, l.lease_id, count(b.id) AS service_count
FROM    sterling.lease l
JOIN    sterling.lease_term lt ON (l.id = lt.lease AND l.current_term = lt.id)
JOIN    sterling.lease_term_v ltv ON (lt.id = ltv.holder)
JOIN    sterling.billable_item b ON (b.id = ltv.lease_products_service_item)
WHERE   l.status = 'Active' 
AND     ltv.to_date IS NULL
GROUP BY l.lease_id
HAVING  count(b.id) > 1
ORDER BY 1,2;
