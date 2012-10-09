/**
***	==============================================================================
***	
***		Delete PAN Group lease with the least effort
***
***	==============================================================================
**/

BEGIN TRANSACTION;


-- OK with Artyom 
/*
DELETE FROM pangroup.apt_unit_occupancy_segment WHERE unit = 21;

INSERT INTO pangroup.apt_unit_occupancy_segment (id,unit,date_from,date_to,status) VALUES
(nextval('public.apt_unit_occupancy_segment_seq'),21,'2012-09-25','3000-01-01','pending');

UPDATE 	pangroup.lease_v 
SET	status = 'Cancelled' 
WHERE 	holder = 151;
*/


-- Manual commit

-- Same thing for existing leases - executed on staging and production on Sept 26, 2012

DELETE FROM pangroup.apt_unit_occupancy_segment WHERE unit IN 
(SELECT unit FROM pangroup.lease WHERE activation_date IS NULL);

INSERT INTO pangroup.apt_unit_occupancy_segment (id,unit,date_from,date_to,status) 
(SELECT nextval('public.apt_unit_occupancy_segment_seq'),unit,'2012-09-26','3000-01-01','pending'
 FROM pangroup.lease WHERE activation_date IS NULL);

UPDATE 	pangroup.lease_v 
SET	status = 'Cancelled' 
WHERE 	holder IN (SELECT id FROM pangroup.lease WHERE activation_date IS NULL);



