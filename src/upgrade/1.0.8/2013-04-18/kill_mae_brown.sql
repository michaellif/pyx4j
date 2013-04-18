/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Delete mae_brown pmc and onboarding user
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

DELETE FROM _admin_.onboarding_user_credential WHERE usr = 274 ;
DELETE FROM _admin_.onboarding_user WHERE id = 274;


--DELETE FROM _admin_.admin_onboarding_merchant_account WHERE pmc = 179;
DELETE FROM _admin_.admin_pmc_account_numbers WHERE pmc = 179;
DELETE FROM _admin_.admin_pmc_dns_name WHERE pmc = 179;
DELETE FROM _admin_.onboarding_user_credential WHERE pmc = 179 ;
DELETE FROM _admin_.scheduler_trigger_pmc WHERE pmc = 179 ;
DELETE FROM _admin_.scheduler_run_data WHERE pmc = 179;
DELETE FROM _admin_.audit_record WHERE namespace = 'mae_brown';
DELETE FROM _admin_.admin_pmc_equifax_info WHERE pmc = 179;
DELETE FROM _admin_.admin_pmc WHERE id = 179;

DROP SCHEMA mae_brown CASCADE;

COMMIT;
