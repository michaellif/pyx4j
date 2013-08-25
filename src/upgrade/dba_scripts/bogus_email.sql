CREATE OR REPLACE VIEW _dba_.bogus_email AS
(SELECT  'berkley' AS pmc,
        l.lease_id,
        b.property_code,
        c.person_name_first_name AS first_name,
        c.person_name_last_name AS last_name,
        c.person_email AS email
FROM    berkley.customer c
JOIN    berkley.lease_participant lp ON (lp.customer = c.id)
JOIN    berkley.lease l ON (lp.lease = l.id)
JOIN    berkley.apt_unit a ON (l.unit = a.id)
JOIN    berkley.building b ON (a.building = b.id)
WHERE   (c.person_email IS NOT NULL AND c.person_email !~  '^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$')
UNION 
SELECT  'greenwin' AS pmc,
        l.lease_id,
        b.property_code,
        c.person_name_first_name AS first_name,
        c.person_name_last_name AS last_name,
        c.person_email AS email
FROM    greenwin.customer c
JOIN    greenwin.lease_participant lp ON (lp.customer = c.id)
JOIN    greenwin.lease l ON (lp.lease = l.id)
JOIN    greenwin.apt_unit a ON (l.unit = a.id)
JOIN    greenwin.building b ON (a.building = b.id)
WHERE   (c.person_email IS NOT NULL AND c.person_email !~  '^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$')
UNION 
SELECT  'realstar' AS pmc,
        l.lease_id,
        b.property_code,
        c.person_name_first_name AS first_name,
        c.person_name_last_name AS last_name,
        c.person_email AS email
FROM    realstar.customer c
JOIN    realstar.lease_participant lp ON (lp.customer = c.id)
JOIN    realstar.lease l ON (lp.lease = l.id)
JOIN    realstar.apt_unit a ON (l.unit = a.id)
JOIN    realstar.building b ON (a.building = b.id)
WHERE   (c.person_email IS NOT NULL AND c.person_email !~  '^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$')
ORDER BY 1,2);


