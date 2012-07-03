-- Database changes for starlight production (prod01)

-- Table Building - duplicate values on propertyCode column
UPDATE Building SET propertyCode = CONCAT('n/a',id) WHERE UPPER(propertyCode) = 'NO CODE GIVEN';
UPDATE Building SET propertyCode = 'pros2069Z' WHERE id = 225;
UPDATE Building SET propertyCode = 'dale019rZ' WHERE id = 248;
UPDATE Building SET propertyCode = 'esse0066Z' WHERE id = 301;

-- Alter table Building 
ALTER TABLE Building MODIFY propertyCode VARCHAR(10);
CREATE UNIQUE INDEX building_property_code_idx ON Building (ns,propertyCode);

-- Table BuildingAmenity

UPDATE BuildingAmenity SET buildingAmenityType = 'fitnessCentre' WHERE buildingAmenityType = 'fitnessCenter';



 
