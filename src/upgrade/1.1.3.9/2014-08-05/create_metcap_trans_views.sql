/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             metcap transaction views
***
***     ======================================================================================================================
**/


CREATE OR REPLACE VIEW _dba_.metcap_payment_details AS
(   SELECT  DISTINCT b.property_code, l.lease_id, 
            c.person_name_first_name ||' '||c.person_name_last_name AS tenant_name,
            ppd.bank_id,ppd.branch_transit_number,
            ppd.account_no_number
    FROM    metcap.payment_method pm
    JOIN    metcap.payment_payment_details ppd ON (ppd.id = pm.details)
    JOIN    metcap.autopay_agreement aa ON (pm.id = aa.payment_method)
    JOIN    metcap.customer c ON (c.id = pm.customer)
    JOIN    metcap.lease_participant lp ON (c.id = lp.customer)
    JOIN    metcap.lease l ON (l.id = lp.lease)
    JOIN    metcap.apt_unit a ON (a.id = l.unit)
    JOIN    metcap.building b ON (b.id = a.building)
    WHERE   b.property_code IN ('rann0001','silv1315','darc7110','east0340',
            'east0350','lake0245')
    AND     l.status = 'Active' 
    ORDER BY 1,2);
    
CREATE OR REPLACE VIEW _dba_.metcap_nsf AS 
(
    SELECT  b.property_code, l.lease_id,
            p.id AS payment_id, p.amount, p.payment_status, 
            p.created_date, p.received_date, p.finalize_date,
            p.transaction_error_message
    FROM    metcap.payment_record p
    JOIN    metcap.lease_term_participant ltp ON (ltp.id = p.lease_term_participant)
    JOIN    metcap.lease_participant lp ON (lp.id = ltp.lease_participant)
    JOIN    metcap.lease l ON (l.id = lp.lease)
    JOIN    metcap.apt_unit a ON (a.id = l.unit)
    JOIN    metcap.building b ON (b.id = a.building)  
    WHERE   --p.transaction_error_message ~ 'NSF'
            p.created_date > '2014-08-01'
    AND     p.payment_status != 'Cleared'
    ORDER BY 1,2
);

SELECT  a.namespace, r.id,r.transaction_id, 
        p.payment_status,
        r.payment_date,r.merchant_terminal_id,r.amount, r.reconciliation_status,r.reason_code, r.reason_text 
FROM    _admin_.funds_reconciliation_record_record r
JOIN    _admin_.admin_pmc_merchant_account_index m ON (r.merchant_terminal_id = m.merchant_terminal_id)
JOIN    _admin_.admin_pmc a ON (a.id = m.pmc)
JOIN    metcap.payment_record p ON (r.transaction_id::bigint = p.id)
WHERE   r.reason_code IS NOT NULL
AND     a.namespace = 'metcap'
AND     r.payment_date >= '01-AUG-2014';

SELECT      DISTINCT b.property_code, l.lease_id, 
            c.person_name_first_name ||' '||c.person_name_last_name AS tenant_name,
            p.amount,p.payment_status,p.transaction_error_message,
            p.created_date,
            ppd.bank_id,ppd.branch_transit_number,
            ppd.account_no_number
    FROM    metcap.payment_method pm
    JOIN    metcap.payment_payment_details ppd ON (ppd.id = pm.details)
    JOIN    metcap.autopay_agreement aa ON (pm.id = aa.payment_method)
    JOIN    metcap.customer c ON (c.id = pm.customer)
    JOIN    metcap.lease_participant lp ON (c.id = lp.customer)
    JOIN    metcap.lease l ON (l.id = lp.lease)
    JOIN    metcap.apt_unit a ON (a.id = l.unit)
    JOIN    metcap.building b ON (b.id = a.building)
    JOIN    metcap.payment_record p ON (pm.id = p.payment_method)
    WHERE   b.property_code IN ('rann0001','silv1315','darc7110','east0340',
            'east0350','lake0245')
    AND     l.status = 'Active' 
    AND     p.created_date > '01-AUG-2014'
    ORDER BY 1,2;
    
SELECT  a.namespace, l.lease_id,b.property_code, r.id,r.transaction_id, 
        p.payment_status,
        r.payment_date,r.merchant_terminal_id,r.amount, r.reconciliation_status,r.reason_code, r.reason_text 
FROM    _admin_.funds_reconciliation_record_record r
JOIN    _admin_.admin_pmc_merchant_account_index m ON (r.merchant_terminal_id = m.merchant_terminal_id)
JOIN    _admin_.admin_pmc a ON (a.id = m.pmc)
JOIN    metcap.payment_record p ON (r.transaction_id::bigint = p.id)
JOIN    metcap.lease_term_participant ltp ON (ltp.id = p.lease_term_participant)
JOIN    metcap.lease_participant lp ON (lp.id = ltp.lease_participant)
JOIN    metcap.lease l ON (l.id = lp.lease)
JOIN    metcap.apt_unit au ON (au.id = l.unit)
JOIN    metcap.building b ON (b.id = au.building)
WHERE   r.reason_code IS NOT NULL
AND     a.namespace = 'metcap'
AND     r.payment_date >= '01-AUG-2014'
ORDER BY 3,2;
