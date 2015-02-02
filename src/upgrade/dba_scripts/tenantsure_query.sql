SELECT  'berkley' AS pmc, b.property_code,
        c.person_name_first_name AS first_name,
        c.person_name_last_name AS last_name,
        ic.insurance_certificate_number,
        ip.status, ic.inception_date,
        ip.cancellation,
        ip.cancellation_date,
        pm.id AS payment_method,
        ppd.expiry_date, b.suspended
FROM    berkley.customer c
JOIN    berkley.lease_participant lp ON (c.id = lp.customer)
JOIN    berkley.insurance_policy ip ON (lp.id = ip.tenant)
JOIN    berkley.insurance_certificate ic ON (ip.id = ic.insurance_policy)
JOIN    berkley.payment_method pm ON (lp.id = pm.tenant)
JOIN    berkley.payment_payment_details ppd ON (ppd.id = pm.details)
JOIN    berkley.lease l ON (l.id = lp.lease)
JOIN    berkley.apt_unit a ON (a.id = l.unit)
JOIN    berkley.building b ON (b.id = a.building)
JOIN    _admin_.tenant_sure_subscribers t ON (t.certificate_number = ic.insurance_certificate_number)
AND NOT pm.is_deleted
UNION
SELECT  'cogir' AS pmc, b.property_code,
        c.person_name_first_name AS first_name,
        c.person_name_last_name AS last_name,
        ic.insurance_certificate_number,
        ip.status, ic.inception_date,
        ip.cancellation,
        ip.cancellation_date,
        pm.id AS payment_method,
        ppd.expiry_date, b.suspended
FROM    cogir.customer c
JOIN    cogir.lease_participant lp ON (c.id = lp.customer)
JOIN    cogir.insurance_policy ip ON (lp.id = ip.tenant)
JOIN    cogir.insurance_certificate ic ON (ip.id = ic.insurance_policy)
JOIN    cogir.payment_method pm ON (lp.id = pm.tenant)
JOIN    cogir.payment_payment_details ppd ON (ppd.id = pm.details)
JOIN    cogir.lease l ON (l.id = lp.lease)
JOIN    cogir.apt_unit a ON (a.id = l.unit)
JOIN    cogir.building b ON (b.id = a.building)
JOIN    _admin_.tenant_sure_subscribers t ON (t.certificate_number = ic.insurance_certificate_number)
AND NOT pm.is_deleted
UNION
SELECT  'dms' AS pmc, b.property_code,
        c.person_name_first_name AS first_name,
        c.person_name_last_name AS last_name,
        ic.insurance_certificate_number,
        ip.status, ic.inception_date,
        ip.cancellation,
        ip.cancellation_date,
        pm.id AS payment_method,
        ppd.expiry_date, b.suspended
FROM    dms.customer c
JOIN    dms.lease_participant lp ON (c.id = lp.customer)
JOIN    dms.insurance_policy ip ON (lp.id = ip.tenant)
JOIN    dms.insurance_certificate ic ON (ip.id = ic.insurance_policy)
JOIN    dms.payment_method pm ON (lp.id = pm.tenant)
JOIN    dms.payment_payment_details ppd ON (ppd.id = pm.details)
JOIN    dms.lease l ON (l.id = lp.lease)
JOIN    dms.apt_unit a ON (a.id = l.unit)
JOIN    dms.building b ON (b.id = a.building)
JOIN    _admin_.tenant_sure_subscribers t ON (t.certificate_number = ic.insurance_certificate_number)
AND NOT pm.is_deleted
UNION
SELECT  'greenwin' AS pmc, b.property_code,
        c.person_name_first_name AS first_name,
        c.person_name_last_name AS last_name,
        ic.insurance_certificate_number,
        ip.status, ic.inception_date,
        ip.cancellation,
        ip.cancellation_date,
        pm.id AS payment_method,
        ppd.expiry_date, b.suspended
FROM    greenwin.customer c
JOIN    greenwin.lease_participant lp ON (c.id = lp.customer)
JOIN    greenwin.insurance_policy ip ON (lp.id = ip.tenant)
JOIN    greenwin.insurance_certificate ic ON (ip.id = ic.insurance_policy)
JOIN    greenwin.payment_method pm ON (lp.id = pm.tenant)
JOIN    greenwin.payment_payment_details ppd ON (ppd.id = pm.details)
JOIN    greenwin.lease l ON (l.id = lp.lease)
JOIN    greenwin.apt_unit a ON (a.id = l.unit)
JOIN    greenwin.building b ON (b.id = a.building)
JOIN    _admin_.tenant_sure_subscribers t ON (t.certificate_number = ic.insurance_certificate_number)
AND NOT pm.is_deleted
UNION
SELECT  'larlyn' AS pmc, b.property_code,
        c.person_name_first_name AS first_name,
        c.person_name_last_name AS last_name,
        ic.insurance_certificate_number,
        ip.status, ic.inception_date,
        ip.cancellation,
        ip.cancellation_date,
        pm.id AS payment_method,
        ppd.expiry_date, b.suspended
FROM    larlyn.customer c
JOIN    larlyn.lease_participant lp ON (c.id = lp.customer)
JOIN    larlyn.insurance_policy ip ON (lp.id = ip.tenant)
JOIN    larlyn.insurance_certificate ic ON (ip.id = ic.insurance_policy)
JOIN    larlyn.payment_method pm ON (lp.id = pm.tenant)
JOIN    larlyn.payment_payment_details ppd ON (ppd.id = pm.details)
JOIN    larlyn.lease l ON (l.id = lp.lease)
JOIN    larlyn.apt_unit a ON (a.id = l.unit)
JOIN    larlyn.building b ON (b.id = a.building)
JOIN    _admin_.tenant_sure_subscribers t ON (t.certificate_number = ic.insurance_certificate_number)
AND NOT pm.is_deleted
UNION
SELECT  'metcap' AS pmc, b.property_code,
        c.person_name_first_name AS first_name,
        c.person_name_last_name AS last_name,
        ic.insurance_certificate_number,
        ip.status, ic.inception_date,
        ip.cancellation,
        ip.cancellation_date,
        pm.id AS payment_method,
        ppd.expiry_date, b.suspended
FROM    metcap.customer c
JOIN    metcap.lease_participant lp ON (c.id = lp.customer)
JOIN    metcap.insurance_policy ip ON (lp.id = ip.tenant)
JOIN    metcap.insurance_certificate ic ON (ip.id = ic.insurance_policy)
JOIN    metcap.payment_method pm ON (lp.id = pm.tenant)
JOIN    metcap.payment_payment_details ppd ON (ppd.id = pm.details)
JOIN    metcap.lease l ON (l.id = lp.lease)
JOIN    metcap.apt_unit a ON (a.id = l.unit)
JOIN    metcap.building b ON (b.id = a.building)
JOIN    _admin_.tenant_sure_subscribers t ON (t.certificate_number = ic.insurance_certificate_number)
AND NOT pm.is_deleted
UNION
SELECT  'nepm' AS pmc, b.property_code,
        c.person_name_first_name AS first_name,
        c.person_name_last_name AS last_name,
        ic.insurance_certificate_number,
        ip.status, ic.inception_date,
        ip.cancellation,
        ip.cancellation_date,
        pm.id AS payment_method,
        ppd.expiry_date, b.suspended
FROM    nepm.customer c
JOIN    nepm.lease_participant lp ON (c.id = lp.customer)
JOIN    nepm.insurance_policy ip ON (lp.id = ip.tenant)
JOIN    nepm.insurance_certificate ic ON (ip.id = ic.insurance_policy)
JOIN    nepm.payment_method pm ON (lp.id = pm.tenant)
JOIN    nepm.payment_payment_details ppd ON (ppd.id = pm.details)
JOIN    nepm.lease l ON (l.id = lp.lease)
JOIN    nepm.apt_unit a ON (a.id = l.unit)
JOIN    nepm.building b ON (b.id = a.building)
JOIN    _admin_.tenant_sure_subscribers t ON (t.certificate_number = ic.insurance_certificate_number)
AND NOT pm.is_deleted
UNION
SELECT  'ofm' AS pmc, b.property_code,
        c.person_name_first_name AS first_name,
        c.person_name_last_name AS last_name,
        ic.insurance_certificate_number,
        ip.status, ic.inception_date,
        ip.cancellation,
        ip.cancellation_date,
        pm.id AS payment_method,
        ppd.expiry_date, b.suspended
FROM    ofm.customer c
JOIN    ofm.lease_participant lp ON (c.id = lp.customer)
JOIN    ofm.insurance_policy ip ON (lp.id = ip.tenant)
JOIN    ofm.insurance_certificate ic ON (ip.id = ic.insurance_policy)
JOIN    ofm.payment_method pm ON (lp.id = pm.tenant)
JOIN    ofm.payment_payment_details ppd ON (ppd.id = pm.details)
JOIN    ofm.lease l ON (l.id = lp.lease)
JOIN    ofm.apt_unit a ON (a.id = l.unit)
JOIN    ofm.building b ON (b.id = a.building)
JOIN    _admin_.tenant_sure_subscribers t ON (t.certificate_number = ic.insurance_certificate_number)
AND NOT pm.is_deleted
UNION
SELECT  'realstar' AS pmc, b.property_code,
        c.person_name_first_name AS first_name,
        c.person_name_last_name AS last_name,
        ic.insurance_certificate_number,
        ip.status, ic.inception_date,
        ip.cancellation,
        ip.cancellation_date,
        pm.id AS payment_method,
        ppd.expiry_date, b.suspended
FROM    realstar.customer c
JOIN    realstar.lease_participant lp ON (c.id = lp.customer)
JOIN    realstar.insurance_policy ip ON (lp.id = ip.tenant)
JOIN    realstar.insurance_certificate ic ON (ip.id = ic.insurance_policy)
JOIN    realstar.payment_method pm ON (lp.id = pm.tenant)
JOIN    realstar.payment_payment_details ppd ON (ppd.id = pm.details)
JOIN    realstar.lease l ON (l.id = lp.lease)
JOIN    realstar.apt_unit a ON (a.id = l.unit)
JOIN    realstar.building b ON (b.id = a.building)
JOIN    _admin_.tenant_sure_subscribers t ON (t.certificate_number = ic.insurance_certificate_number)
AND NOT pm.is_deleted
UNION
SELECT  'sterling' AS pmc, b.property_code,
        c.person_name_first_name AS first_name,
        c.person_name_last_name AS last_name,
        ic.insurance_certificate_number,
        ip.status, ic.inception_date,
        ip.cancellation,
        ip.cancellation_date,
        pm.id AS payment_method,
        ppd.expiry_date, b.suspended
FROM    sterling.customer c
JOIN    sterling.lease_participant lp ON (c.id = lp.customer)
JOIN    sterling.insurance_policy ip ON (lp.id = ip.tenant)
JOIN    sterling.insurance_certificate ic ON (ip.id = ic.insurance_policy)
JOIN    sterling.payment_method pm ON (lp.id = pm.tenant)
JOIN    sterling.payment_payment_details ppd ON (ppd.id = pm.details)
JOIN    sterling.lease l ON (l.id = lp.lease)
JOIN    sterling.apt_unit a ON (a.id = l.unit)
JOIN    sterling.building b ON (b.id = a.building)
JOIN    _admin_.tenant_sure_subscribers t ON (t.certificate_number = ic.insurance_certificate_number)
AND NOT pm.is_deleted
ORDER BY 1,6;
