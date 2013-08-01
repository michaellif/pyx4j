/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***            update greenwin payment records
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

        UPDATE  greenwin.payment_record
        SET     payment_status = 'Canceled',
                finalize_date = '01-AUG-2013'
        WHERE   payment_status = 'PendingAction';
        
COMMIT;
