/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***            Export dependatns with paps
***
***     ======================================================================================================================
**/

CREATE OR REPLACE VIEW _dba_.dep_with_pap AS
(
SELECT 'berkley' AS pmc,
        b.property_code,
        a.info_unit_number AS unit,
        l.lease_id,
        lp.participant_id,
        ltp.participant_role
FROM    berkley.building b
JOIN    berkley.apt_unit a ON (b.id = a.building)
JOIN    berkley.lease l ON (a.id = l.unit)
JOIN    berkley.lease_participant lp ON (l.id = lp.lease)
JOIN    berkley.lease_term_participant ltp ON (lp.id = ltp.lease_participant)
JOIN    berkley.customer c ON (c.id = lp.customer)
JOIN    berkley.payment_method pm ON (c.id = pm.customer)
JOIN    berkley.preauthorized_payment p ON (pm.id = p.payment_method)
WHERE   ltp.participant_role = 'Dependent'
AND     l.status = 'Active'
AND     pm.id_discriminator = 'LeasePaymentMethod'
AND NOT p.is_deleted
AND NOT b.suspended
UNION
SELECT 'cogir' AS pmc,
        b.property_code,
        a.info_unit_number AS unit,
        l.lease_id,
        lp.participant_id,
        ltp.participant_role
FROM    cogir.building b
JOIN    cogir.apt_unit a ON (b.id = a.building)
JOIN    cogir.lease l ON (a.id = l.unit)
JOIN    cogir.lease_participant lp ON (l.id = lp.lease)
JOIN    cogir.lease_term_participant ltp ON (lp.id = ltp.lease_participant)
JOIN    cogir.customer c ON (c.id = lp.customer)
JOIN    cogir.payment_method pm ON (c.id = pm.customer)
JOIN    cogir.preauthorized_payment p ON (pm.id = p.payment_method)
WHERE   ltp.participant_role = 'Dependent'
AND     l.status = 'Active'
AND     pm.id_discriminator = 'LeasePaymentMethod'
AND NOT p.is_deleted
AND NOT b.suspended
UNION
SELECT 'gateway' AS pmc,
        b.property_code,
        a.info_unit_number AS unit,
        l.lease_id,
        lp.participant_id,
        ltp.participant_role
FROM    gateway.building b
JOIN    gateway.apt_unit a ON (b.id = a.building)
JOIN    gateway.lease l ON (a.id = l.unit)
JOIN    gateway.lease_participant lp ON (l.id = lp.lease)
JOIN    gateway.lease_term_participant ltp ON (lp.id = ltp.lease_participant)
JOIN    gateway.customer c ON (c.id = lp.customer)
JOIN    gateway.payment_method pm ON (c.id = pm.customer)
JOIN    gateway.preauthorized_payment p ON (pm.id = p.payment_method)
WHERE   ltp.participant_role = 'Dependent'
AND     l.status = 'Active'
AND     pm.id_discriminator = 'LeasePaymentMethod'
AND NOT p.is_deleted
AND NOT b.suspended
UNION
SELECT 'greenwin' AS pmc,
        b.property_code,
        a.info_unit_number AS unit,
        l.lease_id,
        lp.participant_id,
        ltp.participant_role
FROM    greenwin.building b
JOIN    greenwin.apt_unit a ON (b.id = a.building)
JOIN    greenwin.lease l ON (a.id = l.unit)
JOIN    greenwin.lease_participant lp ON (l.id = lp.lease)
JOIN    greenwin.lease_term_participant ltp ON (lp.id = ltp.lease_participant)
JOIN    greenwin.customer c ON (c.id = lp.customer)
JOIN    greenwin.payment_method pm ON (c.id = pm.customer)
JOIN    greenwin.preauthorized_payment p ON (pm.id = p.payment_method)
WHERE   ltp.participant_role = 'Dependent'
AND     l.status = 'Active'
AND     pm.id_discriminator = 'LeasePaymentMethod'
AND NOT p.is_deleted
AND NOT b.suspended
UNION
SELECT 'larlyn' AS pmc,
        b.property_code,
        a.info_unit_number AS unit,
        l.lease_id,
        lp.participant_id,
        ltp.participant_role
FROM    larlyn.building b
JOIN    larlyn.apt_unit a ON (b.id = a.building)
JOIN    larlyn.lease l ON (a.id = l.unit)
JOIN    larlyn.lease_participant lp ON (l.id = lp.lease)
JOIN    larlyn.lease_term_participant ltp ON (lp.id = ltp.lease_participant)
JOIN    larlyn.customer c ON (c.id = lp.customer)
JOIN    larlyn.payment_method pm ON (c.id = pm.customer)
JOIN    larlyn.preauthorized_payment p ON (pm.id = p.payment_method)
WHERE   ltp.participant_role = 'Dependent'
AND     l.status = 'Active'
AND     pm.id_discriminator = 'LeasePaymentMethod'
AND NOT p.is_deleted
AND NOT b.suspended
UNION
SELECT 'metcap' AS pmc,
        b.property_code,
        a.info_unit_number AS unit,
        l.lease_id,
        lp.participant_id,
        ltp.participant_role
FROM    metcap.building b
JOIN    metcap.apt_unit a ON (b.id = a.building)
JOIN    metcap.lease l ON (a.id = l.unit)
JOIN    metcap.lease_participant lp ON (l.id = lp.lease)
JOIN    metcap.lease_term_participant ltp ON (lp.id = ltp.lease_participant)
JOIN    metcap.customer c ON (c.id = lp.customer)
JOIN    metcap.payment_method pm ON (c.id = pm.customer)
JOIN    metcap.preauthorized_payment p ON (pm.id = p.payment_method)
WHERE   ltp.participant_role = 'Dependent'
AND     l.status = 'Active'
AND     pm.id_discriminator = 'LeasePaymentMethod'
AND NOT p.is_deleted
AND NOT b.suspended
UNION
SELECT 'pangroup' AS pmc,
        b.property_code,
        a.info_unit_number AS unit,
        l.lease_id,
        lp.participant_id,
        ltp.participant_role
FROM    pangroup.building b
JOIN    pangroup.apt_unit a ON (b.id = a.building)
JOIN    pangroup.lease l ON (a.id = l.unit)
JOIN    pangroup.lease_participant lp ON (l.id = lp.lease)
JOIN    pangroup.lease_term_participant ltp ON (lp.id = ltp.lease_participant)
JOIN    pangroup.customer c ON (c.id = lp.customer)
JOIN    pangroup.payment_method pm ON (c.id = pm.customer)
JOIN    pangroup.preauthorized_payment p ON (pm.id = p.payment_method)
WHERE   ltp.participant_role = 'Dependent'
AND     l.status = 'Active'
AND     pm.id_discriminator = 'LeasePaymentMethod'
AND NOT p.is_deleted
AND NOT b.suspended
UNION
SELECT 'realstar' AS pmc,
        b.property_code,
        a.info_unit_number AS unit,
        l.lease_id,
        lp.participant_id,
        ltp.participant_role
FROM    realstar.building b
JOIN    realstar.apt_unit a ON (b.id = a.building)
JOIN    realstar.lease l ON (a.id = l.unit)
JOIN    realstar.lease_participant lp ON (l.id = lp.lease)
JOIN    realstar.lease_term_participant ltp ON (lp.id = ltp.lease_participant)
JOIN    realstar.customer c ON (c.id = lp.customer)
JOIN    realstar.payment_method pm ON (c.id = pm.customer)
JOIN    realstar.preauthorized_payment p ON (pm.id = p.payment_method)
WHERE   ltp.participant_role = 'Dependent'
AND     l.status = 'Active'
AND     pm.id_discriminator = 'LeasePaymentMethod'
AND NOT p.is_deleted
AND NOT b.suspended
UNION
SELECT 'sterling' AS pmc,
        b.property_code,
        a.info_unit_number AS unit,
        l.lease_id,
        lp.participant_id,
        ltp.participant_role
FROM    sterling.building b
JOIN    sterling.apt_unit a ON (b.id = a.building)
JOIN    sterling.lease l ON (a.id = l.unit)
JOIN    sterling.lease_participant lp ON (l.id = lp.lease)
JOIN    sterling.lease_term_participant ltp ON (lp.id = ltp.lease_participant)
JOIN    sterling.customer c ON (c.id = lp.customer)
JOIN    sterling.payment_method pm ON (c.id = pm.customer)
JOIN    sterling.preauthorized_payment p ON (pm.id = p.payment_method)
WHERE   ltp.participant_role = 'Dependent'
AND     l.status = 'Active'
AND     pm.id_discriminator = 'LeasePaymentMethod'
AND NOT p.is_deleted
AND NOT b.suspended
);

\! touch /home/akinareevski/dep_with_pap.csv
\! chmod 0666 /home/akinareevski/dep_with_pap.csv

COPY (SELECT * FROM _dba_.dep_with_pap ORDER BY 1,2,3) TO '/home/akinareevski/dep_with_pap.csv' CSV HEADER;

DROP VIEW _dba_.dep_with_pap;




