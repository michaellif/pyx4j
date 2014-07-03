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
        
        /*
        
        Before update state
        
        SELECT  p.id, p.payment_status, p.last_status_change_date,
                p.finalize_date, p.pad_reconciliation_return_record_key,
                p.pad_reconciliation_debit_record_key,
                p.transaction_error_message, l.lease_id
        FROM    greenwin.payment_record p
        JOIN    greenwin.lease_term_participant ltp ON (ltp.id = p.lease_term_participant)
        JOIN    greenwin.lease_participant lp ON (lp.id = ltp.lease_participant)
        JOIN    greenwin.lease l ON (l.id = lp.lease)
        WHERE   p.id = 52682;
        
          id   | payment_status | last_status_change_date | finalize_date | pad_reconciliation_return_record_key | pad_reconciliation_debit_record_key | transaction_error_message | lease_id 
        -------+----------------+-------------------------+---------------+--------------------------------------+-------------------------------------+---------------------------+----------
         52682 | Cleared        | 2014-06-30              | 2014-06-30    |                                      |                               50281 |                           | t0028168

        */
        
        UPDATE  greenwin.payment_record  
        SET     pad_reconciliation_return_record_key = 50286,
                pad_reconciliation_debit_record_key = NULL,
                payment_status = 'Returned',
                last_status_change_date = '30-JUNE-2014',
                finalize_date = '30-JUNE-2014',
                transaction_error_message = '900 EDIT REJECT'
        WHERE   id = 52682;
       
        UPDATE _admin_.funds_reconciliation_record_record
        SET     processing_status = TRUE
        WHERE   id = 50286;
        
COMMIT;
