/**
***     =============================================================================
***
***             Another removal of extra services - this time for "ottawa" PMC
***     
***     =============================================================================
**/

BEGIN TRANSACTION;

DELETE FROM ottawa.product_item
WHERE product IN 
(SELECT id FROM ottawa.product_v WHERE holder IN (106,108,109) );

DELETE FROM ottawa.product_v WHERE holder IN (106,108,109) ;

DELETE FROM ottawa.product WHERE id IN (106,108,109);

DELETE FROM ottawa.product_v WHERE holder = 103 AND version_number = 2;

UPDATE ottawa.product_v SET to_date = NULL WHERE holder = 103 AND version_number = 1;

-- COMMIT;
