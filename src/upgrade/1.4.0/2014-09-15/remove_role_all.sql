/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Remove role 'All' from anyone who doesn't have role 'PropertyVistaAccountOwner'
***
***     =====================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.remove_role_all() RETURNS VOID AS
$$
DECLARE
    v_schema_name       VARCHAR(64);
BEGIN
    
    FOR v_schema_name IN 
    SELECT  namespace 
    FROM    _admin_.admin_pmc
    WHERE   status != 'Created'
    LOOP
    
        EXECUTE 'WITH t0 AS (   SELECT DISTINCT owner '
                ||'             FROM    '||v_schema_name||'.crm_user_credential$rls ur '
                ||'             JOIN    '||v_schema_name||'.crm_role r ON (r.id = ur.value) '
                ||'             WHERE   r.name = ''PropertyVistaAccountOwner'' ) '
                ||'DELETE FROM '||v_schema_name||'.crm_user_credential$rls AS ur '
                ||'WHERE    ur.value IN (SELECT id FROM '||v_schema_name||'.crm_role WHERE name = ''Super Administrator'') '
                ||'AND      ur.owner NOT IN (SELECT owner FROM t0) ';
                
        EXECUTE 'WITH t AS (    SELECT DISTINCT id FROM '||v_schema_name||'.crm_role '
                ||'             WHERE name = ''PropertyVistaAccountOwner'') '
                ||'DELETE FROM '||v_schema_name||'.crm_user_credential$rls AS ur '
                ||'WHERE    ur.value  IN (SELECT id FROM t) ';
                
        EXECUTE 'DELETE FROM '||v_schema_name||'.crm_role '
                ||'WHERE    name = ''PropertyVistaAccountOwner'' ';
    
    END LOOP;


END;
$$
LANGUAGE plpgsql VOLATILE;


BEGIN TRANSACTION;

    SELECT * FROM _dba_.remove_role_all();

COMMIT;

DROP FUNCTION _dba_.remove_role_all();
