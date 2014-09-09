/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     greenwin payment record update - revert back 
***
***     ===========================================================================================================
**/                                                     

BEGIN TRANSACTION; 

    UPDATE  greenwin.payment_record
    SET     received_date = '31-JUL-2014'
    WHERE   id = 57249;
    
COMMIT;
