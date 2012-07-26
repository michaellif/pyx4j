-- SQL required to migrate pangroup data to new schema version
-- This script depends on the existence of specific functions in _dba_ schema
-- See file dba_scripts/dba_fucntions.sql 

DROP SCHEMA IF EXISTS pangroup_copy CASCADE;
DROP SCHEMA IF EXISTS empty_copy CASCADE;

BEGIN TRANSACTION;

-- Step 1 - create a copy of pangroup schema
-- Note: _dba_.clone_schema_tables does not copy foreign key constaints,
-- which is quite convinient since we won't have to worry about referential integrity at this point

SET client_min_messages = 'error';

SELECT _dba_.clone_schema_tables('pangroup_import','pangroup_copy');

-- Step 2 - same thing with a new version schema. Newly created schema 
-- Will be a destination for pangroup data

SELECT _dba_.clone_schema_tables('pangroup_empty','empty_copy');

-- Step 3 - drop all the tables from pangroup_copy schema that we don't want to migrate

DROP TABLE pangroup_copy.application_documentation_policy;
DROP TABLE pangroup_copy.availability_summary;
DROP TABLE pangroup_copy.available_locale;
DROP TABLE pangroup_copy.building_lister;
DROP TABLE pangroup_copy.building_lister$column_descriptors;
DROP TABLE pangroup_copy.column_descriptor_entity;
DROP TABLE pangroup_copy.dashboard_metadata;
DROP TABLE pangroup_copy.dashboard_metadata$gadgets;
DROP TABLE pangroup_copy.deposit_policy;
DROP TABLE pangroup_copy.deposit_policy_item;
-- DROP TABLE pangroup_copy.feature_item_type;
DROP TABLE pangroup_copy.gl_code;
DROP TABLE pangroup_copy.gl_code_category;
DROP TABLE pangroup_copy.identification_document_type;
DROP TABLE pangroup_copy.layout_module;
DROP TABLE pangroup_copy.late_fee_item;
DROP TABLE pangroup_copy.lease_adjustment_reason;
DROP TABLE pangroup_copy.lease_billing_policy;
DROP TABLE pangroup_copy.lease_terms_policy;
DROP TABLE pangroup_copy.lease_terms_policy$guarantor_summary_terms;
DROP TABLE pangroup_copy.lease_terms_policy$tenant_summary_terms;
DROP TABLE pangroup_copy.legal_terms_content;
DROP TABLE pangroup_copy.legal_terms_descriptor;
DROP TABLE pangroup_copy.legal_terms_descriptor$content;
DROP TABLE pangroup_copy.misc_policy;
DROP TABLE pangroup_copy.page_caption;
DROP TABLE pangroup_copy.page_content;
DROP TABLE pangroup_copy.page_descriptor;
DROP TABLE pangroup_copy.page_descriptor$caption;
DROP TABLE pangroup_copy.portal_image_resource;
-- DROP TABLE pangroup_copy.product_catalog$included_utilities;
DROP TABLE pangroup_copy.service_item_type;
DROP TABLE pangroup_copy.site_descriptor;
DROP TABLE pangroup_copy.site_descriptor_changes;
DROP TABLE pangroup_copy.site_descriptor$homepage_modules;
DROP TABLE pangroup_copy.site_descriptor$logo;
DROP TABLE pangroup_copy.site_descriptor$site_titles;
DROP TABLE pangroup_copy.site_descriptor$social_links;
DROP TABLE pangroup_copy.site_titles;
DROP TABLE pangroup_copy.social_link;
DROP TABLE pangroup_copy.site_palette;
DROP TABLE pangroup_copy.site_image_resource;
DROP TABLE pangroup_copy.unit_availability;
DROP TABLE pangroup_copy.unit_availability$column_descriptors;


-- Step 4 - manual: alter tables in 'pangroup_copy' schema that they will match 
-- 'empty_copy' schema. Columns that added in newer schema version aren't a problem,
-- but renamed/modified/deleted are

ALTER TABLE pangroup_copy.apt_unit RENAME COLUMN belongs_to TO building;
ALTER TABLE pangroup_copy.apt_unit ADD COLUMN notes_and_attachments BIGINT;
ALTER TABLE pangroup_copy.apt_unit ALTER COLUMN info_unit_number TYPE VARCHAR(20);

ALTER TABLE pangroup_copy.billing_account ALTER COLUMN account_number TYPE VARCHAR(14);
ALTER TABLE pangroup_copy.billing_account RENAME COLUMN billing_cycle TO billing_type;
ALTER TABLE pangroup_copy.billing_account RENAME COLUMN initial_balance TO carryforward_balance;
ALTER TABLE pangroup_copy.billing_account DROP COLUMN current_billing_run;
ALTER TABLE pangroup_copy.billing_account DROP COLUMN total;
ALTER TABLE pangroup_copy.billing_account DROP COLUMN billing_period_start_date;

ALTER TABLE pangroup_copy.building_amenity RENAME COLUMN belongs_to TO building;

ALTER TABLE pangroup_copy.crm_user_credential RENAME COLUMN updated TO password_updated;
ALTER TABLE pangroup_copy.customer_user_credential RENAME COLUMN updated TO password_updated;

-- Table product_catalog$included_utilities is deprecated, building_amenity is used instead
-- For the insert statement to work, sequences must be reset 
SELECT _dba_.reset_schema_sequences('pangroup_copy');

INSERT INTO pangroup_copy.building_amenity (id,building,building_amenity_type)
(SELECT nextval('public.building_amenity_seq'),c.building,LOWER(a.name)
FROM pangroup_copy.feature_item_type a JOIN pangroup_copy.product_catalog$included_utilities b ON (a.id = b.value)
JOIN pangroup_copy.product_catalog c ON (b.owner = c.id));

DROP TABLE pangroup_copy.product_catalog$included_utilities;
DROP TABLE pangroup_copy.feature_item_type;

-- ALTER TABLE pangroup_copy.column_descriptor_entity RENAME COLUMN sortable TO is_sortable;
-- ALTER TABLE pangroup_copy.column_descriptor_entity RENAME COLUMN word_wrap TO wrap_words;
-- ALTER TABLE pangroup_copy.column_descriptor_entity RENAME COLUMN visiblily TO is_visible;

-- ALTER TABLE pangroup_copy.deposit_policy_item ALTER COLUMN description TYPE VARCHAR(40);
-- ALTER TABLE pangroup_copy.deposit_policy_item RENAME COLUMN applied_to TO product_type;
-- ALTER TABLE pangroup_copy.deposit_policy_item RENAME COLUMN applied_todiscriminator TO product_typediscriminator;
-- ALTER TABLE pangroup_copy.deposit_policy_item RENAME COLUMN repayment_mode TO deposit_type;
-- ALTER TABLE pangroup_copy.deposit_policy_item ADD COLUMN annual_interest_rate NUMERIC(18,2);
-- ALTER TABLE pangroup_copy.deposit_policy_item ADD COLUMN security_deposit_refund_window INTEGER;

ALTER TABLE pangroup_copy.floorplan_amenity RENAME COLUMN belongs_to TO floorplan;

-- ALTER TABLE pangroup_copy.late_fee_item DROP COLUMN order_in_policy;

ALTER TABLE pangroup_copy.lease RENAME COLUMN create_date TO creation_date;
ALTER TABLE pangroup_copy.lease ADD COLUMN activation_date DATE;

ALTER TABLE pangroup_copy.tenant RENAME COLUMN tenant_role TO participant_role;
ALTER TABLE pangroup_copy.tenant ADD COLUMN participant_id VARCHAR(500);
ALTER TABLE pangroup_copy.tenant ADD COLUMN preauthorized_payment BIGINT;
ALTER TABLE pangroup_copy.tenant ALTER COLUMN percentage TYPE NUMERIC(18,2);

-- ALTER TABLE pangroup_copy.unit_availability RENAME TO unit_availability_gadget_meta;
-- ALTER TABLE pangroup_copy.unit_availability_gadget_meta DROP COLUMN page_number;
-- ALTER TABLE pangroup_copy.unit_availability_gadget_meta RENAME COLUMN primary_sort_column_sortable TO primary_sort_column_is_sortable;
-- ALTER TABLE pangroup_copy.unit_availability_gadget_meta RENAME COLUMN primary_sort_column_word_wrap TO primary_sort_column_wrap_words;
-- ALTER TABLE pangroup_copy.unit_availability_gadget_meta RENAME COLUMN primary_sort_column_visiblily TO primary_sort_column_is_visible;
-- ALTER TABLE pangroup_copy.unit_availability_gadget_meta RENAME COLUMN default_filtering_preset TO filter_preset;

-- ALTER TABLE pangroup_copy.unit_availability$column_descriptors RENAME TO unit_availability_gadget_meta$column_descriptors;

-- ALTER TABLE pangroup_copy.availability_summary RENAME TO unit_availability_summary_gmeta;

-- Remove reference to dashboard_metadata FROM building;
UPDATE pangroup_copy.building SET dashboard = NULL;

-- Populate participant_id column of tenant table 
UPDATE pangroup_copy.tenant SET participant_id = id;

-- Changes to id_assignment_policy table;
-- What was tenants - now customers
UPDATE pangroup_copy.id_assignment_sequence SET target = 'customer' WHERE target = 'tenant';

-- Insert new one - for tenants
INSERT INTO pangroup_copy.id_assignment_sequence(id,target,number)
(SELECT nextval('public.id_assignment_sequence_seq'),'tenant',COALESCE(MAX(participant_id::int),0) FROM pangroup_copy.tenant);

-- Changes to apt_unit_occupancy_segment tatus reserved is now migrated
UPDATE pangroup_copy.apt_unit_occupancy_segment SET status = 'migrated' WHERE status = 'reserved';

COMMIT;

-- Update tuples information - cannot run in transaction block

VACUUM ANALYZE;

BEGIN TRANSACTION;

-- Step 5 - copy over data from non-empty tables of pangroup_copy to empty_copy
SELECT _dba_.copy_schema_data('pangroup_copy','empty_copy');

-- Step 6 - add foreign key constraints to tables in empty_copy schema
SELECT _dba_.clone_schema_tables_fk('pangroup_empty','empty_copy');

-- Step 7 - reset sequences in 
SELECT _dba_.reset_schema_sequences('empty_copy');

-- Step 8 - rename schemas and assign ownership

ALTER SCHEMA empty_copy RENAME TO pangroup;
ALTER SCHEMA pangroup OWNER TO vista;
GRANT USAGE ON SCHEMA pangroup TO vista;
SELECT _dba_.change_schema_tables_ownership('pangroup','vista');
-- GRANT SELECT,UPDATE,INSERT,DELETE ON ALL TABLES IN SCHEMA pangroup TO vista;

-- SELECT _dba_.grant_schema_tables_privs('pangroup','vista');

COMMIT;
