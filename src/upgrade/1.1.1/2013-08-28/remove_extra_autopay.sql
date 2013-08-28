/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             remove extra auto_pay_policy record
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.remove_extra_autopay(v_schema_name TEXT) RETURNS VOID AS
$$
BEGIN
        EXECUTE 'DELETE FROM '||v_schema_name||'.auto_pay_policy '
                ||'WHERE id !=  (SELECT MIN(id) FROM '||v_schema_name||'.auto_pay_policy) ';
                
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

        SELECT  pmc,_dba_.remove_extra_autopay(pmc)
        FROM    _dba_.count_rows_all_pmc('auto_pay_policy') 
        WHERE   row_count = 2;
        
COMMIT;

DROP FUNCTION _dba_.remove_extra_autopay(text);

