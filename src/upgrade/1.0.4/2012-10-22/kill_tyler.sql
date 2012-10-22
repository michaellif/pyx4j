/**
***     =========================================================================================
***
***             Delete onboarding users and pmc's created by Tyler for unknown, 
***             but undoubtedly malicious purpose
***
***     =========================================================================================
**/

BEGIN TRANSACTION;

DELETE FROM _admin_.onboarding_user_credential WHERE pmc IN (36,38,39);
DELETE FROM _admin_.onboarding_user_credential WHERE usr IN (86,88,89);
DELETE FROM _admin_.onboarding_user WHERE id IN (86,88,89);

DROP SCHEMA akttest CASCADE;
DROP SCHEMA rwerqwe CASCADE;

DELETE FROM _admin_.admin_onboarding_merchant_account WHERE pmc IN (36,38,39);
DELETE FROM _admin_.admin_pmc_account_numbers WHERE pmc IN (36,38,39);
DELETE FROM _admin_.admin_pmc_dns_name WHERE pmc IN (36,38,39);
DELETE FROM _admin_.onboarding_user_credential WHERE pmc IN (36,38,39);
DELETE FROM _admin_.scheduler_trigger_pmc WHERE pmc IN (36,38,39);
DELETE FROM _admin_.scheduler_trigger_pmc WHERE pmc IN (36,38,39);
DELETE FROM _admin_.admin_pmc WHERE id IN (36,38,39);

DELETE FROM _admin_.admin_reserved_pmc_names 
WHERE   onboarding_account_id NOT IN 
        (SELECT COALESCE(onboarding_account_id,'0') 
        FROM _admin_.onboarding_user_credential );
        
        
COMMIT;


