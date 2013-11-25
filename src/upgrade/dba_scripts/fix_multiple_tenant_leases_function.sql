/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Function to fix multiple tenant leases. Creates a new tenant, so use with caution
***
***     =====================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.fix_multiple_tenant_leases(v_schema_name TEXT) RETURNS VOID AS
$$
DECLARE 
        v_lease_id              BIGINT;
        v_orig_customer_id      BIGINT;
        v_customer_id           BIGINT;
        v_customer_assigned_id  BIGINT;
BEGIN
        
        FOR v_lease_id,v_orig_customer_id IN
        EXECUTE 'SELECT t0.lease, t0.customer '
                ||'FROM (       WITH t AS (     SELECT lp.customer '
                ||'                             FROM    '||v_schema_name||'.lease_participant lp '
                ||'                             JOIN    '||v_schema_name||'.lease l ON (l.id = lp.lease) '
                ||'                             WHERE   l.status = ''Active'' '
                ||'                             GROUP BY customer '
                ||'                             HAVING COUNT(lease) > 1) '
                ||'     SELECT  lp.lease, lp.customer, row_number() OVER (PARTITION BY t.customer ORDER BY lease) '
                ||'     FROM    '||v_schema_name||'.lease_participant lp '
                ||'     JOIN    t ON (lp.customer = t.customer)) AS t0 '
                ||'WHERE   t0.row_number > 1 '
        LOOP
                
                EXECUTE 'SELECT number+1 '
                        ||'FROM '||v_schema_name||'.id_assignment_sequence '
                        ||'WHERE target = ''customer'' '
                        INTO v_customer_assigned_id;
                        
                        
                EXECUTE 'INSERT INTO '||v_schema_name||'.customer (id,customer_id) '
                        ||'(SELECT nextval(''public.customer_seq'') AS id,'||v_customer_assigned_id||'::VARCHAR(14)) ';
                
                EXECUTE 'SELECT id '
                        ||'FROM '||v_schema_name||'.customer '
                        ||'WHERE customer_id = '||v_customer_assigned_id||'::VARCHAR(14) '
                        INTO v_customer_id;
                       
                        
                EXECUTE 'UPDATE '||v_schema_name||'.lease_participant '
                        ||'SET customer = '||v_customer_id||' '
                        ||'WHERE lease = '||v_lease_id||' '
                        ||'AND  customer = '||v_orig_customer_id ; 
                        
                EXECUTE 'UPDATE '||v_schema_name||'.id_assignment_sequence '
                        ||'SET number = '||v_customer_assigned_id||' '
                        ||'WHERE target = ''customer'' ';
                
        END LOOP;

END;
$$
LANGUAGE plpgsql VOLATILE;
