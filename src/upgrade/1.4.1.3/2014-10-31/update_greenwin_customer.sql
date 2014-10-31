/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Update for greenwin customer 
***
***     ===========================================================================================================
**/                                                     

BEGIN TRANSACTION;

    UPDATE  greenwin.lease_participant 
    SET     customer = 30631
    WHERE   id = 48515;
    
COMMIT;
