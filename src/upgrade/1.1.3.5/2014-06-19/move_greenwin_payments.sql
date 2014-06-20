/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Move payment records from greenwin to dms
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

    /**
    Needed Data :
    
    lease | lease_participant | customer | lease_term_participant 
    -------+-------------------+----------+------------------------
    50907 |             82702 |    82633 |                 350083

    new merchant account       = 153;
     
    **/
    
    -- Insert payment details
    
    INSERT INTO dms.payment_payment_details (id,id_discriminator,name_on,
    bank_id,branch_transit_number,account_no_number,account_no_obfuscated_number,
    received_amount,change_amount,notes,incoming_interac_transaction,
    bank_no,transit_no,account_no,bank_name,account_type,check_no,
    institution_no,card_type,card_obfuscated_number,token,expiry_date,
    bank_phone,location_code,trace_number)
    (SELECT     ppd.id,ppd.id_discriminator,ppd.name_on,ppd.bank_id,ppd.branch_transit_number,
                ppd.account_no_number,ppd.account_no_obfuscated_number,
                ppd.received_amount,ppd.change_amount,ppd.notes,ppd.incoming_interac_transaction,
                ppd.bank_no,ppd.transit_no,ppd.account_no,ppd.bank_name,ppd.account_type,ppd.check_no,
                ppd.institution_no,ppd.card_type,ppd.card_obfuscated_number,ppd.token,ppd.expiry_date,
                ppd.bank_phone,ppd.location_code,ppd.trace_number
    FROM    greenwin.payment_payment_details ppd
    JOIN    greenwin.payment_method pm ON (ppd.id = pm.details)
    JOIN    greenwin.payment_record p ON (pm.id = p.payment_method)
    WHERE   p.id IN (52381,47842,43410));
    
    
    -- Insert payment_method
    
    INSERT INTO dms.payment_method (id,id_discriminator,payment_type,details_discriminator,
    details,same_as_current,billing_address_street1,billing_address_street2,
    billing_address_city,billing_address_province,billing_address_country,
    billing_address_postal_code,is_deleted,creation_date,updated,
    created_by_discriminator,created_by,customer,order_id,is_profiled_method,
    tenant_discriminator,tenant,signature)
    (SELECT pm.id,pm.id_discriminator,pm.payment_type,pm.details_discriminator,
            pm.details,pm.same_as_current,pm.billing_address_street1,pm.billing_address_street2,
            pm.billing_address_city,pm.billing_address_province,pm.billing_address_country,
            pm.billing_address_postal_code,pm.is_deleted,pm.creation_date,pm.updated,
            pm.created_by_discriminator,pm.created_by,dc.id AS customer,pm.order_id,pm.is_profiled_method,
            pm.tenant_discriminator,pm.tenant,pm.signature
    FROM    greenwin.payment_method pm
    JOIN    greenwin.payment_record p ON (pm.id = p.payment_method)
    JOIN    greenwin.customer gc ON (pm.customer = gc.id)
    JOIN    greenwin.lease_participant glp ON (gc.id = glp.customer)
    JOIN    dms.lease_participant dlp ON (glp.participant_id = dlp.participant_id)
    JOIN    dms.customer dc ON (dlp.customer = dc.id)
    WHERE   p.id IN (52381,47842,43410));
    
    -- Insert into payment_record
    
    INSERT INTO dms.payment_record (id,billing_account,pad_billing_cycle,preauthorized_payment,
    lease_term_participant_discriminator,lease_term_participant,yardi_document_number,
    received_date,finalize_date,last_status_change_date,target_date,amount,
    convenience_fee,convenience_fee_reference_number,payment_method_discriminator,
    payment_method,payment_status,merchant_account,transaction_error_message,
    transaction_authorization_number,convenience_fee_transaction_authorization_number,
    batch,aggregated_transfer,aggregated_transfer_return,pad_reconciliation_debit_record_key,
    pad_reconciliation_return_record_key,notice,notes,updated,created_date,created_by_discriminator,
    created_by)
    (SELECT p.id,db.id AS billing_account,p.pad_billing_cycle,p.preauthorized_payment,
            p.lease_term_participant_discriminator,dltp.id AS lease_term_participant,p.yardi_document_number,
            p.received_date,p.finalize_date,p.last_status_change_date,p.target_date,p.amount,
            p.convenience_fee,p.convenience_fee_reference_number,p.payment_method_discriminator,
            p.payment_method,p.payment_status,153 AS merchant_account,p.transaction_error_message,
            p.transaction_authorization_number,p.convenience_fee_transaction_authorization_number,
            p.batch,p.aggregated_transfer,p.aggregated_transfer_return,p.pad_reconciliation_debit_record_key,
            p.pad_reconciliation_return_record_key,p.notice,p.notes,p.updated,p.created_date,p.created_by_discriminator,
            p.created_by
    FROM    greenwin.payment_record p
    JOIN    greenwin.lease_term_participant gltp ON (gltp.id = p.lease_term_participant)
    JOIN    greenwin.lease_participant glp ON (glp.id = gltp.lease_participant)
    JOIN    dms.lease_participant dlp ON (dlp.participant_id = glp.participant_id)
    JOIN    dms.lease_term_participant dltp ON (dlp.id = dltp.lease_participant)
    JOIN    dms.lease_term_v dltv ON (dltp.lease_term_v = dltv.id)
    JOIN    dms.lease dl ON (dl.id = dlp.lease)
    JOIN    dms.billing_account db ON (db.id = dl.billing_account)
    WHERE   p.id IN (52381,47842,43410)
    AND     dltv.to_date IS NULL);
    
    -- billing_invoice_line_item
    
    INSERT INTO dms.billing_invoice_line_item (id,id_discriminator,billing_account,
    billing_cycle,description,amount,post_date,ar_code,order_id,charge_code,
    outstanding_credit,product_charge_discriminator,product_charge,outstanding_debit,
    due_date,tax_total,payment_record,apply_nsf,period,product_type,from_date,
    to_date,adjustment,deposit,transaction_id,comment,service_type,amount_paid,
    balance_due)
    (SELECT nextval('public.billing_invoice_line_item_seq') AS id,b.id_discriminator, db.id AS billing_account,
            dbc.id AS billing_cycle,b.description,b.amount,b.post_date,b.ar_code,b.order_id,
            b.charge_code,b.outstanding_credit,b.product_charge_discriminator,b.product_charge,
            b.outstanding_debit,b.due_date,b.tax_total,b.payment_record,b.apply_nsf,b.period,
            b.product_type,b.from_date,b.to_date,b.adjustment,b.deposit,b.transaction_id,b.comment,
            b.service_type,b.amount_paid,b.balance_due
    FROM    greenwin.billing_invoice_line_item b
    JOIN    greenwin.billing_billing_cycle gbc ON (gbc.id = b.billing_cycle)
    JOIN    dms.payment_record dp ON (dp.id = b.payment_record)
    JOIN    dms.lease_term_participant dltp ON (dltp.id = dp.lease_term_participant)
    JOIN    dms.lease_participant dlp ON (dlp.id = dltp.lease_participant)
    JOIN    dms.lease_term_v dltv ON (dltp.lease_term_v = dltv.id)
    JOIN    dms.lease dl ON (dl.id = dlp.lease)
    JOIN    dms.billing_account db ON (db.id = dl.billing_account)
    JOIN    dms.apt_unit da ON (da.id = dl.unit)
    JOIN    dms.building bd ON (bd.id = da.building)
    JOIN    dms.billing_billing_cycle dbc ON (bd.id = dbc.building AND gbc.billing_cycle_start_date = dbc.billing_cycle_start_date)
    WHERE   b.payment_record IN (52381,47842,43410)
    AND     dltv.to_date IS NULL);
    
    -- Delete payment record, payment method  and payment detail

    DELETE FROM greenwin.billing_invoice_line_item
    WHERE   payment_record IN (52381,47842,43410);
    
    DELETE  FROM greenwin.payment_record 
    WHERE   id IN (52381,47842,43410);
    
    DELETE  FROM greenwin.payment_method 
    WHERE   id IN ( SELECT  payment_method FROM dms.payment_record
                    WHERE   id IN (52381,47842,43410));
    
    DELETE  FROM greenwin.payment_payment_details 
    WHERE   id IN ( SELECT  pm.details
                    FROM    dms.payment_record p
                    JOIN    dms.payment_method pm ON (pm.id = p.payment_method) 
                    WHERE   p.id IN (52381,47842,43410));
                    
    -- Update of _admin_.direct_debit_record
    
    UPDATE  _admin_.direct_debit_record AS d
    SET     pmc = 493
    FROM    dms.payment_record p 
    WHERE   p.transaction_authorization_number = d.payment_reference_number
    AND     p.id IN (52381,47842,43410);

-COMMIT;
