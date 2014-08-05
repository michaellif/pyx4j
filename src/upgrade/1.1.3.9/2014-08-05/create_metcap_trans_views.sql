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
    WHERE   p.transaction_error_message ~ 'NSF'
    AND     p.last_status_change_date > '2014-08-01'
);
