/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             set preathorised payments to deleted for selected pmc
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

        UPDATE  berkley.preauthorized_payment
        SET     is_deleted = TRUE ;
        
        UPDATE  greenwin.preauthorized_payment
        SET     is_deleted = TRUE ;
        
        UPDATE  gateway.preauthorized_payment
        SET     is_deleted = TRUE ;
        
        UPDATE  sterling.preauthorized_payment
        SET     is_deleted = TRUE ;

COMMIT;
        
        
        
