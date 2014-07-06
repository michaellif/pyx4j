/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             revert renaming of suspended buildings for all yardi-enabled pmc but Metcap
***
***     =====================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.revert_rename_suspended_buildings() RETURNS VOID AS
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
    AND     a.namespace != 'metcap'
    LOOP
        
        EXECUTE 'UPDATE '||v_schema_name||'.building AS b '
                ||'SET  property_code = substring(property_code,2) '
                ||'WHERE    suspended '
                ||'AND  property_code ~ ''^!'' '
                ||'AND NOT EXISTS  (    SELECT  property_code '
                ||'                     FROM    '||v_schema_name||'.building '
                ||'                     WHERE   property_code = substring(b.property_code,2))';
    
    END LOOP;
        

END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

    SELECT * FROM _dba_.revert_rename_suspended_buildings();
    
COMMIT;

DROP FUNCTION _dba_.revert_rename_suspended_buildings();
