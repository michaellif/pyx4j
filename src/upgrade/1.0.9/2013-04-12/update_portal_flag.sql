/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Update customer.registered_in portal flag for all pmc
***
***     =====================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.update_portal_flag(v_schema_name TEXT) RETURNS VOID
AS 
$$
BEGIN
        EXECUTE 'UPDATE '||v_schema_name||'.customer '
                ||'SET  registered_in_portal = TRUE '
                ||'WHERE portal_registration_token IS NULL '
                ||'AND UPPER(person_email) IN '
                ||'     (SELECT         UPPER(u.email) '
                ||'     FROM    '||v_schema_name||'.customer_user u '
                ||'     JOIN    '||v_schema_name||'.customer_user_credential uc ON (u.id = uc.usr) '
                ||'     WHERE   uc.enabled) ';
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

        SELECT  namespace,_dba_.update_portal_flag(namespace)
        FROM    _admin_.admin_pmc 
        WHERE   status != 'Created'
        ORDER BY id;
        
COMMIT;

DROP FUNCTION _dba_.update_portal_flag(TEXT);
