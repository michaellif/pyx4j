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

-- pmc_stats - basic per-pmc statistics

CREATE OR REPLACE VIEW _dba_.pmc_stats AS
(       SELECT  ap.name,a.pmc AS namespace,
                TO_CHAR(ap.created,'DD-MON-YYYY') AS created,
                a.row_count AS buildings,
                b.row_count AS units,
                c.row_count AS leases,
                d.row_count AS payment_records,
                e.row_count AS customers_accepted_terms,
                f.row_count AS customer_users,
                g.row_count AS insurance_certificates
        FROM    _admin_.admin_pmc ap
        JOIN    (SELECT * FROM _dba_.count_rows_all_pmc('building')) a ON (ap.namespace = a.pmc)
        JOIN    (SELECT * FROM _dba_.count_rows_all_pmc('apt_unit')) b ON (a.pmc = b.pmc)
        JOIN    (SELECT * FROM _dba_.count_rows_all_pmc('lease')) c ON (a.pmc = c.pmc)
        JOIN    (SELECT * FROM _dba_.count_rows_all_pmc('payment_record')) d ON (a.pmc = d.pmc)
        JOIN    (SELECT * FROM _dba_.count_rows_all_pmc('customer_accepted_terms')) e ON (a.pmc = e.pmc)
        JOIN    (SELECT * FROM _dba_.count_rows_all_pmc('customer_user')) f ON (a.pmc = f.pmc)
        JOIN    (SELECT * FROM _dba_.count_rows_all_pmc('insurance_certificate')) g ON (a.pmc = g.pmc)
        ORDER BY ap.id
);
        
