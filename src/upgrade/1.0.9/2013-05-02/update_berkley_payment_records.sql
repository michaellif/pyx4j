/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Update transaction_error_message on berkley PAPs 
***
***     =====================================================================================================================
**/


BEGIN TRANSACTION;

        UPDATE  berkley.payment_record
        SET     transaction_error_message = '912 INVALID/INCORRECT ACCOUNT NO'
        WHERE   id = 1169;
        
        UPDATE  berkley.payment_record
        SET     transaction_error_message = '903 PAYMENT STOPPED/RECALLED'
        WHERE   id = 907;
        
COMMIT;
