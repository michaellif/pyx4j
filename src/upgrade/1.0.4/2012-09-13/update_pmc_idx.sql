/**
***	===================================================================
***	
***		Create a unique function-based index on building table
***		Might as well add billing_arrears_snapshot_building_idx 
***		for few pmc's that are missing it, and other stuff missed
***		during the August 2012 migration
***
***	===================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.update_pmc_idx_2012_09_12 () RETURNS VOID AS
$$
DECLARE
	v_schema_name		VARCHAR(64);
BEGIN
	FOR v_schema_name IN
	SELECT LOWER(namespace) FROM _admin_.admin_pmc WHERE status IN ('Active','Suspended')
	LOOP
		-- To be sure just drop index and re-create it
		IF EXISTS (	SELECT 'x' FROM pg_indexes
				WHERE 	indexname = 'building_property_code_idx'
				AND 	schemaname = v_schema_name)
		THEN
			EXECUTE	'DROP INDEX '||v_schema_name||'.building_property_code_idx';
		END IF;

		EXECUTE 'CREATE UNIQUE INDEX building_property_code_idx ON '||v_schema_name||'.building USING btree (LOWER(property_code))';
		
		
		-- billing_arrears_snapshot_building_idx for the ones that are missing it

		IF NOT EXISTS(	SELECT 'x' FROM pg_indexes
				WHERE 	indexname = 'billing_arrears_snapshot_building_idx'
				AND 	schemaname = v_schema_name)
		THEN
			EXECUTE 'CREATE INDEX billing_arrears_snapshot_building_idx ON '||v_schema_name||'.billing_arrears_snapshot USING btree (building)';
		END IF;
		
		
			
		-- apt_unit_building_info_unit_number_idx on apt_unit table

		IF EXISTS (	SELECT 'x' FROM pg_indexes
				WHERE 	indexname = 'apt_unit_building_info_unit_number_idx'
				AND 	schemaname = v_schema_name)
		THEN
			EXECUTE	'DROP INDEX '||v_schema_name||'.apt_unit_building_info_unit_number_idx';
		END IF;

		EXECUTE 'CREATE UNIQUE INDEX apt_unit_building_info_unit_number_idx ON '||v_schema_name||'.apt_unit USING btree (building, LOWER(info_unit_number))';
			
		
	END LOOP;

	/** Indexes ( or indicies in proper latin ) in _admin_ schema **/

	-- onboarding_user_email_idx

	IF EXISTS (	SELECT 'x' FROM pg_indexes
			WHERE 	indexname = 'onboarding_user_email_idx'
			AND 	schemaname = '_admin_')
	THEN
		DROP INDEX _admin_.onboarding_user_email_idx;
	END IF;

	CREATE INDEX onboarding_user_email_idx ON _admin_.onboarding_user USING btree (LOWER(email));

	-- admin_user_email_idx

	IF EXISTS (	SELECT 'x' FROM pg_indexes
			WHERE 	indexname = 'admin_user_email_idx'
			AND 	schemaname = '_admin_')
	THEN
		DROP INDEX _admin_.admin_user_email_idx;
	END IF;

	CREATE INDEX admin_user_email_idx ON _admin_.admin_user USING btree (LOWER(email));

END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;
	
	
	-- Delete building with a duplicate property code from starlight
	
	DELETE FROM starlight.product_catalog WHERE building IN (484,485);
	
	DELETE FROM starlight.billing_arrears_snapshot$aging_buckets 
	WHERE owner IN (SELECT id FROM starlight.billing_arrears_snapshot 
			WHERE building IN (484,485));
	DELETE FROM starlight.billing_arrears_snapshot WHERE building IN (484,485);

	DELETE FROM starlight.buildingcontacts$property_contacts WHERE owner IN (484,485);
		
	DELETE FROM starlight.building WHERE id IN (484,485);	

	
		
	SELECT * FROM _dba_.update_pmc_idx_2012_09_12();

COMMIT;

DROP FUNCTION _dba_.update_pmc_idx_2012_09_12();
