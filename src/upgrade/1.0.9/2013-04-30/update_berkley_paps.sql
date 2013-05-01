/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Update payment_status on canceled berkley PAPs 
***
***     =====================================================================================================================
**/


BEGIN TRANSACTION;

        UPDATE  berkley.payment_record
        SET     payment_status = 'Scheduled',
                finalize_date = NULL
        WHERE   payment_status = 'Canceled'
        AND     target_date =  '2013-05-01'
        AND     pad_billing_cycle = 1248 ;
        
COMMIT;
