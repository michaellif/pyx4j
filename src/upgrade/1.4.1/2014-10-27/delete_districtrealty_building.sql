/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Delete districtrealty building
***
***     ===========================================================================================================
**/                                                     

BEGIN TRANSACTION;

    DELETE FROM districtrealty.apt_unit_effective_availability
    WHERE   unit IN (SELECT  a.id 
                    FROM    districtrealty.apt_unit a
                    JOIN    districtrealty.building b ON (b.id = a.building) 
                    WHERE   b.property_code = '1');
                    
    DELETE FROM districtrealty.apt_unit_occupancy_segment
    WHERE   unit IN (SELECT  a.id 
                    FROM    districtrealty.apt_unit a
                    JOIN    districtrealty.building b ON (b.id = a.building) 
                    WHERE   b.property_code = '1');
                    
    DELETE FROM districtrealty.unit_availability_status
    WHERE   unit IN (SELECT  a.id 
                    FROM    districtrealty.apt_unit a
                    JOIN    districtrealty.building b ON (b.id = a.building) 
                    WHERE   b.property_code = '1');

    DELETE FROM districtrealty.apt_unit 
    WHERE   building = 
            (SELECT     id
            FROM    districtrealty.building
            WHERE   property_code = '1');
            
    DELETE FROM districtrealty.floorplan 
    WHERE   building = 
            (SELECT     id
            FROM    districtrealty.building
            WHERE   property_code = '1');
    
    DELETE FROM districtrealty.aging_buckets
    WHERE   arrears_snapshot IN (SELECT  a.id 
                    FROM    districtrealty.billing_arrears_snapshot a
                    JOIN    districtrealty.building b ON (b.id = a.building) 
                    WHERE   b.property_code = '1');
    
    DELETE FROM districtrealty.billing_arrears_snapshot
    WHERE   building = 
            (SELECT     id
            FROM    districtrealty.building
            WHERE   property_code = '1');
            
    DELETE FROM districtrealty.ilsprofile_email
    WHERE   building = 
            (SELECT     id
            FROM    districtrealty.building
            WHERE   property_code = '1');
    
    DELETE FROM districtrealty.product_v$features
    WHERE   value IN (SELECT  a.id 
                    FROM    districtrealty.product a
                    JOIN    districtrealty.product_catalog c ON (c.id = a.catalog)
                    JOIN    districtrealty.building b ON (b.id = c.building) 
                    WHERE   b.property_code = '1');
    
    DELETE FROM districtrealty.product_item
    WHERE   product IN (SELECT  a.id 
                    FROM    districtrealty.product_v a
                    JOIN    districtrealty.product d ON (d.id = a.holder)
                    JOIN    districtrealty.product_catalog c ON (c.id = d.catalog)
                    JOIN    districtrealty.building b ON (b.id = c.building) 
                    WHERE   b.property_code = '1');
    
    DELETE FROM districtrealty.product_v
    WHERE   holder IN (SELECT  a.id 
                    FROM    districtrealty.product a
                    JOIN    districtrealty.product_catalog c ON (c.id = a.catalog)
                    JOIN    districtrealty.building b ON (b.id = c.building) 
                    WHERE   b.property_code = '1');
    
    DELETE FROM districtrealty.product 
    WHERE   catalog IN (SELECT  a.id 
                    FROM    districtrealty.product_catalog a
                    JOIN    districtrealty.building b ON (b.id = a.building) 
                    WHERE   b.property_code = '1');
            
    DELETE FROM districtrealty.product_catalog
    WHERE   building = 
            (SELECT     id
            FROM    districtrealty.building
            WHERE   property_code = '1');
    
    DELETE  FROM    districtrealty.building
    WHERE   property_code = '1';
    
COMMIT;
            
    
