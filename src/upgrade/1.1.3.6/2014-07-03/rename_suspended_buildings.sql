/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             rename suspended buildings for all yardi-enabled pmc
***
***     =====================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.rename_suspended_buildings() RETURNS VOID AS
$$
DECLARE
    
    v_schema_name           VARCHAR(64);

BEGIN

    FOR v_schema_name IN
    SELECT  a.namespace 
    FROM    _admin_.admin_pmc a
    JOIN    _admin_.admin_pmc_vista_features f ON (f.id = a.features)
    WHERE   f.yardi_integration
    AND     a.status != 'Suspended'
    LOOP
        
        EXECUTE 'UPDATE '||v_schema_name||'.building '
                ||'SET  property_code = ''!''||property_code '
                ||'WHERE    suspended '
                ||'AND  property_code ~ ''^!'' ';
    
    END LOOP;
        

END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

    SELECT * FROM _dba_.rename_suspended_buildings();
    
COMMIT;

DROP FUNCTION rename_suspended_buildings();
