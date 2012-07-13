-- Modify startlight data so it would fit into new structure

USE vista_star;
-- On prod01 it is 'vista_starB'
-- USE vista_starB;
BEGIN;

-- Table BuildingAmenity: buildingAmenityType 'recRoom' is now 'recreationalRoom'

UPDATE BuildingAmenity SET buildingAmenityType = 'recreationalRoom' WHERE buildingAmenityType = 'recRoom';


COMMIT;

