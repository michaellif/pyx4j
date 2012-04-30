DROP DATABASE IF EXISTS pangroup_migration_admin;
CREATE DATABASE pangroup_migration_admin;

USE pangroup_migration_admin;

-- PMC creation - might not be needed
CREATE TABLE admin_pmc AS
SELECT 	id,name,dnsName AS dns_name,created 
FROM 	vista_pangroup.adminPmc ORDER BY id;

-- Admin users - also might not be required
CREATE TABLE admin_user AS
SELECT 	id,updated,email,created,name 
FROM 	vista_pangroup.AdminUser ORDER BY id;

-- Admin user credential
CREATE TABLE admin_user_credential AS
SELECT 	id,updated,accessKey AS access_key,
		usr,CASE WHEN enabled = 1 THEN 'TRUE'
		WHEN enabled IS NULL THEN NULL ELSE 'FALSE' END AS enabled,
		CASE WHEN requiredPasswordChangeOnNextLogIn = 1 THEN 'TRUE'
		WHEN requiredPasswordChangeOnNextLogIn IS NULL THEN NULL ELSE 'FALSE' END 
		AS required_password_change_on_next_log_in,
		credential,accessKeyExpire AS access_key_expire
FROM 	vista_pangroup.AdminUserCredential ORDER BY id;

-- Admin user behaviors
CREATE TABLE admin_user_credential$behaviors AS
SELECT 	id,owner,value
FROM 	vista_pangroup.AdminUserCredential$behaviors ORDER BY id;

--
-- From now on - CRM stuff
--

DROP DATABASE IF EXISTS pangroup_migration_crm;
CREATE DATABASE pangroup_migration_crm;

USE pangroup_migration_crm;

-- Users - probably the most important
CREATE TABLE crm_user AS
SELECT 	id,updated,email,created,name
FROM 	vista_pangroup.CrmUser ORDER BY 1;

-- Crm user credentials
CREATE TABLE crm_user_credential AS
SELECT 	id,updated,accessKey AS access_key,
	usr, CASE WHEN enabled = 1 THEN 'TRUE'
	WHEN enabled IS NULL THEN NULL ELSE 'FALSE' END AS enabled,
	CASE WHEN requiredPasswordChangeOnNextLogIn = 1 THEN 'TRUE'
	WHEN requiredPasswordChangeOnNextLogIn IS NULL THEN NULL ELSE 'FALSE' END 
	AS required_password_change_on_next_log_in,
	credential,accessKeyExpire AS access_key_expire,
	CASE WHEN accessAllBuildings = 1 THEN 'TRUE' 
	WHEN accessAllBuildings IS NULL THEN NULL ELSE 'FALSE' END AS access_all_buildings, 
	onboardingUser AS onboarding_user
FROM 	vista_pangroup.CrmUserCredential ORDER BY id;

-- Crm user roles
CREATE TABLE crm_user_credential$rls AS
SELECT 	id,owner,value
FROM 	vista_pangroup.CrmUserCredential$rls ORDER BY id;


-- Table building - might be already migrated by app migration

CREATE TABLE building AS 
SELECT	id, notesAndAttachments AS notes_and_attachments,
	orderInComplex AS order_in_complex,
	complex, complexPrimary AS complex_primary,
	propertyCode AS property_code,
	externalId AS external_id,
	propertyManager AS property_manager,
	info_name,info_address_suiteNumber AS info_address_suite_number,
	info_address_streetNumber AS info_address_street_number,
	info_address_streetNumberSuffix AS info_address_street_number_suffix,
	info_address_streetName AS info_address_street_name,
	info_address_streetType AS info_address_street_type,
	info_address_streetDirection AS info_address_street_direction,
	info_address_city AS info_address_city,
	info_address_county AS info_address_county,
	info_address_province AS info_address_province,
	info_address_country AS info_address_country,
	info_address_postalCode AS info_address_postal_code,
	info_address_location_lat AS info_address_location_lat,
	info_address_location_lng AS info_address_location_lng,
	info_buildingType AS info_building_type,
	info_shape AS info_shape,
	info_totalStoreys AS info_total_storeys,
	info_residentialStoreys AS info_residential_storeys,
	info_structureType AS info_structure_type,
	info_structureBuildYear AS info_structure_build_year,
	info_constructionType AS info_construction_type,
	info_foundationType AS info_foundation_type,
	info_floorType AS info_floor_type,
	info_landArea AS info_land_area,
	info_waterSupply AS info_water_supply,
	info_centralAir AS info_central_air,
	info_centralHeat AS info_central_heat,
	financial_dateAcquired AS financial_date_acquired,
	financial_purchasePrice AS financial_purchase_price,
	financial_marketPrice AS financial_market_price,
	financial_lastAppraisalDate AS financial_last_appraisal_date,
	financial_lastAppraisalValue AS financial_last_appraisal_value,
	financial_currency_name AS financial_currency_name,
	contacts_website,marketing,dashboard,updated
FROM 	vista_pangroup.Building ORDER BY id;

-- Building amenities 
CREATE TABLE building_amenity AS 
SELECT 	id,description,name,belongsTo AS belongs_to,
	orderInBuilding AS order_in_building,
	buildingAmenityType AS building_amenity_type
FROM 	vista_pangroup.BuildingAmenity ORDER BY id;

-- Property Contacts
CREATE TABLE property_contact AS
SELECT 	id,phoneType AS phone_type,
	name,visibility,phoneNumber AS phone_number,
	email,description
FROM 	vista_pangroup.PropertyContact ORDER BY id;

-- Building contacts
CREATE TABLE buildingcontacts$property_contacts AS
SELECT 	id,owner,value,seq
FROM 	vista_pangroup.Building$contacts_propertyContacts ORDER BY id;

-- Property manager
CREATE TABLE property_manager AS
SELECT 	id,name
FROM 	vista_pangroup.PropertyManager ORDER BY id;

-- Floorplan 
CREATE TABLE floorplan AS
SELECT 	 id,building,name,marketingName AS marketing_name,
	description,floorCount AS floor_count,
	bedrooms,dens,bathrooms,halfBath AS half_bath,
	counters,updated
FROM 	vista_pangroup.Floorplan ORDER BY id;

-- FloorplanCounters
CREATE TABLE floorplan_counters AS
SELECT 	id,_unitCount AS _unit_count,_marketingUnitCount AS _marketing_unit_count
FROM 	vista_pangroup.FloorplanCounters ORDER BY id;

-- FloorpanAmenity
CREATE TABLE floorplan_amenity AS
SELECT 	id,description,name,floorplanType AS floorplan_type,
	belongsTo AS belongs_to,orderInParent AS order_in_parent
FROM 	vista_pangroup.FloorplanAmenity ORDER BY id;

-- Appartment units
CREATE TABLE apt_unit AS 
SELECT 	id,belongsTo AS belongs_to,floorplan,                      
	info_economicStatus AS info_economic_status,           
	info_economicStatusDescription AS info_economic_status_description,
	info_floor,info_unitNumber AS info_unit_number,              
	info_area,info_areaUnits AS info_area_units,               
	info__bedrooms,info__bathrooms,financial__unitRent AS financial__unit_rent,           
	financial__marketRent AS financial__market_rent,         
	marketing,updated, NULL AS _available_for_rent
FROM 	vista_pangroup.AptUnit ORDER BY id;            

-- Customer (tenants without lease)
CREATE TABLE customer AS
SELECT 	id,tenantId AS customer_id,
	user_id,person_name_namePrefix AS person_name_name_prefix,
	person_name_firstName AS person_name_first_name,
	person_name_middleName AS person_name_middle_name,
	person_name_lastName AS person_name_last_name,
	person_name_maidenName AS person_name_maiden_name,
	person_name_nameSuffix AS person_name_name_suffix,
	person_sex,person_homePhone AS person_home_phone,
	person_mobilePhone AS person_mobile_phone,
	person_workPhone AS person_work_phone,
	person_email AS person_email,
	person_birthDate AS person_birth_date,
	updated
FROM	vista_pangroup.Tenant ORDER BY id;

-- Emergency contacts
CREATE TABLE emergency_contact AS
SELECT	b.id,a.email,a.sex,a.homePhone AS home_phone,
	a.workPhone AS work_phone,
	a.mobilePhone AS mobile_phone,
	a.birthDate AS birth_date,
	a.name_namePrefix AS name_name_prefix,
	a.name_firstName AS name_first_name,
	a.name_middleName AS name_middle_name,
	a.name_lastName AS name_last_name,
	a.name_maidenName AS name_maiden_name,
	a.name_nameSuffix AS name_name_suffix,
	b.owner AS customer,
	b.seq AS order_in_customer,
	a.address_suiteNumber AS address_suite_number,
	a.address_streetNumber AS address_street_number,
	a.address_streetNumberSuffix AS address_street_number_suffix,
	a.address_streetName AS address_street_name,
	a.address_streetType AS address_street_type,
	a.address_streetDirection AS address_street_direction,
	a.address_city,address_county,address_province,address_country,
	a.address_postalCode AS address_postal_code,
	a.address_location_lat,address_location_lng
FROM 	vista_pangroup.EmergencyContact a 
JOIN 	vista_pangroup.Tenant$emergencyContacts b
ON	(a.id = b.value) ORDER BY a.id;

-- Customer emergency contacts
-- CREATE TABLE customer$emergency_contacts
-- SELECT 	id,owner,value seq
-- FROM 	vista_pangroup.Tenant$emergencyContacts ORDER BY 1;

-- Customer users 
CREATE TABLE customer_user AS
SELECT 	id,updated,email,created,name
FROM 	vista_pangroup.TenantUser ORDER BY id;

-- Customer user credentials
CREATE TABLE customer_user_credential AS
SELECT 	id,updated,accessKey AS access_key,
	usr,CASE WHEN enabled = 1 THEN 'TRUE'
	WHEN enabled IS NULL THEN NULL ELSE 'FALSE' END AS enabled,
	CASE WHEN requiredPasswordChangeOnNextLogIn = 1 THEN 'TRUE'
	WHEN requiredPasswordChangeOnNextLogIn IS NULL THEN NULL ELSE 'FALSE' END 
	AS required_password_change_on_next_log_in,
	credential,accessKeyExpire AS access_key_expire
FROM 	vista_pangroup.TenantUserCredential ORDER BY id;

-- Employee 

CREATE TABLE employee AS
SELECT 	id, email,sex,
		homePhone AS home_phone,
		workPhone AS work_phone,
		mobilePhone AS mobile_phone,
		birthDate AS birth_date,
		name_namePrefix AS name_name_prefix,
		name_firstName AS name_first_name,
		name_middleName AS name_middle_name,
		name_lastName AS name_last_name,
		name_maidenName AS name_maiden_name,
		name_nameSuffix AS name_name_suffix,
		title,description,updated,user_id,manager   
FROM 	vista_pangroup.Employee ORDER BY id;

-- Billing Account
--	CREATE TABLE billing_account AS
--	SELECT 	id,billingCycle AS billing_cycle,
--	accountNumber AS account_number,
--	currentBillingRun AS current_billing_run,
--	billCounter AS bill_counter,
--	total,prorationMethod AS proration_method,
--	billingPeriodStartDate AS billing_period_start_date,
--	NULL AS initial_balance
--	FROM	vista_pangroup.BillingAccount ORDER BY id;

-- Lease 
-- CREATE TABLE lease AS
-- SELECT 	id,leaseId AS lease_id,
--	leaseType AS lease_type,
--	unit,leaseFrom AS lease_from,
--	leaseTo AS lease_to,
--	paymentFrequency AS payment_frequency,
--	billingAccount AS billing_account,
--	createDate AS create_date,
--	updated, approvalDate AS approval_date     
-- FROM 	vista_pangroup.Lease ORDER BY id;

-- Lease_v
-- CREATE TABLE lease_v AS
-- SELECT	id,versionNumber AS version_number,	
--	toDate AS to_date,
--	fromDate AS from_date,
--	holder,createdByUserKey AS created_by_user_key,
--	status,completion,actualLeaseTo AS actual_lease_to,
--	expectedMoveIn AS expected_move_in,
--	expectedMoveOut AS expected_move_out,
--	actualMoveIn AS actual_move_in,
--	actualMoveOut AS actual_move_out,
--	moveOutNotice move_out_notice,
--	leaseProducts_serviceItem AS lease_products_service_item
-- FROM 	vista_pangroup.LeaseV ORDER BY id;

-- Application/Online Application/Lease Application - really messy

-- CREATE TABLE master_online_application AS
-- SELECT	id,applicationId AS online_application_id,
--	status,lease,lease_for,createDate AS create_date,
--	equifaxApproval AS equifax_approval
--  FROM 	vista_pangroup.MasterApplication ORDER BY id;

-- CREATE TABLE online_application AS
-- SELECT 	id,belongsTo AS master_online_application,
--	NULL AS customer,
--	NULL AS role,lease, lease_for, status
--  FROM 	vista_pangroup.Application ORDER BY id;

-- CREATE TABLE lease_application AS
-- SELECT 	id,lease,NULL AS online_application,
--	status,decidedBy AS decided_by,
--	decisionDate AS decision_date,
--	decisionReason AS decision_reason,
--	notes,equifaxApproval AS equifax_approval 
-- FROM 	vista_pangroup.MasterApplication ORDER BY id;


-- Tenants (in lease)

-- CREATE TABLE tenant AS
-- SELECT 	id,application,tenant AS customer,
--	leaseV AS lease_v,
--	orderInLease AS order_in_lease,
--	relationship,status AS tenant_role,
--	takeOwnership AS take_ownership,
--	percentage
-- FROM 	vista_pangroup.TenantInLease ORDER BY id;

-- Apt Unit Occupancy Segment
-- CREATE TABLE apt_unit_occupancy_segment AS
-- SELECT	id,unit,dateFrom AS date_from,
--		dateTo AS date_to,
--		status,offMarket AS off_market,
--		lease, description
-- FROM 	vista_pangroup.AptUnitOccupancySegment ORDER BY id;

-- Unit availability status
CREATE TABLE unit_availability_status AS
SELECT 	id,unit,building,floorplan,complex,
		statusDate AS status_date,
		vacancyStatus AS vacancy_status,
		rentedStatus AS rented_status,
		scoping,rentReadinessStatus rent_readiness_status,
		unitRent AS unit_rent,
		marketRent AS market_rent,
		rentDeltaAbsolute AS rent_delta_absolute,
		rentDeltaRelative AS rent_delta_relative,
		rentEndDay AS rent_end_day,
		vacantSince AS vacant_since,
		rentedFromDay AS rented_from_day,
		moveInDay  AS move_in_day
FROM	vista_pangroup.UnitAvailabilityStatus ORDER BY id; 

-- Product Catalog
CREATE TABLE product_catalog AS
SELECT 	id,updated,building
FROM 	vista_pangroup.ProductCatalog ORDER BY id;

-- Product catalog - included utilities
CREATE TABLE product_catalog$included_utilities AS
SELECT 	id,owner,value,seq
FROM 	vista_pangroup.ProductCatalog$includedUtilities ORDER BY id;

-- Id assignment sequence
CREATE TABLE id_assignment_sequence AS
SELECT 	id,target,number
FROM 	vista_pangroup.IdAssignmentSequence ORDER BY id;

-- Application wizard step
-- CREATE TABLE application_wizard_step AS
-- SELECT 	id,placeId AS place_id,status
-- FROM 	vista_pangroup.ApplicationWizardStep ORDER BY id;

-- Application wizard substeps
-- CREATE TABLE online_application$steps AS
 -- SELECT 	id,owner,value,seq
-- FROM 	vista_pangroup.Application$steps ORDER BY id;

-- Dashboard metadata
-- CREATE TABLE dashboard_metadata AS
-- SELECT 	id,user_id,dashboardType AS dashboard_type,
--	name,description,layoutType AS layout_type,
--	CASE WHEN isFavorite = 1 THEN 'TRUE'
--	WHEN isFavorite IS NULL THEN NULL
--	ELSE 'FALSE' END AS is_favorite,
--	CASE WHEN isShared = 1 THEN 'TRUE'
--	WHEN isShared IS NULL THEN NULL
--	ELSE 'FALSE' END AS is_shared
-- FROM 	vista_pangroup.DashboardMetadata ORDER BY id;

-- Dashboard metadata gadgets
-- CREATE TABLE dashboard_metadata$gadgets AS
-- SELECT 	id,owner,value_disc AS valuediscriminator,
--	value,seq
-- FROM 	vista_pangroup.DashboardMetadata$gadgets;


