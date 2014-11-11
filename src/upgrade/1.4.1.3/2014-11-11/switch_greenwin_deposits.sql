/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Switch greenwin deposit 
***
***     =====================================================================================================================
**/

-- Part 1 

BEGIN TRANSACTION;

    -- Keep existing number - old
    
    SELECT  lease_products_service_item
    FROM    greenwin.lease_term_v ltv
    JOIN    greenwin.lease_term lt ON (lt.id = ltv.holder)
    WHERE   lt.lease = 108135;
    
    -- old = 510983
    
    -- new lease_products_service_item
    
    SELECT  lease_products_service_item
    FROM    greenwin.lease_term_v ltv
    JOIN    greenwin.lease_term lt ON (lt.id = ltv.holder)
    WHERE   lt.lease = 106533;
    
    -- new 491347
    
    -- deposit id
    
    SELECT  id 
    FROM    greenwin.deposit 
    WHERE   billable_item = (   SELECT  lease_products_service_item
                                FROM    greenwin.lease_term_v ltv
                                JOIN    greenwin.lease_term lt ON (lt.id = ltv.holder)
                                WHERE   lt.lease = 108135) ;
    
    -- deposit.id = 518
    
    -- switch deposits
    
    UPDATE  greenwin.deposit
    SET     billable_item = 491347
    WHERE   id = 518;
    
    
    -- switch back
    
    /*
    UPDATE  greenwin.deposit
    SET     billable_item = 510983
    WHERE   id = 518;
    */
    
COMMIT;
    
    
    
    
