SELECT  DISTINCT b.property_code, 
        a.info_unit_number AS unit,
        c.person_name_first_name||' '||c.person_name_last_name AS tenant,
        c.person_email AS email,
        CASE WHEN t.creation_date IS NOT NULL THEN 'Yes' 
        ELSE 'No' END AS autopay_setup,
        t.creation_date
FROM    greenwin.building b 
JOIN    greenwin.apt_unit a ON (b.id = a.building)
JOIN    greenwin.lease l ON (a.id = l.unit)
JOIN    greenwin.lease_participant lp ON (l.id = lp.lease)
JOIN    greenwin.customer c ON (c.id = lp.customer)
LEFT JOIN   (SELECT tenant, TO_CHAR(creation_date,'DD-MON-YYYY') AS creation_date
            FROM    greenwin.autopay_agreement 
            WHERE   NOT is_deleted) AS t ON (lp.id = t.tenant)
WHERE   l.status = 'Active'
AND     c.person_email IS NOT NULL
AND     b.property_code IN ('rich0675','base0297','base0301','regi0300','univ0137')
ORDER BY 1,2;
