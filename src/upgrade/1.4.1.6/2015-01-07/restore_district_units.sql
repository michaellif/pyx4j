/**
***     ===========================================================================================================
***
***     Restore districtrealty units, deleted on Dec. 10, 2014
***
***     ===========================================================================================================
**/  

\i tmp_district_units.sql

-- Delete already existing entries from tmp tables

DELETE FROM _dba_.tmp_apt_unit_occupancy_segment
WHERE   id IN (SELECT id FROM  districtrealty.apt_unit_occupancy_segment);

DELETE FROM _dba_.tmp_apt_unit_effective_availability
WHERE   id IN (SELECT id FROM  districtrealty.apt_unit_effective_availability);

DELETE FROM _dba_.tmp_unit_availability_status
WHERE   id IN (SELECT id FROM  districtrealty.unit_availability_status);

DELETE FROM _dba_.tmp_apt_unit
WHERE   id IN (SELECT id FROM  districtrealty.apt_unit);


BEGIN TRANSACTION;

    -- Insert missing records

    INSERT INTO districtrealty.apt_unit(id,building,floorplan,info_economic_status,
    info_economic_status_description,info_floor,info_unit_number,info_legal_address_override,
    info_legal_address_suite_number,info_legal_address_street_number,info_legal_address_street_name,
    info_legal_address_city,info_legal_address_postal_code,info_area,info_area_units,
    info__bedrooms,info__bathrooms,financial__unit_rent,financial__market_rent,
    updated,info_number_s,info_legal_address_country,info_legal_address_province,
    info_legal_address_street_direction,info_legal_address_street_type) 
    (SELECT id,building,floorplan,info_economic_status,
            info_economic_status_description,info_floor,info_unit_number,
            info_legal_address_override,info_legal_address_suite_number,
            info_legal_address_street_number,info_legal_address_street_name,
            info_legal_address_city,info_legal_address_postal_code,info_area,
            info_area_units,info__bedrooms,info__bathrooms,financial__unit_rent,
            financial__market_rent,updated,info_number_s,info_legal_address_country,
            info_legal_address_province,info_legal_address_street_direction,
            info_legal_address_street_type 
    FROM   _dba_.tmp_apt_unit);
 
    INSERT INTO  districtrealty.unit_availability_status(id,unit,building,floorplan,
    complex,status_from,status_until,vacancy_status,rented_status,scoping,rent_readiness_status,
    unit_rent,market_rent,rent_delta_absolute,rent_delta_relative,rent_end_day,
    vacant_since,rented_from_day,move_in_day)
    (SELECT id,unit,building,floorplan,complex,status_from,status_until,vacancy_status,
            rented_status,scoping,rent_readiness_status,unit_rent,market_rent,
            rent_delta_absolute,rent_delta_relative,rent_end_day,vacant_since,
            rented_from_day,move_in_day
    FROM   _dba_.tmp_unit_availability_status);
 

    INSERT INTO districtrealty.apt_unit_effective_availability(id,unit,available_for_rent,updated)
    (SELECT id,unit,available_for_rent,updated
    FROM   _dba_.tmp_apt_unit_effective_availability);
 

    INSERT INTO districtrealty.apt_unit_occupancy_segment(id,unit,date_from,date_to,
    status,off_market,lease,description)
    (SELECT id,unit,date_from,date_to,status,off_market,lease,description
     FROM   _dba_.tmp_apt_unit_occupancy_segment);
     
COMMIT;


DROP TABLE _dba_.tmp_apt_unit_occupancy_segment;
DROP TABLE _dba_.tmp_apt_unit_effective_availability;
DROP TABLE _dba_.tmp_unit_availability_status;
DROP TABLE _dba_.tmp_apt_unit;



