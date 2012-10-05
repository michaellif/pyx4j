/**
***	=====================================================================
***	
***		Closed expired leases for Waterfront
***	
***	=====================================================================
**/

BEGIN TRANSACTION;

-- Close lease -  already ended

DELETE FROM waterfront.apt_unit_occupancy_segment WHERE unit IN 
(SELECT unit FROM waterfront.lease WHERE lease_to < '2012-10-01' );

INSERT INTO waterfront.apt_unit_occupancy_segment (id,unit,date_from,date_to,status) 
(SELECT nextval('public.apt_unit_occupancy_segment_seq'),unit,'2012-10-01','3000-01-01','pending'
 FROM waterfront.lease WHERE lease_to < '2012-10-01' );

UPDATE 	waterfront.lease_v 
SET	status = 'Closed' 
WHERE 	holder IN (SELECT id FROM waterfront.lease WHERE lease_to < '2012-10-01');
