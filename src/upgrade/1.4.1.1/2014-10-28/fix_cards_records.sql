/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Fix cards records in _admin_ schema 
***
***     =====================================================================================================================
**/



BEGIN TRANSACTION;

    UPDATE  _admin_.cards_clearance_record
    SET     status = 'Received'
    WHERE   merchant_id = 'PRVNEP01'
    AND     reference_number = '1234'
    AND     id IN (675, 674);


    UPDATE  _admin_.cards_clearance_record
    SET     card_type = 'MCRD'
    WHERE   merchant_id = 'PRVNEP01'
    AND     reference_number = '1234'
    AND     id = 675;


    UPDATE  _admin_.cards_clearance_record
    SET     card_type = 'VISA'
    WHERE   merchant_id = 'PRVNEP01'
    AND     reference_number = '1234'
    AND     id = 674;


    UPDATE _admin_.cards_reconciliation_record
    SET     status = 'Received'
    WHERE   merchant_terminal_id = 'PRVNEP01'
    AND     deposit_date= '09-SEP-2014';



COMMIT;
    
    
