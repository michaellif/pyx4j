/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Cancel sterling PAPs that was not approved
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

UPDATE sterling.payment_record p
   SET payment_status = 'Canceled',
       last_status_change_date  = '2013-04-30',
       finalize_date  = '2013-04-30'
 WHERE (p.payment_status = 'Scheduled' OR p.payment_status = 'PendingAction')
   AND p.pad_billing_cycle IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM sterling.building b, sterling.billing_billing_cycle bc
                           WHERE b.id = bc.building
                             AND bc.id = p.pad_billing_cycle
                             AND b.property_code IN ('bath4141', 'bath4190'))

COMMIT;