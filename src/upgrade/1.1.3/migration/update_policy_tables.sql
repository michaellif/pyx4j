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
        v_column_name           VARCHAR(64);
BEGIN
        FOR v_table_name, v_column_name IN
        SELECT  table_name, column_name
        FROM    information_schema.columns
        WHERE   column_name ~ 'node_discriminator'
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
                        ||'SET  '||v_column_name||' = ''AptUnit'' '
                        ||'WHERE '||v_column_name||' = ''Unit_BuildingElement'' ';
                        
                EXECUTE 'UPDATE '||v_schema_name||'.'||v_table_name||' '
                        ||'SET  '||v_column_name||' = ''Building'' '
                        ||'WHERE '||v_column_name||' = ''Disc_Building'' ';
                        
                EXECUTE 'UPDATE '||v_schema_name||'.'||v_table_name||' '
                        ||'SET  '||v_column_name||' = ''Complex'' '
                        ||'WHERE '||v_column_name||' = ''Disc Complex'' ';
                        
                EXECUTE 'UPDATE '||v_schema_name||'.'||v_table_name||' '
                        ||'SET  '||v_column_name||' = ''Country'' '
                        ||'WHERE '||v_column_name||' = ''Disc_Country'' ';
                        
                EXECUTE 'UPDATE '||v_schema_name||'.'||v_table_name||' '
                        ||'SET  '||v_column_name||' = ''Floorplan'' '
                        ||'WHERE '||v_column_name||' = ''Disc_Floorplan'' ';
                        
                EXECUTE 'UPDATE '||v_schema_name||'.'||v_table_name||' '
                        ||'SET  '||v_column_name||' = ''Province'' '
                        ||'WHERE '||v_column_name||' = ''Disc_Province'' ';
                        
        END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;
