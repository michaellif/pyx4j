/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Update _admin_.pad_reconciliation_debit_record
***
***     ======================================================================================================================
**/


BEGIN TRANSACTION;

        UPDATE  _admin_.pad_reconciliation_debit_record
        SET     processing_status = TRUE
        WHERE transaction_id = '14798'
        AND NOT processing_status;
        
COMMIT;
