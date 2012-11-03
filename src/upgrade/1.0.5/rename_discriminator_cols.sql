/**
***     =================================================================
***
***             One-time procedure to rename discriminator columns 
***             Safe to drop after migration to 1.0.5
***
***     =================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.rename_discriminator_cols() RETURNS VOID AS
$$
DECLARE
        v_schema_name           VARCHAR(64);
        v_table_name            VARCHAR(64);
        v_col_name              VARCHAR(64);
        v_new_col_name          VARCHAR(64);
BEGIN

        FOR v_schema_name,v_table_name,v_col_name,v_new_col_name IN 
        SELECT  table_schema,table_name,column_name,
                regexp_replace(column_name,'discriminator$','_discriminator') AS new_col_name
        FROM    information_schema.columns 
        WHERE   column_name ~ 'discriminator$'
        AND     (table_schema IN (SELECT namespace FROM _admin_.admin_pmc) 
                OR table_schema IN ('_admin_','public'))
        ORDER BY 1,2,3
        LOOP
                EXECUTE 'ALTER TABLE '||v_schema_name||'.'||v_table_name||' RENAME COLUMN '||v_col_name||' TO '||v_new_col_name;
        END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;
