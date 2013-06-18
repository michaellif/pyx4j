/**
***     ======================================================================================================================
***
***             @version $Revision: $ ($Author: $) $Date: $
***
***             version 1.1.0.5 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_1105(v_schema_name TEXT) RETURNS VOID AS
$$
DECLARE
        v_billable_item         BIGINT;
BEGIN
        EXECUTE 'SET search_path = '||v_schema_name;
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/
        
        -- foreign keys
       
        
        -- check constraints
  
       
        /**
        ***     ======================================================================================================
        ***
        ***             DROP TABLES 
        ***
        ***     ======================================================================================================
        **/
        
        
        
        /**
        ***     ======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     ======================================================================================================
        **/
        
        -- auto_pay_change_policy
        
        CREATE TABLE auto_pay_change_policy
        (
                id                      BIGINT                          NOT NULL,
                node_discriminator      VARCHAR(50),
                node                    BIGINT,
                updated                 TIMESTAMP,
                rule                    VARCHAR(50),
                        CONSTRAINT auto_pay_change_policy_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE auto_pay_change_policy OWNER TO vista;
        
	-- notification

        CREATE TABLE notification
        (
                id                      BIGINT                          NOT NULL,
                employee                BIGINT                          NOT NULL,
                tp                      VARCHAR(50),
                        CONSTRAINT notification_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE notification OWNER TO vista;
        
        -- notification$buildings
        
        CREATE TABLE notification$buildings
        (
                id                      BIGINT                          NOT NULL,
                owner                   BIGINT,
                value                   BIGINT,
                seq                     INT,
                        CONSTRAINT notification$buildings_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE notification$buildings OWNER TO vista;
        
        
        CREATE TABLE notification$portfolios
        (
                id                      BIGINT                          NOT NULL,
                owner                   BIGINT,
                value                   BIGINT,
                seq                     INT,
                        CONSTRAINT notification$portfolios_pk PRIMARY KEY(id)
                
        );
        
        ALTER TABLE notification$portfolios OWNER TO vista;
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.notification (id,employee,tp) '
                ||'(SELECT      nextval(''public.notification_seq'') AS id, a.id AS employee,'
                ||'             ''Nsf'' AS tp '
                ||'FROM         (SELECT DISTINCT e.id FROM '||v_schema_name||'.employee e '
                ||'             JOIN '||v_schema_name||'.property_contact pc ON (e.email = pc.email) '
                ||'             WHERE pc.description = ''NSF_NOTIFICATIONS'') AS a )';
                
       
        EXECUTE 'INSERT INTO '||v_schema_name||'.notification (id,employee,tp) '
                ||'(SELECT      nextval(''public.notification_seq'') AS id, a.id AS employee,'
                ||'             ''PreauthorizedPaymentSuspension'' AS tp '
                ||'FROM         (SELECT DISTINCT e.id FROM '||v_schema_name||'.employee e '
                ||'             JOIN '||v_schema_name||'.property_contact pc ON (e.email = pc.email) '
                ||'             WHERE pc.description = ''PAP_SUSPENTION_NOTIFICATIONS'') AS a )';
                
        
        EXECUTE 'WITH t AS (SELECT       COALESCE(MAX(seq),-1) AS seq '
                ||'         FROM         '||v_schema_name||'.notification$buildings) '
                ||'INSERT INTO '||v_schema_name||'.notification$buildings (id,owner,value,seq) '
                ||'( SELECT     nextval(''public.notification$buildings_seq'') AS id,'
                ||'             n.id AS owner, b.id AS value, t.seq + 1 AS seq '
                ||' FROM    '||v_schema_name||'.notification n, '||v_schema_name||'.building b, t )';
        
        
        
        -- Delete data
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.buildingcontacts$property_contacts '
                ||'WHERE value IN (     SELECT  id FROM '||v_schema_name||'.property_contact '
                ||'                     WHERE   description IN (''PAP_SUSPENTION_NOTIFICATIONS'',''NSF_NOTIFICATIONS'')) ';
                
                
        EXECUTE 'DELETE FROM '||v_schema_name||'.property_contact '
                ||'WHERE description IN (''PAP_SUSPENTION_NOTIFICATIONS'',''NSF_NOTIFICATIONS'') ';
                
                
        /**
        ***     ============================================================================================
        ***             Delete billable_item with expiration_date = '2013-06-30' and current lease
        ***             Applicable only to yardi-enabled pmc
        ***     ============================================================================================
        **/
        
        IF EXISTS (SELECT 'x' FROM _admin_.admin_pmc a JOIN _admin_.admin_pmc_vista_features f 
                        ON (a.features = f.id AND f.yardi_integration AND a.namespace = v_schema_name ))
        THEN
                FOR v_billable_item IN 
                EXECUTE 'SELECT  b.id '
                        ||'FROM    '||v_schema_name||'.billable_item b '
                        ||'JOIN    '||v_schema_name||'.lease_term_vlease_products$feature_items ltf ON (b.id = ltf.value) '
                        ||'JOIN    '||v_schema_name||'.lease_term_v ltv ON (ltv.id = ltf.owner) '
                        ||'WHERE   b.expiration_date = ''2013-06-30'' '
                        ||'AND     ltv.to_date IS NULL '
                LOOP
                        EXECUTE 'DELETE FROM '||v_schema_name||'.lease_term_vlease_products$feature_items '
                                ||'WHERE value = '||v_billable_item;
                                
                        EXECUTE 'DELETE FROM '||v_schema_name||'.billable_item WHERE id = '||v_billable_item;
                END LOOP;
        END IF;
        
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        
                 
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- foreign key
        
        ALTER TABLE notification$buildings ADD CONSTRAINT notification$buildings_owner_fk FOREIGN KEY(owner) REFERENCES notification(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE notification$buildings ADD CONSTRAINT notification$buildings_value_fk FOREIGN KEY(value) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE notification$portfolios ADD CONSTRAINT notification$portfolios_owner_fk FOREIGN KEY(owner) REFERENCES notification(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE notification$portfolios ADD CONSTRAINT notification$portfolios_value_fk FOREIGN KEY(value) REFERENCES portfolio(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE notification ADD CONSTRAINT notification_employee_fk FOREIGN KEY(employee) REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;

        
        -- check constraints
        
        ALTER TABLE auto_pay_change_policy ADD CONSTRAINT auto_pay_change_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('Disc Complex', 'Disc_Building', 'Disc_Country', 'Disc_Floorplan', 'Disc_Province', 'OrganizationPoliciesNode', 'Unit_BuildingElement'));
        ALTER TABLE auto_pay_change_policy ADD CONSTRAINT auto_pay_change_policy_rule_e_ck CHECK ((rule) IN ('keepPercentage', 'keepUnchanged'));
        ALTER TABLE notification ADD CONSTRAINT notification_tp_e_ck CHECK ((tp) IN ('Nsf', 'PreauthorizedPaymentSuspension'));
        
       
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        
        CREATE INDEX notification$buildings_owner_idx ON notification$buildings USING btree (owner);
        CREATE INDEX notification$portfolios_owner_idx ON notification$portfolios USING btree (owner);
        CREATE INDEX notification_employee_idx ON notification USING btree (employee);
        CREATE UNIQUE INDEX id_assignment_item_policy_target_idx ON id_assignment_item USING btree (policy, target);

        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.1.0.5',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
