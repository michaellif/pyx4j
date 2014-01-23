/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Update greenwin customer
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

        UPDATE  greenwin.customer
        SET     user_id = 2637,
                person_email = 'delp1977@gmail.com'
        WHERE   id = 45966;
        
        UPDATE  greenwin.customer
        SET     user_id = NULL,
                person_email = NULL 
        WHERE   id = 31195;
        
COMMIT;
        
