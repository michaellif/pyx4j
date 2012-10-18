/**
***     Delete orphaned waterfront service
**/

BEGIN TRANSACTION ;

DELETE FROM waterfront.service WHERE id NOT IN 
        (SELECT DISTINCT holder FROM waterfront.service_v);
        
-- COMMIT;
