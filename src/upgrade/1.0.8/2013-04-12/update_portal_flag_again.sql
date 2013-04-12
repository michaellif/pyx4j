/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Update customer.registered_in portal flag for all pmc - make II 
***             Now we remove flag for users that don't have a password
***
***     =====================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.update_portal_flag_again(v_schema_name TEXT) RETURNS VOID
AS 
$$
BEGIN
        EXECUTE 'UPDATE '||v_schema_name||'.customer '
                ||'SET  registered_in_portal = FALSE '
                ||'WHERE portal_registration_token IS NULL '
                ||'AND registered_in_portal = TRUE '
                ||'AND UPPER(person_email) IN '
                ||'     (SELECT         UPPER(u.email) '
                ||'     FROM    '||v_schema_name||'.customer_user u '
                ||'     JOIN    '||v_schema_name||'.customer_user_credential uc ON (u.id = uc.usr) '
                ||'     WHERE   uc.enabled AND uc.credential IS NULL) ';
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

        SELECT  namespace,_dba_.update_portal_flag_again(namespace)
        FROM    _admin_.admin_pmc 
        WHERE   status != 'Created'
        ORDER BY id;
        
COMMIT;

DROP FUNCTION _dba_.update_portal_flag_again(TEXT);
