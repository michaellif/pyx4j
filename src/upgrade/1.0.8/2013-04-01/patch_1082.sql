/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***            Patch 1.0.8.2 schema changes
***
***     =====================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.patch_1082(v_schema_name TEXT) RETURNS VOID AS
$$
DECLARE 
        v_row_count             INT := 0;
BEGIN
        
        EXECUTE 'SELECT COUNT(id) '
                ||'FROM '||v_schema_name||'.lease_billing_type_policy_item '
                INTO v_row_count;
                
        IF ( v_row_count = 0 )
        THEN
                EXECUTE 'INSERT INTO '||v_schema_name||'.lease_billing_type_policy_item (id,lease_billing_policy,order_in_parent,billing_period,billing_cycle_start_day,'
                ||'bill_execution_day_offset,payment_due_day_offset,final_due_day_offset,pad_calculation_day_offset,pad_execution_day_offset) '
                ||'(SELECT nextval(''public.lease_billing_type_policy_item_seq'') AS id, l.id AS lease_billing_policy,0 AS order_in_parent,''Monthly'', '
                ||'1 AS billing_cycle_start_day,-15 AS bill_execution_day_offset,'
                ||'0 AS payment_due_day_offset,15 AS final_due_day_offset, -3 AS pad_calculation_day_offset,'
                ||'0 AS pad_execution_day_offset '
                ||'FROM         '||v_schema_name||'.lease_billing_policy l ) ';
                
        END IF; 
        
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.0.8.2'
        WHERE   namespace = v_schema_name;
        
END;
$$
LANGUAGE plpgsql VOLATILE;

SET client_min_messages = 'error';

BEGIN TRANSACTION;

        SELECT  namespace,_dba_.patch_1082(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version = '1.0.8.1'
        AND     status != 'Created';
        
COMMIT;

DROP FUNCTION _dba_.patch_1082(text);

BEGIN TRANSACTION;
        DELETE FROM waterfront.lease_billing_type_policy_item WHERE id = 26;
COMMIT;

