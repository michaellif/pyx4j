/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Tenants without electronic payments 
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.get_no_pap(v_schema_name TEXT) 
RETURNS SETOF RECORD
AS 
$$
DECLARE 
    v_sql       TEXT;
BEGIN
    
    /**
    *** ============================================================================
    ***
    ***     EXCEPT clause in needed because there is no surefire way 
    ***     to distinguish customer who has used electronic payments 
    ***     (e.g. has payment_record) and the customer who has not. 
    ***     There is always a change that there are multiple lease_term_participants
    ***     for the same lease_participant
    ***
    *** ============================================================================
    **/
    
    
    v_sql :=    'SELECT DISTINCT    b.property_code::text, l.lease_id::text,'
                ||'                 c.person_name_first_name::text AS first_name,'
                ||'                 c.person_name_last_name::text AS last_name,'
                ||'                 c.person_email::text as email '
                ||'FROM '||v_schema_name||'.lease l '
                ||'JOIN '||v_schema_name||'.apt_unit a ON (a.id = l.unit) '
                ||'JOIN '||v_schema_name||'.building b ON (b.id = a.building) '
                ||'JOIN '||v_schema_name||'.lease_participant lp ON (l.id = lp.lease) '
                ||'JOIN '||v_schema_name||'.customer c ON (c.id = lp.customer) '
                ||'WHERE  l.status = ''Active'' '
                ||'AND NOT b.suspended '
                ||'AND  c.person_email IS NOT NULL '
                ||'EXCEPT '
                ||'SELECT DISTINCT    b.property_code::text, l.lease_id::text,'
                ||'                 c.person_name_first_name::text AS first_name,'
                ||'                 c.person_name_last_name::text AS last_name,'
                ||'                 c.person_email::text as email '
                ||'FROM '||v_schema_name||'.lease l '
                ||'JOIN '||v_schema_name||'.apt_unit a ON (a.id = l.unit) '
                ||'JOIN '||v_schema_name||'.building b ON (b.id = a.building) '
                ||'JOIN '||v_schema_name||'.lease_participant lp ON (l.id = lp.lease) '
                ||'JOIN '||v_schema_name||'.customer c ON (c.id = lp.customer) '
                ||'JOIN '||v_schema_name||'.lease_term_participant ltp ON (lp.id = ltp.lease_participant) '
                ||'JOIN '||v_schema_name||'.lease_term_v ltv ON (ltv.id = ltp.lease_term_v) '
                ||'WHERE EXISTS(  SELECT ''x'' FROM '||v_schema_name||'.payment_record  '
                ||'                WHERE   lease_term_participant = ltp.id ) '
                ||'AND  l.status = ''Active'' '
                ||'AND NOT b.suspended '
                ||'AND  c.person_email IS NOT NULL '
                ||'AND  ltv.to_date IS NULL '
                ||'ORDER BY 1, 4, 3 ';
                
    RETURN QUERY EXECUTE v_sql;

END;
$$
LANGUAGE plpgsql VOLATILE;

/**

Usage :  

SELECT * FROM _dba_.get_no_pap('greenwin') AS (property_code TEXT, lease_id TEXT, first_name TEXT, last_name TEXT,
                                                    email TEXT);
                                                    
**/

CREATE OR REPLACE VIEW _dba_.greenwin_no_pap AS
(
    SELECT * FROM _dba_.get_no_pap('greenwin') AS (property_code TEXT, lease_id TEXT, first_name TEXT, 
                                                        last_name TEXT, email TEXT)
);
