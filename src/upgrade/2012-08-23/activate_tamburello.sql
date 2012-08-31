/**
***	----------------------------------------------------------------------
***	
***	Creation of Tamburello Realty PMC 
***	
***	----------------------------------------------------------------------
**/

-- Remove from reserved_dns_names
DELETE FROM _admin_.admin_reserved_pmc_names
WHERE dns_name = 'tamburellorealty';


-- Update pmc with onboarding user details, after that pmc should be activated OK
UPDATE 	_admin_.admin_pmc
SET	onboarding_account_id = a.onboarding_account_id,
	interface_uid_base = a.interface_uid
FROM 	(SELECT onboarding_account_id,interface_uid||':' AS interface_uid
	FROM _admin_.onboarding_user_credential
	WHERE usr = 20) AS a
WHERE 	id = 12;
