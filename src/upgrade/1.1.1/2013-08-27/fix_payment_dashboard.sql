/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             fix for payment dashboard
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.fix_payment_dashboard(v_schema_name TEXT) RETURNS VOID AS
$$
BEGIN

        EXECUTE 'UPDATE '||v_schema_name||'.gadget_metadata_holder '
                ||'SET  serialized_form = regexp_replace(serialized_form, ''EFT'', ''DirectBanking'',''g'' ) ' 
                ||'WHERE class_name = ''PaymentRecordsGadgetMetadata'' ';
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

        SELECT  namespace,_dba_.fix_payment_dashboard(namespace) 
        FROM    _admin_.admin_pmc 
        WHERE   status != 'Created'
        ORDER BY id;
COMMIT;

DROP FUNCTION _dba_.fix_payment_dashboard(text);

