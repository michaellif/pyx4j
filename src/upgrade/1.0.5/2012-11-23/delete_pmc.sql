/**
***     =========================================================================================
***
***             Delete onboarding users and pmc's created by Tyler for testing. 
***             Why is he so inclined on using product systems for that is beyond me
***
***     =========================================================================================
**/

BEGIN TRANSACTION;

DELETE FROM _admin_.onboarding_user_credential WHERE pmc = 57;
DELETE FROM _admin_.onboarding_user_credential WHERE usr IN (133,134);
DELETE FROM _admin_.onboarding_user WHERE id IN (133,134);

DELETE FROM _admin_.admin_onboarding_merchant_account WHERE pmc = 57;
DELETE FROM _admin_.admin_pmc_account_numbers WHERE pmc = 57 ;
DELETE FROM _admin_.admin_pmc_dns_name WHERE pmc = 57;
DELETE FROM _admin_.admin_pmc_equifax_info WHERE pmc = 57;
DELETE FROM _admin_.admin_pmc_payment_type_info WHERE pmc = 57;
DELETE FROM _admin_.onboarding_user_credential WHERE pmc = 57;
DELETE FROM _admin_.scheduler_trigger_pmc WHERE pmc = 57;
DELETE FROM _admin_.scheduler_trigger_pmc WHERE pmc = 57;
DELETE FROM _admin_.scheduler_run_data WHERE pmc = 57;
DELETE FROM _admin_.admin_pmc WHERE id = 57 ;

DROP SCHEMA asdasd CASCADE;

COMMIT;


