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
DECLARE 
        v_row_count     INT := 0;
BEGIN
        -- payment_record
        
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_record ADD COLUMN yardi_document_number VARCHAR(500)';
        
        EXECUTE 'UPDATE '||v_schema_name||'.payment_record '
                ||'SET yardi_document_number = ''eCheque (EFT):''||id ';
                
        -- resident_portal_settings - only when table is empty
        
        EXECUTE 'SELECT COUNT(id) '
                ||'FROM         '||v_schema_name||'.resident_portal_settings '
                INTO    v_row_count;
                
        IF ( v_row_count = 0 ) 
        THEN
                EXECUTE 'INSERT INTO '||v_schema_name||'.resident_portal_settings (id,enabled,use_custom_html) '
                        ||' VALUES (nextval(''public.resident_portal_settings_seq''),TRUE,FALSE)';
                        
                EXECUTE 'UPDATE '||v_schema_name||'.site_descriptor AS a '
                        ||'SET  resident_portal_settings = b.id '
                        ||'FROM '||v_schema_name||'.resident_portal_settings AS b ';
        END IF;
        
        -- Update schema version
        
        UPDATE  _admin_.admin_pmc 
        SET     schema_version = '1.0.8.1'
        WHERE   namespace = v_schema_name;
                      
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

-- Update for sterling payment_records

BEGIN TRANSACTION;
        
        UPDATE  sterling.payment_record 
        SET     target_date = '2013-04-03'
        WHERE   target_date = '2013-04-01'
        AND     payment_status = 'Scheduled';

COMMIT;

