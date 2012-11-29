/**
***     ===========================================================================
***
***     Function to remove PropertyVistaAccountOwner role from All role
***     Will have to be executed for all newly created PMC
***
***     ===========================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.remove_acc_owner_from_all(v_schema_name VARCHAR(64)) RETURNS VOID AS
$$
BEGIN
        EXECUTE 'DELETE FROM '||v_schema_name||'.crm_role$behaviors '
        ||'WHERE value = ''PropertyVistaAccountOwner'' '
        ||'AND owner IN (SELECT id FROM '||v_schema_name||'.crm_role '
        ||'             WHERE   name = ''All'') ';
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

SELECT  namespace,_dba_.remove_acc_owner_from_all(namespace)
FROM    _admin_.admin_pmc 
WHERE   status != 'Created';

-- COMMIT;
