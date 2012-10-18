/**
***     ==========================================================================
***     
***             Fix unique admin indexes
***
***     ==========================================================================
**/

BEGIN TRANSACTION;

SET search_path = '_admin_';


DROP INDEX admin_pmc_namespace_idx;
CREATE UNIQUE INDEX admin_pmc_namespace_idx ON admin_pmc USING btree (LOWER(namespace));

DROP INDEX admin_pmc_dns_name_idx;
CREATE UNIQUE INDEX admin_pmc_dns_name_idx ON admin_pmc USING btree(LOWER(dns_name));

DROP INDEX admin_pmc_dns_name_dns_name_idx;
CREATE UNIQUE INDEX admin_pmc_dns_name_dns_name_idx ON admin_pmc_dns_name USING btree (LOWER(dns_name));

DROP INDEX admin_reserved_pmc_names_dns_name_idx;
CREATE UNIQUE INDEX admin_reserved_pmc_names_dns_name_idx ON admin_reserved_pmc_names USING btree (LOWER(dns_name));

DROP INDEX admin_user_email_idx;
CREATE UNIQUE INDEX admin_user_email_idx ON admin_user USING btree (LOWER(email));

-- multiple onboarding account for sales
DELETE FROM onboarding_user_credential 
WHERE usr IN (SELECT id FROM onboarding_user WHERE email IN ('ashley-annt@hotmail.com','kevan@propertyvista.com'));
DELETE FROM onboarding_user WHERE email IN ('ashley-annt@hotmail.com','kevan@propertyvista.com');

-- duplicate record for lenny@drimmer.ca
DELETE FROM onboarding_user_credential WHERE usr = 69;
DELETE FROM onboarding_user WHERE id = 69;

DROP INDEX onboarding_user_email_idx;
CREATE UNIQUE INDEX onboarding_user_email_idx ON _admin_.onboarding_user USING btree (LOWER(email));

-- COMMIT;
