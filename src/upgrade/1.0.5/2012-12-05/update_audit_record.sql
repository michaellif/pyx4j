/**
***     ===================================================================
***
***             Update _admin_.audit_record table - pantmp schema 
***             was renamed to pangroup
***
***     ===================================================================
**/

BEGIN TRANSACTION;

UPDATE  _admin_.audit_record
SET     namespace = 'pangroup'
WHERE   namespace = 'pantmp';

COMMIT;


