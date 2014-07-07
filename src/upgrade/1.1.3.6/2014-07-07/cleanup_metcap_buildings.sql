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
        
         -- Rename building to ZZZ||property_code
    
        UPDATE  metcap.building 
        SET property_code = 'ZZ'||property_code
        WHERE   id = v_building_id;
        
        -- Remove ! from property_code and set suspended false
        UPDATE  metcap.building
        SET suspended = FALSE,
            property_code = substring(property_code,2)
        WHERE   property_code = '!'||v_property_code;
        
        -- delete lease_term_participants
        
        DELETE FROM metcap.lease_term_participant 
        WHERE   id IN ( SELECT  ltp.id
                        FROM    metcap.building b
                        JOIN    metcap.apt_unit a ON (b.id = a.building)
                        JOIN    metcap.lease l ON (a.id = l.unit)
                        JOIN    metcap.lease_participant lp ON (l.id = lp.lease)
                        JOIN    metcap.lease_term_participant ltp ON (lp.id = ltp.lease_participant)
                        WHERE   b.id = v_building_id);
        
        -- Update lease to set applicant and current_term null (circular dependency on lease_participant and lease_term)
        
        UPDATE  metcap.lease AS l
        SET     _applicant = NULL,
                current_term = NULL
        FROM    metcap.building b,
                metcap.apt_unit a
        WHERE   a.building = b.id
        AND     l.unit = a.id
        AND     b.id = v_building_id;
        
        
        -- delete lease_participants
        
        DELETE FROM metcap.lease_participant 
        WHERE   id IN ( SELECT  lp.id
                        FROM    metcap.building b
                        JOIN    metcap.apt_unit a ON (b.id = a.building)
                        JOIN    metcap.lease l ON (a.id = l.unit)
                        JOIN    metcap.lease_participant lp ON (l.id = lp.lease)
                        WHERE   b.id = v_building_id);
        
        -- lease_term_vlease_products$feature_items
        
        DELETE FROM metcap.lease_term_vlease_products$feature_items
        WHERE id IN (   SELECT  ltvf.id
                        FROM    metcap.building b
                        JOIN    metcap.apt_unit a ON (b.id = a.building)
                        JOIN    metcap.lease l ON (a.id = l.unit)
                        JOIN    metcap.lease_term lt ON (a.id = lt.unit)
                        JOIN    metcap.lease_term_v ltv ON (lt.id = ltv.holder)
                        JOIN    metcap.lease_term_vlease_products$feature_items ltvf ON (ltv.id = ltvf.owner)
                        WHERE   b.id = v_building_id);
        
        -- lease_term_v
        
        DELETE FROM metcap.lease_term_v
        WHERE id IN (   SELECT  ltv.id
                        FROM    metcap.building b
                        JOIN    metcap.apt_unit a ON (b.id = a.building)
                        JOIN    metcap.lease l ON (a.id = l.unit)
                        JOIN    metcap.lease_term lt ON (a.id = lt.unit)
                        JOIN    metcap.lease_term_v ltv ON (lt.id = ltv.holder)
                        WHERE   b.id = v_building_id);
        
        -- lease_term$agreement_confirmation_terms
        
        DELETE FROM metcap.lease_term$agreement_confirmation_terms
        WHERE id IN (   SELECT  ltac.id
                        FROM    metcap.building b
                        JOIN    metcap.apt_unit a ON (b.id = a.building)
                        JOIN    metcap.lease l ON (a.id = l.unit)
                        JOIN    metcap.lease_term lt ON (a.id = lt.unit)
                        JOIN    metcap.lease_term$agreement_confirmation_terms ltac ON (lt.id = ltac.owner)
                        WHERE   b.id = v_building_id);
                        
        -- lease_term$agreement_legal_terms
        
        DELETE FROM metcap.lease_term$agreement_legal_terms
        WHERE id IN (   SELECT  ltal.id
                        FROM    metcap.building b
                        JOIN    metcap.apt_unit a ON (b.id = a.building)
                        JOIN    metcap.lease l ON (a.id = l.unit)
                        JOIN    metcap.lease_term lt ON (a.id = lt.unit)
                        JOIN    metcap.lease_term$agreement_legal_terms ltal ON (lt.id = ltal.owner)
                        WHERE   b.id = v_building_id);
        
        -- lease_term
        
        DELETE FROM metcap.lease_term
        WHERE id IN (   SELECT  lt.id
                        FROM    metcap.building b
                        JOIN    metcap.apt_unit a ON (b.id = a.building)
                        JOIN    metcap.lease l ON (a.id = l.unit)
                        JOIN    metcap.lease_term lt ON (a.id = lt.unit)
                        WHERE   b.id = v_building_id);
                        
        -- lease
        
        DELETE FROM metcap.lease
        WHERE id IN (   SELECT  l.id
                        FROM    metcap.building b
                        JOIN    metcap.apt_unit a ON (b.id = a.building)
                        JOIN    metcap.lease l ON (a.id = l.unit)
                        WHERE   b.id = v_building_id);
        
        -- unit_availability_status
        
        DELETE FROM metcap.unit_availability_status
        WHERE unit IN (   SELECT  a.id
                        FROM    metcap.building b
                        JOIN    metcap.apt_unit a ON (b.id = a.building)
                        WHERE   b.id = v_building_id);
                        
                        
        -- apt_unit_effective_availability
        
        DELETE FROM metcap.apt_unit_effective_availability
        WHERE unit IN (   SELECT  a.id
                        FROM    metcap.building b
                        JOIN    metcap.apt_unit a ON (b.id = a.building)
                        WHERE   b.id = v_building_id);
        
        -- apt_unit
        
        DELETE FROM metcap.apt_unit
        WHERE building = v_building_id;
        
        -- Delete floorplans
        
        DELETE FROM metcap.floorplan
        WHERE building = v_building_id;
        
        -- product_v$features
        
        DELETE  FROM metcap.product_v$features
        WHERE  id IN (  SELECT  pvf.id 
                        FROM    metcap.building b
                        JOIN    metcap.product_catalog pc ON (pc.building = b.id)
                        JOIN    metcap.product p ON (pc.id = p.catalog)
                        JOIN    metcap.product_v$features pvf ON (p.id = pvf.value)
                        WHERE   building = v_building_id);
        
        -- product_item
        
        DELETE  FROM metcap.product_item
        WHERE  id IN (  SELECT  pi.id 
                        FROM    metcap.building b
                        JOIN    metcap.product_catalog pc ON (pc.building = b.id)
                        JOIN    metcap.product p ON (pc.id = p.catalog)
                        JOIN    metcap.product_v pv ON (p.id = pv.holder)
                        JOIN    metcap.product_item pi ON (pv.id = pi.product)
                        WHERE   building = v_building_id);
        
        -- product_v
        
        DELETE  FROM metcap.product_v
        WHERE  id IN (  SELECT  pv.id 
                        FROM    metcap.building b
                        JOIN    metcap.product_catalog pc ON (pc.building = b.id)
                        JOIN    metcap.product p ON (pc.id = p.catalog)
                        JOIN    metcap.product_v pv ON (p.id = pv.holder)
                        WHERE   building = v_building_id);
        
        -- delete product
        
        DELETE  FROM metcap.product
        WHERE  id IN (  SELECT p.id 
                        FROM    metcap.building b
                        JOIN    metcap.product_catalog pc ON (pc.building = b.id)
                        JOIN    metcap.product p ON (pc.id = p.catalog)
                        WHERE   building = v_building_id);
        
        -- delete product_catalog
        
        DELETE  FROM metcap.product_catalog
        WHERE   building = v_building_id;
        
        -- billing_invoice_line_item
        
        DELETE FROM metcap.billing_invoice_line_item
        WHERE   billing_cycle IN (  SELECT id FROM metcap.billing_billing_cycle 
                                    WHERE building = v_building_id);
        
        -- billing_billing_cycle
        
        DELETE FROM metcap.billing_billing_cycle
        WHERE   building = v_building_id;
        
        
        -- aging_buckets
        
        DELETE FROM metcap.aging_buckets
        WHERE arrears_snapshot IN ( SELECT id FROM metcap.billing_arrears_snapshot
                                    WHERE   building = v_building_id);
        
        -- billing_arrears_snapshot
        
        DELETE FROM metcap.billing_arrears_snapshot
        WHERE   building = v_building_id;
        
        -- delete building
        DELETE FROM metcap.building 
        WHERE   id = v_building_id;
        
        
        
    END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

    SELECT * FROM _dba_.cleanup_metcap_buildings();
    
COMMIT;

DROP FUNCTION _dba_.cleanup_metcap_buildings();

    
    
    
    
