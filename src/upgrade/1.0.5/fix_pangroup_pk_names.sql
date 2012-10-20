/**
***     ===================================================================
***             @version $Revision$ ($Author$) $Date$
***             
***             Fix Pangroup PK constraints names - results in re-creating 
***             half of the schema (Foreign keys, indexes and such)
***     
***     ====================================================================
**/


CREATE OR REPLACE FUNCTION _dba_.fix_pangroup_pk_names() RETURNS VOID AS
$$
DECLARE
        v_const_oid                     OID;
        v_pk_const_name                 VARCHAR(64);
        v_pk_table_name                 VARCHAR(64);
        v_pk_col_name                   VARCHAR(64);
        v_ind_oid                       OID;
        v_fk_const_name                 VARCHAR(64);
        v_fk_table_name                 VARCHAR(64);
        v_fk_col_name                   VARCHAR(64);
        v_sql                           TEXT;
        v_create_fk_sql                 TEXT[];
        
BEGIN
        FOR     v_const_oid,v_pk_const_name,v_pk_table_name,v_pk_col_name,v_ind_oid IN 
        SELECT  a.oid AS con_oid,a.conname,b.relname,
                f.attname AS column_name,d.objid AS ind_oid 
        FROM    pg_constraint a
        JOIN    pg_class b ON (a.conrelid = b.oid)
        JOIN    pg_namespace c ON (a.connamespace = c.oid)
        JOIN    pg_depend d ON (a.oid = d.refobjid AND d.classid = 'pg_class'::regclass)
        JOIN    pg_class e ON (d.objid = e.oid AND e.relkind = 'i')
        JOIN    pg_attribute f ON (a.conrelid = f.attrelid AND f.attnum = ANY (a.conkey))
        WHERE   c.nspname = 'pangroup'
        AND     a.contype = 'p'
        AND     a.conname ~ '_pkey$'
        LOOP
                v_create_fk_sql := '{}';
                
                FOR     v_fk_const_name,v_fk_table_name,v_fk_col_name IN 
                SELECT  a.conname,b.relname,c.attname
                FROM    pg_constraint a
                JOIN    pg_class b ON (a.conrelid = b.oid)
                JOIN    pg_attribute c ON (a.conrelid = c.attrelid AND c.attnum = ANY(a.conkey)) 
                JOIN    pg_depend d ON (a.oid = d.objid)  
                WHERE   d.refobjid = v_ind_oid
                LOOP
                        
                   v_create_fk_sql := array_append(v_create_fk_sql,'ALTER TABLE pangroup.'||v_fk_table_name||
                   ' ADD CONSTRAINT '||v_fk_const_name||' FOREIGN KEY('||v_fk_col_name||') REFERENCES pangroup.'||
                   v_pk_table_name||'('||v_pk_col_name||');');
                   
                   EXECUTE 'ALTER TABLE pangroup.'||v_fk_table_name||' DROP CONSTRAINT '||v_fk_const_name;
                                                      
       
                END LOOP;
                
                EXECUTE 'ALTER TABLE pangroup.'||v_pk_table_name||' DROP CONSTRAINT '||v_pk_const_name;
                EXECUTE 'ALTER TABLE pangroup.'||v_pk_table_name||' ADD CONSTRAINT '||v_pk_table_name||'_pk PRIMARY KEY('||v_pk_col_name||')';
                
                FOREACH v_sql IN ARRAY(v_create_fk_sql)
                LOOP
                        EXECUTE v_sql;
                END LOOP;
                
        END LOOP;        

END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;
        SET client_min_messages = 'WARNING';
        SELECT * FROM _dba_.fix_pangroup_pk_names();
        SET client_min_messages = 'NOTICE';
        DROP FUNCTION _dba_.fix_pangroup_pk_names();
COMMIT;
