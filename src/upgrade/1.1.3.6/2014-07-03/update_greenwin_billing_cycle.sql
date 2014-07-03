/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             update greenwin billing cycles 
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

    UPDATE  greenwin.billing_billing_cycle AS bc
    SET     actual_autopay_execution_date = NULL,
            target_autopay_execution_date = '05-JUL-2014'
    FROM    greenwin.building b 
    WHERE   b.property_code IN ('chan0286','chan0294','west0093','erb0285','rose0001')
    AND     b.id = bc.building 
    AND     bc.billing_cycle_start_date = '01-JUL-2014';
    
COMMIT;
