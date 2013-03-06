/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _admin_ schema changes for 1.0.7.4 
***
***     =====================================================================================================================
**/

SET client_min_messages = 'error';

BEGIN TRANSACTION;

SET search_path = '_admin_';

ALTER TABLE scheduler_trigger_notification DROP CONSTRAINT scheduler_trigger_notification_event_e_ck;
ALTER TABLE scheduler_trigger_notification ADD CONSTRAINT scheduler_trigger_notification_event_e_ck 
        CHECK ((event) IN ('All', 'Completed', 'Error', 'NonEmpty'));

COMMIT;

SET client_min_messages = 'notice';

