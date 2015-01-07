/**
***     ===========================================================================================================
***
***     Create tables necessary to restore districtrealty units, deleted on Dec. 10, 2014
***
***     ===========================================================================================================
**/                                                     

CREATE TABLE _dba_.tmp_apt_unit_occupancy_segment AS
(   SELECT  *   
    FROM    districtrealty.apt_unit_occupancy_segment
    WHERE   unit IN (   SELECT id
                        FROM    districtrealty.apt_unit 
                        WHERE   building = 1747 ));
                        
CREATE TABLE _dba_.tmp_apt_unit_effective_availability AS 
(   SELECT  *
    FROM    districtrealty.apt_unit_effective_availability
    WHERE   unit IN (   SELECT id
                        FROM    districtrealty.apt_unit 
                        WHERE   building = 1747 ));
                        
CREATE TABLE _dba_.tmp_unit_availability_status AS
(   SELECT  *
    FROM    districtrealty.unit_availability_status
    WHERE   unit IN (   SELECT id
                        FROM    districtrealty.apt_unit 
                        WHERE   building = 1747 ));
                        
CREATE TABLE _dba_.tmp_apt_unit AS 
(   SELECT  *
    FROM    districtrealty.apt_unit 
    WHERE   building = 1747);
    
/*    
pg_dump -U psql_dba -h localhost -O -t _dba_.tmp_apt_unit_occupancy_segment \
-t _dba_.tmp_apt_unit_effective_availability -t _dba_.tmp_unit_availability_status \
-t _dba_.tmp_apt_unit vista77 > tmp_district_units.sql 
*/

