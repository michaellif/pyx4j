/**
***     ===============================================================
***
***             Fix onboarding_user_credential record for Arlene
***
***     ===============================================================
**/

BEGIN TRANSACTION;

UPDATE  _admin_.onboarding_user_credential 
SET     crm_user = 32
WHERE   usr = 43;

-- COMMIT;
