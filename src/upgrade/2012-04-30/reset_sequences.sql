CREATE OR REPLACE FUNCTION _dba_.reset_seq() RETURNS VOID AS
$$
DECLARE
v_table_name 	TEXT;
v_rowcount	INTEGER;
v_seq_name	TEXT;
v_query		TEXT;
BEGIN

FOR v_table_name IN
SELECT a.relname FROM pg_class a JOIN pg_namespace b ON (a.relnamespace = b.oid) WHERE a.relkind = 'r' AND b.nspname = '_admin_'
LOOP
	SELECT v_table_name||'_seq' INTO v_seq_name;
	IF EXISTS (SELECT 'X' FROM pg_class a JOIN pg_namespace b ON (a.relnamespace = b.oid) WHERE a.relkind = 'S' AND a.relname = v_seq_name AND b.nspname = 'public')
	THEN
		EXECUTE 'SELECT COALESCE(MAX(id),0)+1 FROM _admin_.'||v_table_name INTO v_rowcount;
		EXECUTE 'ALTER SEQUENCE public.'||v_seq_name||' RESTART WITH '||v_rowcount;
	END IF;
END LOOP;

FOR v_table_name IN
SELECT a.relname FROM pg_class a JOIN pg_namespace b ON (a.relnamespace = b.oid) WHERE a.relkind = 'r' AND b.nspname = 'pangroup'
LOOP
        SELECT v_table_name||'_seq' INTO v_seq_name;
        IF EXISTS (SELECT 'X' FROM pg_class a JOIN pg_namespace b ON (a.relnamespace = b.oid) WHERE a.relkind = 'S' AND a.relname = v_seq_name AND b.nspname = 'public')
        THEN
                EXECUTE 'SELECT COALESCE(MAX(id),0)+1 FROM pangroup.'||v_table_name INTO v_rowcount;
		EXECUTE 'ALTER SEQUENCE public.'||v_seq_name||' RESTART WITH '||v_rowcount;
        END IF;
END LOOP;

END;
$$
LANGUAGE plpgsql VOLATILE;
