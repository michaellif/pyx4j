/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Mark returns for berkley, gateway, greenwin, and pangroup 
***
***     ======================================================================================================================
**/


BEGIN TRANSACTION;
        
        UPDATE  berkley.payment_record AS p 
        SET     pad_reconciliation_debit_record_key = t.id,
                payment_status = 'Rejected',
                last_status_change_date = current_date,
                finalize_date = current_date,
                transaction_error_message = t.msg
        FROM    (SELECT id,transaction_id::bigint AS transaction_id,
                        reason_code||' '||reason_text AS msg
                FROM    _admin_.pad_reconciliation_debit_record 
                WHERE NOT processing_status) AS t
        WHERE   p.id = t.transaction_id;
        
        UPDATE  gateway.payment_record AS p 
        SET     pad_reconciliation_debit_record_key = t.id,
                payment_status = 'Rejected',
                last_status_change_date = current_date,
                finalize_date = current_date,
                transaction_error_message = t.msg
        FROM    (SELECT id,transaction_id::bigint AS transaction_id,
                        reason_code||' '||reason_text AS msg
                FROM    _admin_.pad_reconciliation_debit_record 
                WHERE NOT processing_status) AS t
        WHERE   p.id = t.transaction_id;
        
        UPDATE  greenwin.payment_record AS p 
        SET     pad_reconciliation_debit_record_key = t.id,
                payment_status = 'Rejected',
                last_status_change_date = current_date,
                finalize_date = current_date,
                transaction_error_message = t.msg
        FROM    (SELECT id,transaction_id::bigint AS transaction_id,
                        reason_code||' '||reason_text AS msg
                FROM    _admin_.pad_reconciliation_debit_record 
                WHERE NOT processing_status) AS t
        WHERE   p.id = t.transaction_id;


        UPDATE  pangroup.payment_record AS p 
        SET     pad_reconciliation_debit_record_key = t.id,
                payment_status = 'Rejected',
                last_status_change_date = current_date,
                finalize_date = current_date,
                transaction_error_message = t.msg
        FROM    (SELECT id,transaction_id::bigint AS transaction_id,
                        reason_code||' '||reason_text AS msg
                FROM    _admin_.pad_reconciliation_debit_record 
                WHERE NOT processing_status) AS t
        WHERE   p.id = t.transaction_id;
        
        UPDATE _admin_.pad_debit_record
        SET     processing_status = 'ReconciliationProcessed',
                processed = TRUE
        WHERE   transaction_id IN       (SELECT transaction_id 
                                        FROM    _admin_.pad_reconciliation_debit_record 
                                        WHERE NOT processing_status)
        AND     pad_batch IN    (SELECT id 
                                FROM _admin_.pad_batch 
                                WHERE pmc IN 
                                        (SELECT id 
                                        FROM    _admin_.admin_pmc 
                                        WHERE   namespace IN ('berkley','gateway','greenwin','pangroup')))
        AND     processing_status = 'AcknowledgeReject';

        UPDATE  _admin_.pad_reconciliation_debit_record
        SET     processing_status = TRUE
        WHERE NOT processing_status;
        
COMMIT;
