/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Update policy tables in bulk
***
***     ===========================================================================================================
**/                                                     

CREATE OR REPLACE FUNCTION _dba_.update_policy_tables(v_schema_name TEXT) RETURNS VOID 
AS
$$
DECLARE
        v_table_name            VARCHAR(64);
BEGIN
        FOR v_table_name IN
        SELECT  table_name 
        FROM    information_schema.columns
        WHERE   column_name = 'node_discriminator'
        AND     table_name ~ 'policy'
        AND     table_schema = v_schema_name
        --('agreement_legal_policy','application_documentation_policy','arpolicy',
        --'auto_pay_policy','background_check_policy','dates_policy','deposit_policy',
        --'email_templates_policy','id_assignment_policy','lease_adjustment_policy',
        --'lease_billing_policy','legal_terms_policy','n4_policy','online_application_legal_policy',
        --'payment_transactions_policy','payment_type_selection_policy','pet_policy',
        --'product_tax_policy','restrictions_policy','tenant_insurance_policy','yardi_interface_policy')
        LOOP
                EXECUTE 'UPDATE '||v_schema_name||'.'||v_table_name||' '
                        ||'SET  node_discriminator = ''AptUnit'' '
                        ||'WHERE node_discriminator = ''Unit_BuildingElement'' ';
                        
                EXECUTE 'UPDATE '||v_schema_name||'.'||v_table_name||' '
                        ||'SET  node_discriminator = ''Building'' '
                        ||'WHERE node_discriminator = ''Disc_Building'' ';
                        
                EXECUTE 'UPDATE '||v_schema_name||'.'||v_table_name||' '
                        ||'SET  node_discriminator = ''Complex'' '
                        ||'WHERE node_discriminator = ''Disc Complex'' ';
                        
                EXECUTE 'UPDATE '||v_schema_name||'.'||v_table_name||' '
                        ||'SET  node_discriminator = ''Country'' '
                        ||'WHERE node_discriminator = ''Disc_Country'' ';
                        
                EXECUTE 'UPDATE '||v_schema_name||'.'||v_table_name||' '
                        ||'SET  node_discriminator = ''Floorplan'' '
                        ||'WHERE node_discriminator = ''Disc_Floorplan'' ';
                        
                EXECUTE 'UPDATE '||v_schema_name||'.'||v_table_name||' '
                        ||'SET  node_discriminator = ''Province'' '
                        ||'WHERE node_discriminator = ''Disc_Province'' ';
                        
        END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;
