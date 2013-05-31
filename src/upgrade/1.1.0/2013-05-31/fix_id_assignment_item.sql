/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Fix id_assignment_item
***
***     ======================================================================================================================
**/


CREATE OR REPLACE FUNCTION _dba_.fix_id_assignment_items(v_schema_name TEXT) RETURNS VOID AS
$$
BEGIN
        
        EXECUTE 'SET search_path = '||v_schema_name;
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.id_assignment_item (id,policy,order_in_policy,target,tp) '
                ||'(SELECT nextval(''public.id_assignment_item_seq'') AS id, p.id AS policy, m.cnt + 1 as order_in_policy,'
                ||' ''maintenance'',''generatedNumber'' '
                ||'FROM '||v_schema_name||'.id_assignment_policy p, (SELECT MAX(order_in_policy) AS cnt FROM '||v_schema_name||'.id_assignment_item ) m )';
                
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

        SELECT  namespace,_dba_.fix_id_assignment_items(namespace)
        FROM    _admin_.admin_pmc 
        WHERE   status != 'Created' 
        AND     namespace IN (SELECT pmc FROM _dba_.count_rows_all_pmc('id_assignment_item',ARRAY['target = ''maintenance'' ']) WHERE row_count = 0);
        
COMMIT;

DROP FUNCTION _dba_.fix_id_assignment_items(text);

