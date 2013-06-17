/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Delete all yardi-enabled maintenance requests
***
***     ======================================================================================================================
**/


CREATE OR REPLACE FUNCTION _dba_.remove_yardi_maintenance_request() RETURNS VOID AS
$$
DECLARE
        v_schema_name   VARCHAR(64);
BEGIN
        FOR v_schema_name IN 
        SELECT  a.namespace
        FROM    _admin_.admin_pmc a
        JOIN    _admin_.admin_pmc_vista_features f
        ON      (a.features = f.id AND f.yardi_integration)
        LOOP
                
                EXECUTE 'DELETE FROM '||v_schema_name||'.maintenance_request';
                EXECUTE 'DELETE FROM '||v_schema_name||'.maintenance_request_category';
                EXECUTE 'DELETE FROM '||v_schema_name||'.maintenance_request_priority';
                EXECUTE 'DELETE FROM '||v_schema_name||'.maintenance_request_status';
                
                
        END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;
        SELECT * FROM _dba_.remove_yardi_maintenance_request();
COMMIT;

DROP FUNCTION _dba_.remove_yardi_maintenance_request();
