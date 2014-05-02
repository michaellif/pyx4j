/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             DMS aggregated transfer update
***
***     ======================================================================================================================
**/


\o dms_aggregated_tarnsfer_may1.txt

SELECT  id,previous_balance,merchant_balance,funds_released,status,
        pad_reconciliation_summary_key
FROM    dms.aggregated_transfer
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
    WHERE   s.id = 2765
    AND     a.id = 2764;
    
    UPDATE  dms.aggregated_transfer AS a
    SET     status = 'Paid',
            pad_reconciliation_summary_key = s.id,
            previous_balance = s.previous_balance ,
            merchant_balance = s.merchant_balance,
            funds_released = s.funds_released
    FROM    _admin_.funds_reconciliation_summary s
    WHERE   s.id = 2764
    AND     a.id = 2760;
    
    UPDATE  dms.aggregated_transfer AS a
    SET     status = 'Paid',
            pad_reconciliation_summary_key = s.id,
            previous_balance = s.previous_balance ,
            merchant_balance = s.merchant_balance,
            funds_released = s.funds_released
    FROM    _admin_.funds_reconciliation_summary s
    WHERE   s.id = 2763
    AND     a.id = 2763;
    
    UPDATE  dms.aggregated_transfer AS a
    SET     status = 'Paid',
            pad_reconciliation_summary_key = s.id,
            previous_balance = s.previous_balance ,
            merchant_balance = s.merchant_balance,
            funds_released = s.funds_released
    FROM    _admin_.funds_reconciliation_summary s
    WHERE   s.id = 2762
    AND     a.id = 2761;
    
    UPDATE  _admin_.funds_reconciliation_summary
    SET     processing_status = TRUE
    WHERE   id IN (2769, 2768, 2767, 2766, 2765, 2764, 2763, 2762);
    
COMMIT;
