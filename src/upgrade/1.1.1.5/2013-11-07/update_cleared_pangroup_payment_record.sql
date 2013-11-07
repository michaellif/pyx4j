/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Set status to Returned for records with status 'Cleared' and aggregated_transfer_return
***
***     ======================================================================================================================
**/


BEGIN TRANSACTION;

        \o 2013-11-07_pangroup_before_update.txt
        
        SELECT * 
        FROM    pangroup.payment_record
        WHERE   payment_status = 'Cleared'
        AND     aggregated_transfer_return IS NOT NULL;
        
        \o


        UPDATE  pangroup.payment_record AS p 
        SET     pad_reconciliation_return_record_key = t.id,
                pad_reconciliation_debit_record_key = NULL,
                payment_status = 'Returned',
                last_status_change_date = current_date,
                finalize_date = current_date,
                transaction_error_message = t.msg
        FROM    (SELECT id, 9566 AS transaction_id,
                        reason_code||' '||reason_text AS msg
                FROM    _admin_.pad_reconciliation_debit_record 
                WHERE   transaction_id = '9566'
                AND     reconciliation_status = 'RETURNED') AS t
        WHERE   p.id = t.transaction_id
        AND     p.payment_status = 'Cleared'
        AND     p.aggregated_transfer_return IS NOT NULL;
        
        
COMMIT;
