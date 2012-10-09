/**
***	=========================================================================
***
***		Update date acquired and structure build year for Starlight
***
***	=========================================================================
**/

BEGIN TRANSACTION;

UPDATE starlight.building SET info_structure_build_year = NULL WHERE info_structure_build_year = '01-JAN-2017';
UPDATE starlight.building SET financial_date_acquired = NULL WHERE financial_date_acquired = '01-AUG-2012';

COMMIT;

