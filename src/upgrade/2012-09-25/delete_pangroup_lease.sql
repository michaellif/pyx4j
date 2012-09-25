/**
***	==============================================================================
***	
***		Delete PAN Group lease with the least effort
***
***	==============================================================================
**/

BEGIN TRANSACTION;


-- OK with Artyom 
DELETE FROM pangroup.apt_unit_occupancy_segment WHERE unit = 21;

INSERT INTO pangroup.apt_unit_occupancy_segment (id,unit,date_from,date_to,status) VALUES
(nextval('public.apt_unit_occupancy_segment_seq'),21,'2012-09-25','3000-01-01','pending');

UPDATE 	pangroup.lease_v 
SET	status = 'Cancelled' 
WHERE 	holder = 151;


-- Manual commit

