/**
***     ====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$  
***           
***             Yet another attempt to auto-generate SQL script for data model migration
***                
***     
***     ======================================================================================================================
**/

/**
***     ======================================================================================================================
***     
***             Low-hanging fruit - sequences
***             First argument - old schema name (the one that needs changing)
***             Second argument - new schema name (example)
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.generate_sql_sequences(TEXT, TEXT) 
RETURNS TABLE ( sql_text TEXT)
AS 
$$
       
        (SELECT  'SET search_path = '''||$1||''';' AS sql_text) 
        UNION ALL
        (SELECT ' ')
        UNION ALL
        -- Sequences to drop
        (SELECT '-- Sequences to drop')
        UNION ALL 
        (SELECT  'DROP SEQUENCE '||sequence_name||';' AS sql_text
        FROM    _dba_.compare_schema_sequences($1,$2)
        WHERE   schema_version = $1 
        ORDER BY 1 ASC) 
        UNION ALL
        (SELECT ' ')
        UNION ALL
        -- New sequences 
        (SELECT '-- New sequences')
        UNION ALL
        (SELECT  'CREATE SEQUENCE '||sequence_name||' START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;'
        AS      sql_text
        FROM    _dba_.compare_schema_sequences($1,$2)
        WHERE   schema_version = $2 
        ORDER BY 1 ASC) 
        UNION ALL
        (SELECT ' ')
        UNION ALL 
        (SELECT '-- Change owner to vista')
        UNION ALL
        (SELECT  'ALTER SEQUENCE '||sequence_name||' OWNER TO vista ;'
        AS      sql_text
        FROM    _dba_.compare_schema_sequences($1,$2)
        WHERE   schema_version = $2 
        ORDER BY 1 ASC) 
              
$$
LANGUAGE SQL VOLATILE;

/**
***     ========================================================================================================
***
***             Schema constraints changes - also not too complicated
***             First argument - old schema name (the one that needs changing)
***             Second argument - new schema name (example)
***             Third argument - constraint type ('p','f' or 'c')
***
***     ========================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.generate_sql_constraints(TEXT,TEXT,CHAR)
RETURNS TABLE (sql_text TEXT)
AS
$$
        WITH t1 AS (    SELECT * FROM _dba_.compare_schema_constraints($1,$2))
        SELECT  '-- Constraints to drop'
        UNION ALL
        (SELECT 'ALTER TABLE '||table_name||' DROP CONSTRAINT '||constraint_name||';'
        FROM    t1
        WHERE   schema_version = $1
        AND     constraint_type = $3
        ORDER BY 1)
        UNION ALL
        (SELECT '-- Constraint to create')
        UNION ALL
        (SELECT 'ALTER TABLE '||table_name||' ADD CONSTRAINT '||constraint_name||
                CASE WHEN constraint_type = 'p' THEN ' PRIMARY KEY('||column_name||');'
                WHEN constraint_type = 'f' THEN ' FOREIGN KEY('||column_name||') '||
                'REFERENCES '||ref_table_name||'('||ref_column_name||') '||
                        CASE WHEN is_deferrable THEN ' DEFERRABLE' END ||
                        CASE WHEN is_deferred THEN ' INITIALLY DEFERRED' 
                        ELSE ' INITIALLY IMMEDIATE' END ||
                ';'       
                WHEN constraint_type = 'c' THEN ' CHECK '||constraint_text||';' END
        FROM    t1
        WHERE   schema_version = $2
        AND     constraint_type = $3
        ORDER BY 1);                
$$
LANGUAGE SQL VOLATILE;


