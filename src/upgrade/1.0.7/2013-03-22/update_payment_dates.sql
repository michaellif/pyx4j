/**
***     ===================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***             
***             Update payment_date for selected pmc's
***     
***     ====================================================================================================================
**/


BEGIN TRANSACTION;

        -- berkley,greenwin and sterling(karamar) initial payment on March 1, 2013
        
        UPDATE  berkley.aggregated_transfer 
        SET     payment_date = '2013-MAR-01'
        WHERE   id IN (7,8);
        
        UPDATE  greenwin.aggregated_transfer 
        SET     payment_date = '2013-MAR-01'
        WHERE   id IN (3,4);
        
        UPDATE  sterling.aggregated_transfer 
        SET     payment_date = '2013-MAR-01'
        WHERE   id IN (5,6);
        
        -- sterling(karamar) return on March 06, 2013
        
        UPDATE  sterling.aggregated_transfer 
        SET     payment_date = '2013-MAR-06'
        WHERE   id = 9;
        
        -- gateway payment on March 7, 2013
        
        UPDATE  gateway.aggregated_transfer 
        SET     payment_date = '2013-MAR-07'
        WHERE   id IN (10,11);
        
COMMIT;
