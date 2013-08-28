/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             enable visa debit for all pmc
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.enable_vista_debit(v_schema_name TEXT) RETURNS VOID AS
$$
BEGIN
        EXECUTE 'UPDATE '||v_schema_name||'.payment_type_selection_policy '
                ||'SET  accepted_visa_debit = TRUE, '
                ||'     cash_equivalent_visa_debit = TRUE,'
                ||'     resident_portal_visa_debit = TRUE ';
       
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

        SELECT  namespace,_dba_.enable_vista_debit(namespace)
        FROM    _admin_.admin_pmc
        WHERE   status != 'Created'
        ORDER BY id;
        
COMMIT;

DROP FUNCTION _dba_.enable_vista_debit(text);

