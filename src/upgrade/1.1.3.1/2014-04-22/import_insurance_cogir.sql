/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Greenwin to Cogir insurance move
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

    DROP TABLE IF EXISTS _dba_.insurance_policy;
    DROP TABLE IF EXISTS _dba_.insurance_certificate;
    DROP TABLE IF EXISTS _dba_.insurance_certificate_scan;
    DROP TABLE IF EXISTS _dba_.insurance_certificate_scan_blob;

    CREATE TABLE _dba_.insurance_policy AS 
    (
        SELECT  i.*,lp.participant_id,cu.email
        FROM    greenwin.insurance_policy i
        JOIN    greenwin.lease_participant lp ON (lp.id = i.tenant)
        JOIN    greenwin.lease l ON (l.id = lp.lease)
        JOIN    greenwin.apt_unit a ON (a.id = l.unit)
        JOIN    greenwin.building b ON (b.id = a.building)
        LEFT JOIN   greenwin.customer_user cu ON (cu.id = i.user_id) 
        WHERE   b.property_code = 'rich0033'
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
    
    ALTER TABLE cogir.insurance_policy ADD COLUMN old_id BIGINT;
    ALTER TABLE cogir.insurance_certificate ADD COLUMN old_id BIGINT;
    ALTER TABLE cogir.insurance_certificate_scan_blob ADD COLUMN old_id BIGINT;
    
    
    INSERT INTO cogir.insurance_policy (id,old_id,tenant,id_discriminator,tenant_discriminator,
    is_deleted,client,quote_id,status,cancellation,cancellation_description_reason_from_tenant_sure,
    payment_day,cancellation_date,annual_premium,underwriter_fee,total_annual_tax,
    total_annual_payable,total_first_payable,total_anniversary_first_month_payable,
    total_monthly_payable,contents_coverage,deductible,payment_schedule,
    user_id,signature,broker_fee) 
    (SELECT nextval('public.insurance_policy_seq') AS id,t.id AS old_id,
            lp.id AS tenant,t.id_discriminator,t.tenant_discriminator,
            t.is_deleted,t.client,t.quote_id,t.status,t.cancellation,t.cancellation_description_reason_from_tenant_sure,
            t.payment_day,t.cancellation_date,t.annual_premium,t.underwriter_fee,t.total_annual_tax,
            t.total_annual_payable,t.total_first_payable,t.total_anniversary_first_month_payable,
            t.total_monthly_payable,t.contents_coverage,t.deductible,t.payment_schedule,
            cu.id AS user_id,t.signature,t.broker_fee
    FROM    _dba_.insurance_policy t
    JOIN    cogir.lease_participant lp ON (lp.participant_id = t.participant_id)
    LEFT JOIN   cogir.customer_user cu ON (cu.email = t.email));
    
    
    INSERT INTO cogir.insurance_certificate (id,old_id,id_discriminator,insurance_policy_discriminator,
    insurance_policy,is_managed_by_tenant,insurance_provider,insurance_certificate_number,
    liability_coverage,inception_date,expiry_date)
    (SELECT nextval('public.insurance_certificate_seq') AS id,t.id AS old_id,
            t.id_discriminator,t.insurance_policy_discriminator,i.id AS insurance_policy,
            t.is_managed_by_tenant,t.insurance_provider,t.insurance_certificate_number,
            t.liability_coverage,t.inception_date,t.expiry_date
    FROM    _dba_.insurance_certificate t 
    JOIN    cogir.insurance_policy i ON (i.old_id = t.insurance_policy));
    
    
    INSERT INTO cogir.insurance_certificate_scan_blob (id,old_id,content_type,
    data,created,name,updated)
    (SELECT nextval('public.insurance_certificate_scan_blob_seq') AS id,
            t.id AS old_id,t.content_type,t.data,t.created,t.name,t.updated
    FROM    _dba_.insurance_certificate_scan_blob t);
    
    
    INSERT INTO cogir.insurance_certificate_scan (id,file_file_name,file_updated_timestamp,
    file_cache_version,file_file_size,file_content_mime_type,file_blob_key,
    description,certificate,certificate_discriminator)
    (SELECT nextval('public.insurance_certificate_scan_seq') AS id,
            t.file_file_name,t.file_updated_timestamp,t.file_cache_version,
            t.file_file_size,t.file_content_mime_type,b.id AS file_blob_key,
            t.description,ic.id AS certificate,t.certificate_discriminator
    FROM    _dba_.insurance_certificate_scan t
    JOIN    cogir.insurance_certificate ic ON (ic.old_id = t.certificate)
    JOIN    cogir.insurance_certificate_scan_blob b ON (b.old_id = t.file_blob_key)); 
    
    UPDATE  greenwin.insurance_policy
    SET     status = 'Moved'
    WHERE   id IN (SELECT id FROM _dba_.insurance_policy);
    
    SET CONSTRAINTS ALL IMMEDIATE;
    
    ALTER TABLE cogir.insurance_policy DROP COLUMN old_id;
    ALTER TABLE cogir.insurance_certificate DROP COLUMN old_id;
    ALTER TABLE cogir.insurance_certificate_scan_blob DROP COLUMN old_id;
    
    DROP TABLE _dba_.insurance_policy;
    DROP TABLE _dba_.insurance_certificate;
    DROP TABLE _dba_.insurance_certificate_scan;
    DROP TABLE _dba_.insurance_certificate_scan_blob;
    
COMMIT;
    
