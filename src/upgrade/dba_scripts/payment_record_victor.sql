SELECT  p.id AS payment_record, m.id AS payment_method,
        m.id_discriminator,p.payment_status,d.card_type,
        p.amount, p.created_date,p.convenience_fee,
        p.convenience_fee_reference_number,
        m1.merchant_terminal_id, m1.merchant_terminal_id_convenience_fee,
        l.lease_id
FROM    greenwin.payment_record p 
JOIN    greenwin.payment_method m ON (m.id = p.payment_method)
JOIN    greenwin.customer c ON (c.id = m.customer)
JOIN    greenwin.lease_participant lp ON (c.id = lp.customer) 
JOIN    greenwin.lease l ON (l.id = lp.lease)
JOIN    greenwin.merchant_account m1 ON (m1.id = p.merchant_account)
JOIN    greenwin.payment_payment_details d ON (d.id = m.details) 
WHERE   p.aggregated_transfer is null 
AND     p.finalize_date >= '2014-06-17' 
AND     p.payment_status = 'Cleared'
AND     m.payment_type = 'CreditCard'
ORDER BY l.lease_id, p.finalize_date;


SELECT  p.id AS payment_record, m.id AS payment_method,
        m.id_discriminator,p.payment_status,d.card_type,
        p.amount, p.created_date,p.convenience_fee,
        p.convenience_fee_reference_number,
        m1.merchant_terminal_id, m1.merchant_terminal_id_convenience_fee,
        l.lease_id
FROM    sterling.payment_record p 
JOIN    sterling.payment_method m ON (m.id = p.payment_method)
JOIN    sterling.customer c ON (c.id = m.customer)
JOIN    sterling.lease_participant lp ON (c.id = lp.customer) 
JOIN    sterling.lease l ON (l.id = lp.lease)
JOIN    sterling.merchant_account m1 ON (m1.id = p.merchant_account)
JOIN    sterling.payment_payment_details d ON (d.id = m.details) 
WHERE   p.aggregated_transfer is null 
AND     p.finalize_date >= '2014-06-17' 
AND     p.payment_status = 'Cleared'
AND     m.payment_type = 'CreditCard'
ORDER BY l.lease_id, p.finalize_date;
