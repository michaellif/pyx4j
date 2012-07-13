/**
***	===============================================================
***	VIEWS TO SIMPLIFY DATABASE ADMINITRATION
***	===============================================================
**/

-- dba_proc view - all procedures in _dba_ schema
CREATE OR REPLACE VIEW _dba_.dba_proc AS
(	SELECT 	a.proname AS procedure_name,
			array_to_string(a.proargnames,',') AS arguments,
			b.typname AS return_type, c.lanname AS language,
			d.usename AS owner
	FROM 	pg_proc a 
	JOIN	pg_type b ON (a.prorettype = b.oid)
	JOIN	pg_language c ON (a.prolang = c.oid)
	JOIN	pg_shadow d ON (a.proowner = d.usesysid)
	JOIN	pg_namespace e ON (a.pronamespace = e.oid)
	WHERE 	e.nspname = '_dba_'
	ORDER BY 1
);