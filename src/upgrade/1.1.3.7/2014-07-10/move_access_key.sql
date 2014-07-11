/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             move access_key from suspended buildings to active ones
***
***     =====================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.move_access_key(v_schema_name TEXT, v_building_list TEXT[])
RETURNS VOID AS
$$
DECLARE 

    v_sql TEXT;
    v_suspended TEXT; 
    
BEGIN

    -- Create suspended list 
    
    SELECT  array_to_string(ARRAY(SELECT quote_literal('!'||UNNEST(v_building_list))),',')
    INTO    v_suspended;
    
    v_sql =     'WITH   t0 AS ( SELECT  b.property_code, a.info_unit_number,
                                        c.id AS customer_id, c.user_id, 
                                        c.person_name_first_name,
                                        c.person_name_last_name,
                                        c.portal_registration_token 
                                FROM    greenwin.building b
                                JOIN    greenwin.apt_unit a ON (b.id = a.building)
                                JOIN    greenwin.lease l ON (a.id = l.unit)
                                JOIN    greenwin.lease_participant lp ON (l.id = lp.lease)
                                JOIN    greenwin.customer c ON (c.id = lp.customer) 
                                WHERE   b.suspended ),
                        t1 AS ( SELECT  b.property_code, a.info_unit_number,
                                        c.id AS customer_id, c.user_id, 
                                        c.person_name_first_name,
                                        c.person_name_last_name,
                                        c.portal_registration_token 
                                FROM    greenwin.building b
                                JOIN    greenwin.apt_unit a ON (b.id = a.building)
                                JOIN    greenwin.lease l ON (a.id = l.unit)
                                JOIN    greenwin.lease_participant lp ON (l.id = lp.lease)
                                JOIN    greenwin.customer c ON (c.id = lp.customer) 
                                WHERE   NOT b.suspended)
                SELECT  t0.property_code AS old_property_code, 
                        t1.property_code AS new_property_code, 
                        t0.customer_id AS old_cid,
                        t1.customer_id AS new_cid,
                        t0.user_id AS old_uid,
                        t1.user_id AS new_uid,
                        t0.portal_registration_token AS old_token,
                        t1.portal_registration_token AS new_token
                FROM    t0
                JOIN    t1 ON (t0.property_code = '!'||t1.property_code 
                                AND t0.info_unit_number = t1.info_unit_number 
                                AND t0.person_name_first_name = t1.person_name_first_name
                                AND t0.person_name_last_name = t1.person_name_last_name)
                WHERE   t1.user_id IS NULL;
                



END;
$$
LANGUAGE plpgsql VOLATILE;


SELECT _dba_.move_access_key('cogir',ARRAY['!colb0002','!west0025','!wate0840','!ride0100','!rich0033']);





  
