/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _admin_ schema changes
***
***     =====================================================================================================================
**/

SET client_min_messages = 'error';
SET search_path = '_expiring_';

BEGIN TRANSACTION;

-- customer_credit_check_report

ALTER TABLE customer_credit_check_report ADD COLUMN public_key BIGINT;

-- customer_credit_check_report_no_backup

ALTER TABLE customer_credit_check_report_no_backup ADD COLUMN public_key BIGINT;


COMMIT;

SET client_min_messages = 'notice';

