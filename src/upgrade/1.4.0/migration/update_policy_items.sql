/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Update legal_terms_policy_items
***
***     ===========================================================================================================
**/                                                  

-- load new policy items to temp table 
\i tmp_policy_items.sql

CREATE OR REPLACE FUNCTION _dba_.update_policy_items() RETURNS VOID AS
$$
DECLARE 
    
    v_schema_name       VARCHAR(64);
    
BEGIN

    FOR v_schema_name IN 
    SELECT  a.namespace 
    FROM    _admin_.admin_pmc a
    JOIN    pg_catalog.pg_namespace n ON (a.namespace = n.nspname)
    LOOP
    
        -- DELETE EXTRA legal_term_policy records
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.legal_terms_policy '
                ||'WHERE    id NOT IN ( SELECT  MIN(id) '
                ||'                     FROM    '||v_schema_name||'.legal_terms_policy) ';
                
        -- delete extra resident portal terms and conditions 
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.legal_terms_policy_item '
                ||'WHERE   caption = ''RESIDENT PORTAL TERMS AND CONDITIONS'' '
                ||'AND     id NOT IN (  SELECT  resident_portal_terms_and_conditions '
                ||'                     FROM    '||v_schema_name||'.legal_terms_policy) ';
        
        -- resident_portal_privacy_policy
        
        
        EXECUTE 'DELETE  FROM '||v_schema_name||'.legal_terms_policy_item '
                ||'WHERE   caption = ''RESIDENT PORTAL PRIVACY POLICY'' '
                ||'AND     id NOT IN (  SELECT  resident_portal_privacy_policy '
                ||'                     FROM    '||v_schema_name||'.legal_terms_policy)';
   

        -- prospect_portal_terms_and_conditions
        
        EXECUTE 'DELETE  FROM '||v_schema_name||'.legal_terms_policy_item '
                ||'WHERE   caption = ''ONLINE APPLICATION TERMS AND CONDITIONS'' '
                ||'AND     id NOT IN (  SELECT  prospect_portal_terms_and_conditions '
                ||'                     FROM    '||v_schema_name||'.legal_terms_policy)';
      
        -- prospect_portal_privacy_policy
        
        EXECUTE 'DELETE  FROM '||v_schema_name||'.legal_terms_policy_item '
                ||'WHERE   caption = ''ONLINE APPLICATION PRIVACY POLICY'' '
                ||'AND     id NOT IN (  SELECT  prospect_portal_privacy_policy '
                ||'                     FROM    '||v_schema_name||'.legal_terms_policy)';
                
                
        -- now update content of what is left
        
        EXECUTE 'UPDATE '||v_schema_name||'.legal_terms_policy_item AS pi '
                ||'SET  content = t.content '
                ||'FROM     (SELECT     caption, content, md5(content) AS checksum '
                ||'         FROM    _dba_.tmp_policy_items) AS t '
                ||'WHERE    pi.caption = t.caption '
                ||'AND      md5(pi.content) != t.checksum ';
                
    END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

    SELECT * FROM _dba_.update_policy_items();
    
COMMIT;

DROP FUNCTION _dba_.update_policy_items();
DROP TABLE _dba_.tmp_policy_items;
                
