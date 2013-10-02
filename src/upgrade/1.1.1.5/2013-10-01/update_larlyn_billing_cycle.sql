/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Larlyn billing cycle update
***
***     ======================================================================================================================
**/


BEGIN TRANSACTION;
        
        UPDATE  larlyn.billing_billing_cycle AS bc
        SET     target_pad_generation_date = '04-OCT-2013',
                target_pad_execution_date = '04-OCT-2013',
                actual_pad_generation_date = NULL
        FROM    (SELECT bc.id AS billing_cycle
                FROM    larlyn.billing_billing_cycle bc
                JOIN    larlyn.building b ON (b.id = bc.building)
                WHERE   b.property_code IN ('15th2014','23rd1304','23rd3210',
                        '32nd2201','4th1115','5th0407','berk0037','cent1219',
                        'colu0175','colu0590','esqu0804','esqu0841','gov1030b',
                        'gove0681','gove1030','harw1100','mayo0256','scen1603')
                AND     bc.billing_cycle_start_date = '01-OCT-2013') AS t
        WHERE   bc.id = t.billing_cycle
        RETURNING *;
        

COMMIT;
