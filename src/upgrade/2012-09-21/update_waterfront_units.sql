/**
***	===========================================================================================
***
***		Update for Waterfront apt_unit table, area and units to be taken from floorplan
***
***	===========================================================================================
**/

BEGIN TRANSACTION;

UPDATE 	waterfront.apt_unit a
SET	info_area = b.area,
	info_area_units = b.area_units
FROM 	(SELECT a.id,b.area,b.area_units
	 FROM 	waterfront.apt_unit a
	 JOIN 	waterfront.floorplan b
	 ON 	(a.floorplan = b.id)) b
WHERE	a.id = b.id;

-- Manual commit
