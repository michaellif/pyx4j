/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Delete districtrealty units
***
***     ===========================================================================================================
**/                                                     


BEGIN TRANSACTION;

    DELETE  FROM districtrealty.apt_unit_occupancy_segment 
    WHERE   unit IN (SELECT id
                    FROM    districtrealty.apt_unit 
                    WHERE   info_floor IS NULL );
                    
    DELETE  FROM districtrealty.apt_unit_effective_availability
    WHERE   unit IN (SELECT id
                    FROM    districtrealty.apt_unit 
                    WHERE   info_floor IS NULL );
    
    DELETE  FROM districtrealty.unit_availability_status
    WHERE   unit IN (SELECT id
                    FROM    districtrealty.apt_unit 
                    WHERE   info_floor IS NULL );
    
    DELETE  FROM districtrealty.apt_unit  
    WHERE   info_floor IS NULL ;
    
COMMIT;
