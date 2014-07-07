/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             rename newly created buildings in  Metcap
***
***     =====================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.cleanup_metcap_buildings() RETURNS VOID AS
$$
DECLARE 
    
    v_building_id             BIGINT;
    v_property_code           TEXT;
    
BEGIN

    
    FOR v_building_id, v_property_code IN 
    SELECT  id, property_code 
    FROM    metcap.building AS b
    WHERE EXISTS    (SELECT 	property_code
                    FROM    metcap.building
                    WHERE   property_code = '!'||b.property_code
                    AND     suspended)
    AND DATE_TRUNC('day',created) = '03-JUL-2014'
    LOOP
        
        // Remove ! from property_code and set suspended false
        UPDATE  metcap.building
        SET suspended = FALSE,
            property_code = substring(property_code,2)
        WHERE   property_code = '!'||v_property_code;
        
        // Rename building to ZZZ||property_code
    
        UPDATE  metcap.building 
        SET property_code = 'ZZZ'||property_code
        WHERE   id = v_building_id;
        
    END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

    SELECT * FROM _dba_.cleanup_metcap_buildings();
    
COMMIT;

DROP FUNCTION _dba_.cleanup_metcap_buildings();

    
    
    
    
