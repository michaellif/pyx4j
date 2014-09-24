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
    SET     status = 'Processed'
    WHERE   merchant_id = 'PRVNEP01'
    AND     reference_number = '1234'
    AND     amount IN (2.00, 2.10);
 
 
    
    UPDATE _admin_.cards_reconciliation_record
    SET     status = 'Processed'
    WHERE   merchant_terminal_id = 'PRVNEP01'
    AND     date = '09-SEP-2014';


    
COMMIT;
    
    
