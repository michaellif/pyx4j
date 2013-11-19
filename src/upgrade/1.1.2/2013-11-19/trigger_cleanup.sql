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

        -- trigger 20, Yardi Import Process [manual] Realstar

        UPDATE  _admin_.scheduler_run
        SET     trgr = 17
        WHERE   trgr = 20;
        
        DELETE FROM _admin_.scheduler_trigger_pmc WHERE trgr = 20;
        
        DELETE FROM _admin_.scheduler_trigger WHERE id = 20;
        
        
        -- trigger 29, Yardi Import Process [Manual] 2

        UPDATE  _admin_.scheduler_run
        SET     trgr = 17
        WHERE   trgr = 29;
        
        DELETE FROM _admin_.scheduler_trigger_pmc WHERE trgr = 29;
        
        DELETE FROM _admin_.scheduler_trigger WHERE id = 29;
        
        
        -- trigger 26, Yardi Import Process [Manual] 2

        UPDATE  _admin_.scheduler_run
        SET     trgr = 12
        WHERE   trgr = 26;
        
        DELETE FROM _admin_.scheduler_trigger_pmc WHERE trgr = 26;
                
        DELETE FROM _admin_.scheduler_trigger WHERE id = 26;
        
COMMIT;


