/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Search across all the tables for pattern
***
***     ===========================================================================================================
**/                                                

CREATE OR REPLACE FUNCTION _dba_.search_all_tables(v_pattern TEXT) 
RETURNS TABLE ( v_table_schema        TEXT,
                v_table_name          TEXT,
                v_column_name         TEXT,
                v_match               TEXT)
AS
$$
BEGIN

    FOR v_table_schema,v_table_name,v_column_name IN
    SELECT  table_schema,table_name,column_name
    FROM    information_schema.columns
    WHERE   data_type IN ('character varying','text')
    AND     table_schema NOT IN ('information_schema','pg_catalog')
    ORDER BY 1,2
    LOOP
    
        EXECUTE 'SELECT substring("'||v_column_name||'",position('||quote_literal(v_pattern)
                ||' in "'||v_column_name||'"),64) '
                ||'FROM     '||v_table_schema||'."'||v_table_name||'" '
                ||'WHERE    "'||v_column_name||'" ~* '||quote_literal(v_pattern)||' '
        INTO    v_match;
        
        IF v_match IS NOT NULL
        THEN 
            RETURN NEXT;
        END IF;
        
    END LOOP;


END;
$$
LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION _dba_.replace_in_all_tables(v_pattern TEXT, v_replace TEXT) 
RETURNS TABLE ( v_table_schema        TEXT,
                v_table_name          TEXT,
                v_column_name         TEXT,
                v_replaced            TEXT)
AS 
$$
DECLARE 
    
        v_match     TEXT;
    
BEGIN

    FOR v_table_schema,v_table_name,v_column_name IN
    SELECT  table_schema,table_name,column_name
    FROM    information_schema.columns
    WHERE   data_type IN ('character varying','text')
    AND     table_schema NOT IN ('information_schema','pg_catalog')
    ORDER BY 1,2
    LOOP
    
        EXECUTE 'SELECT substring("'||v_column_name||'",position('||quote_literal(v_pattern)
                ||' in "'||v_column_name||'"),64) '
                ||'FROM     '||v_table_schema||'."'||v_table_name||'" '
                ||'WHERE    "'||v_column_name||'" ~* '||quote_literal(v_pattern)||' '
        INTO    v_match;
        
        IF v_match IS NOT NULL
        THEN 
            
            EXECUTE 'UPDATE '||v_table_schema||'."'||v_table_name||'" '
                    ||'SET  "'||v_column_name||'" = regexp_replace("'||v_column_name||'",'
                    ||quote_literal(v_pattern)||','||quote_literal(v_replace)||',''g'') '
                    ||'WHERE    "'||v_column_name||'" ~* '||quote_literal(v_pattern)||' ';
            
            GET DIAGNOSTICS v_replaced = ROW_COUNT;
                    
            RETURN NEXT;
            
        END IF;
        
    END LOOP;


END;
$$
LANGUAGE plpgsql VOLATILE;
