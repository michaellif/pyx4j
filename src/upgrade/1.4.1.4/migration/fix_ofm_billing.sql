/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             fix ofm billing issue
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

    UPDATE  ofm.billing_account
    SET     billing_type = 13
    WHERE   id = 105727;
    
    UPDATE  ofm.billing_billing_cycle 
    SET     billing_type = 13 
    WHERE   billing_type = 90; 
    
    DELETE  FROM ofm.billing_billing_type 
    WHERE   id = 90;
    
COMMIT;
