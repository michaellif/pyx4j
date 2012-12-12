/**
***     ===============================================================================
***     
***             Remove extra services added by customer
***
***     ===============================================================================
**/

BEGIN TRANSACTION;

DELETE FROM renters.product_item
WHERE product IN 
(SELECT id FROM renters.product_v WHERE holder IN (94,98,102));

DELETE FROM renters.product_v WHERE holder IN (94,98,102);

DELETE FROM renters.product WHERE id IN (94,98,102);

-- COMMIT;
