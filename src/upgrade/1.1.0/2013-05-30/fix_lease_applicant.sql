/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Fix app bug overwriting _applicant coluumn
***
***     ======================================================================================================================
**/


CREATE OR REPLACE FUNCTION _dba_.fix_lease_applicant(v_schema_name TEXT) RETURNS VOID AS
$$
BEGIN
        EXECUTE 'UPDATE '||v_schema_name||'.lease AS l '
                ||'SET   _applicant = lp.id '
                ||'FROM '||v_schema_name||'.lease_participant lp, '
                ||'     '||v_schema_name||'.lease_term_participant ltp '
                ||'WHERE l.id = lp.lease '
                ||'AND  lp.id = ltp.lease_participant '
                ||'AND  ltp.participant_role = ''Applicant'' '
                ||'AND  lp.id_discriminator = ''Tenant'' '; 
                
END;
$$
LANGUAGE plpgsql VOLATILE;


BEGIN TRANSACTION;

        SELECT  namespace,_dba_.fix_lease_applicant(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created';
        
COMMIT;


DROP FUNCTION _dba_.fix_lease_applicant(text);
