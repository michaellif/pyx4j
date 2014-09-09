/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     greenwin payment record update
***
***     ===========================================================================================================
**/                                                     

BEGIN TRANSACTION; 

    UPDATE  greenwin.payment_record
    SET     received_date = '01-AUG-2014'
    WHERE   id = 57249;
    
COMMIT;
