/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Update transaction_error_message on berkley PAPs, take II
***
***     =====================================================================================================================
**/


BEGIN TRANSACTION;

        UPDATE  berkley.payment_record
        SET     transaction_error_message = '901 NSF(DEBIT ONLY)'
        WHERE   id IN (919,980,987,1096,874,1135,1119,1055);
        
        
        
COMMIT;
