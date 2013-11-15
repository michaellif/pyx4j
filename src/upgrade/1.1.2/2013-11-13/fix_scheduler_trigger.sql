/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Restore deleted paymentsUpdate trigger, disable and delete again
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

        INSERT INTO _admin_.scheduler_trigger (id,trigger_type,name,population_type,created) VALUES
        (21,'cleanup','Dummy Trigger','allPmc',current_timestamp);
        
COMMIT;

BEGIN TRANSACTION;

        DELETE FROM _admin_.scheduler_trigger
        WHERE id = 21;
        
COMMIT;
