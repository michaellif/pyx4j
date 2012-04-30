CREATE OR REPLACE FUNCTION _dba_.clone_table_constraints(v_source_schema TEXT,v_target_schema TEXT) RETURNS VOID AS 
$$
DECLARE 
v_table_name	TEXT;
v_conname	TEXT;
v_col_name 	TEXT;
v_ref_table_name	TEXT;
v_ref_col_name		TEXT;
BEGIN
FOR v_conname,v_table_name,v_ref_table_name,v_col_name,v_ref_col_name IN
SELECT 	a.conname,b.relname,c.relname,d.attname,e.attname
FROM 	pg_constraint a 
	JOIN pg_class b ON (a.conrelid = b.oid)
	JOIN pg_class c ON (a.confrelid = c.oid)
	JOIN pg_attribute d ON (a.conrelid = d.attrelid AND array_to_string(a.conkey,' ')::integer = d.attnum)
	JOIN pg_attribute e ON (a.confrelid = e.attrelid AND array_to_string(a.confkey,' ')::integer = e.attnum)
	JOIN pg_namespace f ON (a.connamespace = f.oid)
WHERE	a.contype = 'f'
AND	f.nspname = v_source_schema
LOOP
	EXECUTE 'ALTER TABLE '||v_target_schema||'.'||v_table_name||' ADD CONSTRAINT '||v_conname||
	' FOREIGN KEY ('||v_col_name||') REFERENCES '||v_target_schema||'.'||v_ref_table_name||' ('||v_ref_col_name||')';
END LOOP;


END;
$$
LANGUAGE plpgsql VOLATILE;
