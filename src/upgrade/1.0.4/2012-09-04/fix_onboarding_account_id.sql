/**
***	Update of onboarding_user_credential table - bug VISTA-1887
**/

UPDATE 	_admin_.onboarding_user_credential
SET	onboarding_account_id = a.onboarding_account_id
FROM 	
	(SELECT id, onboarding_account_id
	FROM _admin_.admin_pmc) a
WHERE 	pmc = a.id;


