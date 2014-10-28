/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Tenants without renters insurance - post-1.1.4 version
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.get_no_insurance(v_schema_name TEXT) 
RETURNS SETOF RECORD
/*
RETURNS TABLE   (   property_code       TEXT,
                    first_name          TEXT,
                    last_name           TEXT,
                    email               TEXT,
                    address1            TEXT,
                    address2            TEXT,
                    city                TEXT,
                    province            TEXT,
                    postal_code         TEXT
                )
*/
AS 
$$
DECLARE 
    v_sql       TEXT;
BEGIN
    
    v_sql :=    'SELECT DISTINCT    b.property_code::text, l.lease_id::text,'
                ||'                 c.person_name_first_name::text AS first_name,'
                ||'                 c.person_name_last_name::text AS last_name,'
                ||'                 c.person_email::text as email,'
                ||'                 b.info_address_street_number||'' ''||b.info_address_street_name::text AS address1,'
                ||'                 a.info_unit_number::text AS address2,'
                ||'                 b.info_address_city::text  AS city,'
                ||'                 b.info_address_province::text AS province,'
                ||'                 b.info_address_postal_code::text AS postal_code '
                ||'FROM '||v_schema_name||'.lease l '
                ||'JOIN '||v_schema_name||'.apt_unit a ON (a.id = l.unit) '
                ||'JOIN '||v_schema_name||'.building b ON (b.id = a.building) '
                ||'JOIN '||v_schema_name||'.lease_participant lp ON (l.id = lp.lease) '
                ||'JOIN '||v_schema_name||'.customer c ON (c.id = lp.customer) '
                ||'WHERE   NOT EXISTS(  SELECT ''x'' FROM '||v_schema_name||'.insurance_policy '
                ||'                     WHERE   tenant = lp.id) '
                ||'AND  l.status = ''Active'' '
                ||'AND NOT b.suspended '
                ||'ORDER BY 1, 4, 3 ';
                
    RETURN QUERY EXECUTE v_sql;

END;
$$
LANGUAGE plpgsql VOLATILE;

/**

Usage :  

SELECT * FROM _dba_.get_no_insurance('greenwin') AS (property_code TEXT, lease_id TEXT, first_name TEXT, last_name TEXT,
                                                    email TEXT, address1 TEXT, address2 TEXT, city TEXT,
                                                    province TEXT, postal_code TEXT);
                                                    
**/

CREATE VIEW _dba_.greenwin_no_insurance AS
(
    SELECT * FROM _dba_.get_no_insurance('greenwin') AS (property_code TEXT, lease_id TEXT, first_name TEXT, last_name TEXT,
                                                    email TEXT, address1 TEXT, address2 TEXT, city TEXT,
                                                    province TEXT, postal_code TEXT)
);

/*

COPY (WITH t AS ( SELECT  property_code, lease_id,
                    first_name, last_name,
                    email, address1, address2, city, province ,
                    postal_code ,
                    row_number() OVER (PARTITION BY lease_id ORDER BY COALESCE(first_name, 'ZZZZ')) AS rnum
            FROM    _dba_.greenwin_no_insurance 
            WHERE   property_code IN ('rich0675','base0297','base0301','regi0300','univ0137')
            ORDER BY    property_code, lease_id)
SELECT  property_code, lease_id,
        first_name, last_name,
        email, address1, address2, city, province ,
        postal_code 
FROM t 
WHERE   rnum = 1) TO '/tmp/no_insurance.csv' CSV HEADER ;

*/
                                                    
