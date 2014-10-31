/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Greenwin duplicate issues fix part II
***
***     ======================================================================================================================
**/

-- load saved tenants data
\i tmp_tenant_data.sql

-- Look for changed records in customer table
/**
SELECT  t.customer_id AS customer,
        t.lease_id, 
        c.registered_in_portal AS current_portal,
        t.registered_in_portal AS correct_portal,
        c.person_name_first_name AS curr_fname,
        c.person_name_last_name AS curr_lname,
        t.person_name_first_name AS corr_fname,
        t.person_name_last_name AS corr_lname,
        c.person_email AS curr_email,
        t.person_email AS corr_email
FROM    greenwin.customer c
JOIN    _dba_.tmp_tenant_data t ON (c.id = t.customer_id)
WHERE  ((COALESCE(c.registered_in_portal,FALSE) != COALESCE(t.registered_in_portal,FALSE))
OR      (COALESCE(c.person_name_first_name,'') != COALESCE(t.person_name_first_name,''))
OR      (COALESCE(c.person_name_last_name,'') != COALESCE(t.person_name_last_name,''))
OR      (COALESCE(c.person_email,'') != COALESCE(t.person_email,'')));
**/

BEGIN TRANSACTION;

    UPDATE  greenwin.customer AS c 
    SET     registered_in_portal = t.registered_in_portal,
            person_name_first_name = t.person_name_first_name,
            person_name_last_name = t.person_name_last_name,
            person_email = t.person_email,
            updated = DATE_TRUNC('second',current_timestamp)::timestamp
    FROM    _dba_.tmp_tenant_data t
    WHERE   c.id = t.customer_id
    AND    ((COALESCE(c.registered_in_portal,FALSE) != COALESCE(t.registered_in_portal,FALSE))
    OR      (COALESCE(c.person_name_first_name,'') != COALESCE(t.person_name_first_name,''))
    OR      (COALESCE(c.person_name_last_name,'') != COALESCE(t.person_name_last_name,''))
    OR      (COALESCE(c.person_email,'') != COALESCE(t.person_email,'')));

COMMIT;


DROP TABLE _dba_.tmp_tenant_data;
