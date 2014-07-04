/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Set sterling payment records 47762 and 47928 as cleared
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

    UPDATE  sterling.payment_record 
    SET     payment_status = 'Cleared'
    WHERE   id IN (47762,47928)
    AND     payment_status = 'Submitted'
    
COMMIT;
