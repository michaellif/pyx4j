/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.1.4 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_114(v_schema_name TEXT) RETURNS VOID AS
$$
DECLARE
        v_rowcount      INT     := 0;
BEGIN
        EXECUTE 'SET search_path = '||v_schema_name;
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/
        
        
        -- primary keys 
        
        ALTER TABLE province DROP CONSTRAINT province_pk;
        
        -- foreign keys
        
        ALTER TABLE apt_unit DROP CONSTRAINT apt_unit_info_legal_address_country_fk;
        ALTER TABLE apt_unit DROP CONSTRAINT apt_unit_info_legal_address_province_fk;
        ALTER TABLE building DROP CONSTRAINT building_info_address_country_fk;
        ALTER TABLE building DROP CONSTRAINT building_info_address_province_fk;
        ALTER TABLE city_intro_page DROP CONSTRAINT city_intro_page_province_fk;
        ALTER TABLE city DROP CONSTRAINT city_province_fk;
        ALTER TABLE communication_message$to DROP CONSTRAINT communication_message$to_owner_fk;
        ALTER TABLE communication_message_attachment DROP CONSTRAINT communication_message_attachment_message_fk;
        ALTER TABLE customer_screening_income_info DROP CONSTRAINT customer_screening_income_info_address_country_fk;
        ALTER TABLE customer_screening_income_info DROP CONSTRAINT customer_screening_income_info_address_province_fk;
        ALTER TABLE customer_screening_v DROP CONSTRAINT customer_screening_v_current_address_country_fk;
        ALTER TABLE customer_screening_v DROP CONSTRAINT customer_screening_v_current_address_province_fk;
        ALTER TABLE customer_screening_v DROP CONSTRAINT customer_screening_v_previous_address_country_fk;
        ALTER TABLE customer_screening_v DROP CONSTRAINT customer_screening_v_previous_address_province_fk;
        ALTER TABLE emergency_contact DROP CONSTRAINT emergency_contact_address_country_fk;
        ALTER TABLE emergency_contact DROP CONSTRAINT emergency_contact_address_province_fk;
        ALTER TABLE landlord DROP CONSTRAINT landlord_address_country_fk;
        ALTER TABLE landlord DROP CONSTRAINT landlord_address_province_fk;
        ALTER TABLE marketing DROP CONSTRAINT marketing_marketing_address_country_fk;
        ALTER TABLE marketing DROP CONSTRAINT marketing_marketing_address_province_fk;
        ALTER TABLE master_online_application DROP CONSTRAINT master_online_application_building_fk;
        ALTER TABLE master_online_application DROP CONSTRAINT master_online_application_floorplan_fk;
        ALTER TABLE n4_policy DROP CONSTRAINT n4_policy_mailing_address_country_fk;
        ALTER TABLE n4_policy DROP CONSTRAINT n4_policy_mailing_address_province_fk;
        ALTER TABLE payment_method DROP CONSTRAINT payment_method_billing_address_country_fk;
        ALTER TABLE payment_method DROP CONSTRAINT payment_method_billing_address_province_fk;
        ALTER TABLE province DROP CONSTRAINT province_country_fk;
        ALTER TABLE pt_vehicle DROP CONSTRAINT pt_vehicle_country_fk;
        ALTER TABLE pt_vehicle DROP CONSTRAINT pt_vehicle_province_fk;
        
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEXES
        ***
        ***     ======================================================================================================
        **/
        
        DROP INDEX province_code_idx;
        DROP INDEX province_name_idx;
      
        
        /**
        ***    ======================================================================================================
        ***
        ***             Very special case for billing_arrears_snapshot_from_date_to_date_idx
        ***             This index doesn''t exist in new schemas, and may be bloated for schemas
        ***             where it does exists due to removal of extra rows from billing_arrears_snapshot table 
        ***             So I''ll just drop and recreate it
        ***
        ***     ===================================================================================================== 
        **/
        
        DROP INDEX IF EXISTS billing_arrears_snapshot_from_date_to_date_idx;
        
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
        
        -- apt_unit
        
        ALTER TABLE apt_unit RENAME COLUMN info_legal_address_country TO info_legal_address_country_old;
        ALTER TABLE apt_unit RENAME COLUMN info_legal_address_province TO info_legal_address_province_old;
        
        ALTER TABLE apt_unit    ADD COLUMN info_legal_address_country VARCHAR(50),
                                ADD COLUMN info_legal_address_province VARCHAR(500);
        
        
        -- billing_arrears_snapshot
        
        ALTER TABLE billing_arrears_snapshot    ADD COLUMN legal_status VARCHAR(50),
                                                ADD COLUMN legal_status_date TIMESTAMP;
        
        -- building 
        
        ALTER TABLE building RENAME COLUMN info_address_country TO info_address_country_old;
        ALTER TABLE building RENAME COLUMN info_address_province TO info_address_province_old;
        
        ALTER TABLE building    ADD COLUMN contacts_support_phone VARCHAR(500),
                                ADD COLUMN info_address_country VARCHAR(50),
                                ADD COLUMN info_address_province VARCHAR(500);
                                
        -- city
        
        ALTER TABLE city RENAME COLUMN province TO province_old;
        ALTER TABLE city ADD COLUMN province VARCHAR(50);
        
        -- city_intro_page
        
        ALTER TABLE city_intro_page RENAME COLUMN province TO province_old;
        ALTER TABLE city_intro_page ADD COLUMN province VARCHAR(50);
        
        
        -- country_policy_node
        
        CREATE TABLE country_policy_node
        (
            id                          BIGINT              NOT NULL,
            country                     VARCHAR(50),
                CONSTRAINT  country_policy_node_pk  PRIMARY KEY(id)
        );
        
        ALTER TABLE country_policy_node OWNER TO vista;
        
        
        -- legal_status
        
        ALTER TABLE legal_status    ADD COLUMN cancellation_threshold NUMERIC(18,2),
                                    ADD COLUMN expiry TIMESTAMP,
                                    ADD COLUMN termination_date DATE;
        
        -- master_online_application
        
        ALTER TABLE master_online_application RENAME COLUMN building TO ils_building;
        ALTER TABLE master_online_application RENAME COLUMN floorplan TO ils_floorplan;
        
        
        -- n4_policy
        
        ALTER TABLE n4_policy   ADD COLUMN cancellation_threshold NUMERIC(18,2),
                                ADD COLUMN expiry_days INT;
        
        
        -- online_application
        
        ALTER TABLE online_application ADD COLUMN create_date DATE;
        
        -- province 
        
        ALTER TABLE province RENAME TO province_policy_node;
        
        ALTER TABLE province_policy_node ADD COLUMN province VARCHAR(50);
        
        -- restrictions_policy
        
        ALTER TABLE restrictions_policy ADD COLUMN no_need_guarantors BOOLEAN;
        
        -- site_titles
        
        ALTER TABLE site_titles RENAME COLUMN resident_portal_promotions TO site_promo_title;
        
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        -- apt_unit
        
        EXECUTE 'UPDATE '||v_schema_name||'.apt_unit AS a '
                ||'SET    info_legal_address_country = replace(c.name,'' '','''') '
                ||'FROM   '||v_schema_name||'.country AS c '
                ||'WHERE  a.info_legal_address_country_old = c.id ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.apt_unit AS a '
                ||'SET    info_legal_address_province = replace(p.name,'' '','''') '
                ||'FROM   '||v_schema_name||'.province AS p '
                ||'WHERE  a.info_legal_address_province_old = p.id ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.apt_unit '
                ||'SET  info_legal_address_street_number = '
                ||'     info_legal_address_street_number||info_legal_address_street_number_suffix '
                ||'WHERE    info_legal_address_street_number_suffix IS NOT NULL';
        
        EXECUTE 'UPDATE '||v_schema_name||'.apt_unit '
                ||'SET  info_legal_address_street_name = '
                ||'     TRIM(info_legal_address_street_name)||'' ''||INITCAP(TRIM(info_legal_address_street_type)) '
                ||'WHERE    info_legal_address_street_type IS NOT NULL ';
        
        EXECUTE 'UPDATE '||v_schema_name||'.apt_unit '
                ||'SET  info_legal_address_street_name = '
                ||'     TRIM(info_legal_address_street_name)||'' ''||INITCAP(TRIM(info_legal_address_street_direction)) '
                ||'WHERE    info_legal_address_street_direction IS NOT NULL ';
        
               
        -- building
        
        EXECUTE 'UPDATE '||v_schema_name||'.building AS b '
                ||'SET  info_address_country = replace(c.name,'' '','''') '
                ||'FROM '||v_schema_name||'.country AS c '
                ||'WHERE b.info_address_country_old = c.id ';
        
        EXECUTE 'UPDATE '||v_schema_name||'.building AS b '
                ||'SET  info_address_province = replace(p.name,'' '','''') '
                ||'FROM '||v_schema_name||'.province p '
                ||'WHERE b.info_address_province_old = p.id ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.building '
                ||'SET  info_address_street_number = '
                ||' info_address_street_number||info_address_street_number_suffix '
                ||'WHERE    info_address_street_number_suffix IS NOT NULL';
                
        EXECUTE 'UPDATE '||v_schema_name||'.building '
                ||'SET  info_address_street_name = '
                ||' TRIM(info_address_street_name)||'' ''||INITCAP(TRIM(info_address_street_type)) '
                ||'WHERE    info_address_street_type IS NOT NULL';
                
        EXECUTE 'UPDATE '||v_schema_name||'.building '
                ||'SET  info_address_street_name = '
                ||' TRIM(info_address_street_name)||'' ''||INITCAP(TRIM(info_address_street_direction)) '
                ||'WHERE    info_address_street_direction IS NOT NULL';
                
        
        -- city
        
        EXECUTE 'UPDATE '||v_schema_name||'.city AS c '
                ||'SET  province = replace(p.name,'' '','''') '
                ||'FROM '||v_schema_name||'.province p '
                ||'WHERE    c.province_old = p.id ';
                
        
        -- city_intro_page
        
        EXECUTE 'UPDATE '||v_schema_name||'.city_intro_page AS c '
                ||'SET  province = replace(p.name,'' '','''') '
                ||'FROM '||v_schema_name||'.province p '
                ||'WHERE    c.province_old = p.id ';
                
        
        
        -- Phone numbers update
        
        PERFORM * FROM _dba_.update_phone_numbers(v_schema_name);
       
        
        -- province_policy_node
        
        EXECUTE 'UPDATE '||v_schema_name||'.province_policy_node '
                ||'SET  province = replace(name,'' '','''') ';
        
        -- online_application
        
        EXECUTE 'UPDATE '||v_schema_name||'.online_application AS a '
                ||'SET  create_date = m.create_date '
                ||'FROM '||v_schema_name||'.master_online_application AS m '
                ||'WHERE    m.id = a.master_online_application ';
                
        -- restrictions_policy
        
        EXECUTE 'UPDATE '||v_schema_name||'.restrictions_policy '
                ||'SET  no_need_guarantors = FALSE ';
       
       
        SET CONSTRAINTS ALL IMMEDIATE;
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- apt_unit
        
        ALTER TABLE apt_unit    DROP COLUMN info_legal_address_country_old,
                                DROP COLUMN info_legal_address_province_old,
                                DROP COLUMN info_legal_address_county,
                                DROP COLUMN info_legal_address_street_direction,
                                DROP COLUMN info_legal_address_street_number_suffix,
                                DROP COLUMN info_legal_address_street_type;
                                
                                
        -- building
        
        ALTER TABLE building    DROP COLUMN info_address_country_old,
                                DROP COLUMN info_address_province_old,
                                DROP COLUMN info_address_county,
                                DROP COLUMN info_address_street_direction,
                                DROP COLUMN info_address_street_number_suffix,
                                DROP COLUMN info_address_street_type;
                                
        -- city 
        
        ALTER TABLE city DROP COLUMN province_old;
        
        
        -- city_intro_page 
        
        ALTER TABLE city_intro_page DROP COLUMN province_old;
        
        -- country
        
        DROP TABLE country;
        
        
        -- province_policy_node
        
        ALTER TABLE province_policy_node    DROP COLUMN code,
                                            DROP COLUMN county,
                                            DROP COLUMN name;
                 
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- foreign keys
        
        ALTER TABLE master_online_application ADD CONSTRAINT master_online_application_ils_building_fk FOREIGN KEY(ils_building) 
            REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE master_online_application ADD CONSTRAINT master_online_application_ils_floorplan_fk FOREIGN KEY(ils_floorplan) 
            REFERENCES floorplan(id)  DEFERRABLE INITIALLY DEFERRED;
            
        -- check constraints
        
        ALTER TABLE apt_unit ADD CONSTRAINT apt_unit_info_legal_address_country_e_ck 
            CHECK ((info_legal_address_country) IN ('Afghanistan', 'AlandIslands', 'Albania', 'Algeria', 'AmericanSamoa', 'Andorra', 
                'Angola', 'Anguilla', 'Antarctica', 'Antigua', 'Argentina', 'Armenia', 'Aruba', 'Australia', 'Austria', 'Azerbaijan', 
                'Bahamas', 'Bahrain', 'Bangladesh', 'Barbados', 'Belarus', 'Belgium', 'Belize', 'Benin', 'Bermuda', 'Bhutan', 'Bolivia', 
                'Bonaire', 'BosniaHerzegovina', 'Botswana', 'BouvetIsland', 'Brazil', 'BruneiDarussalam', 'Bulgaria', 'BurkinaFaso', 
                'Burundi', 'CaboVerde', 'Cambodia', 'Cameroon', 'Canada', 'CaymanIslands', 'CentralAfricanRepublic', 'Chad', 'Chile', 
                'China', 'ChristmasIsland', 'CocosIslands', 'Colombia', 'Comoros', 'Congo', 'CookIslands', 'CostaRica', 'Croatia', 'Cuba', 
                'Curacao', 'Cyprus', 'CzechRepublic', 'Denmark', 'Djibouti', 'Dominica', 'DominicanRepublic', 'Ecuador', 'Egypt', 'ElSalvador', 
                'EquatorialGuinea', 'Eritrea', 'Estonia', 'Ethiopia', 'FalklandIslands', 'FaroeIslands', 'Fiji', 'Finland', 'France', 'FrenchGuiana', 
                'FrenchPolynesia', 'FrenchTerritories', 'Gabon', 'Gambia', 'Georgia', 'Germany', 'Ghana', 'Gibraltar', 'Greece', 'Greenland', 'Grenada', 
                'Guadeloupe', 'Guam', 'Guatemala', 'Guernsey', 'Guinea', 'GuineaBissau', 'Guyana', 'Haiti', 'HeardIslands', 'Honduras', 'HongKong', 
                'Hungary', 'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 'IsleOfMan', 'Israel', 'Italy', 'Jamaica', 'Japan', 'Jersey', 
                'Jordan', 'Kazakhstan', 'Kenya', 'Kiribati', 'Kuwait', 'Kyrgyzstan', 'LaoRepublic', 'Latvia', 'Lebanon', 'Lesotho', 'Liberia', 'Libya', 
                'Liechtenstein', 'Lithuania', 'Luxembourg', 'Macao', 'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 'Maldives', 'Mali', 'Malta', 
                'MarshallIslands', 'Martinique', 'Mauritania', 'Mauritius', 'Mayotte', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 'Mongolia', 
                'Montenegro', 'Montserrat', 'Morocco', 'Mozambique', 'Myanmar', 'Namibia', 'Nauru', 'Nepal', 'Netherlands', 'NewCaledonia', 'NewZealand', 
                'Nicaragua', 'Niger', 'Nigeria', 'Niue', 'NorfolkIsland', 'NorthKorea', 'NorthernMarianaIslands', 'Norway', 'Oman', 'Pakistan', 'Palau', 
                'Palestine', 'Panama', 'PapuaNewGuinea', 'Paraguay', 'Peru', 'Philippines', 'Pitcairn', 'Poland', 'Portugal', 'PuertoRico', 'Qatar', 
                'Reunion', 'Romania', 'RussianFederation', 'Rwanda', 'SaintBarthelemy', 'SaintHelena', 'SaintKitts', 'SaintLucia', 'SaintMartin', 
                'SaintPierre', 'SaintVincent', 'Samoa', 'SanMarino', 'SaoTome', 'SaudiArabia', 'Senegal', 'Serbia', 'Seychelles', 'SierraLeone', 
                'Singapore', 'SintMaartenDutch', 'Slovakia', 'Slovenia', 'SolomonIslands', 'Somalia', 'SouthAfrica', 'SouthKorea', 'SouthSudan', 
                'Spain', 'SriLanka', 'Sudan', 'Suriname', 'Svalbard', 'Swaziland', 'Sweden', 'Switzerland', 'Syria', 'Taiwan', 'Tajikistan', 
                'Tanzania', 'Thailand', 'TimorLeste', 'Togo', 'Tokelau', 'Tonga', 'Trinidad', 'Tunisia', 'Turkey', 'Turkmenistan', 'TurksCaicos', 
                'Tuvalu', 'Uganda', 'Ukraine', 'UnitedArabEmirates', 'UnitedKingdom', 'UnitedStates', 'Uruguay', 'Uzbekistan', 'Vanuatu', 'Vatican', 
                'Venezuela', 'VietNam', 'VirginIslands', 'VirginIslandsGB', 'WallisFutuna', 'WesternSahara', 'Yemen', 'Zambia', 'Zimbabwe'));

        
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        
        
        -- billing_arrears_snapshot -GiST index!
        
        CREATE INDEX billing_arrears_snapshot_from_date_to_date_idx ON billing_arrears_snapshot 
                USING GiST (box(point(from_date,from_date),point(to_date,to_date)) box_ops);
        
        
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.1.4',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;      
