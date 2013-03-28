/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***            Patch 1.0.8.1 schema changes
***
***     =====================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.patch_1081(v_schema_name TEXT) RETURNS VOID AS
$$
BEGIN
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_record ADD COLUMN yardi_document_number VARCHAR(500)';
        
        EXECUTE 'UPDATE '||v_schema_name||'.payment_record '
                ||'SET yardi_document_number = ''eCheque (EFT):''||id ';
        
END;
$$
LANGUAGE plpgsql VOLATILE;

SET client_min_messages = 'error';

BEGIN TRANSACTION;

        SELECT  namespace,_dba_.patch_1081(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version = '1.0.8'
        AND     status != 'Created';
        
COMMIT;

DROP FUNCTION _dba_.patch_1081(text);

