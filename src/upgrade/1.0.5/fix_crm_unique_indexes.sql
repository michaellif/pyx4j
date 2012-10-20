/**
***     ==================================================================
***
***             @version $Revision$ ($Author$) $Date$
***                     
***             Fix crm unique indexes
***     
***     ==================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.fix_crm_unique_indexes() RETURNS VOID AS
$$
DECLARE
        v_schema_name           VARCHAR(64);
BEGIN
        FOR v_schema_name IN 
        SELECT namespace FROM _admin_.admin_pmc
        WHERE status IN ('Active','Suspended')
        LOOP
                EXECUTE 'SET search_path = '||v_schema_name;
                
                DROP INDEX crm_user_email_idx;
                CREATE UNIQUE INDEX crm_user_email_idx ON crm_user USING btree (LOWER(email));
                
                DROP INDEX customer_customer_id_idx;
                CREATE UNIQUE INDEX customer_customer_id_idx ON customer USING btree (LOWER(customer_id));
                
                DROP INDEX customer_user_email_idx;
                CREATE UNIQUE INDEX customer_user_email_idx ON customer_user USING btree (LOWER(email));
                
                DROP INDEX employee_employee_id_idx;
                CREATE UNIQUE INDEX employee_employee_id_idx ON employee USING btree (LOWER(employee_id));
                
                DROP INDEX lead_lead_id_idx;
                CREATE UNIQUE INDEX lead_lead_id_idx ON lead USING btree (LOWER(lead_id));
                
                DROP INDEX lease_lease_id_idx;
                CREATE UNIQUE INDEX lease_lease_id_idx ON lease USING btree (LOWER(lease_id));
                
                DROP INDEX  master_online_application_online_application_id_idx;
                CREATE UNIQUE INDEX master_online_application_online_application_id_idx ON master_online_application USING btree (LOWER(online_application_id));
        END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;
SELECT * FROM _dba_.fix_crm_unique_indexes();
COMMIT;

DROP FUNCTION _dba_.fix_crm_unique_indexes();



