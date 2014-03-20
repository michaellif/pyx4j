/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             function to compare row count in policy tables
***
***     =====================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.compare_policies_row_count (v_old_schema TEXT, v_new_schema TEXT) 
RETURNS TABLE ( policy_table_name           VARCHAR(64),
                old_schema_row_count        INT,
                preload_row_count           INT)
AS 
$$
DECLARE 
    v_table_name            VARCHAR(64);
    v_old_schema_row_count  INT;
    v_preload_row_count     INT;
BEGIN
    
    FOR v_table_name IN
    SELECT  table_name
    FROM    information_schema.tables
    WHERE   table_name ~ 'policy'
    AND     table_schema = v_new_schema
    ORDER BY 1
    LOOP
        
        EXECUTE 'SELECT COUNT(id) '
                ||'FROM '||v_new_schema||'.'||v_table_name 
                INTO    v_preload_row_count ;
                
        EXECUTE 'SELECT COUNT(id) '
                ||'FROM '||v_old_schema||'.'||v_table_name 
                INTO    v_old_schema_row_count ;
        
        
        SELECT  v_table_name, v_old_schema_row_count, v_preload_row_count
        INTO    policy_table_name, old_schema_row_count, preload_row_count;
        
        RETURN NEXT;
    END LOOP;
    
END;
$$
LANGUAGE plpgsql VOLATILE;
