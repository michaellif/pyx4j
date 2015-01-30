/**
***     ======================================================================================================================
***
***             Legal questions migration for 1.4.2 
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_legal_questions(v_schema_name TEXT) 
RETURNS VOID AS
$$
DECLARE

    v_screen_id             BIGINT;
    
BEGIN

    FOR v_screen_id IN 
    EXECUTE 'SELECT id FROM '||v_schema_name||'.customer_screening_v'
    LOOP
    
        EXECUTE 'INSERT INTO '||v_schema_name||'.customer_screening_legal_question '
                ||'(id,owner,order_in_owner,question,answer) '
                ||'(SELECT  nextval(''public.customer_screening_legal_question_seq'') AS id,'
                ||'         '||v_screen_id||',0,''Have you ever been sued for rent?'',q.sued_for_rent '
                ||'FROM     '||v_schema_name||'.customer_screening_legal_questions q '
                ||'JOIN     '||v_schema_name||'.customer_screening_v c ON (c.legal_questions = q.id))';
                
        EXECUTE 'INSERT INTO '||v_schema_name||'.customer_screening_legal_question '
                ||'(id,owner,order_in_owner,question,answer) '
                ||'(SELECT  nextval(''public.customer_screening_legal_question_seq'') AS id,'
                ||'         '||v_screen_id||',1,''Have you ever been sued for damages?'',q.sued_for_damages '
                ||'FROM     '||v_schema_name||'.customer_screening_legal_questions q '
                ||'JOIN     '||v_schema_name||'.customer_screening_v c ON (c.legal_questions = q.id))';
                
                
        EXECUTE 'INSERT INTO '||v_schema_name||'.customer_screening_legal_question '
                ||'(id,owner,order_in_owner,question,answer) '
                ||'(SELECT  nextval(''public.customer_screening_legal_question_seq'') AS id,'
                ||'         '||v_screen_id||',2,''Have you ever been evicted?'',ever_evicted '
                ||'FROM     '||v_schema_name||'.customer_screening_legal_questions )';
                
        EXECUTE 'INSERT INTO '||v_schema_name||'.customer_screening_legal_question '
                ||'(id,owner,order_in_owner,question,answer) '
                ||'(SELECT  nextval(''public.customer_screening_legal_question_seq'') AS id,'
                ||'         '||v_screen_id||',3,''Have you ever defaulted on a lease?'',defaulted_on_lease '
                ||'FROM     '||v_schema_name||'.customer_screening_legal_questions )';
                
        EXECUTE 'INSERT INTO '||v_schema_name||'.customer_screening_legal_question '
                ||'(id,owner,order_in_owner,question,answer) '
                ||'(SELECT  nextval(''public.customer_screening_legal_question_seq'') AS id,'
                ||'         '||v_screen_id||',4,'
                ||'''Have you ever been convicted of a crime/felony that involved an offense against property, persons, government officials, or that involved firearms, illegal drugs, or sex or sex crimes?'','
                ||'     q.convicted_of_felony '
                ||'FROM     '||v_schema_name||'.customer_screening_legal_questions q '
                ||'JOIN     '||v_schema_name||'.customer_screening_v c ON (c.legal_questions = q.id))';
                
                
        EXECUTE 'INSERT INTO '||v_schema_name||'.customer_screening_legal_question '
                ||'(id,owner,order_in_owner,question,answer) '
                ||'(SELECT  nextval(''public.customer_screening_legal_question_seq'') AS id,'
                ||'         '||v_screen_id||',5,''Have you ever had any liens, court judgments or repossessions?'',q.legal_troubles '
                ||'FROM     '||v_schema_name||'.customer_screening_legal_questions  q '
                ||'JOIN     '||v_schema_name||'.customer_screening_v c ON (c.legal_questions = q.id))';
                
                
        EXECUTE 'INSERT INTO '||v_schema_name||'.customer_screening_legal_question '
                ||'(id,owner,order_in_owner,question,answer) '
                ||'(SELECT  nextval(''public.customer_screening_legal_question_seq'') AS id,'
                ||'         '||v_screen_id||',6,''Have you ever filed for bankruptcy protection?'',q.filed_bankruptcy '
                ||'FROM     '||v_schema_name||'.customer_screening_legal_questions q '
                ||'JOIN     '||v_schema_name||'.customer_screening_v c ON (c.legal_questions = q.id))';
                
        
    END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;
