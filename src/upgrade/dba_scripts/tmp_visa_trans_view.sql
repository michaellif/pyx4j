/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             visa transactions view (very temporary)
***
***     ======================================================================================================================
**/

CREATE OR REPLACE VIEW _dba_.visa_trans AS
(
    SELECT  'berkley' AS pmc, b.property_code, l.lease_id, 
            c.person_name_first_name||' '||c.person_name_last_name AS name,
            c.person_email,
            p.amount, ct.fee_amount,p.id AS payment_id, p.finalize_date
    FROM    _admin_.card_transaction_record ct
    JOIN    berkley.payment_record p ON (p.id = regexp_replace(ct.payment_transaction_id, '[A-Z]', '')::bigint)
    JOIN    berkley.lease_term_participant ltp ON (ltp.id = p.lease_term_participant)
    JOIN    berkley.lease_participant lp ON (lp.id = ltp.lease_participant)
    JOIN    berkley.customer c ON (c.id = lp.customer)
    JOIN    berkley.lease l ON (l.id = lp.lease) 
    JOIN    berkley.apt_unit a ON (a.id = l.unit)
    JOIN    berkley.building b ON (b.id = a.building)
    WHERE   ct.card_type = 'Visa'
    AND     ct.fee_amount IS NOT NULL
    AND     ct.sale_response_code = '0000'
    UNION
    SELECT  'cogir' AS pmc, b.property_code, l.lease_id, 
            c.person_name_first_name||' '||c.person_name_last_name AS name,
            c.person_email,
            p.amount, ct.fee_amount,p.id AS payment_id, p.finalize_date
    FROM    _admin_.card_transaction_record ct
    JOIN    cogir.payment_record p ON (p.id = regexp_replace(ct.payment_transaction_id, '[A-Z]', '')::bigint)
    JOIN    cogir.lease_term_participant ltp ON (ltp.id = p.lease_term_participant)
    JOIN    cogir.lease_participant lp ON (lp.id = ltp.lease_participant)
    JOIN    cogir.customer c ON (c.id = lp.customer)
    JOIN    cogir.lease l ON (l.id = lp.lease) 
    JOIN    cogir.apt_unit a ON (a.id = l.unit)
    JOIN    cogir.building b ON (b.id = a.building)
    WHERE   ct.card_type = 'Visa'
    AND     ct.fee_amount IS NOT NULL
    AND     ct.sale_response_code = '0000'
    UNION
    SELECT  'greenwin' AS pmc, b.property_code, l.lease_id, 
            c.person_name_first_name||' '||c.person_name_last_name AS name,
            c.person_email,
            p.amount, ct.fee_amount,p.id AS payment_id, p.finalize_date
    FROM    _admin_.card_transaction_record ct
    JOIN    greenwin.payment_record p ON (p.id = regexp_replace(ct.payment_transaction_id, '[A-Z]', '')::bigint)
    JOIN    greenwin.lease_term_participant ltp ON (ltp.id = p.lease_term_participant)
    JOIN    greenwin.lease_participant lp ON (lp.id = ltp.lease_participant)
    JOIN    greenwin.customer c ON (c.id = lp.customer)
    JOIN    greenwin.lease l ON (l.id = lp.lease) 
    JOIN    greenwin.apt_unit a ON (a.id = l.unit)
    JOIN    greenwin.building b ON (b.id = a.building)
    WHERE   ct.card_type = 'Visa'
    AND     ct.fee_amount IS NOT NULL
    AND     ct.sale_response_code = '0000'
    UNION
    SELECT  'larlyn' AS pmc, b.property_code, l.lease_id, 
            c.person_name_first_name||' '||c.person_name_last_name AS name,
            c.person_email,
            p.amount, ct.fee_amount,p.id AS payment_id, p.finalize_date
    FROM    _admin_.card_transaction_record ct
    JOIN    larlyn.payment_record p ON (p.id = regexp_replace(ct.payment_transaction_id, '[A-Z]', '')::bigint)
    JOIN    larlyn.lease_term_participant ltp ON (ltp.id = p.lease_term_participant)
    JOIN    larlyn.lease_participant lp ON (lp.id = ltp.lease_participant)
    JOIN    larlyn.customer c ON (c.id = lp.customer)
    JOIN    larlyn.lease l ON (l.id = lp.lease) 
    JOIN    larlyn.apt_unit a ON (a.id = l.unit)
    JOIN    larlyn.building b ON (b.id = a.building)
    WHERE   ct.card_type = 'Visa'
    AND     ct.fee_amount IS NOT NULL
    AND     ct.sale_response_code = '0000'
    UNION
    SELECT  'realstar' AS pmc, b.property_code, l.lease_id, 
            c.person_name_first_name||' '||c.person_name_last_name AS name,
            c.person_email,
            p.amount, ct.fee_amount,p.id AS payment_id, p.finalize_date
    FROM    _admin_.card_transaction_record ct
    JOIN    realstar.payment_record p ON (p.id = regexp_replace(ct.payment_transaction_id, '[A-Z]', '')::bigint)
    JOIN    realstar.lease_term_participant ltp ON (ltp.id = p.lease_term_participant)
    JOIN    realstar.lease_participant lp ON (lp.id = ltp.lease_participant)
    JOIN    realstar.customer c ON (c.id = lp.customer)
    JOIN    realstar.lease l ON (l.id = lp.lease) 
    JOIN    realstar.apt_unit a ON (a.id = l.unit)
    JOIN    realstar.building b ON (b.id = a.building)
    WHERE   ct.card_type = 'Visa'
    AND     ct.fee_amount IS NOT NULL
    AND     ct.sale_response_code = '0000'
    UNION
    SELECT  'sterling' AS pmc, b.property_code, l.lease_id, 
            c.person_name_first_name||' '||c.person_name_last_name AS name,
            c.person_email,
            p.amount, ct.fee_amount,p.id AS payment_id, p.finalize_date
    FROM    _admin_.card_transaction_record ct
    JOIN    sterling.payment_record p ON (p.id = regexp_replace(ct.payment_transaction_id, '[A-Z]', '')::bigint)
    JOIN    sterling.lease_term_participant ltp ON (ltp.id = p.lease_term_participant)
    JOIN    sterling.lease_participant lp ON (lp.id = ltp.lease_participant)
    JOIN    sterling.customer c ON (c.id = lp.customer)
    JOIN    sterling.lease l ON (l.id = lp.lease) 
    JOIN    sterling.apt_unit a ON (a.id = l.unit)
    JOIN    sterling.building b ON (b.id = a.building)
    WHERE   ct.card_type = 'Visa'
    AND     ct.fee_amount IS NOT NULL
    AND     ct.sale_response_code = '0000'
    ORDER BY 1,8 
);
