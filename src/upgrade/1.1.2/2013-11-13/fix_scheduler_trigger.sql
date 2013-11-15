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
        
        INSERT INTO _admin_.scheduler_trigger_schedule (id,trgr,repeat_type,repeat_every,tm,starts_on,ends_on)
        VALUES (25,21,'Daily',1,'13:25','2013-11-15','2013-11-16');
        
COMMIT;

BEGIN TRANSACTION;
        
        DELETE FROM _admin_.scheduler_trigger_schedule
        WHERE id = 25;

        DELETE FROM _admin_.scheduler_trigger
        WHERE id = 21;
        
COMMIT;
