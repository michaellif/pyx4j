/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             set direct debit record invalid
***
***     =====================================================================================================================
**/


BEGIN TRANSACTION;

    UPDATE  _admin_.direct_debit_record
    SET processing_status = 'Invalid'
    WHERE   id = 1359;
    
COMMIT;
