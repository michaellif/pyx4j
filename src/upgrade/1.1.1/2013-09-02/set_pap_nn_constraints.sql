/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Set NOT NULL constraints on preauthorized_payment.payment_method and payment_method_discriminator
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.set_pap_nn(v_schema_name TEXT) RETURNS VOID AS
$$
BEGIN
        EXECUTE 'SET search_path = '||v_schema_name;
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.preauthorized_payment_covered_item '
                ||'WHERE        pap IN (SELECT id FROM '||v_schema_name||'.preauthorized_payment '
                ||'                     WHERE        is_deleted '
                ||'                     AND          payment_method IS NULL)';
        
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.preauthorized_payment '
                ||'WHERE        is_deleted '
                ||'AND          payment_method IS NULL';
                
        
        SET CONSTRAINTS         preauthorized_payment_payment_method_fk, preauthorized_payment_tenant_fk, 
                                payment_record_preauthorized_payment_fk,preauthorized_payment_covered_item_pap_fk IMMEDIATE;
                                
                                
        ALTER TABLE preauthorized_payment ALTER COLUMN payment_method SET NOT NULL;
        ALTER TABLE preauthorized_payment ALTER COLUMN payment_method_discriminator SET NOT NULL;
        
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.set_pap_nn(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        ORDER BY a.id;
COMMIT;

DROP FUNCTION _dba_.set_pap_nn(text);
