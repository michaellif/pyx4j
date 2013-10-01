/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Greenwin billing cycle update
***
***     ======================================================================================================================
**/


BEGIN TRANSACTION;
        
        UPDATE  greenwin.billing_billing_cycle AS bc
        SET     target_pad_generation_date = '03-OCT-2013',
                target_pad_execution_date = '04-OCT-2013',
                actual_pad_generation_date = NULL
        FROM    (SELECT bc.id AS billing_cycle
                FROM    greenwin.billing_billing_cycle bc
                JOIN    greenwin.building b ON (b.id = bc.building)
                WHERE   b.property_code IN ('albe0383','albe0457','belm0545',
                        'belm0547','belm0565','conf0104','erb0285','oldc0100',
                        'oldc0120','oldc0170','park0400','shak0200','univ0137',
                        'west0093','west0109')
                AND     bc.billing_cycle_start_date = '01-OCT-2013') AS t
        WHERE   bc.id = t.billing_cycle
        RETURNING *;
        

COMMIT;
