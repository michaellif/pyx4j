/**
***     Script to merge imported Pangroup data with data created via onboarding
**/

ALTER SCHEMA pantmp RENAME TO pangroup;

UPDATE pangroup.crm_user_credential
SET     onboarding_user = a.id,
        credential = a.credential,
        interface_uid = a.interface_uid
FROM    (SELECT id,credential,interface_uid FROM _admin_.onboarding_user_credential
	WHERE id = 5) a
WHERE 	usr = 1; 


UPDATE _admin_.onboarding_user_credential
SET     crm_user = 1,
        behavior = 'Client'
WHERE   id = 5;

UPDATE _admin_.admin_pmc 
SET     status = 'Active'
WHERE   id = 2;

