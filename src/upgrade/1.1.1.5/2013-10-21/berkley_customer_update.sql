/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Update berkley user email 
***
***     ===========================================================================================================
**/                                                     

BEGIN TRANSACTION;

        UPDATE  berkley.customer
        SET     person_email = 'babak359@gmail.com'
        WHERE   id = 2470;
        
        UPDATE  berkley.customer_user
        SET     email = 'shery_moh_tmp@hotmail.com'
        WHERE   id = 1239;
        
        
        UPDATE  berkley.customer_user
        SET     email = 'babak359@gmail.com'
        WHERE   id = 1475;
        
        UPDATE  berkley.customer_user
        SET     email = 'shery_moh@hotmail.com'
        WHERE   id = 1239;
        
        
        
COMMIT;
