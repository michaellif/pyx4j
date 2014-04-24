/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Greenwin to dms insurance move
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

    DROP TABLE IF EXISTS _dba_.insurance_policy;
    DROP TABLE IF EXISTS _dba_.insurance_certificate;
    DROP TABLE IF EXISTS _dba_.insurance_certificate_scan;
    DROP TABLE IF EXISTS _dba_.insurance_certificate_scan_blob;
    DROP TABLE IF EXISTS _dba_.payment_method;
    DROP TABLE IF EXISTS _dba_.payment_payment_details;
    DROP TABLE IF EXISTS _dba_.tenant_sure_transaction;
    DROP TABLE IF EXISTS _dba_.tenant_sure_insurance_policy_report;
    DROP TABLE IF EXISTS _dba_.tenant_sure_insurance_policy_client;

    CREATE TABLE _dba_.insurance_policy AS 
    (
        SELECT  i.*,lp.participant_id,cu.email
        FROM    greenwin.insurance_policy i
        JOIN    greenwin.lease_participant lp ON (lp.id = i.tenant)
        JOIN    greenwin.lease l ON (l.id = lp.lease)
        JOIN    greenwin.apt_unit a ON (a.id = l.unit)
        JOIN    greenwin.building b ON (b.id = a.building)
        LEFT JOIN   greenwin.customer_user cu ON (cu.id = i.user_id) 
        WHERE   b.property_code IN ('mark0150','mark0155','mark0160',
                'quee0297','firs0053','huro2465')
        AND     COALESCE(i.status,'') != 'Moved'
    );
    
    CREATE TABLE _dba_.insurance_certificate AS
    (
        SELECT  *
        FROM    greenwin.insurance_certificate
        WHERE   insurance_policy IN (SELECT id FROM _dba_.insurance_policy)
    );
    
    CREATE TABLE _dba_.insurance_certificate_scan AS
    (
        SELECT *
        FROM    greenwin.insurance_certificate_scan
        WHERE   certificate IN (SELECT id FROM _dba_.insurance_certificate)
    );
    
    CREATE TABLE _dba_.insurance_certificate_scan_blob AS
    (
        SELECT  *
        FROM    greenwin.insurance_certificate_scan_blob 
        WHERE   id IN (SELECT file_blob_key FROM _dba_.insurance_certificate_scan)
    );
    
    
    /**
    *** ================================================================
    ***
    ***     For TenantSure 
    ***
    *** ================================================================
    **/
    
    CREATE TABLE _dba_.payment_method AS
    (
        SELECT  pm.*,lp.participant_id,
                p.code,c.name
        FROM    greenwin.payment_method pm
        JOIN    greenwin.lease_participant lp ON (lp.id = pm.tenant)
        JOIN    greenwin.lease l ON (l.id = lp.lease)
        JOIN    greenwin.apt_unit a ON (a.id = l.unit)
        JOIN    greenwin.building b ON (b.id = a.building)
        JOIN    greenwin.province p ON (p.id = pm.billing_address_province)
        JOIN    greenwin.country c ON (c.id = pm.billing_address_country)
        WHERE   b.property_code IN ('mark0150','mark0155','mark0160',
                'quee0297','firs0053','huro2465')
    );
    
    CREATE TABLE _dba_.payment_payment_details AS
    (
        SELECT  *
        FROM    greenwin.payment_payment_details
        WHERE   id IN (SELECT details FROM _dba_.payment_method)
    );
    
    CREATE TABLE _dba_.tenant_sure_transaction AS 
    (
        SELECT  *
        FROM    greenwin.tenant_sure_transaction
        WHERE   insurance IN (SELECT id FROM _dba_.insurance_policy)
    );
    
    CREATE TABLE _dba_.tenant_sure_insurance_policy_report AS
    (
        SELECT  *
        FROM    greenwin.tenant_sure_insurance_policy_report
        WHERE   insurance IN (SELECT id FROM _dba_.insurance_policy)
    );
    
    CREATE TABLE _dba_.tenant_sure_insurance_policy_client AS
    (
        SELECT  t.*,lp.participant_id
        FROM    greenwin.tenant_sure_insurance_policy_client t
        JOIN    greenwin.lease_participant lp ON (t.tenant = lp.id)
        WHERE   t.id IN (SELECT client FROM _dba_.insurance_policy)
    );
    
    ALTER TABLE dms.payment_payment_details ADD COLUMN old_id BIGINT;
    ALTER TABLE dms.payment_method ADD COLUMN old_id BIGINT;
    ALTER TABLE dms.tenant_sure_insurance_policy_client ADD COLUMN old_id BIGINT;
    
    
    INSERT INTO dms.payment_payment_details (id,old_id,id_discriminator,
    received_amount,change_amount,notes,name_on,bank_id,branch_transit_number,
    account_no_number,account_no_obfuscated_number,bank_name,account_type,
    check_no,transit_no,institution_no,account_no,card_type,card_obfuscated_number,
    token,expiry_date,bank_phone,incoming_interac_transaction,bank_no,
    location_code,trace_number )
    (SELECT nextval('public.payment_payment_details_seq') AS id,t.id AS old_id,
            t.id_discriminator,t.received_amount,t.change_amount,t.notes,
            t.name_on,t.bank_id,t.branch_transit_number,t.account_no_number,
            t.account_no_obfuscated_number,t.bank_name,t.account_type,
            t.check_no,t.transit_no,t.institution_no,t.account_no,t.card_type,
            t.card_obfuscated_number,t.token,t.expiry_date,t.bank_phone,
            t.incoming_interac_transaction,t.bank_no,t.location_code,t.trace_number 
    FROM    _dba_.payment_payment_details t);
    
    INSERT INTO dms.payment_method(id,old_id,tenant,details,id_discriminator,payment_type,
    details_discriminator,same_as_current,billing_address_street1,billing_address_street2,
    billing_address_city,billing_address_province,billing_address_country,billing_address_postal_code,
    is_deleted,creation_date,updated,created_by_discriminator,created_by,customer,order_id,
    is_profiled_method,tenant_discriminator,signature)
    (SELECT nextval('public.payment_method_seq') AS id,t.id AS old_id,
            lp.id AS tenant,ppd.id AS details,t.id_discriminator,t.payment_type,
            t.details_discriminator,t.same_as_current,t.billing_address_street1,
            t.billing_address_street2,t.billing_address_city,p.id AS billing_address_province,
            c.id AS billing_address_country,t.billing_address_postal_code,
            t.is_deleted,t.creation_date,t.updated,t.created_by_discriminator,t.created_by,
            t.customer,t.order_id,t.is_profiled_method,t.tenant_discriminator,t.signature
    FROM    _dba_.payment_method t
    JOIN    dms.payment_payment_details ppd ON (ppd.old_id = t.details)
    JOIN    dms.lease_participant lp ON (lp.participant_id = t.participant_id)
    JOIN    dms.province p ON (p.code = t.code)
    JOIN    dms.country c ON (c.name = t.name));
    
    ALTER TABLE dms.insurance_policy ADD COLUMN old_id BIGINT;
    ALTER TABLE dms.insurance_certificate ADD COLUMN old_id BIGINT;
    ALTER TABLE dms.insurance_certificate_scan_blob ADD COLUMN old_id BIGINT;
    
    INSERT INTO dms.tenant_sure_insurance_policy_client (id,old_id,tenant_discriminator,
    tenant,client_reference_number)
    (SELECT nextval('public.tenant_sure_insurance_policy_client_seq') AS id,
            t.id AS old_id,t.tenant_discriminator,lp.id AS tenant,
            t.client_reference_number 
    FROM    _dba_.tenant_sure_insurance_policy_client t
    JOIN    dms.lease_participant lp ON (t.participant_id = lp.participant_id));
    
    INSERT INTO dms.insurance_policy (id,old_id,tenant,id_discriminator,tenant_discriminator,
    is_deleted,client,quote_id,status,cancellation,cancellation_description_reason_from_tenant_sure,
    payment_day,cancellation_date,annual_premium,underwriter_fee,total_annual_tax,
    total_annual_payable,total_first_payable,total_anniversary_first_month_payable,
    total_monthly_payable,contents_coverage,deductible,payment_schedule,
    user_id,signature,broker_fee) 
    (SELECT nextval('public.insurance_policy_seq') AS id,t.id AS old_id,
            lp.id AS tenant,t.id_discriminator,t.tenant_discriminator,
            t.is_deleted,ts.id AS client,t.quote_id,t.status,t.cancellation,t.cancellation_description_reason_from_tenant_sure,
            t.payment_day,t.cancellation_date,t.annual_premium,t.underwriter_fee,t.total_annual_tax,
            t.total_annual_payable,t.total_first_payable,t.total_anniversary_first_month_payable,
            t.total_monthly_payable,t.contents_coverage,t.deductible,t.payment_schedule,
            cu.id AS user_id,t.signature,t.broker_fee
    FROM    _dba_.insurance_policy t
    JOIN    dms.lease_participant lp ON (lp.participant_id = t.participant_id)
    LEFT JOIN   dms.customer_user cu ON (cu.email = t.email)
    LEFT JOIN   dms.tenant_sure_insurance_policy_client ts ON (ts.old_id = t.client));
    
    
    INSERT INTO dms.insurance_certificate (id,old_id,id_discriminator,insurance_policy_discriminator,
    insurance_policy,is_managed_by_tenant,insurance_provider,insurance_certificate_number,
    liability_coverage,inception_date,expiry_date)
    (SELECT nextval('public.insurance_certificate_seq') AS id,t.id AS old_id,
            t.id_discriminator,t.insurance_policy_discriminator,i.id AS insurance_policy,
            t.is_managed_by_tenant,t.insurance_provider,t.insurance_certificate_number,
            t.liability_coverage,t.inception_date,t.expiry_date
    FROM    _dba_.insurance_certificate t 
    JOIN    dms.insurance_policy i ON (i.old_id = t.insurance_policy));
    
    
    INSERT INTO dms.insurance_certificate_scan_blob (id,old_id,content_type,
    data,created,name,updated)
    (SELECT nextval('public.insurance_certificate_scan_blob_seq') AS id,
            t.id AS old_id,t.content_type,t.data,t.created,t.name,t.updated
    FROM    _dba_.insurance_certificate_scan_blob t);
    
    
    INSERT INTO dms.insurance_certificate_scan (id,file_file_name,file_updated_timestamp,
    file_cache_version,file_file_size,file_content_mime_type,file_blob_key,
    description,certificate,certificate_discriminator)
    (SELECT nextval('public.insurance_certificate_scan_seq') AS id,
            t.file_file_name,t.file_updated_timestamp,t.file_cache_version,
            t.file_file_size,t.file_content_mime_type,b.id AS file_blob_key,
            t.description,ic.id AS certificate,t.certificate_discriminator
    FROM    _dba_.insurance_certificate_scan t
    JOIN    dms.insurance_certificate ic ON (ic.old_id = t.certificate)
    JOIN    dms.insurance_certificate_scan_blob b ON (b.old_id = t.file_blob_key)); 
    
    INSERT INTO dms.tenant_sure_transaction(id,insurance_discriminator,
    insurance,amount,payment_due,payment_method_discriminator,payment_method,
    status,transaction_authorization_number,transaction_date)
    (SELECT nextval('public.tenant_sure_transaction_seq') AS id,t.insurance_discriminator,
            i.id AS insurance,t.amount,t.payment_due,t.payment_method_discriminator,
            pm.id AS payment_method,t.status,t.transaction_authorization_number,t.transaction_date
    FROM    _dba_.tenant_sure_transaction t 
    JOIN    dms.insurance_policy i ON (i.old_id = t.insurance)
    JOIN    dms.payment_method pm ON (pm.old_id = t.payment_method));
    
    INSERT INTO dms.tenant_sure_insurance_policy_report(id,insurance_discriminator,
    insurance,reported_status,status_from)
    (SELECT nextval('public.tenant_sure_insurance_policy_report_seq') AS id,
            t.insurance_discriminator,i.id AS insurance,t.reported_status,t.status_from
    FROM    _dba_.tenant_sure_insurance_policy_report t
    JOIN    dms.insurance_policy i ON (i.old_id = t.insurance));
    
    UPDATE  greenwin.insurance_policy
    SET     status = 'Moved'
    WHERE   id IN (SELECT id FROM _dba_.insurance_policy);
    
    UPDATE  _admin_.tenant_sure_subscribers AS a 
    SET     pmc = p.id 
    FROM    _admin_.admin_pmc p 
    WHERE   p.namespace = 'dms'
    AND     a.certificate_number IN 
            (SELECT insurance_certificate_number
            FROM    dms.insurance_certificate
            WHERE   id_discriminator = 'InsuranceTenantSure');
    
    
    SET CONSTRAINTS ALL IMMEDIATE;
    
    ALTER TABLE dms.insurance_policy DROP COLUMN old_id;
    ALTER TABLE dms.insurance_certificate DROP COLUMN old_id;
    ALTER TABLE dms.insurance_certificate_scan_blob DROP COLUMN old_id;
    ALTER TABLE dms.payment_payment_details DROP COLUMN old_id;
    ALTER TABLE dms.payment_method DROP COLUMN old_id;
    ALTER TABLE dms.tenant_sure_insurance_policy_client DROP COLUMN old_id;
    
    DROP TABLE _dba_.insurance_policy;
    DROP TABLE _dba_.insurance_certificate;
    DROP TABLE _dba_.insurance_certificate_scan;
    DROP TABLE _dba_.insurance_certificate_scan_blob;
    DROP TABLE _dba_.payment_method;
    DROP TABLE _dba_.payment_payment_details;
    DROP TABLE _dba_.tenant_sure_transaction;
    DROP TABLE _dba_.tenant_sure_insurance_policy_report;
    DROP TABLE _dba_.tenant_sure_insurance_policy_client;
    
COMMIT;
    
