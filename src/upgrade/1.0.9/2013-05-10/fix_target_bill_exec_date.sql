/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             fix null target_bill_execution_date
***
***     =====================================================================================================================
**/

CREATE FUNCTION _dba_.fix_target_bill_exec_date(v_schema_name TEXT) RETURNS VOID
AS
$$
BEGIN
        EXECUTE 'UPDATE '||v_schema_name||'.billing_billing_cycle '
                ||'SET  target_bill_execution_date = (target_pad_generation_date - 12) '
                ||'WHERE target_bill_execution_date IS NULL ';
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

        SELECT  namespace, _dba_.fix_target_bill_exec_date(namespace)
        FROM    _admin_.admin_pmc
        WHERE   status != 'Created'
        ORDER BY 1;
COMMIT;

DROP FUNCTION _dba_.fix_target_bill_exec_date(TEXT);   
