/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             update sterling billing cycles 
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

    UPDATE  sterling.billing_billing_cycle AS bc
    SET     actual_autopay_execution_date = NULL,
            target_autopay_execution_date = '04-JUL-2014'
    FROM    sterling.building b 
    WHERE   b.property_code IN ('maxw0131','west2292')
    AND     b.id = bc.building 
    AND     bc.billing_cycle_start_date = '01-JUL-2014';
    
COMMIT;
