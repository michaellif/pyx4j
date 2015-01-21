/**
***     ======================================================================================================================
***
***             credit cards records update
***
***     ======================================================================================================================
**/

-- Phase I
BEGIN TRANSACTION;

    UPDATE  _admin_.cards_clearance_record
    SET     amount = 200.00
    WHERE   id = 1703
    AND     status = 'Received'
    AND     amount = 204.98;
    
    UPDATE  _admin_.cards_reconciliation_record
    SET     total_deposit = 200.00,
            mastercard_deposit = 200.00
    WHERE   id = 960
    AND     status = 'Received';

COMMIT;
    
-- Phase II - revert everything back
/*

BEGIN TRANSACTION;

    UPDATE  _admin_.cards_clearance_record
    SET     amount = 204.98
    WHERE   id = 1703
    AND     status = 'Processed'
    AND     amount = 200.00;
    
    UPDATE  _admin_.cards_reconciliation_record
    SET     total_deposit = 204.98,
            mastercard_deposit = 204.98
    WHERE   id = 960
    AND     status = 'Processed';
    
COMMIT;

*/
