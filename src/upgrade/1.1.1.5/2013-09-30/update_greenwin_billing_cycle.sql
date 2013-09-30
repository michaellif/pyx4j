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
        
        UPDATE  greenwin.billing_billing_cycle
        SET     target_pad_generation_date = '03-OCT-2013',
                target_pad_execution_date = '04-OCT-2013',
                actual_pad_generation_date = NULL
        WHERE   id IN ( 50147, 49729, 49541);

COMMIT;
