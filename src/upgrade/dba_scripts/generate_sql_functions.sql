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


