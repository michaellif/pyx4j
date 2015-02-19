/**
***	    ==================================================================================================================
***
***	        @version $Revision$ ($Author$) $Date$ 
***
***         VIEWS TO SIMPLIFY DATABASE ADMINITRATION
***
***
***	    ==================================================================================================================
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
	JOIN	pg_user d ON (a.proowner = d.usesysid)
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
                e.row_count AS customer_users,
                f.row_count AS tenant_sure
        FROM    _admin_.admin_pmc ap
        JOIN    (SELECT * FROM _dba_.count_rows_all_pmc('building',ARRAY['NOT suspended'])) a ON (ap.namespace = a.pmc)
        JOIN    (SELECT * FROM _dba_.count_rows_all_pmc('apt_unit')) b ON (a.pmc = b.pmc)
        JOIN    (SELECT * FROM _dba_.count_rows_all_pmc('lease')) c ON (a.pmc = c.pmc)
        JOIN    (SELECT * FROM _dba_.count_rows_all_pmc('payment_record')) d ON (a.pmc = d.pmc)
        JOIN    (SELECT * FROM _dba_.count_rows_all_pmc('customer',ARRAY['registered_in_portal'])) e ON (a.pmc = e.pmc)
        JOIN    (SELECT * FROM _dba_.count_rows_all_pmc('insurance_policy',ARRAY['id_discriminator = ''TenantSureInsurancePolicy'' ','status = ''Active'' '])) f 
            ON (a.pmc = f.pmc)
        ORDER BY ap.id
);

-- Mail queue status

CREATE OR REPLACE VIEW _dba_.mail_queue AS
(
    SELECT  status,COUNT(id) 
    FROM    _admin_.outgoing_mail_queue 
    GROUP BY    status 
    ORDER BY    status
);

-- Empty PMC

CREATE OR REPLACE VIEW _dba_.empty_pmc AS
(
    WITH t AS ( SELECT  namespace, 
                DATE_TRUNC('day',MAX(created)) AS last_login
                FROM    _admin_.audit_record
                GROUP BY namespace) 
    SELECT  s.name, s.namespace, s.created,
            TO_CHAR(t.last_login, 'DD-MON-YYYY') AS last_login,
            s.buildings, s.units, s.leases
    FROM    t 
    JOIN    _dba_.pmc_stats s ON (t.namespace = s.namespace)
    WHERE   s.buildings = 0
    AND     s.units = 0
    ORDER BY  t.last_login
);

-- Contacts for empty PMC

CREATE OR REPLACE VIEW _dba_.empty_pmc_contacts AS 
(
    SELECT  a.name AS pmc, u.first_name, u.last_name, u.email
    FROM    _admin_.admin_pmc a 
    JOIN    _admin_.onboarding_user u ON (a.id = u.pmc)
    JOIN    _dba_.pmc_stats s ON (a.namespace = s.namespace)
    WHERE   s.buildings = 0
    AND     s.units = 0
    ORDER BY a.created

);







        
