SELECT  b.property_code, 
        a.info_unit_number AS unit,
        c.person_name_first_name||' '||c.person_name_last_name AS tenant
FROM    greenwin.building b 
JOIN    greenwin.apt_unit a ON (b.id = a.building)
JOIN    greenwin.lease l ON (a.id = l.unit)
JOIN    greenwin.lease_participant lp ON (l.id = lp.lease)
JOIN    greenwin.customer c ON (c.id = lp.customer)
WHERE   c.registered_in_portal
AND     l.status = 'Active'
AND     b.property_code IN ('rich0675','base0297','base0301','regi0300','univ0137')
ORDER BY 1,2;
