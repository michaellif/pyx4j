/**
***	================================================================
***
***		Changes to production DB as of August 28, 2012
***	
***	================================================================
**/

BEGIN TRANSACTION;

/**
***	----------------------------------------------------------------
***		public schema
***	----------------------------------------------------------------
**/

SET search_path = 'public';

CREATE SEQUENCE legal_document_seq 
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;
ALTER SEQUENCE legal_document_seq OWNER TO vista;


CREATE SEQUENCE pad_test_transaction_offset_seq 
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;
ALTER SEQUENCE pad_test_transaction_offset_seq OWNER TO vista;

CREATE SEQUENCE vista_terms_seq 
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;
ALTER SEQUENCE vista_terms_seq OWNER TO vista;

CREATE SEQUENCE vista_terms_v_seq 
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;
ALTER SEQUENCE vista_terms_v_seq OWNER TO vista;

CREATE SEQUENCE vista_terms_v$document_seq 
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;
ALTER SEQUENCE vista_terms_v$document_seq OWNER TO vista;


CREATE SEQUENCE customer_accepted_terms_seq 
	START WITH 1
	INCREMENT BY 1
	NO MAXVALUE
	NO MINVALUE
	CACHE 1;
ALTER SEQUENCE customer_accepted_terms_seq OWNER TO vista;



/** 
***	----------------------------------------------------------------
***		_admin_ schema 
***	----------------------------------------------------------------
**/

/** New tables **/

SET search_path = '_admin_';

-- legal_document

CREATE TABLE legal_document
(
	id			BIGINT 			NOT NULL,
	locale			VARCHAR(50),
	content			VARCHAR(20845)
);

ALTER TABLE legal_document ADD CONSTRAINT legal_document_pk PRIMARY KEY (id);
ALTER TABLE legal_document OWNER TO vista;


-- pad_test_transaction_offset

CREATE TABLE pad_test_transaction_offset
(
	id 			BIGINT			NOT NULL,
	number			INT
);

ALTER TABLE pad_test_transaction_offset ADD CONSTRAINT pad_test_transaction_offset_pk PRIMARY KEY (id);
ALTER TABLE pad_test_transaction_offset OWNER TO vista;

-- vista_terms

CREATE TABLE vista_terms
(
	id			BIGINT			NOT NULL,
	x			INT
);

ALTER TABLE vista_terms ADD CONSTRAINT vista_terms_pk PRIMARY KEY (id);
ALTER TABLE vista_terms OWNER TO vista;

-- vista_terms_v 

CREATE TABLE vista_terms_v 
(
	id			BIGINT			NOT NULL,
	version_number		INT,
	to_date			TIMESTAMP,
	from_date		TIMESTAMP,
	holder			BIGINT,
	created_by_user_key	BIGINT
);

CREATE INDEX vista_terms_v_holder_from_date_to_date_idx ON vista_terms_v USING btree (holder, from_date, to_date);
ALTER TABLE vista_terms_v ADD CONSTRAINT vista_terms_v_pk PRIMARY KEY (id);
ALTER TABLE vista_terms_v ADD CONSTRAINT vista_terms_v_holder_fk FOREIGN KEY (holder) REFERENCES vista_terms (id);
ALTER TABLE vista_terms_v OWNER TO vista;

-- vista_terms_v$document

CREATE TABLE vista_terms_v$document
(
	id			BIGINT			NOT NULL,
	owner			BIGINT,
	value			BIGINT,
	seq			INT
);

CREATE INDEX vista_terms_v$document_owner_idx ON vista_terms_v$document USING btree (owner);
ALTER TABLE vista_terms_v$document ADD CONSTRAINT vista_terms_v$document_pk PRIMARY KEY (id);
ALTER TABLE vista_terms_v$document ADD CONSTRAINT vista_terms_v$document_owner_fk FOREIGN KEY (owner) REFERENCES vista_terms_v (id);
ALTER TABLE vista_terms_v$document ADD CONSTRAINT vista_terms_v$document_value_fk FOREIGN KEY (value) REFERENCES legal_document (id);
ALTER TABLE vista_terms_v$document OWNER TO vista;

/** Modified tables **/

ALTER TABLE admin_pmc_payment_type_info 
	ADD COLUMN interac_payment_pad_fee NUMERIC(18,2),
	ADD COLUMN interac_payment_pad_payment_available BOOLEAN,
	DROP COLUMN cc_fee,
	DROP COLUMN cc_payment_available;

/** Case-sensitive index on pad_debit_record **/

CREATE INDEX pad_debit_record_transaction_id_idx ON pad_debit_record USING btree (transaction_id);

/** Case-insensitive indexes on existing tables **/

DROP INDEX admin_pmc_dns_name_dns_name_idx;
CREATE INDEX admin_pmc_dns_name_dns_name_idx ON _admin_.admin_pmc (LOWER(dns_name));

DROP INDEX admin_pmc_namespace_idx;
CREATE INDEX admin_pmc_namespace_idx ON _admin_.admin_pmc (LOWER(namespace));

DROP INDEX _admin_.admin_pmc_dns_name_dns_name_idx;
CREATE INDEX admin_pmc_dns_name_dns_name_idx ON _admin_.admin_pmc_dns_name (LOWER(dns_name));

DROP INDEX _admin_.admin_reserved_pmc_names_dns_name_idx;
CREATE INDEX admin_reserved_pmc_names_dns_name_idx ON _admin_.admin_reserved_pmc_names (LOWER(dns_name));

-- Insert new dummy record into newly created tables, to be edited later

INSERT INTO legal_document (id,locale,content) VALUES (nextval('public.legal_document_seq'),'en','There could be something meaningful here...');
INSERT INTO vista_terms (id) VALUES (nextval('public.vista_terms_seq'));
INSERT INTO vista_terms_v (id,version_number,from_date,holder) 
(SELECT nextval('public.vista_terms_v_seq') AS id, 1, current_date AS from_date, id AS holder FROM vista_terms);
INSERT INTO vista_terms_v$document (id,owner,value,seq)
(SELECT nextval('public.vista_terms_v$document_seq') AS id,a.id AS owner,b.id AS value, 0 AS seq
FROM vista_terms a JOIN legal_document b ON (a.id = b.id));

-- Triggers (app triggers, not db triggers) - simple delete

DELETE FROM scheduler_run_data WHERE id IN (SELECT id FROM scheduler_run WHERE status = 'Sleeping');


/**
***	-------------------------------------------------------------------------------------
***		Customer schemas
***	-------------------------------------------------------------------------------------
**/

-- Since there are multiple schemas, a stored procedure is appropriate

CREATE OR REPLACE FUNCTION _dba_.migration_aug_28_2012() RETURNS VOID AS
$$
DECLARE
	v_schema_name		VARCHAR(64);
	v_void			VARCHAR(1);
BEGIN
	FOR v_schema_name IN 
	SELECT LOWER(namespace) FROM _admin_.admin_pmc WHERE status IN ('Active','Suspended')
	LOOP
		-- Alter table aggregated_transfer
		-- Column 'previous_balance' does not exist for pangroup, starlight or woodbineave
		-- but shows up for pmc's created on or after Aug. 20, 2012 

		IF NOT EXISTS (	SELECT 'x' FROM information_schema.columns WHERE table_schema = v_schema_name 
				AND table_name = 'aggregated_transfer' AND column_name = 'previous_balance')		
		THEN
			EXECUTE 'ALTER TABLE '||v_schema_name||'.aggregated_transfer ADD COLUMN previous_balance NUMERIC(18,2)';

		END IF;

		-- Customer table
		
		EXECUTE 'ALTER TABLE '||v_schema_name||'.customer ALTER COLUMN customer_id TYPE VARCHAR(14)';		

		-- Create table customer_accepted_terms

		EXECUTE 'CREATE TABLE '||v_schema_name||'.customer_accepted_terms '||
		'(	id		BIGINT		NOT NULL,'||
		'	customer	BIGINT,'||
		'	vista_terms	BIGINT)';

		EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_accepted_terms ADD CONSTRAINT customer_accepted_terms_pk PRIMARY KEY (id)';
		EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_accepted_terms ADD CONSTRAINT customer_accepted_terms_customer_fk '||
		'FOREIGN KEY (customer) REFERENCES '||v_schema_name||'.customer(id)';
		EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_accepted_terms OWNER TO vista';

		
		-- Alter table floorplan
		
		EXECUTE 'ALTER TABLE '||v_schema_name||'.floorplan ADD COLUMN area DOUBLE PRECISION, ADD COLUMN area_units VARCHAR(50)';

		-- Employee table - a bit more fun

		EXECUTE 'ALTER TABLE '||v_schema_name||'.employee ADD COLUMN employee_id VARCHAR(14)';

		EXECUTE 'UPDATE '||v_schema_name||'.employee SET employee_id = a.rownum '||
		'FROM (SELECT id, row_number() OVER (ORDER BY id) AS rownum FROM '||v_schema_name||'.employee) a '||
		'WHERE '||v_schema_name||'.employee.id = a.id ';	

		EXECUTE 'INSERT INTO '||v_schema_name||'.id_assignment_sequence (id,target,number) '||
		'(SELECT nextval(''public.id_assignment_sequence_seq''),''employee'',MAX(employee_id)::bigint AS number FROM '||
		v_schema_name||'.employee)';

		EXECUTE 'INSERT INTO '||v_schema_name||'.id_assignment_item (id,target,tp) VALUES '||
		'(nextval(''public.id_assignment_item_seq''),''employee'',''generatedNumber'') ';
				

		-- guarantor table

		EXECUTE 'ALTER TABLE '||v_schema_name||'.guarantor ALTER COLUMN participant_id TYPE VARCHAR(14)';


		-- lead table

		EXECUTE 'ALTER TABLE '||v_schema_name||'.lead ALTER COLUMN lead_id TYPE VARCHAR(14)';	


		-- lease table

		EXECUTE 'ALTER TABLE '||v_schema_name||'.lease ALTER COLUMN lease_id TYPE VARCHAR(14)';	

		
		-- master_online_application

		EXECUTE 'ALTER TABLE '||v_schema_name||'.master_online_application ALTER COLUMN online_application_id TYPE VARCHAR(14)';		

		
		-- payment_payment_details - even more fun
		
		EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_payment_details ALTER COLUMN number_refference TYPE VARCHAR(4)';
		EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_payment_details RENAME COLUMN number_refference TO card_reference';
		EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_payment_details ALTER COLUMN account_no TYPE VARCHAR(12)';
		EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_payment_details RENAME COLUMN account_no TO account_no_number';
		EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_payment_details ADD COLUMN account_no_reference VARCHAR(4),'||
										' ADD COLUMN account_no VARCHAR(500)';

		EXECUTE 'UPDATE '||v_schema_name||'.payment_payment_details SET account_no_reference = substring(account_no_number,length(account_no_number) - 3)';

		-- tenant table

		EXECUTE 'ALTER TABLE '||v_schema_name||'.tenant ALTER COLUMN participant_id TYPE VARCHAR(14)';

		-- index on billing_arrears_snapshot

		-- EXECUTE 'CREATE INDEX billing_arrears_snapshot_building_idx ON '||v_schema_name||'.billing_arrears_snapshot USING btree (building)';

		-- unique constraints 	
		
		-- for crm_user and customer_user indexes already exist, should be dropped first

		IF EXISTS (SELECT 'x' FROM pg_class a JOIN pg_namespace b ON (a.relnamespace = b.oid)
				WHERE b.nspname = v_schema_name AND a.relname = 'crm_user_email_idx' 
				AND a.relkind = 'i')
		THEN 
			
			EXECUTE 'DROP INDEX '||v_schema_name||'.crm_user_email_idx';
			
		END IF;
		
		EXECUTE 'CREATE INDEX crm_user_email_idx ON '||v_schema_name||'.crm_user (LOWER(email))';		
	
		EXECUTE 'CREATE INDEX customer_customer_id_idx ON '||v_schema_name||'.customer (LOWER(customer_id))';

		IF EXISTS (SELECT 'x' FROM pg_class a JOIN pg_namespace b ON (a.relnamespace = b.oid)
				WHERE b.nspname = v_schema_name AND a.relname = 'customer_user_email_idx' 
				AND a.relkind = 'i')
		THEN 
			
			EXECUTE 'DROP INDEX '||v_schema_name||'.customer_user_email_idx';
			
		END IF;

		EXECUTE 'CREATE INDEX customer_user_email_idx ON '||v_schema_name||'.customer_user (LOWER(email))';

		EXECUTE 'CREATE INDEX employee_employee_id_idx ON '||v_schema_name||'.employee (LOWER(employee_id))';

		EXECUTE 'CREATE INDEX lead_lead_id_idx ON '||v_schema_name||'.lead (LOWER(lead_id))';

		EXECUTE 'CREATE INDEX lease_lease_id_idx ON '||v_schema_name||'.lease (LOWER(lease_id))';

		EXECUTE 'CREATE INDEX master_online_application_online_application_id_idx ON '||v_schema_name||'.master_online_application (LOWER(online_application_id))';

		/**
		***	For  PMCs created after Aug. 17, 2012 some ar all foreign key constraints might not be created
		***	If it is indeed the case, copy foreign key constraints from pangroup schema
		***/

		IF EXISTS (SELECT 'x' FROM _admin_.admin_pmc 
				WHERE	namespace = v_schema_name 
				AND 	created > '2012-08-17 11:21:00')
		THEN
			SELECT * INTO v_void FROM _dba_.clone_schema_tables_fk('pangroup',v_schema_name)  AS a;
		END IF; 

	END LOOP;
END;
$$
LANGUAGE PLPGSQL VOLATILE;

-- Actual update of crm schemas
SELECT _dba_.migration_aug_28_2012();

DROP FUNCTION _dba_.migration_aug_28_2012();

COMMIT;

			
 




