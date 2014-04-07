/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Estimate number of emails to be sent every month
***
***     ===========================================================================================================
**/  

CREATE OR REPLACE VIEW _dba_.pmc_email_count AS 
(
    SELECT  'berkley' AS pmc,DATE_TRUNC('month',current_date) AS month,
            COUNT(p.id) AS email_count
    FROM    berkley.payment_record p
    JOIN    berkley.autopay_agreement a ON (a.id = p.preauthorized_payment)
    JOIN    berkley.lease_participant lp ON (lp.id = a.tenant)
    JOIN    berkley.customer c ON (c.id = lp.customer)
    WHERE   DATE_TRUNC('month',created_date) = DATE_TRUNC('month',current_date)
    AND     c.user_id IS NOT NULL
    UNION
    SELECT  'cogir' AS pmc,DATE_TRUNC('month',current_date) AS month,
            COUNT(p.id) AS email_count
    FROM    cogir.payment_record p
    JOIN    cogir.autopay_agreement a ON (a.id = p.preauthorized_payment)
    JOIN    cogir.lease_participant lp ON (lp.id = a.tenant)
    JOIN    cogir.customer c ON (c.id = lp.customer)
    WHERE   DATE_TRUNC('month',created_date) = DATE_TRUNC('month',current_date)
    AND     c.user_id IS NOT NULL
    UNION
    SELECT  'greenwin' AS pmc,DATE_TRUNC('month',current_date) AS month,
            COUNT(p.id) AS email_count
    FROM    greenwin.payment_record p
    JOIN    greenwin.autopay_agreement a ON (a.id = p.preauthorized_payment)
    JOIN    greenwin.lease_participant lp ON (lp.id = a.tenant)
    JOIN    greenwin.customer c ON (c.id = lp.customer)
    WHERE   DATE_TRUNC('month',created_date) = DATE_TRUNC('month',current_date)
    AND     c.user_id IS NOT NULL
    UNION
    SELECT  'larlyn' AS pmc,DATE_TRUNC('month',current_date) AS month,
            COUNT(p.id) AS email_count
    FROM    larlyn.payment_record p
    JOIN    larlyn.autopay_agreement a ON (a.id = p.preauthorized_payment)
    JOIN    larlyn.lease_participant lp ON (lp.id = a.tenant)
    JOIN    larlyn.customer c ON (c.id = lp.customer)
    WHERE   DATE_TRUNC('month',created_date) = DATE_TRUNC('month',current_date)
    AND     c.user_id IS NOT NULL
    UNION
    SELECT  'metcap' AS pmc,DATE_TRUNC('month',current_date) AS month,
            COUNT(p.id) AS email_count
    FROM    metcap.payment_record p
    JOIN    metcap.autopay_agreement a ON (a.id = p.preauthorized_payment)
    JOIN    metcap.lease_participant lp ON (lp.id = a.tenant)
    JOIN    metcap.customer c ON (c.id = lp.customer)
    WHERE   DATE_TRUNC('month',created_date) = DATE_TRUNC('month',current_date)
    AND     c.user_id IS NOT NULL
    UNION
    SELECT  'ofm' AS pmc,DATE_TRUNC('month',current_date) AS month,
            COUNT(p.id) AS email_count
    FROM    ofm.payment_record p
    JOIN    ofm.autopay_agreement a ON (a.id = p.preauthorized_payment)
    JOIN    ofm.lease_participant lp ON (lp.id = a.tenant)
    JOIN    ofm.customer c ON (c.id = lp.customer)
    WHERE   DATE_TRUNC('month',created_date) = DATE_TRUNC('month',current_date)
    AND     c.user_id IS NOT NULL
    UNION
    SELECT  'realstar' AS pmc,DATE_TRUNC('month',current_date) AS month,
            COUNT(p.id) AS email_count
    FROM    realstar.payment_record p
    JOIN    realstar.autopay_agreement a ON (a.id = p.preauthorized_payment)
    JOIN    realstar.lease_participant lp ON (lp.id = a.tenant)
    JOIN    realstar.customer c ON (c.id = lp.customer)
    WHERE   DATE_TRUNC('month',created_date) = DATE_TRUNC('month',current_date)
    AND     c.user_id IS NOT NULL
    UNION
    SELECT  'sterling' AS pmc,DATE_TRUNC('month',current_date) AS month,
            COUNT(p.id) AS email_count
    FROM    sterling.payment_record p
    JOIN    sterling.autopay_agreement a ON (a.id = p.preauthorized_payment)
    JOIN    sterling.lease_participant lp ON (lp.id = a.tenant)
    JOIN    sterling.customer c ON (c.id = lp.customer)
    WHERE   DATE_TRUNC('month',created_date) = DATE_TRUNC('month',current_date)
    AND     c.user_id IS NOT NULL
    UNION
    SELECT  'woodbuffaloproperties' AS pmc,DATE_TRUNC('month',current_date) AS month,
            COUNT(p.id) AS email_count
    FROM    woodbuffaloproperties.payment_record p
    JOIN    woodbuffaloproperties.autopay_agreement a ON (a.id = p.preauthorized_payment)
    JOIN    woodbuffaloproperties.lease_participant lp ON (lp.id = a.tenant)
    JOIN    woodbuffaloproperties.customer c ON (c.id = lp.customer)
    WHERE   DATE_TRUNC('month',created_date) = DATE_TRUNC('month',current_date)
    AND     c.user_id IS NOT NULL
    ORDER BY 1
);
