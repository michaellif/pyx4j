/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Greenwin duplicate issues fix
***
***     ======================================================================================================================
**/

/*

-- general overview of the situation

WITH t AS (SELECT  l.lease_id, l.integration_system_id,
                    lp.id AS lp_id, lp.participant_id,
                    c.id AS c_id, 
                    c.person_name_first_name, c.person_name_last_name,
                    c.person_email,
                    b.property_code
            FROM    greenwin.building b
            JOIN    greenwin.apt_unit a ON (b.id = a.building)
            JOIN    greenwin.lease l ON (a.id = l.unit)
            JOIN    greenwin.lease_participant lp ON (l.id = lp.lease)
            JOIN    greenwin.customer c ON (c.id = lp.customer))
SELECT  t0.lease_id, t0.participant_id AS old_participant_id,
        t1.participant_id AS new_participant_id,
        t0.property_code AS old_property_code,
        t1.property_code AS new_property_code,
        t0.integration_system_id AS old_yardi,
        t1.integration_system_id AS new_yardi,
        t0.lp_id AS old_lp_id, 
        t1.lp_id AS new_lp_id,
        t0.c_id AS old_cid,
        t1.c_id AS new_cid,
        t0.person_name_first_name, t0.person_name_last_name,
        t0.person_email
FROM    (SELECT * FROM t WHERE integration_system_id = 6) AS t0
JOIN    (SELECT * FROM t WHERE integration_system_id != 6 )AS t1
ON      (t0.c_id = t1.c_id)
ORDER BY 1;

-- still 470

*/

-- Executed on db-ro 
/*
COPY (  SELECT  lp.id
        FROM    greenwin.lease_participant lp
        JOIN    greenwin.lease l ON (l.id = lp.lease)
        WHERE   l.integration_system_id = 6
        AND  EXISTS (SELECT customer FROM greenwin.lease_participant lp2
                    JOIN   greenwin.lease l2 ON (l2.id = lp2.lease)
                    WHERE  l2.integration_system_id != 6
                    AND    lp.customer = lp2.customer)) TO '/tmp/tenants_to_fix.csv' CSV HEADER;
                    
*/

-- On statging, on restored db from a previous day

/*

CREATE TABLE _dba_.tmp_tenants 
(
    id          BIGINT
);

COPY _dba_.tmp_tenants   FROM '/tmp/tenants_to_fix.csv'  DELIMITER ',' CSV HEADER;

CREATE TABLE  _dba_.tmp_tenant_data AS 
(SELECT     t.id, c.id AS customer_id, 
            c.registered_in_portal, c.person_name_first_name,
            c.person_name_last_name, c.person_email, 
            c.person_home_phone, c.person_mobile_phone,
            c.person_work_phone, 
            l.lease_id, lp.participant_id,
            aa.id AS pap, pm.id AS payment_method
 FROM   greenwin.lease_participant lp
 JOIN   greenwin.lease l ON (l.id = lp.lease) 
 JOIN   greenwin.customer c ON (c.id = lp.customer)
 JOIN   _dba_.tmp_tenants t ON (t.id = lp.id)
 LEFT JOIN  greenwin.autopay_agreement aa ON (lp.id = aa.tenant)
 LEFT JOIN  greenwin.payment_method pm ON (c.id = pm.customer)) ;

pg_dump -U akinareevski -h localhost -O -t _dba_.tmp_tenant_data vista_copy > tmp_tenant_data.sql

*/

-- On prod 

\i tmp_tenant_data.sql

/*
BEGIN TRANSACTION;

    -- DELETE payment_methods
    
    DELETE FROM greenwin.payment_method 
    WHERE  customer IN (SELECT customer_id FROM _dba_.tmp_tenant_data)
    AND id NOT IN (SELECT payment_method FROM _dba_.tmp_tenant_data);
    
    -- Delete autopay_agreement
    
    DELETE FROM greenwin.automay_agreement 
    WHERE  tenant IN (SELECT id FROM _dba_.tmp_tenant_data)
    AND id NOT IN (SELECT pap FROM _dba_.tmp_tenant_data);
    
    -- Delete lease participants with customer_id already used
    
    DELETE FROM greenwin.lease_participant
    WHERE  customer IN (SELECT customer_id FROM _dba_.tmp_tenant_data)
    AND id NOT IN (SELECT id FROM _dba_.tmp_tenant_data);
    
    
    -- fix customer table
    
    UPDATE  greenwin.customer AS c
    SET     registered_in_portal = t.registered_in_portal,
            person_name_first_name = t.person_name_first_name,
            person_name_last_name = t.person_name_last_name,
            person_email = t.person_email
    FROM    _dba_.tmp_tenant_data t 
    WHERE   c.id = t.customer_id;
            
    
    
COMMIT;
*/

CREATE OR REPLACE FUNCTION _dba_.fix_greenwin_duplicates() RETURNS VOID AS
$$
DECLARE     
            v_lp_id         BIGINT;
            v_cust_id       BIGINT;
            v_nextval       BIGINT;
BEGIN

    FOR     v_lp_id IN 
    SELECT  id 
    FROM    greenwin.lease_participant 
    WHERE   customer IN (SELECT customer_id FROM _dba_.tmp_tenant_data) 
    AND     id NOT IN (SELECT id FROM _dba_.tmp_tenant_data) 
    LOOP
    
        SELECT  number + 1 INTO v_cust_id
        FROM    greenwin.id_assignment_sequence
        WHERE   target = 'customer';
        
        SELECT  nextval('public.customer_seq') INTO v_nextval;
        
        INSERT INTO greenwin.customer (id,customer_id,created) 
        VALUES (v_nextval,v_cust_id,DATE_TRUNC('second',current_timestamp)::timestamp);
        
        UPDATE  greenwin.id_assignment_sequence
        SET     number = v_cust_id 
        WHERE   target = 'customer';
        
        UPDATE  greenwin.lease_participant
        SET     customer = v_nextval
        WHERE   id = v_lp_id;
        
        UPDATE  greenwin.payment_method AS pm
        SET     customer = v_nextval
        FROM    greenwin.lease_participant lp
        JOIN    greenwin.autopay_agreement aa ON (lp.id = aa.tenant)
        JOIN    greenwin.payment_method pm0 ON (pm0.id = aa.payment_method) 
        WHERE   pm.id = pm0.id 
        AND     lp.id = v_lp_id;
        
    END LOOP;
    
    /**
    -- Just in case I would want to update existing customers
    UPDATE  greenwin.customer AS c
    SET     person_name_first_name = t.person_name_first_name,
            person_name_last_name = t.person_name_last_name,
            person_email = t.person_email
    FROM    _dba_.tmp_tenant_data t 
    WHERE   c.id = t.customer_id;
    **/
    
    UPDATE  greenwin.customer AS c
    SET     person_home_phone = t.person_home_phone,
            person_mobile_phone = t.person_mobile_phone,
            person_work_phone = t.person_work_phone
    FROM    _dba_.tmp_tenant_data t 
    WHERE   c.id = t.customer_id;
    
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

    SELECT * FROM _dba_.fix_greenwin_duplicates();
    
    SELECT  COUNT(lp.id)
    FROM    greenwin.lease_participant lp
    JOIN    greenwin.lease l ON (l.id = lp.lease)
    WHERE   l.integration_system_id = 6
    AND  EXISTS (SELECT customer FROM greenwin.lease_participant lp2
                 JOIN   greenwin.lease l2 ON (l2.id = lp2.lease)
                 WHERE  l2.integration_system_id != 6
                 AND    lp.customer = lp2.customer);
    

COMMIT;

DROP FUNCTION _dba_.fix_greenwin_duplicates();
DROP TABLE _dba_.tmp_tenant_data ;
