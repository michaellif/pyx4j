/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Larlyn billing cycle update for building 5th0407
***
***     ======================================================================================================================
**/


BEGIN TRANSACTION;
        
        UPDATE  larlyn.billing_billing_cycle AS bc
        SET     target_pad_generation_date = '05-OCT-2013',
                target_pad_execution_date = '05-OCT-2013',
                actual_pad_generation_date = NULL
        FROM    (SELECT bc.id AS billing_cycle
                FROM    larlyn.billing_billing_cycle bc
                JOIN    larlyn.building b ON (b.id = bc.building)
                WHERE   b.property_code = '5th0407'
                AND     bc.billing_cycle_start_date = '01-OCT-2013') AS t
        WHERE   bc.id = t.billing_cycle
        RETURNING *;
        

COMMIT;
