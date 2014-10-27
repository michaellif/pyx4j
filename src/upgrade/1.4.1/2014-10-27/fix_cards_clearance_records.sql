/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Cards clearance record fix
***
***     ===========================================================================================================
**/                                                     

BEGIN TRANSACTION;

    UPDATE  _admin_.card_clearance_record 
    SET     card_type = 'VISA'
    WHERE   id = 777;
    
    UPDATE  _admin_.card_clearance_record 
    SET     card_type = 'MCRD'
    WHERE   id = 778;
    
COMMIT;
