/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Delete sterling PAPs run on Apr. 28, 2013 by mistake
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

DELETE FROM     sterling.payment_record 
WHERE   created_date = '2013-04-28';

UPDATE  sterling.billing_billing_cycle
SET     actual_pad_generation_date = NULL
WHERE   actual_pad_generation_date = '2013-04-28';

COMMIT;
