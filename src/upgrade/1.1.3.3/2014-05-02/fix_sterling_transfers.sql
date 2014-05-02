/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Sterling aggregated transfer update
***
***     ======================================================================================================================
**/


\o sterling_aggregated_tarnsfer_may2.txt

SELECT  id,previous_balance,merchant_balance,funds_released,status,
        pad_reconciliation_summary_key
FROM    sterling.aggregated_transfer
WHERE   id IN (2760,2761,2763,2764);

\o

BEGIN TRANSACTION;

    UPDATE  dms.aggregated_transfer AS a
    SET     status = 'Paid',
            pad_reconciliation_summary_key = s.id,
            previous_balance = s.previous_balance ,
            merchant_balance = s.merchant_balance,
            funds_released = s.funds_released
    FROM    _admin_.funds_reconciliation_summary s
    WHERE   s.id = 2823
    AND     a.id = 2810;
    
    
    UPDATE  _admin_.funds_reconciliation_summary
    SET     processing_status = TRUE
    WHERE   id = 2823;
    
COMMIT;
