CREATE OR REPLACE FUNCTION _dba_.pg_schema_size(text) RETURNS bigint AS $$
SELECT 	sum(pg_relation_size(schemaname||'.'||tablename))::bigint
FROM 	pg_tables
WHERE 	schemaname = $1;
$$
LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _dba_.table_depends_on(text,text) RETURNS name[] AS
$$
SELECT array(SELECT a.relname FROM pg_class a JOIN pg_constraint b ON (a.oid = b.confrelid)
			JOIN pg_class c ON (c.oid = b.conrelid)
			JOIN pg_namespace d ON (d.oid = b.connamespace)
			WHERE d.nspname = $1
			AND c.relname = $2);
$$
LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _dba_.table_dependants(text,text) RETURNS name[] AS
$$
SELECT array(SELECT a.relname FROM pg_class a JOIN pg_constraint b ON (a.oid = b.conrelid)
			JOIN pg_class c ON (c.oid = b.confrelid)
			JOIN pg_namespace d ON (d.oid = b.connamespace)
			WHERE d.nspname = $1
			AND c.relname = $2);
$$
LANGUAGE SQL;

CREATE TYPE schema_depndencies_type AS
(
	schema_name			name,
	table_name			name,
	table_depends_on 	name[],
	table_dependants 	name[]
);

CREATE OR REPLACE FUNCTION _dba_.schema_dependencies(text) RETURNS setof schema_depndencies_type AS
$$
	SELECT 	a.nspname,b.relname,_dba_.table_depends_on(a.nspname,b.relname),
			_dba_.table_dependants(a.nspname,b.relname)
	FROM 	pg_namespace a JOIN pg_class b ON (b.relnamespace = a.oid)
	WHERE 	a.nspname = $1
	AND 	b.relkind = 'r'
	ORDER BY 2;
$$
LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _dba_.clone_schema_tables(v_original TEXT, v_replica TEXT) RETURNS void AS
$$
DECLARE
	v_table_name 	VARCHAR(64);
BEGIN
	-- Check if the original schema exists
	IF NOT EXISTS (	SELECT 'x' FROM pg_namespace
					WHERE nspname = v_original)
	THEN
		RAISE EXCEPTION 'Schema % does not exist!', v_original;
	END IF;

	-- If replica schema already exists, let user drop it himself
	IF EXISTS (	SELECT 'x' FROM pg_namespace
				WHERE nspname = v_replica)
	THEN
		RAISE NOTICE 'Schema % already exists!', v_replica;
		RAISE EXCEPTION 'Please drop it first with DROP SCHEMA % CASCADE', v_replica;
		-- EXECUTE 'DROP SCHEMA '||v_replica||' CASCADE';
	END IF;

	EXECUTE 'CREATE SCHEMA '||v_replica;

	FOR v_table_name IN
	SELECT table_name FROM information_schema.tables WHERE table_schema = v_original
	LOOP
		EXECUTE 'CREATE TABLE '||v_replica||'.'||v_table_name||' (LIKE '||v_original||'.'||v_table_name||
		' INCLUDING CONSTRAINTS INCLUDING INDEXES INCLUDING DEFAULTS)';
		EXECUTE 'INSERT INTO '||v_replica||'.'||v_table_name||' (SELECT * FROM '||v_original||'.'||v_table_name||')';
	END LOOP;

END;
$$
LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION _dba_.clone_schema_tables_fk(v_source_schema TEXT,v_target_schema TEXT) RETURNS VOID AS
$$
DECLARE

v_table_name    		TEXT;
v_conname       		TEXT;
v_col_name      		TEXT;
v_ref_table_name        TEXT;
v_ref_col_name          TEXT;
v_concolumns			TEXT[];
v_confcolumns			TEXT[];

BEGIN

/**
***	Most of the tables have a plain-vanilla 1-to-1 foreign keys
***	The following code is intended for those simple cases
**/

FOR v_conname,v_table_name,v_ref_table_name,v_col_name,v_ref_col_name IN
SELECT  a.conname,b.relname,c.relname,d.attname,e.attname
FROM    pg_constraint a
        JOIN pg_class b ON (a.conrelid = b.oid)
        JOIN pg_class c ON (a.confrelid = c.oid)
        JOIN pg_attribute d ON (a.conrelid = d.attrelid AND array_to_string(a.conkey,' ')::integer = d.attnum)
        JOIN pg_attribute e ON (a.confrelid = e.attrelid AND array_to_string(a.confkey,' ')::integer = e.attnum)
        JOIN pg_namespace f ON (a.connamespace = f.oid)
WHERE   a.contype = 'f'
AND     f.nspname = v_source_schema
AND		(array_length(a.conkey,1) = 1 AND array_length(a.confkey,1) = 1)
LOOP
        -- In some cases application does not create fk constraint at all,
	-- or creates just a few
	IF NOT EXISTS (SELECT 'x' FROM information_schema.constraint_column_usage
			WHERE 	table_schema = v_target_schema
			AND 	constraint_schema = v_target_schema
			AND 	table_name = v_ref_table_name
			AND 	column_name = v_ref_col_name
			AND	constraint_name = v_conname)
	THEN
		EXECUTE 'ALTER TABLE '||v_target_schema||'.'||v_table_name||' ADD CONSTRAINT '||v_conname||
        	' FOREIGN KEY ('||v_col_name||') REFERENCES '||v_target_schema||'.'||v_ref_table_name||' ('||v_ref_col_name||')';
	END IF;
END LOOP;

/**
***	More complicated case - foreign key is based on more than 1 column
**/

FOR v_conname,v_table_name,v_ref_table_name IN
SELECT  a.conname,b.relname,c.relname
FROM    pg_constraint a
        JOIN pg_class b ON (a.conrelid = b.oid)
        JOIN pg_class c ON (a.confrelid = c.oid)
        JOIN pg_namespace d ON (a.connamespace = d.oid)
WHERE   a.contype = 'f'
AND     d.nspname = v_source_schema
AND		(array_length(a.conkey,1) > 1 OR array_length(a.confkey,1) > 1)
LOOP
          	-- Constrained columns
          	SELECT INTO v_concolumns
        	ARRAY(SELECT b.attname FROM
        			(SELECT a.conrelid, unnest(a.conkey) AS conkey
        			 FROM pg_constraint a
        			JOIN pg_class b ON (a.conrelid = b.oid)
        			JOIN pg_namespace c ON (a.connamespace = c.oid)
        			WHERE   a.contype = 'f'
					AND 	b.relname = v_table_name
					AND     c.nspname = v_source_schema) a
				JOIN pg_attribute b ON (a.conrelid = b.attrelid AND a.conkey = b.attnum)
			ORDER BY a.conkey);

        	-- Referenced columns
          	SELECT INTO v_confcolumns
        	ARRAY(SELECT b.attname FROM
        			(SELECT a.confrelid,unnest(a.confkey) AS confkey
        			 FROM pg_constraint a
        			 JOIN pg_class b ON (a.confrelid = b.oid)
        			 JOIN pg_namespace c ON (a.connamespace = c.oid)
        			 JOIN pg_class d ON (a.conrelid = d.oid)
					 WHERE   a.contype = 'f'
					 AND 	b.relname = v_ref_table_name
					 AND    c.nspname = v_source_schema
					 AND	d.relname = v_table_name) a
					JOIN pg_attribute b ON (a.confrelid = b.attrelid AND a.confkey = b.attnum)
				ORDER BY a.confkey);
        	-- In some cases application does not create fk constraint at all,
		-- or creates just a few
		-- Probably not a problem in this case, since muti-column constraints
		-- are created only in public schema (at least for now)
		IF NOT EXISTS (SELECT 'x' FROM information_schema.constraint_column_usage
				WHERE 	table_schema = v_target_schema
				AND 	constraint_schema = v_target_schema
				AND 	table_name = v_ref_table_name
				AND	constraint_name = v_conname)
		THEN
        		EXECUTE 'ALTER TABLE '||v_target_schema||'.'||v_table_name||' ADD CONSTRAINT '||v_conname||
        		' FOREIGN KEY ('||array_to_string(v_concolumns,',')||') REFERENCES '
        		||v_target_schema||'.'||v_ref_table_name||' ('||array_to_string(v_confcolumns,',')||')';
		END IF;
END LOOP;

END;
$$
LANGUAGE plpgsql VOLATILE;


/**
***	---------------------------------------------------------------------------------------------------
***
***	Variation of clone_schema_tables_fk, the difference being that instead of default
***	ON DELETE RESTRICT foreign key constraints, it creates ON DELETE CASCADE constraints.
***	Very useful for getting rid of large chunks of data quickly.
***
***	----------------------------------------------------------------------------------------------------
**/

CREATE OR REPLACE FUNCTION _dba_.clone_schema_tables_fk_delete_cascade(v_source_schema TEXT,v_target_schema TEXT) RETURNS VOID AS
$$
DECLARE
	v_table_name    		TEXT;
	v_conname       		TEXT;
	v_col_name      		TEXT;
	v_ref_table_name        	TEXT;
	v_ref_col_name          	TEXT;
	v_concolumns			TEXT[];
	v_confcolumns			TEXT[];
BEGIN
/**
***	Most of the tables have a plain-vanilla 1-to-1 foreign keys
***	The following code is intendet for those simple cases
**/
FOR 	v_conname,v_table_name,v_ref_table_name,v_col_name,v_ref_col_name IN
	SELECT  a.conname,b.relname,c.relname,d.attname,e.attname
	FROM    pg_constraint a
        JOIN 	pg_class b ON (a.conrelid = b.oid)
        JOIN 	pg_class c ON (a.confrelid = c.oid)
        JOIN 	pg_attribute d ON (a.conrelid = d.attrelid AND array_to_string(a.conkey,' ')::integer = d.attnum)
        JOIN 	pg_attribute e ON (a.confrelid = e.attrelid AND array_to_string(a.confkey,' ')::integer = e.attnum)
        JOIN 	pg_namespace f ON (a.connamespace = f.oid)
	WHERE   a.contype = 'f'
	AND     f.nspname = v_source_schema
	AND	(array_length(a.conkey,1) = 1 AND array_length(a.confkey,1) = 1)
LOOP
        EXECUTE 'ALTER TABLE '||v_target_schema||'.'||v_table_name||' ADD CONSTRAINT '||v_conname||
        ' FOREIGN KEY ('||v_col_name||') REFERENCES '||v_target_schema||'.'||v_ref_table_name||' ('||v_ref_col_name||') '||
	'ON DELETE CASCADE';
END LOOP;
/**
***	More complicated case - foreign key is based on more than 1 column
**/

FOR 	v_conname,v_table_name,v_ref_table_name IN
	SELECT  a.conname,b.relname,c.relname
	FROM    pg_constraint a
        JOIN 	pg_class b ON (a.conrelid = b.oid)
        JOIN 	pg_class c ON (a.confrelid = c.oid)
        JOIN 	pg_namespace d ON (a.connamespace = d.oid)
	WHERE   a.contype = 'f'
	AND     d.nspname = v_source_schema
	AND	(array_length(a.conkey,1) > 1 OR array_length(a.confkey,1) > 1)
LOOP
          	-- Constrained columns
          	SELECT INTO v_concolumns
        	ARRAY(SELECT b.attname FROM
        			(SELECT a.conrelid, unnest(a.conkey) AS conkey
        			 FROM pg_constraint a
        			JOIN pg_class b ON (a.conrelid = b.oid)
        			JOIN pg_namespace c ON (a.connamespace = c.oid)
        			WHERE   a.contype = 'f'
					AND 	b.relname = v_table_name
					AND     c.nspname = v_source_schema) a
				JOIN pg_attribute b ON (a.conrelid = b.attrelid AND a.conkey = b.attnum)
			ORDER BY a.conkey);

        	-- Referenced columns
          	SELECT INTO v_confcolumns
        	ARRAY(SELECT b.attname FROM
        			(SELECT a.confrelid,unnest(a.confkey) AS confkey
        			 FROM pg_constraint a
        			 JOIN pg_class b ON (a.confrelid = b.oid)
        			 JOIN pg_namespace c ON (a.connamespace = c.oid)
        			 JOIN pg_class d ON (a.conrelid = d.oid)
					 WHERE   a.contype = 'f'
					 AND 	b.relname = v_ref_table_name
					 AND    c.nspname = v_source_schema
					 AND	d.relname = v_table_name) a
					JOIN pg_attribute b ON (a.confrelid = b.attrelid AND a.confkey = b.attnum)
				ORDER BY a.confkey);

        	EXECUTE 'ALTER TABLE '||v_target_schema||'.'||v_table_name||' ADD CONSTRAINT '||v_conname||
        	' FOREIGN KEY ('||array_to_string(v_concolumns,',')||') REFERENCES '
        	||v_target_schema||'.'||v_ref_table_name||' ('||array_to_string(v_confcolumns,',')||') '||
		'ON DELETE CASCADE';
END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION _dba_.clone_schema_sequences(v_source_schema TEXT,v_target_schema TEXT) RETURNS VOID
AS
$$
DECLARE
v_seq_name	VARCHAR(64);
BEGIN
	FOR v_seq_name IN
	SELECT a.relname FROM pg_class a JOIN pg_namespace b ON (a.relnamespace = b.oid)
	WHERE	a.relkind = 'S'
	AND		b.nspname = v_source_schema
	EXCEPT
	SELECT a.relname FROM pg_class a JOIN pg_namespace b ON (a.relnamespace = b.oid)
	WHERE	a.relkind = 'S'
	AND		b.nspname = v_target_schema
	LOOP
		EXECUTE 'CREATE SEQUENCE '||v_target_schema||'.'||v_seq_name||' START WITH 1 INCREMENT BY 1';
	END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION _dba_.check_table_altered (v_old_schema_name TEXT, v_new_schema_name TEXT, v_table_name TEXT) RETURNS BOOLEAN AS
$$
BEGIN
IF EXISTS (WITH old_stuff AS (SELECT    table_name,column_name,
                                        CASE WHEN data_type = 'character' THEN 'char('||character_maximum_length||')'
                                        WHEN data_type = 'character varying' THEN 'varchar('||character_maximum_length||')'
                                        WHEN data_type = 'numeric' THEN 'numeric('||numeric_precision||','||numeric_scale||')'
                                        ELSE  data_type END as data_type,
                                        is_nullable::boolean as is_nullable
                                FROM            information_schema.columns
                                WHERE           table_name = v_table_name
                                AND             table_schema = v_old_schema_name),
                new_stuff AS (SELECT    table_name,column_name,
                                        CASE WHEN data_type = 'character' THEN 'char('||character_maximum_length||')'
                                        WHEN data_type = 'character varying' THEN 'varchar('||character_maximum_length||')'
                                        WHEN data_type = 'numeric' THEN 'numeric('||numeric_precision||','||numeric_scale||')'
                                        ELSE  data_type END as data_type,
                                        is_nullable::boolean as is_nullable
                                FROM            information_schema.columns
                                WHERE           table_name = v_table_name
                                AND             table_schema = v_new_schema_name)
                (SELECT * FROM old_stuff
                 EXCEPT
                 SELECT * FROM new_stuff)
                UNION
                (SELECT * FROM new_stuff
                 EXCEPT
                 SELECT * FROM new_stuff))
THEN
        RETURN(TRUE);
ELSE
        RETURN(FALSE);
END IF;
END;
$$
LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION _dba_.reset_schema_sequences(v_schema_name TEXT) RETURNS VOID AS
$$
DECLARE
v_table_name    TEXT;
v_rowcount      INTEGER;
v_seq_name      TEXT;
v_query         TEXT;
BEGIN

FOR v_table_name IN
SELECT a.relname FROM pg_class a JOIN pg_namespace b ON (a.relnamespace = b.oid)
WHERE a.relkind = 'r' AND b.nspname = v_schema_name
LOOP
        SELECT v_table_name||'_seq' INTO v_seq_name;
        IF EXISTS (SELECT 'X' FROM pg_class a JOIN pg_namespace b ON (a.relnamespace = b.oid) WHERE a.relkind = 'S' AND a.relname = v_seq_name AND b.nspname = 'public')
        THEN
                EXECUTE 'SELECT COALESCE(MAX(id),0)+1 FROM '||v_schema_name||'.'||v_table_name INTO v_rowcount;
                EXECUTE 'ALTER SEQUENCE public.'||v_seq_name||' RESTART WITH '||v_rowcount;
        END IF;
END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION _dba_.table_diff (v_source_schema text, v_target_schema text, v_table text)
RETURNS TABLE (table_name VARCHAR(64),column_name VARCHAR(64),data_type	VARCHAR(64),is_nullable	BOOLEAN,schema_version VARCHAR(12))
AS
$$
(SELECT a.*, 'NEW' as schema_version
FROM
		(SELECT 	table_name,column_name,
					CASE WHEN data_type = 'character' THEN 'char('||character_maximum_length||')'
					WHEN data_type = 'character varying' THEN 'varchar('||character_maximum_length||')'
					WHEN data_type = 'numeric' THEN 'numeric('||numeric_precision||','||numeric_scale||')'
					ELSE  data_type END as data_type,
					is_nullable::boolean as is_nullable
		FROM 		information_schema.columns
		WHERE 		table_schema = $2
		AND			table_name = $3
		EXCEPT
		SELECT 	table_name,column_name,
				CASE WHEN data_type = 'character' THEN 'char('||character_maximum_length||')'
				WHEN data_type = 'character varying' THEN 'varchar('||character_maximum_length||')'
				WHEN data_type = 'numeric' THEN 'numeric('||numeric_precision||','||numeric_scale||')'
				ELSE  data_type END as data_type,
				is_nullable::boolean as is_nullable
		FROM 	information_schema.columns
		WHERE 	table_schema = $1
		AND		table_name = $3) as a)
	UNION
	(SELECT a.*, 'OLD' as schema_version
	FROM
		(SELECT 	table_name,column_name,/*ordinal_position,*/
				CASE WHEN data_type = 'character' THEN 'char('||character_maximum_length||')'
				WHEN data_type = 'character varying' THEN 'varchar('||character_maximum_length||')'
				WHEN data_type = 'numeric' THEN 'numeric('||numeric_precision||','||numeric_scale||')'
				ELSE  data_type END as data_type,
				is_nullable::boolean as is_nullable
		FROM 	information_schema.columns
		WHERE 	table_schema = $1
		AND		table_name = $3
		EXCEPT
		SELECT 	table_name,column_name,
				CASE WHEN data_type = 'character' THEN 'char('||character_maximum_length||')'
				WHEN data_type = 'character varying' THEN 'varchar('||character_maximum_length||')'
				WHEN data_type = 'numeric' THEN 'numeric('||numeric_precision||','||numeric_scale||')'
				ELSE  data_type END as data_type,
				is_nullable::boolean as is_nullable
		FROM 	information_schema.columns
		WHERE 	table_schema = $2
		AND		table_name = $3) as a);

$$
LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _dba_.copy_schema_data(v_source_schema TEXT, v_target_schema TEXT) RETURNS VOID AS
$$
DECLARE
	v_table_name 	VARCHAR(64);
	v_column_list	TEXT;
BEGIN
	-- Check that there are no schema version incompatibilities
	IF EXISTS (SELECT a.relname FROM pg_class a JOIN pg_namespace b ON (a.relnamespace = b.oid)
				WHERE b.nspname = v_source_schema
				AND a.relkind = 'r'
				AND _dba_.check_table_altered(v_source_schema,v_target_schema,a.relname) = TRUE
				AND a.reltuples > 0 )
	THEN
		RAISE EXCEPTION 'Modified tables detected - cannot continue';
	END IF;

	FOR v_table_name  IN
	SELECT a.relname FROM pg_class a JOIN pg_namespace b ON (a.relnamespace = b.oid)
				WHERE b.nspname = v_source_schema
				AND a.relkind = 'r'
				AND _dba_.check_table_altered(v_source_schema,v_target_schema,a.relname) = FALSE
				AND a.reltuples > 0
	LOOP
		SELECT INTO v_column_list array_to_string(ARRAY(SELECT column_name::text FROM information_schema.columns
		WHERE table_name = v_table_name AND table_schema = v_source_schema ORDER BY ordinal_position),',');

		EXECUTE 'DELETE FROM '||v_target_schema||'.'||v_table_name;
		EXECUTE 'INSERT INTO '||v_target_schema||'.'||v_table_name||' ('||v_column_list||') (SELECT '||
		v_column_list||' FROM '||v_source_schema||'.'||v_table_name||')';

	END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION _dba_.grant_schema_tables_privs(v_schema_name TEXT, v_user TEXT,
v_privs TEXT DEFAULT 'SELECT,INSERT,UPDATE,DELETE') RETURNS VOID AS
$$
DECLARE
	v_table		VARCHAR(64);
	v_stmt 	TEXT;
BEGIN
	FOR v_table IN
	SELECT relname FROM pg_class a JOIN pg_namespace b ON (a.relnamespace = b.oid)
	WHERE b.nspname = v_schema_name
	AND a.relkind = 'r'
	LOOP
		v_stmt := 'GRANT '||v_privs||' ON TABLE '||v_schema_name||'.'||v_table||' TO '||v_user;
		EXECUTE v_stmt;

	END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION _dba_.change_schema_tables_ownership(v_schema_name TEXT, v_user TEXT) RETURNS VOID AS
$$
DECLARE
        v_table         VARCHAR(64);
BEGIN
        FOR v_table IN
        SELECT relname FROM pg_class a JOIN pg_namespace b ON (a.relnamespace = b.oid)
        WHERE b.nspname = v_schema_name
        AND a.relkind = 'r'
        LOOP
                EXECUTE 'ALTER TABLE '||v_schema_name||'.'||v_table||' OWNER TO '||v_user;

	END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;

/**
***	==================================================================
***
***		Schema comparison functions
***
***	==================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.compare_schema_tables(text,text)
RETURNS TABLE(table_name VARCHAR(64),column_name VARCHAR(64),data_type VARCHAR(64),is_nullable BOOLEAN,schema_version VARCHAR(64)) AS
$$
	SELECT a.*, $2 as schema_version
	FROM
		(SELECT 	table_name,column_name,
				CASE WHEN data_type = 'character' THEN 'char('||character_maximum_length||')'
				WHEN data_type = 'character varying' THEN 'varchar('||character_maximum_length||')'
				WHEN data_type = 'numeric' THEN 'numeric('||numeric_precision||','||numeric_scale||')'
				ELSE  data_type END as data_type,
				is_nullable::boolean as is_nullable
		FROM 		information_schema.columns
		WHERE 	table_schema = $2
		EXCEPT
		SELECT 		table_name,column_name,
				CASE WHEN data_type = 'character' THEN 'char('||character_maximum_length||')'
				WHEN data_type = 'character varying' THEN 'varchar('||character_maximum_length||')'
				WHEN data_type = 'numeric' THEN 'numeric('||numeric_precision||','||numeric_scale||')'
				ELSE  data_type END as data_type,
				is_nullable::boolean as is_nullable
		FROM 		information_schema.columns
		WHERE 	table_schema = $1) as a
	UNION
	SELECT a.*, $1 as schema_version
	FROM
		(SELECT 	table_name,column_name,
				CASE WHEN data_type = 'character' THEN 'char('||character_maximum_length||')'
				WHEN data_type = 'character varying' THEN 'varchar('||character_maximum_length||')'
				WHEN data_type = 'numeric' THEN 'numeric('||numeric_precision||','||numeric_scale||')'
				ELSE  data_type END as data_type,
				is_nullable::boolean as is_nullable
		FROM 		information_schema.columns
		WHERE 	table_schema = $1
		EXCEPT
		SELECT 		table_name,column_name,
				CASE WHEN data_type = 'character' THEN 'char('||character_maximum_length||')'
				WHEN data_type = 'character varying' THEN 'varchar('||character_maximum_length||')'
				WHEN data_type = 'numeric' THEN 'numeric('||numeric_precision||','||numeric_scale||')'
				ELSE  data_type END as data_type,
				is_nullable::boolean as is_nullable
		FROM 		information_schema.columns
		WHERE 	table_schema = $2) as a;
$$
LANGUAGE SQL VOLATILE;

CREATE OR REPLACE FUNCTION _dba_.compare_schema_sequences(text,text)
RETURNS TABLE(	sequence_name 	VARCHAR(64),
		data_type 	VARCHAR(64),
		start_value 	VARCHAR(18),
		minimum_value 	VARCHAR(18),
		maximum_value 	VARCHAR(18),
		increment 	VARCHAR(18),
		cycle_option 	VARCHAR(3),
		schema_version VARCHAR(64)) AS
$$
	SELECT a.*,$2 AS schema_version
	FROM
		(SELECT 	sequence_name,data_type,start_value,minimum_value,
				maximum_value,increment,cycle_option
		 FROM 		information_schema.sequences
		 WHERE		sequence_schema = $2
		EXCEPT
		 SELECT 	sequence_name,data_type,start_value,minimum_value,
				maximum_value,increment,cycle_option
		 FROM 		information_schema.sequences
		 WHERE		sequence_schema = $1 ) AS a
	UNION
	SELECT a.*,$1 AS schema_version
	FROM
		(SELECT 	sequence_name,data_type,start_value,minimum_value,
				maximum_value,increment,cycle_option
		 FROM 		information_schema.sequences
		 WHERE		sequence_schema = $1
		EXCEPT
		 SELECT 	sequence_name,data_type,start_value,minimum_value,
				maximum_value,increment,cycle_option
		 FROM 		information_schema.sequences
		 WHERE		sequence_schema = $2 ) AS a ;

$$
LANGUAGE SQL VOLATILE;

/**
***	---------------------------------------------------------------------
***		Compares schema table constraints
***		This function works only with regular 1-to-1 constraints,
***		which is mostly the sort used in crm. Many-to-many constraints
***		used only in quartz tables in public schema.
***	----------------------------------------------------------------------
**/

CREATE OR REPLACE FUNCTION _dba_.compare_schema_constraints(text,text)
RETURNS TABLE (	constraint_name 	pg_catalog.name,
		constraint_type 	CHAR(1),
		table_name 		pg_catalog.name,
		ref_table_name		pg_catalog.name,
		column_name		pg_catalog.name,
		ref_column_name		pg_catalog.name,
		schema_version		VARCHAR(64)) AS
$$
	SELECT a.*,$2 AS schema_version
	FROM
		(SELECT 	a.conname AS constraint_name,
				a.contype::char AS constraint_type,
				b.relname AS table_name,
				c.relname AS ref_table_name,
				d.attname AS column_name,
				e.attname AS ref_column_name
		FROM    pg_constraint a
        	JOIN 	pg_class b ON (a.conrelid = b.oid)
        	JOIN 	pg_class c ON (a.confrelid = c.oid)
        	JOIN 	pg_attribute d ON (a.conrelid = d.attrelid AND array_to_string(a.conkey,' ')::integer = d.attnum)
        	JOIN 	pg_attribute e ON (a.confrelid = e.attrelid AND array_to_string(a.confkey,' ')::integer = e.attnum)
        	JOIN 	pg_namespace f ON (a.connamespace = f.oid)
		WHERE   f.nspname = $2
		AND	(array_length(a.conkey,1) = 1 AND array_length(a.confkey,1) = 1)
		EXCEPT
		SELECT 		a.conname AS constraint_name,
				a.contype::char AS constraint_type,
				b.relname AS table_name,
				c.relname AS ref_table_name,
				d.attname AS column_name,
				e.attname AS ref_column_name
		FROM    pg_constraint a
        	JOIN 	pg_class b ON (a.conrelid = b.oid)
        	JOIN 	pg_class c ON (a.confrelid = c.oid)
        	JOIN 	pg_attribute d ON (a.conrelid = d.attrelid AND array_to_string(a.conkey,' ')::integer = d.attnum)
        	JOIN 	pg_attribute e ON (a.confrelid = e.attrelid AND array_to_string(a.confkey,' ')::integer = e.attnum)
        	JOIN 	pg_namespace f ON (a.connamespace = f.oid)
		WHERE   f.nspname = $1
		AND	(array_length(a.conkey,1) = 1 AND array_length(a.confkey,1) = 1)) AS a
	UNION
	SELECT a.*,$1 AS schema_version
	FROM
		(SELECT 	a.conname AS constraint_name,
				a.contype::char AS constraint_type,
				b.relname AS table_name,
				c.relname AS ref_table_name,
				d.attname AS column_name,
				e.attname AS ref_column_name
		FROM    pg_constraint a
        	JOIN 	pg_class b ON (a.conrelid = b.oid)
        	JOIN 	pg_class c ON (a.confrelid = c.oid)
        	JOIN 	pg_attribute d ON (a.conrelid = d.attrelid AND array_to_string(a.conkey,' ')::integer = d.attnum)
        	JOIN 	pg_attribute e ON (a.confrelid = e.attrelid AND array_to_string(a.confkey,' ')::integer = e.attnum)
        	JOIN 	pg_namespace f ON (a.connamespace = f.oid)
		WHERE   f.nspname = $1
		AND	(array_length(a.conkey,1) = 1 AND array_length(a.confkey,1) = 1)
		EXCEPT
		SELECT 	a.conname AS constraint_name,
				a.contype::char AS constraint_type,
				b.relname AS table_name,
				c.relname AS ref_table_name,
				d.attname AS column_name,
				e.attname AS ref_column_name
		FROM    pg_constraint a
        	JOIN 	pg_class b ON (a.conrelid = b.oid)
        	JOIN 	pg_class c ON (a.confrelid = c.oid)
        	JOIN 	pg_attribute d ON (a.conrelid = d.attrelid AND array_to_string(a.conkey,' ')::integer = d.attnum)
        	JOIN 	pg_attribute e ON (a.confrelid = e.attrelid AND array_to_string(a.confkey,' ')::integer = e.attnum)
        	JOIN 	pg_namespace f ON (a.connamespace = f.oid)
		WHERE   f.nspname = $2
		AND	(array_length(a.conkey,1) = 1 AND array_length(a.confkey,1) = 1)) AS a;
$$
LANGUAGE SQL VOLATILE;

CREATE OR REPLACE FUNCTION _dba_.compare_schema_indexes_simple(text,text)
RETURNS TABLE (	table_name 		pg_catalog.name,
		index_name		pg_catalog.name,
		schema_version		VARCHAR(64)) AS
$$
	SELECT a.*, $2 as schema_version
	FROM
		(SELECT tablename AS table_name,
			indexname AS index_name
		FROM pg_indexes
		WHERE schemaname = $2
		EXCEPT
		SELECT tablename AS table_name,
			indexname AS index_name
		FROM pg_indexes
		WHERE schemaname = $1) AS a
	UNION
	SELECT a.*, $1 as schema_version
	FROM
		(SELECT tablename AS table_name,
			indexname AS index_name
		FROM pg_indexes
		WHERE schemaname = $1
		EXCEPT
		SELECT tablename AS table_name,
			indexname AS index_name
		FROM pg_indexes
		WHERE schemaname = $2) AS a;
$$
LANGUAGE SQL VOLATILE;


CREATE OR REPLACE FUNCTION _dba_.drop_schema_table
(v_schema_name TEXT, v_table_name TEXT, v_drop_non_empty BOOLEAN DEFAULT FALSE) RETURNS VOID
AS
$$
DECLARE
	v_constraint_name 	VARCHAR(64);
	v_con_table_name	VARCHAR(64);
	v_rowcount		    BIGINT;
BEGIN

	EXECUTE 'SELECT COUNT(*) FROM '||v_schema_name||'.'||v_table_name INTO v_rowcount;

	-- Nothing to be done if table is not empty
	-- v_drop_not_empty is true
	IF (v_rowcount != 0 AND v_drop_non_empty = FALSE)
	THEN
		RAISE NOTICE 'Table %.% has % rows',v_schema_name,v_table_name,v_rowcount;
		RAISE EXCEPTION 'Cannot drop non-empty table %.%',v_schema_name,v_table_name;
	END IF;

	FOR v_constraint_name,v_con_table_name IN
	SELECT 	a.conname, b.relname
	FROM 	pg_constraint a
	JOIN 	pg_class b ON (a.conrelid = b.oid)
	JOIN 	pg_class c ON (a.confrelid = c.oid)
	JOIN 	pg_namespace d ON (a.connamespace = d.oid)
	WHERE 	c.relname = v_table_name
	AND 	d.nspname = v_schema_name
	LOOP
		EXECUTE 'ALTER TABLE '||v_schema_name||'.'||v_con_table_name||' DROP CONSTRAINT '||v_constraint_name;
	END LOOP;

	-- Drop part - shouldn't be any problem by now

	EXECUTE 'DROP TABLE '||v_schema_name||'.'||v_table_name;
END;
$$
LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION _dba_.truncate_schema_table
(v_schema_name TEXT, v_table_name TEXT, v_drop_non_empty BOOLEAN DEFAULT FALSE) RETURNS VOID
AS
$$
DECLARE
	v_constraint_name 	VARCHAR(64);
	v_con_table_name	VARCHAR(64);
	v_rowcount		    BIGINT;
BEGIN

	EXECUTE 'SELECT COUNT(*) FROM '||v_schema_name||'.'||v_table_name INTO v_rowcount;

	-- Nothing to be done if table is not empty
	-- v_drop_not_empty is true
	IF (v_rowcount != 0 AND v_drop_non_empty = FALSE)
	THEN
		RAISE NOTICE 'Table %.% has % rows',v_schema_name,v_table_name,v_rowcount;
		RAISE EXCEPTION 'Cannot drop non-empty table %.%',v_schema_name,v_table_name;
	END IF;

	FOR v_constraint_name,v_con_table_name IN
	SELECT 	a.conname, b.relname
	FROM 	pg_constraint a
	JOIN 	pg_class b ON (a.conrelid = b.oid)
	JOIN 	pg_class c ON (a.confrelid = c.oid)
	JOIN 	pg_namespace d ON (a.connamespace = d.oid)
	WHERE 	c.relname = v_table_name
	AND 	d.nspname = v_schema_name
	LOOP
		EXECUTE 'ALTER TABLE '||v_schema_name||'.'||v_con_table_name||' DROP CONSTRAINT '||v_constraint_name;
	END LOOP;

	-- Drop part - shouldn't be any problem by now

	EXECUTE 'TRUNCATE TABLE '||v_schema_name||'.'||v_table_name;
END;
$$
LANGUAGE plpgsql VOLATILE;