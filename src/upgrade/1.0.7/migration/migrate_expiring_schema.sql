/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _expiring_ schema changes
***
***     =====================================================================================================================
**/

SET client_min_messages = 'error';
SET search_path = '_expiring_';

BEGIN TRANSACTION;

-- customer_credit_check_report

ALTER TABLE customer_credit_check_report ADD COLUMN public_key BIGINT;
ALTER TABLE customer_credit_check_report ALTER COLUMN public_key SET NOT NULL;

-- customer_credit_check_report_no_backup

ALTER TABLE customer_credit_check_report_no_backup ADD COLUMN public_key BIGINT;
ALTER TABLE customer_credit_check_report_no_backup ALTER COLUMN public_key SET NOT NULL;


COMMIT;

SET client_min_messages = 'notice';

