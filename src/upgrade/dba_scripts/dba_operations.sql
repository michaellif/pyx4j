/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Functions for operations support
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.change_lease_customer(v_schema_name TEXT, v_participant_id TEXT) 
RETURNS VOID AS 
$$
DECLARE 

    v_customer_id   BIGINT;
    
BEGIN

    SELECT nextval('public.customer_seq') INTO v_customer_id;
    
    EXECUTE 'INSERT INTO '||v_schema_name||'.customer (id, created, updated) '
            ||'VALUES ('||v_customer_id||',DATE_TRUNC(''second'',current_timestamp)::timestamp, '
            ||'                     DATE_TRUNC(''second'',current_timestamp)::timestamp) ';
            
    EXECUTE 'UPDATE '||v_schema_name||'.lease_participant '
            ||'SET  customer = '||v_customer_id||' '
            ||'WHERE    participant_id = '||quote_literal(v_participant_id)||' ';
            
    EXECUTE 'UPDATE '||v_schema_name||'.id_assignment_sequence
 '
            ||'SET  number = number + 1 '
            ||'WHERE    target = ''customer'' ';
            
    EXECUTE 'UPDATE '||v_schema_name||'.customer AS c '
            ||'SET  customer_id = s.number '
            ||'FROM     '||v_schema_name||'.id_assignment_sequence s '
            ||'WHERE    s.target = ''customer'' '
            ||'AND  c.id = '||v_customer_id||' ';


END;
$$
LANGUAGE plpgsql VOLATILE;


