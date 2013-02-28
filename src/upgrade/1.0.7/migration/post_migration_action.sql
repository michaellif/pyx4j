/**
***     =======================================================================
***
***     @version $Revision$ ($Author$) $Date$
***
***             Post-1.0.7 migration actions
***
**/

CREATE OR REPLACE FUNCTION _dba_.post_107_action() RETURNS VOID
AS
$$
DECLARE 
        v_schema_name   VARCHAR(64);
BEGIN
        
        FOR v_schema_name IN 
        SELECT  namespace
        FROM    _admin_.admin_pmc
        WHERE   schema_version = '1.0.7'
        AND     status != 'Created'
        LOOP
                EXECUTE 'INSERT INTO '||v_schema_name||'.payment_type_selection_policy '
                        ||'(id, updated, node_discriminator, node, accepted_cash, accepted_check,'
                        ||'accepted_echeck, accepted_eft, accepted_credit_card, accepted_interac,'
                        ||'resident_portal_echeck, resident_portal_eft, resident_portal_credit_card,'
                        ||'resident_portal_interac, cash_equivalent_cash, cash_equivalent_check,'
                        ||'cash_equivalent_echeck, cash_equivalent_eft, cash_equivalent_credit_card,'
                        ||' cash_equivalent_interac) '
                        ||'(SELECT nextval(''public.payment_type_selection_policy_seq'') AS id,'
                        ||'current_timestamp AS updated,''OrganizationPoliciesNode'' AS node_discriminator,'
                        ||'id AS node,TRUE accepted_cash,TRUE AS accepted_check,TRUE AS accepted_echeck,'
                        ||'TRUE AS accepted_eft,TRUE AS accepted_credit_card,TRUE AS accepted_interac,'
                        ||'TRUE AS resident_portal_echeck,TRUE AS resident_portal_eft,TRUE AS resident_portal_credit_card,'
                        ||'TRUE AS resident_portal_interac,TRUE AS cash_equivalent_cash,FALSE AS cash_equivalent_check,'
                        ||'FALSE AS cash_equivalent_echeck,TRUE AS cash_equivalent_eft,TRUE AS cash_equivalent_credit_card,'
                        ||'TRUE AS cash_equivalent_interac '
                        ||'FROM '||v_schema_name||'.organization_policies_node )';
        END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

        SELECT * FROM _dba_.post_107_action();
COMMIT;
