/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             processing status update
***
***     ======================================================================================================================
**/

\o processing_status_may3.txt

SELECT  id,processing_status
FROM    _admin_.funds_reconciliation_record_record
WHERE   reconciliation_summary IN (2769,2768,2767,2766,2765,2764,2763,2762,2823);

\o

BEGIN TRANSACTION;

    UPDATE  _admin_.funds_reconciliation_record_record
    SET     processing_status = TRUE
    WHERE   reconciliation_summary IN (2769,2768,2767,2766,2765,2764,2763,2762,2823);
    
COMMIT;
