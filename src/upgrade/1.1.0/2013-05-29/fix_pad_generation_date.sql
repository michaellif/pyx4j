/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Fix for billing_billing_cycle, update target_pad_generation_date
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.fix_pad_generation_date(v_schema_name TEXT) RETURNS VOID AS
$$
BEGIN
        
        EXECUTE 'UPDATE '||v_schema_name||'.billing_billing_cycle '
                ||'SET          target_pad_generation_date = ''31-MAY-2013'' '
                ||'WHERE        target_pad_generation_date = ''29-MAY-2013'' ';

END;
$$
LANGUAGE plpgsql VOLATILE;


BEGIN TRANSACTION;

        SELECT  namespace,_dba_.fix_pad_generation_date(namespace) 
        FROM    _admin_.admin_pmc a
        JOIN    _admin_.admin_pmc_vista_features f  ON (a.features = f.id)
        WHERE   f.yardi_integration;

COMMIT;

DROP FUNCTION _dba_.fix_pad_generation_date(TEXT);
        
      
