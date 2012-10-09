/**
***	===========================================================================
***	
***		Updates of Sept. 27, 2012
***
***	===========================================================================
**/

BEGIN TRANSACTION;

-- Cancel lease -  agreed price that was not entered

DELETE FROM pangroup.apt_unit_occupancy_segment WHERE unit IN 
(SELECT unit FROM pangroup.lease WHERE id = 231 );

INSERT INTO pangroup.apt_unit_occupancy_segment (id,unit,date_from,date_to,status) 
(SELECT nextval('public.apt_unit_occupancy_segment_seq'),unit,'2012-09-27','3000-01-01','pending'
 FROM pangroup.lease WHERE id = 231 );

UPDATE 	pangroup.lease_v 
SET	status = 'Cancelled' 
WHERE 	holder IN (SELECT id FROM pangroup.lease WHERE id = 231);

SAVEPOINT upd_lease;

-- Update of PAN Group Clonsila merchant account;

UPDATE 	pangroup.merchant_account
SET	merchant_terminal_id = 'PRVPPCLO',
	charge_description = 'PAN Group Clonsila property'
WHERE 	id = 1;

UPDATE 	_admin_.admin_onboarding_merchant_account
SET	merchant_terminal_id = 'PRVPPCLO',
	charge_description = 'PAN Group Clonsila property'
WHERE 	merchant_account_key = 1;

-- Manual commit


