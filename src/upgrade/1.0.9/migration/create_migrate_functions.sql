/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             VISTA-2778 (future version 1.0.9) PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_109(v_schema_name TEXT) RETURNS VOID AS
$$
BEGIN
        EXECUTE 'SET search_path = '||v_schema_name;
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/
        
        -- Foreign Keys
        
        ALTER TABLE aggregated_transfer DROP CONSTRAINT aggregated_transfer_merchant_account_fk;
        ALTER TABLE application_wizard_substep DROP CONSTRAINT application_wizard_substep_step_fk;
        ALTER TABLE appointment DROP CONSTRAINT appointment_agent_fk;
        ALTER TABLE appointment DROP CONSTRAINT appointment_lead_fk;
        ALTER TABLE apt_unit DROP CONSTRAINT apt_unit_building_fk;
        ALTER TABLE apt_unit DROP CONSTRAINT apt_unit_floorplan_fk;
        ALTER TABLE apt_unit_item DROP CONSTRAINT apt_unit_item_apt_unit_fk;
        ALTER TABLE apt_unit DROP CONSTRAINT apt_unit_marketing_fk;
        ALTER TABLE apt_unit_occupancy_segment DROP CONSTRAINT apt_unit_occupancy_segment_lease_fk;
        ALTER TABLE apt_unit_occupancy_segment DROP CONSTRAINT apt_unit_occupancy_segment_unit_fk;
        ALTER TABLE background_check_policy DROP CONSTRAINT background_check_policy_version_fk;
        ALTER TABLE billable_item_adjustment DROP CONSTRAINT billable_item_adjustment_billable_item_fk;
        ALTER TABLE billable_item_adjustment DROP CONSTRAINT billable_item_adjustment_created_by_fk;
        ALTER TABLE billable_item DROP CONSTRAINT billable_item_item_fk;
        ALTER TABLE billing_account DROP CONSTRAINT billing_account_billing_type_fk;
        ALTER TABLE billing_arrears_snapshot$aging_buckets DROP CONSTRAINT billing_arrears_snapshot$aging_buckets_owner_fk;
        ALTER TABLE billing_arrears_snapshot$aging_buckets DROP CONSTRAINT billing_arrears_snapshot$aging_buckets_value_fk;
        ALTER TABLE billing_arrears_snapshot DROP CONSTRAINT billing_arrears_snapshot_billing_account_fk;
        ALTER TABLE billing_arrears_snapshot DROP CONSTRAINT billing_arrears_snapshot_building_fk;
        ALTER TABLE billing_arrears_snapshot DROP CONSTRAINT billing_arrears_snapshot_total_aging_buckets_fk;
        ALTER TABLE billing_bill$line_items DROP CONSTRAINT billing_bill$line_items_owner_fk;
        ALTER TABLE billing_bill$warnings DROP CONSTRAINT billing_bill$warnings_owner_fk;
        ALTER TABLE billing_bill DROP CONSTRAINT billing_bill_billing_account_fk;
        ALTER TABLE billing_bill DROP CONSTRAINT billing_bill_billing_cycle_fk;
        ALTER TABLE billing_bill DROP CONSTRAINT billing_bill_lease_fk;
        ALTER TABLE billing_bill DROP CONSTRAINT billing_bill_previous_cycle_bill_fk;
        ALTER TABLE billing_billing_cycle DROP CONSTRAINT billing_billing_cycle_billing_type_fk;
        ALTER TABLE billing_billing_cycle DROP CONSTRAINT billing_billing_cycle_building_fk;
        ALTER TABLE billing_billing_cycle_stats DROP CONSTRAINT billing_billing_cycle_stats_billing_cycle_fk;
        ALTER TABLE billing_debit_credit_link DROP CONSTRAINT billing_debit_credit_link_credit_item_fk;
        ALTER TABLE billing_debit_credit_link DROP CONSTRAINT billing_debit_credit_link_debit_item_fk;
        ALTER TABLE billing_invoice_charge_tax DROP CONSTRAINT billing_invoice_charge_tax_tax_fk;
        ALTER TABLE billing_invoice_line_item$taxes DROP CONSTRAINT billing_invoice_line_item$taxes_owner_fk;
        ALTER TABLE billing_invoice_line_item$taxes DROP CONSTRAINT billing_invoice_line_item$taxes_value_fk;
        ALTER TABLE billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_adjustment_fk;
        ALTER TABLE billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_billing_account_fk;
        ALTER TABLE billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_billing_cycle_fk;
        ALTER TABLE billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_deposit_fk;
        ALTER TABLE billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_payment_record_fk;
        ALTER TABLE billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_product_charge_fk;
        ALTER TABLE boiler DROP CONSTRAINT boiler_building_fk;
        ALTER TABLE boiler DROP CONSTRAINT boiler_maintenance_contract_contractor_fk;
        ALTER TABLE boiler DROP CONSTRAINT boiler_warranty_contract_contractor_fk;
        ALTER TABLE boilerwarranty$items DROP CONSTRAINT boilerwarranty$items_owner_fk;
        ALTER TABLE boilerwarranty$items DROP CONSTRAINT boilerwarranty$items_value_fk;
        ALTER TABLE building$external_utilities DROP CONSTRAINT building$external_utilities_owner_fk;
        ALTER TABLE building$external_utilities DROP CONSTRAINT building$external_utilities_value_fk;
        ALTER TABLE building$included_utilities DROP CONSTRAINT building$included_utilities_owner_fk;
        ALTER TABLE building$included_utilities DROP CONSTRAINT building$included_utilities_value_fk;
        ALTER TABLE building$media DROP CONSTRAINT building$media_owner_fk;
        ALTER TABLE building$media DROP CONSTRAINT building$media_value_fk;
        ALTER TABLE building_amenity DROP CONSTRAINT building_amenity_building_fk;
        ALTER TABLE building DROP CONSTRAINT building_complex_fk;
        ALTER TABLE building DROP CONSTRAINT building_info_address_country_fk;
        ALTER TABLE building DROP CONSTRAINT building_info_address_province_fk;
        ALTER TABLE building DROP CONSTRAINT building_marketing_fk;
        ALTER TABLE building_merchant_account DROP CONSTRAINT building_merchant_account_building_fk;
        ALTER TABLE building_merchant_account DROP CONSTRAINT building_merchant_account_merchant_account_fk;
        ALTER TABLE building DROP CONSTRAINT building_property_manager_fk;
        ALTER TABLE buildingcontacts$organization_contacts DROP CONSTRAINT buildingcontacts$organization_contacts_owner_fk;
        ALTER TABLE buildingcontacts$organization_contacts DROP CONSTRAINT buildingcontacts$organization_contacts_value_fk;
        ALTER TABLE buildingcontacts$property_contacts DROP CONSTRAINT buildingcontacts$property_contacts_owner_fk;
        ALTER TABLE buildingcontacts$property_contacts DROP CONSTRAINT buildingcontacts$property_contacts_value_fk;
        ALTER TABLE campaign$audience DROP CONSTRAINT campaign$audience_owner_fk;
        ALTER TABLE campaign$audience DROP CONSTRAINT campaign$audience_value_fk;
        ALTER TABLE campaign$media DROP CONSTRAINT campaign$media_owner_fk;
        ALTER TABLE campaign$media DROP CONSTRAINT campaign$media_value_fk;
        ALTER TABLE campaign$schedule DROP CONSTRAINT campaign$schedule_owner_fk;
        ALTER TABLE campaign_history DROP CONSTRAINT campaign_history_campaign_fk;
        ALTER TABLE campaign_history DROP CONSTRAINT campaign_history_tenant_fk;
        ALTER TABLE campaign DROP CONSTRAINT campaign_message_fk;
        ALTER TABLE charge_line_list$charges DROP CONSTRAINT charge_line_list$charges_owner_fk;
        ALTER TABLE charge_line_list$charges DROP CONSTRAINT charge_line_list$charges_value_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_application_charges_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_application_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_monthly_charges_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_one_time_charges_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_payment_split_charges_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_prorated_charges_fk;
        ALTER TABLE city_intro_page$content DROP CONSTRAINT city_intro_page$content_owner_fk;
        ALTER TABLE city_intro_page$content DROP CONSTRAINT city_intro_page$content_value_fk;
        ALTER TABLE city_intro_page DROP CONSTRAINT city_intro_page_province_fk;
        ALTER TABLE city DROP CONSTRAINT city_province_fk;
        ALTER TABLE communication_favorited_messages DROP CONSTRAINT communication_favorited_messages_message_fk;
        ALTER TABLE communication_favorited_messages DROP CONSTRAINT communication_favorited_messages_person_fk;
        ALTER TABLE communication_message DROP CONSTRAINT communication_message_destination_fk;
        ALTER TABLE communication_message DROP CONSTRAINT communication_message_parent_fk;
        ALTER TABLE communication_message DROP CONSTRAINT communication_message_sender_fk;
        ALTER TABLE company$contacts DROP CONSTRAINT company$contacts_owner_fk;
        ALTER TABLE company$contacts DROP CONSTRAINT company$contacts_value_fk;
        ALTER TABLE company$emails DROP CONSTRAINT company$emails_owner_fk;
        ALTER TABLE company$emails DROP CONSTRAINT company$emails_value_fk;
        ALTER TABLE company$phones DROP CONSTRAINT company$phones_owner_fk;
        ALTER TABLE company$phones DROP CONSTRAINT company$phones_value_fk;
        ALTER TABLE concession DROP CONSTRAINT concession_catalog_fk;
        ALTER TABLE concession_v DROP CONSTRAINT concession_v_holder_fk;
        ALTER TABLE concession_v DROP CONSTRAINT concession_v_product_item_type_fk;
        ALTER TABLE contract DROP CONSTRAINT contract_contractor_fk;
        ALTER TABLE crm_role$behaviors DROP CONSTRAINT crm_role$behaviors_owner_fk;
        ALTER TABLE crm_role$rls DROP CONSTRAINT crm_role$rls_owner_fk;
        ALTER TABLE crm_role$rls DROP CONSTRAINT crm_role$rls_value_fk;
        ALTER TABLE crm_user_buildings DROP CONSTRAINT crm_user_buildings_building_fk;
        ALTER TABLE crm_user_buildings DROP CONSTRAINT crm_user_buildings_usr_fk;
        ALTER TABLE crm_user_credential$rls DROP CONSTRAINT crm_user_credential$rls_owner_fk;
        ALTER TABLE crm_user_credential$rls DROP CONSTRAINT crm_user_credential$rls_value_fk;
        ALTER TABLE crm_user_credential DROP CONSTRAINT crm_user_credential_usr_fk;
        ALTER TABLE customer_accepted_terms DROP CONSTRAINT customer_accepted_terms_customer_fk;
        ALTER TABLE customer_credit_check DROP CONSTRAINT customer_credit_check_background_check_policy_fk;
        ALTER TABLE customer_credit_check DROP CONSTRAINT customer_credit_check_created_by_fk;
        ALTER TABLE customer_credit_check DROP CONSTRAINT customer_credit_check_screening_fk;
        ALTER TABLE customer_screening_income DROP CONSTRAINT customer_screening_income_details_fk;
        ALTER TABLE customer_screening_income_info DROP CONSTRAINT customer_screening_income_info_address_country_fk;
        ALTER TABLE customer_screening_income_info DROP CONSTRAINT customer_screening_income_info_address_province_fk;
        ALTER TABLE customer_screening_income DROP CONSTRAINT customer_screening_income_owner_fk;
        ALTER TABLE customer_screening_personal_asset DROP CONSTRAINT customer_screening_personal_asset_owner_fk;
        ALTER TABLE customer_screening DROP CONSTRAINT customer_screening_screene_fk;
        ALTER TABLE customer_screening_v DROP CONSTRAINT customer_screening_v_current_address_country_fk;
        ALTER TABLE customer_screening_v DROP CONSTRAINT customer_screening_v_current_address_province_fk;
        ALTER TABLE customer_screening_v DROP CONSTRAINT customer_screening_v_holder_fk;
        ALTER TABLE customer_screening_v DROP CONSTRAINT customer_screening_v_legal_questions_fk;
        ALTER TABLE customer_screening_v DROP CONSTRAINT customer_screening_v_previous_address_country_fk;
        ALTER TABLE customer_screening_v DROP CONSTRAINT customer_screening_v_previous_address_province_fk;
        ALTER TABLE customer_user_credential DROP CONSTRAINT customer_user_credential_usr_fk;
        ALTER TABLE customer DROP CONSTRAINT customer_user_id_fk;
        ALTER TABLE dashboard_metadata DROP CONSTRAINT dashboard_metadata_owner_user_id_fk;
        ALTER TABLE deposit DROP CONSTRAINT deposit_billable_item_fk;
        ALTER TABLE deposit_lifecycle$interest_adjustments DROP CONSTRAINT deposit_lifecycle$interest_adjustments_owner_fk;
        ALTER TABLE deposit_lifecycle$interest_adjustments DROP CONSTRAINT deposit_lifecycle$interest_adjustments_value_fk;
        ALTER TABLE deposit_lifecycle DROP CONSTRAINT deposit_lifecycle_billing_account_fk;
        ALTER TABLE deposit DROP CONSTRAINT deposit_lifecycle_fk;
        ALTER TABLE deposit_policy_item DROP CONSTRAINT deposit_policy_item_policy_fk;
        ALTER TABLE deposit_policy_item DROP CONSTRAINT deposit_policy_item_product_type_fk;
        ALTER TABLE digital_signature DROP CONSTRAINT digital_signature_person_fk;
        ALTER TABLE elevator DROP CONSTRAINT elevator_building_fk;
        ALTER TABLE elevator DROP CONSTRAINT elevator_maintenance_contract_contractor_fk;
        ALTER TABLE elevator DROP CONSTRAINT elevator_warranty_contract_contractor_fk;
        ALTER TABLE elevatorwarranty$items DROP CONSTRAINT elevatorwarranty$items_owner_fk;
        ALTER TABLE elevatorwarranty$items DROP CONSTRAINT elevatorwarranty$items_value_fk;
        ALTER TABLE email_template DROP CONSTRAINT email_template_policy_fk;
        ALTER TABLE emergency_contact DROP CONSTRAINT emergency_contact_address_country_fk;
        ALTER TABLE emergency_contact DROP CONSTRAINT emergency_contact_address_province_fk;
        ALTER TABLE emergency_contact DROP CONSTRAINT emergency_contact_customer_fk;
        ALTER TABLE employee$employees DROP CONSTRAINT employee$employees_owner_fk;
        ALTER TABLE employee$employees DROP CONSTRAINT employee$employees_value_fk;
        ALTER TABLE employee$portfolios DROP CONSTRAINT employee$portfolios_owner_fk;
        ALTER TABLE employee$portfolios DROP CONSTRAINT employee$portfolios_value_fk;
        ALTER TABLE employee DROP CONSTRAINT employee_manager_fk;
        ALTER TABLE employee DROP CONSTRAINT employee_user_id_fk;
        ALTER TABLE floorplan$media DROP CONSTRAINT floorplan$media_owner_fk;
        ALTER TABLE floorplan$media DROP CONSTRAINT floorplan$media_value_fk;
        ALTER TABLE floorplan_amenity DROP CONSTRAINT floorplan_amenity_floorplan_fk;
        ALTER TABLE floorplan DROP CONSTRAINT floorplan_building_fk;
        ALTER TABLE floorplan DROP CONSTRAINT floorplan_counters_fk;
        ALTER TABLE gadget_content$news DROP CONSTRAINT gadget_content$news_owner_fk;
        ALTER TABLE gadget_content$news DROP CONSTRAINT gadget_content$news_value_fk;
        ALTER TABLE gadget_content$testimonials DROP CONSTRAINT gadget_content$testimonials_owner_fk;
        ALTER TABLE gadget_content$testimonials DROP CONSTRAINT gadget_content$testimonials_value_fk;
        ALTER TABLE gadget_content DROP CONSTRAINT gadget_content_html_content_fk;
        ALTER TABLE gl_code DROP CONSTRAINT gl_code_gl_code_category_fk;
        ALTER TABLE home_page_gadget DROP CONSTRAINT home_page_gadget_content_fk;
        ALTER TABLE html_content DROP CONSTRAINT html_content_locale_fk;
        ALTER TABLE id_assignment_item DROP CONSTRAINT id_assignment_item_policy_fk;
        ALTER TABLE identification_document DROP CONSTRAINT identification_document_id_type_fk;
        ALTER TABLE identification_document_type DROP CONSTRAINT identification_document_type_policy_fk;
        ALTER TABLE insurance_certificate DROP CONSTRAINT insurance_certificate_client_fk;
        ALTER TABLE insurance_certificate DROP CONSTRAINT insurance_certificate_tenant_fk;
        ALTER TABLE insurance_tenant_sure_client DROP CONSTRAINT insurance_tenant_sure_client_tenant_fk;
        ALTER TABLE insurance_tenant_sure_report DROP CONSTRAINT insurance_tenant_sure_report_insurance_fk;
        ALTER TABLE insurance_tenant_sure_transaction DROP CONSTRAINT insurance_tenant_sure_transaction_insurance_fk;
        ALTER TABLE insurance_tenant_sure_transaction DROP CONSTRAINT insurance_tenant_sure_transaction_payment_method_fk;
        ALTER TABLE invoice_adjustment_sub_line_item DROP CONSTRAINT invoice_adjustment_sub_line_item_billable_item_adjustment_fk;
        ALTER TABLE invoice_adjustment_sub_line_item DROP CONSTRAINT invoice_adjustment_sub_line_item_line_item_fk;
        ALTER TABLE invoice_charge_sub_line_item DROP CONSTRAINT invoice_charge_sub_line_item_billable_item_fk;
        ALTER TABLE invoice_charge_sub_line_item DROP CONSTRAINT invoice_charge_sub_line_item_line_item_fk;
        ALTER TABLE invoice_concession_sub_line_item DROP CONSTRAINT invoice_concession_sub_line_item_concession_fk;
        ALTER TABLE invoice_concession_sub_line_item DROP CONSTRAINT invoice_concession_sub_line_item_line_item_fk;
        ALTER TABLE issue_classification DROP CONSTRAINT issue_classification_subject_details_fk;
        ALTER TABLE issue_repair_subject DROP CONSTRAINT issue_repair_subject_issue_element_fk;
        ALTER TABLE issue_subject_details DROP CONSTRAINT issue_subject_details_subject_fk;
        ALTER TABLE late_fee_item DROP CONSTRAINT late_fee_item_policy_fk;
        ALTER TABLE lead DROP CONSTRAINT lead_agent_fk;
        ALTER TABLE lead DROP CONSTRAINT lead_floorplan_fk;
        ALTER TABLE lead_guest DROP CONSTRAINT lead_guest_lead_fk;
        ALTER TABLE lead DROP CONSTRAINT lead_lease_fk;
        ALTER TABLE lease_adjustment DROP CONSTRAINT lease_adjustment_billing_account_fk;
        ALTER TABLE lease_adjustment DROP CONSTRAINT lease_adjustment_created_by_fk;
        ALTER TABLE lease_adjustment DROP CONSTRAINT lease_adjustment_item_type_fk;
        ALTER TABLE lease_adjustment_policy_item$taxes DROP CONSTRAINT lease_adjustment_policy_item$taxes_owner_fk;
        ALTER TABLE lease_adjustment_policy_item$taxes DROP CONSTRAINT lease_adjustment_policy_item$taxes_value_fk;
        ALTER TABLE lease_adjustment_policy_item DROP CONSTRAINT lease_adjustment_policy_item_lease_adjustment_reason_fk;
        ALTER TABLE lease_adjustment_policy_item DROP CONSTRAINT lease_adjustment_policy_item_policy_fk;
        ALTER TABLE lease_adjustment_reason DROP CONSTRAINT lease_adjustment_reason_gl_code_fk;
        ALTER TABLE lease_application DROP CONSTRAINT lease_application_decided_by_fk;
        ALTER TABLE lease_application DROP CONSTRAINT lease_application_lease_fk;
        ALTER TABLE lease_application DROP CONSTRAINT lease_application_online_application_fk;
        ALTER TABLE lease DROP CONSTRAINT lease_billing_account_fk;
        ALTER TABLE lease_billing_type_policy_item DROP CONSTRAINT lease_billing_type_policy_item_lease_billing_policy_fk;
        ALTER TABLE lease DROP CONSTRAINT lease_current_term_fk;
        ALTER TABLE lease DROP CONSTRAINT lease_next_term_fk;
        ALTER TABLE lease_participant DROP CONSTRAINT lease_participant_customer_fk;
        ALTER TABLE lease_participant DROP CONSTRAINT lease_participant_lease_fk;
        ALTER TABLE lease DROP CONSTRAINT lease_previous_term_fk;
        ALTER TABLE lease_term DROP CONSTRAINT lease_term_lease_fk;
        ALTER TABLE lease_term_participant DROP CONSTRAINT lease_term_participant_application_fk;
        ALTER TABLE lease_term_participant DROP CONSTRAINT lease_term_participant_credit_check_fk;
        ALTER TABLE lease_term_participant DROP CONSTRAINT lease_term_participant_lease_participant_fk;
        ALTER TABLE lease_term_participant DROP CONSTRAINT lease_term_participant_lease_term_v_fk;
        ALTER TABLE lease_term_participant DROP CONSTRAINT lease_term_participant_screening_fk;
        ALTER TABLE lease_term_participant DROP CONSTRAINT lease_term_participant_tenant_fk;
        ALTER TABLE lease_term_v DROP CONSTRAINT lease_term_v_holder_fk;
        ALTER TABLE lease_term_v DROP CONSTRAINT lease_term_v_lease_products_service_item_fk;
        ALTER TABLE lease_term_vlease_products$concessions DROP CONSTRAINT lease_term_vlease_products$concessions_owner_fk;
        ALTER TABLE lease_term_vlease_products$concessions DROP CONSTRAINT lease_term_vlease_products$concessions_value_fk;
        ALTER TABLE lease_term_vlease_products$feature_items DROP CONSTRAINT lease_term_vlease_products$feature_items_owner_fk;
        ALTER TABLE lease_term_vlease_products$feature_items DROP CONSTRAINT lease_term_vlease_products$feature_items_value_fk;
        ALTER TABLE lease DROP CONSTRAINT lease_unit_fk;
        ALTER TABLE legal_documentation$co_application DROP CONSTRAINT legal_documentation$co_application_owner_fk;
        ALTER TABLE legal_documentation$co_application DROP CONSTRAINT legal_documentation$co_application_value_fk;
        ALTER TABLE legal_documentation$guarantor_application DROP CONSTRAINT legal_documentation$guarantor_application_owner_fk;
        ALTER TABLE legal_documentation$guarantor_application DROP CONSTRAINT legal_documentation$guarantor_application_value_fk;
        ALTER TABLE legal_documentation$lease DROP CONSTRAINT legal_documentation$lease_owner_fk;
        ALTER TABLE legal_documentation$lease DROP CONSTRAINT legal_documentation$lease_value_fk;
        ALTER TABLE legal_documentation$main_application DROP CONSTRAINT legal_documentation$main_application_owner_fk;
        ALTER TABLE legal_documentation$main_application DROP CONSTRAINT legal_documentation$main_application_value_fk;
        ALTER TABLE legal_documentation$payment_authorization DROP CONSTRAINT legal_documentation$payment_authorization_owner_fk;
        ALTER TABLE legal_documentation$payment_authorization DROP CONSTRAINT legal_documentation$payment_authorization_value_fk;
        ALTER TABLE legal_terms_content DROP CONSTRAINT legal_terms_content_locale_fk;
        ALTER TABLE legal_terms_descriptor$content DROP CONSTRAINT legal_terms_descriptor$content_owner_fk;
        ALTER TABLE legal_terms_descriptor$content DROP CONSTRAINT legal_terms_descriptor$content_value_fk;
        ALTER TABLE locker_area DROP CONSTRAINT locker_area_building_fk;
        ALTER TABLE locker DROP CONSTRAINT locker_locker_area_fk;
        ALTER TABLE maintenance DROP CONSTRAINT maintenance_contract_contractor_fk;
        ALTER TABLE maintenance_request DROP CONSTRAINT maintenance_request_issue_classification_fk;
        ALTER TABLE maintenance_request DROP CONSTRAINT maintenance_request_lease_participant_fk;
        ALTER TABLE marketing$ad_blurbs DROP CONSTRAINT marketing$ad_blurbs_owner_fk;
        ALTER TABLE marketing$ad_blurbs DROP CONSTRAINT marketing$ad_blurbs_value_fk;
        ALTER TABLE news DROP CONSTRAINT news_locale_fk;
        ALTER TABLE notes_and_attachments DROP CONSTRAINT notes_and_attachments_crmuser_fk;
        ALTER TABLE nsf_fee_item DROP CONSTRAINT nsf_fee_item_policy_fk;
        ALTER TABLE online_application$signatures DROP CONSTRAINT online_application$signatures_owner_fk;
        ALTER TABLE online_application$signatures DROP CONSTRAINT online_application$signatures_value_fk;
        ALTER TABLE online_application$steps DROP CONSTRAINT online_application$steps_owner_fk;
        ALTER TABLE online_application$steps DROP CONSTRAINT online_application$steps_value_fk;
        ALTER TABLE online_application DROP CONSTRAINT online_application_customer_fk;
        ALTER TABLE online_application DROP CONSTRAINT online_application_master_online_application_fk;
        ALTER TABLE organization_contact DROP CONSTRAINT organization_contact_person_fk;
        ALTER TABLE organization_contacts$contact_list DROP CONSTRAINT organization_contacts$contact_list_owner_fk;
        ALTER TABLE organization_contacts$contact_list DROP CONSTRAINT organization_contacts$contact_list_value_fk;
        ALTER TABLE organization_contacts DROP CONSTRAINT organization_contacts_company_role_fk;
        ALTER TABLE owner DROP CONSTRAINT owner_building_fk;
        ALTER TABLE owner DROP CONSTRAINT owner_company_fk;
        ALTER TABLE owner_group$owner_list DROP CONSTRAINT owner_group$owner_list_owner_fk;
        ALTER TABLE owner_group$owner_list DROP CONSTRAINT owner_group$owner_list_value_fk;
        ALTER TABLE padpolicy_item DROP CONSTRAINT padpolicy_item_padpolicy_fk;
        ALTER TABLE page_caption DROP CONSTRAINT page_caption_locale_fk;
        ALTER TABLE page_content DROP CONSTRAINT page_content_descriptor_fk;
        ALTER TABLE page_content DROP CONSTRAINT page_content_image_fk;
        ALTER TABLE page_content DROP CONSTRAINT page_content_locale_fk;
        ALTER TABLE page_descriptor$caption DROP CONSTRAINT page_descriptor$caption_owner_fk;
        ALTER TABLE page_descriptor$caption DROP CONSTRAINT page_descriptor$caption_value_fk;
        ALTER TABLE page_meta_tags DROP CONSTRAINT page_meta_tags_locale_fk;
        ALTER TABLE parking DROP CONSTRAINT parking_building_fk;
        ALTER TABLE parking_spot DROP CONSTRAINT parking_spot_parking_fk;
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_application_fk;
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_payment_method_billing_address_country_fk;
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_payment_method_billing_address_province_fk;
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_payment_method_customer_fk;
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_payment_method_details_fk;
        ALTER TABLE payment_method DROP CONSTRAINT payment_method_billing_address_country_fk;
        ALTER TABLE payment_method DROP CONSTRAINT payment_method_billing_address_province_fk;
        ALTER TABLE payment_method DROP CONSTRAINT payment_method_customer_fk;
        ALTER TABLE payment_method DROP CONSTRAINT payment_method_details_fk;
        ALTER TABLE payment_method DROP CONSTRAINT payment_method_tenant_fk;
        ALTER TABLE payment_record DROP CONSTRAINT payment_record_aggregated_transfer_fk;
        ALTER TABLE payment_record DROP CONSTRAINT payment_record_aggregated_transfer_return_fk;
        ALTER TABLE payment_record DROP CONSTRAINT payment_record_billing_account_fk;
        ALTER TABLE payment_record_external DROP CONSTRAINT payment_record_external_billing_account_fk;
        ALTER TABLE payment_record_external DROP CONSTRAINT payment_record_external_payment_record_fk;
        ALTER TABLE payment_record DROP CONSTRAINT payment_record_lease_term_participant_fk;
        ALTER TABLE payment_record DROP CONSTRAINT payment_record_merchant_account_fk;
        ALTER TABLE payment_record DROP CONSTRAINT payment_record_pad_billing_cycle_fk;
        ALTER TABLE payment_record DROP CONSTRAINT payment_record_payment_method_fk;
        ALTER TABLE payment_record DROP CONSTRAINT payment_record_preauthorized_payment_fk;
        ALTER TABLE payment_record_processing DROP CONSTRAINT payment_record_processing_aggregated_transfer_fk;
        ALTER TABLE payment_record_processing DROP CONSTRAINT payment_record_processing_payment_record_fk;
        ALTER TABLE payment_transaction_account$allowed_transaction DROP CONSTRAINT payment_transaction_account$allowed_transaction_owner_fk;
        ALTER TABLE payment_transactions_policy$policy_items DROP CONSTRAINT payment_transactions_policy$policy_items_owner_fk;
        ALTER TABLE payment_transactions_policy$policy_items DROP CONSTRAINT payment_transactions_policy$policy_items_value_fk;
        ALTER TABLE payments_summary DROP CONSTRAINT payments_summary_building_fk;
        ALTER TABLE payments_summary DROP CONSTRAINT payments_summary_merchant_account_fk;
        ALTER TABLE pet_constraints DROP CONSTRAINT pet_constraints_pet_fk;
        ALTER TABLE pet_policy$pet_constraints DROP CONSTRAINT pet_policy$pet_constraints_owner_fk;
        ALTER TABLE pet_policy$pet_constraints DROP CONSTRAINT pet_policy$pet_constraints_value_fk;
        ALTER TABLE portal_image_resource DROP CONSTRAINT portal_image_resource_image_resource_fk;
        ALTER TABLE portal_image_resource DROP CONSTRAINT portal_image_resource_locale_fk;
        ALTER TABLE portal_image_set$image_set DROP CONSTRAINT portal_image_set$image_set_owner_fk;
        ALTER TABLE portal_image_set$image_set DROP CONSTRAINT portal_image_set$image_set_value_fk;
        ALTER TABLE portal_image_set DROP CONSTRAINT portal_image_set_locale_fk;
        ALTER TABLE portfolio$buildings DROP CONSTRAINT portfolio$buildings_owner_fk;
        ALTER TABLE portfolio$buildings DROP CONSTRAINT portfolio$buildings_value_fk;
        ALTER TABLE preauthorized_payment DROP CONSTRAINT preauthorized_payment_payment_method_fk;
        ALTER TABLE preauthorized_payment DROP CONSTRAINT preauthorized_payment_tenant_fk;
        ALTER TABLE product_catalog DROP CONSTRAINT product_catalog_building_fk;
        ALTER TABLE product DROP CONSTRAINT product_catalog_fk;
        ALTER TABLE product_item DROP CONSTRAINT product_item_item_type_fk;
        ALTER TABLE product_item DROP CONSTRAINT product_item_product_fk;
        ALTER TABLE product_item_type DROP CONSTRAINT product_item_type_gl_code_fk;
        ALTER TABLE product_tax_policy_item$taxes DROP CONSTRAINT product_tax_policy_item$taxes_owner_fk;
        ALTER TABLE product_tax_policy_item$taxes DROP CONSTRAINT product_tax_policy_item$taxes_value_fk;
        ALTER TABLE product_tax_policy_item DROP CONSTRAINT product_tax_policy_item_policy_fk;
        ALTER TABLE product_tax_policy_item DROP CONSTRAINT product_tax_policy_item_product_item_type_fk;
        ALTER TABLE product_v$concessions DROP CONSTRAINT product_v$concessions_owner_fk;
        ALTER TABLE product_v$concessions DROP CONSTRAINT product_v$concessions_value_fk;
        ALTER TABLE product_v$features DROP CONSTRAINT product_v$features_owner_fk;
        ALTER TABLE product_v$features DROP CONSTRAINT product_v$features_value_fk;
        ALTER TABLE product_v DROP CONSTRAINT product_v_holder_fk;
        ALTER TABLE property_account_info DROP CONSTRAINT property_account_info_property_fk;
        ALTER TABLE property_phone DROP CONSTRAINT property_phone_provider_fk;
        ALTER TABLE province DROP CONSTRAINT province_country_fk;
        ALTER TABLE pt_vehicle DROP CONSTRAINT pt_vehicle_country_fk;
        ALTER TABLE pt_vehicle DROP CONSTRAINT pt_vehicle_province_fk;
        ALTER TABLE recipient$contact_list DROP CONSTRAINT recipient$contact_list_owner_fk;
        ALTER TABLE recipient$contact_list DROP CONSTRAINT recipient$contact_list_value_fk;
        ALTER TABLE reservation_schedule DROP CONSTRAINT reservation_schedule_building_fk;
        ALTER TABLE reservation_schedule DROP CONSTRAINT reservation_schedule_tenant_fk;
        ALTER TABLE resident_portal_settings$custom_html DROP CONSTRAINT resident_portal_settings$custom_html_owner_fk;
        ALTER TABLE resident_portal_settings$custom_html DROP CONSTRAINT resident_portal_settings$custom_html_value_fk;
        ALTER TABLE resident_portal_settings$proxy_whitelist DROP CONSTRAINT resident_portal_settings$proxy_whitelist_owner_fk;
        ALTER TABLE roof DROP CONSTRAINT roof_building_fk;
        ALTER TABLE roof DROP CONSTRAINT roof_maintenance_contract_contractor_fk;
        ALTER TABLE roof_segment DROP CONSTRAINT roof_segment_roof_fk;
        ALTER TABLE roof DROP CONSTRAINT roof_warranty_contract_contractor_fk;
        ALTER TABLE roofwarranty$items DROP CONSTRAINT roofwarranty$items_owner_fk;
        ALTER TABLE roofwarranty$items DROP CONSTRAINT roofwarranty$items_value_fk;
        ALTER TABLE showing DROP CONSTRAINT showing_appointment_fk;
        ALTER TABLE showing DROP CONSTRAINT showing_unit_fk;
        ALTER TABLE site_descriptor$banner DROP CONSTRAINT site_descriptor$banner_owner_fk;
        ALTER TABLE site_descriptor$banner DROP CONSTRAINT site_descriptor$banner_value_fk;
        ALTER TABLE site_descriptor$city_intro_pages DROP CONSTRAINT site_descriptor$city_intro_pages_owner_fk;
        ALTER TABLE site_descriptor$city_intro_pages DROP CONSTRAINT site_descriptor$city_intro_pages_value_fk;
        ALTER TABLE site_descriptor$home_page_gadgets_narrow DROP CONSTRAINT site_descriptor$home_page_gadgets_narrow_owner_fk;
        ALTER TABLE site_descriptor$home_page_gadgets_narrow DROP CONSTRAINT site_descriptor$home_page_gadgets_narrow_value_fk;
        ALTER TABLE site_descriptor$home_page_gadgets_wide DROP CONSTRAINT site_descriptor$home_page_gadgets_wide_owner_fk;
        ALTER TABLE site_descriptor$home_page_gadgets_wide DROP CONSTRAINT site_descriptor$home_page_gadgets_wide_value_fk;
        ALTER TABLE site_descriptor$logo DROP CONSTRAINT site_descriptor$logo_owner_fk;
        ALTER TABLE site_descriptor$logo DROP CONSTRAINT site_descriptor$logo_value_fk;
        ALTER TABLE site_descriptor$meta_tags DROP CONSTRAINT site_descriptor$meta_tags_owner_fk;
        ALTER TABLE site_descriptor$meta_tags DROP CONSTRAINT site_descriptor$meta_tags_value_fk;
        ALTER TABLE site_descriptor$site_titles DROP CONSTRAINT site_descriptor$site_titles_owner_fk;
        ALTER TABLE site_descriptor$site_titles DROP CONSTRAINT site_descriptor$site_titles_value_fk;
        ALTER TABLE site_descriptor$slogan DROP CONSTRAINT site_descriptor$slogan_owner_fk;
        ALTER TABLE site_descriptor$slogan DROP CONSTRAINT site_descriptor$slogan_value_fk;
        ALTER TABLE site_descriptor$social_links DROP CONSTRAINT site_descriptor$social_links_owner_fk;
        ALTER TABLE site_descriptor$social_links DROP CONSTRAINT site_descriptor$social_links_value_fk;
        ALTER TABLE site_descriptor DROP CONSTRAINT site_descriptor_crm_logo_fk;
        ALTER TABLE site_descriptor DROP CONSTRAINT site_descriptor_resident_portal_settings_fk;
        ALTER TABLE site_descriptor DROP CONSTRAINT site_descriptor_site_palette_fk;
        ALTER TABLE site_descriptor DROP CONSTRAINT site_descriptor_update_flag_fk;
        ALTER TABLE site_titles DROP CONSTRAINT site_titles_locale_fk;
        ALTER TABLE summary DROP CONSTRAINT summary_application_fk;
        ALTER TABLE tenant_charge_list$charges DROP CONSTRAINT tenant_charge_list$charges_owner_fk;
        ALTER TABLE tenant_charge_list$charges DROP CONSTRAINT tenant_charge_list$charges_value_fk;
        ALTER TABLE tenant_charge DROP CONSTRAINT tenant_charge_tenant_fk;
        ALTER TABLE testimonial DROP CONSTRAINT testimonial_locale_fk;
        ALTER TABLE unit_availability_status DROP CONSTRAINT unit_availability_status_building_fk;
        ALTER TABLE unit_availability_status DROP CONSTRAINT unit_availability_status_complex_fk;
        ALTER TABLE unit_availability_status DROP CONSTRAINT unit_availability_status_floorplan_fk;
        ALTER TABLE unit_availability_status DROP CONSTRAINT unit_availability_status_unit_fk;
        ALTER TABLE unit_turnover_stats DROP CONSTRAINT unit_turnover_stats_building_fk;
        ALTER TABLE vendor$contacts DROP CONSTRAINT vendor$contacts_owner_fk;
        ALTER TABLE vendor$contacts DROP CONSTRAINT vendor$contacts_value_fk;
        ALTER TABLE vendor$emails DROP CONSTRAINT vendor$emails_owner_fk;
        ALTER TABLE vendor$emails DROP CONSTRAINT vendor$emails_value_fk;
        ALTER TABLE vendor$phones DROP CONSTRAINT vendor$phones_owner_fk;
        ALTER TABLE vendor$phones DROP CONSTRAINT vendor$phones_value_fk;
        ALTER TABLE warranty$items DROP CONSTRAINT warranty$items_owner_fk;
        ALTER TABLE warranty$items DROP CONSTRAINT warranty$items_value_fk;
        ALTER TABLE warranty DROP CONSTRAINT warranty_contract_contractor_fk;
        ALTER TABLE yardi_charge_code DROP CONSTRAINT yardi_charge_code_product_item_type_fk;


        -- Check Constraints
        
        ALTER TABLE aging_buckets DROP CONSTRAINT aging_buckets_debit_type_e_ck;
        ALTER TABLE arpolicy DROP CONSTRAINT arpolicy_credit_debit_rule_e_ck;
        ALTER TABLE concession_v DROP CONSTRAINT concession_v_product_item_type_discriminator_d_ck;
        ALTER TABLE deposit_policy_item DROP CONSTRAINT deposit_policy_item_product_type_discriminator_d_ck;
        ALTER TABLE lead DROP CONSTRAINT lead_lease_type_e_ck;
        ALTER TABLE lease_adjustment_reason DROP CONSTRAINT lease_adjustment_reason_action_type_e_ck;
        ALTER TABLE lease DROP CONSTRAINT lease_lease_type_e_ck;
        ALTER TABLE padpolicy DROP CONSTRAINT padpolicy_charge_type_e_ck;
        ALTER TABLE padpolicy_item DROP CONSTRAINT padpolicy_item_debit_type_e_ck;
        ALTER TABLE pet_constraints DROP CONSTRAINT pet_constraints_pet_discriminator_d_ck;
        ALTER TABLE product DROP CONSTRAINT product_feature_type_ck;
        ALTER TABLE product DROP CONSTRAINT product_feature_type_e_ck;
        ALTER TABLE product_item DROP CONSTRAINT product_item_item_type_discriminator_d_ck;
        ALTER TABLE product_item_type DROP CONSTRAINT product_item_type_feature_type_e_ck;
        ALTER TABLE product_item_type DROP CONSTRAINT product_item_type_id_discriminator_ck;
        ALTER TABLE product_item_type DROP CONSTRAINT product_item_type_service_type_e_ck;
        ALTER TABLE product DROP CONSTRAINT product_service_type_ck;
        ALTER TABLE product DROP CONSTRAINT product_service_type_e_ck;
        ALTER TABLE product_tax_policy_item DROP CONSTRAINT product_tax_policy_item_product_item_type_discriminator_d_ck;
        ALTER TABLE yardi_charge_code DROP CONSTRAINT yardi_charge_code_product_item_type_discriminator_d_ck;

        
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
        
        -- aging_buckets
        
        ALTER TABLE aging_buckets ADD COLUMN ar_code VARCHAR(50);
        
        -- arcode
        
        CREATE TABLE arcode
        (
                id                              BIGINT                          NOT NULL,
                code_type                       VARCHAR(50),
                name                            VARCHAR(50),
                gl_code                         BIGINT,
                updated                         TIMESTAMP,
                reserved                        BOOLEAN,
                lad_id                          BIGINT,                        -- lease_adjustment_reason.id - to be dropped at the end
                pit_id                          BIGINT,                        -- product_item_type.id - also not gonna last
                        CONSTRAINT      arcode_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE arcode OWNER TO vista;
        
        
        -- billing_invoice_line_item
        
        ALTER TABLE billing_invoice_line_item ADD COLUMN ar_code BIGINT;
        
        
        -- concession_v
        
        ALTER TABLE concession_v ADD COLUMN product_code BIGINT;
        
        -- deposit_policy_item
        
        ALTER TABLE deposit_policy_item ADD COLUMN product_code BIGINT;
        
        -- lease_adjustment
        
        ALTER TABLE lease_adjustment ADD COLUMN code BIGINT;
        
        -- lease_adjustment_policy_item
        
        ALTER TABLE lease_adjustment_policy_item ADD COLUMN code BIGINT;
        
        -- maintenance_request
        
        ALTER TABLE maintenance_request ADD COLUMN category BIGINT;
        
        -- maintenance_request_category
        
        CREATE TABLE maintenance_request_category
        (
                id                      BIGINT                  NOT NULL,
                level                   BIGINT,
                name                    VARCHAR(500),
                        CONSTRAINT      maintenance_request_category_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE maintenance_request_category OWNER TO vista;
        
        -- maintenance_request_category_level
        
        CREATE TABLE maintenance_request_category_level
        (
                id                      BIGINT                  NOT NULL,
                level                   INT,
                name                    VARCHAR(500),
                        CONSTRAINT      maintenance_request_category_level_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE maintenance_request_category_level OWNER TO vista;
        
        -- maintenance_request_category$sub_categories
        
        CREATE TABLE maintenance_request_category$sub_categories
        (
                id                      BIGINT                  NOT NULL,
                owner                   BIGINT,
                value                   BIGINT,
                seq                     INTEGER,
                        CONSTRAINT      maintenance_request_category$sub_categories_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE maintenance_request_category$sub_categories OWNER TO vista;
        
        
        -- padpolicy_item
        
        ALTER TABLE padpolicy_item RENAME COLUMN debit_type TO debit_type_old;
        
        ALTER TABLE padpolicy_item ADD COLUMN debit_type BIGINT;
        
        
        -- payment_record
        
        ALTER TABLE payment_record ADD COLUMN notice VARCHAR(500);
        
        -- product
        
        ALTER TABLE product ADD COLUMN code_type VARCHAR(50);
        
        -- product_item
        
        ALTER TABLE product_item ADD COLUMN code BIGINT;
        
        -- product_tax_policy_item
        
        ALTER TABLE product_tax_policy_item ADD COLUMN product_code BIGINT;
        
        -- yardi_charge_code
        
        ALTER TABLE yardi_charge_code ADD COLUMN ar_code BIGINT;
        
        -- yardi_service_request
        
        CREATE TABLE yardi_service_request
        (
                id                              BIGINT          NOT NULL,
                request_id                      INT,
                property_code                   VARCHAR(500),
                unit_code                       VARCHAR(500),
                tenant_code                     VARCHAR(500),
                vendor_code                     VARCHAR(500),
                request_description_brief       VARCHAR(500),
                request_description_full        VARCHAR(250),
                priority                        VARCHAR(500),
                permission_to_enter             BOOLEAN,
                access_notes                    VARCHAR(500),
                problem_description             VARCHAR(500),
                technical_notes                 VARCHAR(500),
                tenant_caused                   BOOLEAN,
                requestor_name                  VARCHAR(500),
                requestor_phone                 VARCHAR(500),
                requestor_email                 VARCHAR(500),
                authorized_by                   VARCHAR(500),
                current_status                  VARCHAR(500),
                resolution                      VARCHAR(500),
                        CONSTRAINT      yardi_service_request_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE yardi_service_request OWNER TO vista;
        
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        -- aging_buckets
        
        EXECUTE 'UPDATE '||v_schema_name||'.aging_buckets '
                ||'SET  ar_code = '
                ||'     CASE WHEN debit_type = ''accountCharge'' THEN ''AccountCharge'' '
                ||'     WHEN debit_type = ''addOn'' THEN ''AddOn'' '
                ||'     WHEN debit_type = ''booking'' THEN ''OneTime'' '
                ||'     WHEN debit_type = ''deposit'' THEN ''Deposit'' '
                ||'     WHEN debit_type = ''latePayment'' THEN ''LatePayment'' '
                ||'     WHEN debit_type = ''lease'' THEN ''Residential'' '
                ||'     WHEN debit_type = ''locker'' THEN ''Locker'' '
                ||'     WHEN debit_type = ''nsf'' THEN ''NSF'' '
                ||'     WHEN debit_type = ''other'' THEN ''ExternalCharge'' '
                ||'     WHEN debit_type = ''parking'' THEN ''Parking'' '
                ||'     WHEN debit_type = ''pet'' THEN ''Pet'' '
                ||'     WHEN debit_type = ''total'' THEN NULL '
                ||'     WHEN debit_type = ''utility'' THEN ''Utility'' END ';
        
        
        -- arcode data import from lease_adjustment_type
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.arcode (id,code_type,name,gl_code,updated,lad_id) '
                ||'(SELECT      nextval(''public.arcode_seq'') AS id, '
                ||'             CASE WHEN action_type = ''charge'' THEN ''AccountCharge'' '
                ||'             WHEN action_type = ''credit'' THEN ''AccountCredit'' END AS code_type, '
                ||'             name,gl_code,updated,id AS lad_id '
                ||' FROM        '||v_schema_name||'.lease_adjustment_reason '
                ||' ORDER BY    id )';
                
        
        -- arcode data import from product_item_type
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.arcode (id,code_type,name,gl_code,updated,pit_id) '
                ||'(SELECT      nextval(''public.arcode_seq'') AS id, '
                ||'             CASE WHEN service_type = ''commercialUnit'' THEN ''Commercial'' '
                ||'             WHEN service_type = ''residentialShortTermUnit'' THEN ''ResidentialShortTerm'' '
                ||'             WHEN service_type = ''residentialUnit'' THEN ''Residential'' '
                ||'             WHEN feature_type = ''addOn'' THEN ''AddOn'' '
                ||'             WHEN feature_type = ''booking'' THEN ''Residential'' '
                ||'             WHEN feature_type = ''locker'' THEN ''Locker'' '
                ||'             WHEN feature_type = ''oneTimeCharge'' THEN ''OneTime'' '
                ||'             WHEN feature_type = ''parking'' THEN ''Parking'' '
                ||'             WHEN feature_type = ''pet'' THEN ''Pet'' '
                ||'             WHEN feature_type = ''utility'' THEN ''Utility'' END AS code_type,'
                ||'             name,gl_code,updated,id AS pit_id '
                ||' FROM        '||v_schema_name||'.product_item_type '
                ||' ORDER BY    id ) ';
                
              
        
        
        -- arpolicy
        
        EXECUTE 'UPDATE '||v_schema_name||'.arpolicy '
                ||'SET credit_debit_rule = '
                ||'CASE WHEN credit_debit_rule IN (''byDueDate'',''byAgingBucketAndDebitType'') THEN ''oldestDebtFirst'' '
                ||'WHEN credit_debit_rule = ''byDebitType'' THEN ''rentDebtLast'' END ';
        
        
        -- billing_invoice_line_item
        
        EXECUTE 'WITH t AS (SELECT      id,CASE WHEN debit_type = ''accountCharge'' THEN ''AccountCharge'' '
                ||'             WHEN debit_type = ''addOn'' THEN ''AddOn'' '
                ||'             WHEN debit_type = ''booking'' THEN ''OneTime'' '
                ||'             WHEN debit_type = ''deposit'' THEN ''Deposit'' '
                ||'             WHEN debit_type = ''latePayment'' THEN ''LatePayment'' '
                ||'             WHEN debit_type = ''lease'' THEN ''Residential'' '
                ||'             WHEN debit_type = ''locker'' THEN ''Locker'' '
                ||'             WHEN debit_type = ''nsf'' THEN ''NSF'' '
                ||'             WHEN debit_type = ''other'' THEN ''ExternalCharge'' '
                ||'             WHEN debit_type = ''parking'' THEN ''Parking'' '
                ||'             WHEN debit_type = ''pet'' THEN ''Pet'' '
                ||'             WHEN debit_type = ''total'' THEN NULL '
                ||'             WHEN debit_type = ''utility'' THEN ''Utility'' END  AS debit_type_new '
                ||'     FROM    '||v_schema_name||'.billing_invoice_line_item ) '
                ||'UPDATE '||v_schema_name||'.billing_invoice_line_item AS a '
                ||'SET  ar_code = b.arcode '
                ||'FROM (SELECT t.id,a.id AS arcode '
                ||'     FROM t '
                ||'     JOIN '||v_schema_name||'.arcode a ON (t.debit_type_new = a.name)) AS b '
                ||'WHERE a.id = b.id ';   
        
        
        -- concession_v
        
        EXECUTE 'UPDATE '||v_schema_name||'.concession_v AS c '
                ||'SET  product_code = a.id '
                ||'FROM '||v_schema_name||'.arcode  AS a '
                ||'WHERE c.product_item_type = a.pit_id ';
                
        
        -- deposit_policy_item
        
        EXECUTE 'UPDATE '||v_schema_name||'.deposit_policy_item AS d '
                ||'SET  product_code = a.id '
                ||'FROM '||v_schema_name||'.arcode  AS a '
                ||'WHERE d.product_type = a.pit_id ';
        
        
        -- html_content
        
        ALTER TABLE html_content ADD COLUMN updated TIMESTAMP;
                
        
        -- lead
        
        EXECUTE 'UPDATE '||v_schema_name||'.lead '
                ||'SET lease_type = ''Residential'' '
                ||'WHERE lease_type = ''residentialUnit'' ';
                
        
        EXECUTE 'UPDATE '||v_schema_name||'.lead '
                ||'SET lease_type = ''ResidentialShortTerm'' '
                ||'WHERE lease_type = ''residentialShortTermUnit'' ';
        
       
       EXECUTE 'UPDATE '||v_schema_name||'.lead '
                ||'SET lease_type = ''Commercial'' '
                ||'WHERE lease_type = ''commercialUnit'' ';
                
               
        -- lease
        
        EXECUTE 'UPDATE '||v_schema_name||'.lease '
                ||'SET lease_type = ''Residential'' '
                ||'WHERE lease_type = ''residentialUnit'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.lease '
                ||'SET lease_type = ''ResidentialShortTerm'' '
                ||'WHERE lease_type = ''residentialShortTermUnit'' '; 
                
        EXECUTE 'UPDATE '||v_schema_name||'.lease '
                ||'SET lease_type = ''Commercial'' '
                ||'WHERE lease_type = ''commercialUnit'' ';
               
        -- lease_adjustment
        
        EXECUTE 'UPDATE '||v_schema_name||'.lease_adjustment  AS l '
                ||'SET  code = a.id '
                ||'FROM '||v_schema_name||'.arcode AS a '
                ||'WHERE l.item_type = a.lad_id ';
                
        -- lease_adjustment_policy_item
        
        EXECUTE 'UPDATE '||v_schema_name||'.lease_adjustment_policy_item AS l '
                ||'SET  code = a.id '
                ||'FROM '||v_schema_name||'.arcode AS a '
                ||'WHERE        l.lease_adjustment_reason = a.lad_id ';
                
        -- padpolicy_item
       
        EXECUTE 'WITH t AS (SELECT debit_type_old, '
                ||'             CASE WHEN debit_type_old = ''accountCharge'' THEN ''AccountCharge'' '
                ||'             WHEN debit_type_old = ''addOn'' THEN ''AddOn'' '
                ||'             WHEN debit_type_old = ''booking'' THEN ''OneTime'' '
                ||'             WHEN debit_type_old = ''deposit'' THEN ''Deposit'' '
                ||'             WHEN debit_type_old = ''latePayment'' THEN ''LatePayment'' '
                ||'             WHEN debit_type_old = ''lease'' THEN ''Residential'' '
                ||'             WHEN debit_type_old = ''locker'' THEN ''Locker'' '
                ||'             WHEN debit_type_old = ''nsf'' THEN ''NSF'' '
                ||'             WHEN debit_type_old = ''other'' THEN ''ExternalCharge'' '
                ||'             WHEN debit_type_old = ''parking'' THEN ''Parking'' '
                ||'             WHEN debit_type_old = ''pet'' THEN ''Pet'' '
                ||'             WHEN debit_type_old = ''total'' THEN NULL '
                ||'             WHEN debit_type_old = ''utility'' THEN ''Utility'' END  AS debit_type '
                ||'     FROM    '||v_schema_name||'.padpolicy_item ) '
                ||'UPDATE '||v_schema_name||'.padpolicy_item AS a '
                ||'SET  debit_type = b.id '
                ||'FROM (SELECT t.debit_type_old,a.id '
                ||'     FROM t '
                ||'     JOIN '||v_schema_name||'.arcode a ON (t.debit_type = a.name)) AS b '
                ||'WHERE a.debit_type_old = b.debit_type_old ';   
        
        
        -- pet_constraints
        
        EXECUTE 'UPDATE '||v_schema_name||'.pet_constraints AS p '
                ||'SET  pet = a.id '
                ||'FROM '||v_schema_name||'.arcode AS a '
                ||'WHERE p.pet = a.pit_id ';    
                
         
        -- product 
        
        EXECUTE 'UPDATE '||v_schema_name||'.product AS p '
                ||'SET  code_type = '
                ||'CASE WHEN service_type = ''commercialUnit'' THEN ''Commercial'' '
                ||'WHEN service_type = ''residentialShortTermUnit'' THEN ''ResidentialShortTerm'' '
                ||'WHEN service_type = ''residentialUnit'' THEN ''Residential'' '
                ||'WHEN feature_type = ''addOn'' THEN ''AddOn'' '
                ||'WHEN feature_type = ''booking'' THEN ''Residential'' '
                ||'WHEN feature_type = ''locker'' THEN ''Locker'' '
                ||'WHEN feature_type = ''oneTimeCharge'' THEN ''OneTime'' '
                ||'WHEN feature_type = ''parking'' THEN ''Parking'' '
                ||'WHEN feature_type = ''pet'' THEN ''Pet'' '
                ||'WHEN feature_type = ''utility'' THEN ''Utility'' END ';
            
        
        -- product_item
        
        EXECUTE 'UPDATE '||v_schema_name||'.product_item  AS p '
                ||'SET  code = a.id '
                ||'FROM '||v_schema_name||'.arcode a '
                ||'WHERE p.item_type = a.pit_id ';
        
        
        -- product_tax_policy_item
        
        EXECUTE 'UPDATE '||v_schema_name||'.product_tax_policy_item AS p '
                ||'SET  product_code = a.id '
                ||'FROM '||v_schema_name||'.arcode a '
                ||'WHERE p.product_item_type = a.pit_id ';
        
        
        -- reports_settings_holder
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.reports_settings_holder '
                ||'WHERE        class_name = ''PapReportMetadata'' ';
        
                
        -- yardi_charge_code
        
        EXECUTE 'UPDATE '||v_schema_name||'.yardi_charge_code AS y '
                ||'SET  ar_code = a.id '
                ||'FROM '||v_schema_name||'.arcode a '
                ||'WHERE y.product_item_type = a.pit_id ';
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- aging_buckets
        
        ALTER TABLE aging_buckets DROP COLUMN debit_type;
        
        -- arcode 
        
        ALTER TABLE arcode      DROP COLUMN lad_id,
                                DROP COLUMN pit_id;
                                
        --  billing_bill
        
        ALTER TABLE  billing_bill DROP COLUMN previous_cycle_bill;
        
        -- billing_invoice_line_item
        
        ALTER TABLE billing_invoice_line_item   DROP COLUMN target_date,
                                                DROP COLUMN debit_type ;
        
        -- building$external_utilities
        
        DROP TABLE building$external_utilities;
        
        -- building$included_utilities
        
        DROP TABLE building$included_utilities;
        
        -- concession_v
        
        ALTER TABLE concession_v        DROP COLUMN product_item_type,
                                        DROP COLUMN product_item_type_discriminator;
                                        
        -- deposit_policy_item
        
        ALTER TABLE deposit_policy_item DROP COLUMN product_type,
                                        DROP COLUMN product_type_discriminator;
                                        
        -- lease_adjustment
        
        ALTER TABLE lease_adjustment    DROP COLUMN item_type;
        
        -- lease_adjustment_policy_item
        
        ALTER TABLE lease_adjustment_policy_item        DROP COLUMN lease_adjustment_reason;
        
        
        -- lease_adjustment_reason
        
        DROP TABLE lease_adjustment_reason;
        
        
        -- padpolicy_item
        
        ALTER TABLE padpolicy_item DROP COLUMN debit_type_old;
        
        -- pet_constraints
        
        ALTER TABLE pet_constraints     DROP COLUMN pet_discriminator;
        
        
        -- product
        
        ALTER TABLE product     DROP COLUMN feature_type,
                                DROP COLUMN service_type;
                                
                                
        -- product_item
        
        ALTER TABLE product_item        DROP COLUMN item_type,
                                        DROP COLUMN item_type_discriminator;
                                        
                                        
        -- product_item_type
        
        DROP TABLE product_item_type;
        
        
        -- product_tax_policy_item
        
        ALTER TABLE product_tax_policy_item     DROP COLUMN product_item_type,
                                                DROP COLUMN product_item_type_discriminator;
                                                
        -- utility
        
        DROP TABLE utility;
                                                
        -- yardi_charge_code
        
        ALTER TABLE yardi_charge_code   DROP COLUMN product_item_type,
                                        DROP COLUMN product_item_type_discriminator;
         
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- Not Null
        
        ALTER TABLE product ALTER COLUMN code_type SET NOT NULL;
        ALTER TABLE yardi_charge_code ALTER COLUMN ar_code SET NOT NULL;
        
        -- Foreign Keys
        
        ALTER TABLE aggregated_transfer ADD CONSTRAINT aggregated_transfer_merchant_account_fk FOREIGN KEY(merchant_account) REFERENCES merchant_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE application_wizard_substep ADD CONSTRAINT application_wizard_substep_step_fk FOREIGN KEY(step) REFERENCES application_wizard_step(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE appointment ADD CONSTRAINT appointment_agent_fk FOREIGN KEY(agent) REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE appointment ADD CONSTRAINT appointment_lead_fk FOREIGN KEY(lead) REFERENCES lead(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE apt_unit ADD CONSTRAINT apt_unit_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE apt_unit ADD CONSTRAINT apt_unit_floorplan_fk FOREIGN KEY(floorplan) REFERENCES floorplan(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE apt_unit_item ADD CONSTRAINT apt_unit_item_apt_unit_fk FOREIGN KEY(apt_unit) REFERENCES apt_unit(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE apt_unit ADD CONSTRAINT apt_unit_marketing_fk FOREIGN KEY(marketing) REFERENCES marketing(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE apt_unit_occupancy_segment ADD CONSTRAINT apt_unit_occupancy_segment_lease_fk FOREIGN KEY(lease) REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE apt_unit_occupancy_segment ADD CONSTRAINT apt_unit_occupancy_segment_unit_fk FOREIGN KEY(unit) REFERENCES apt_unit(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE arcode ADD CONSTRAINT arcode_gl_code_fk FOREIGN KEY(gl_code) REFERENCES gl_code(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE background_check_policy ADD CONSTRAINT background_check_policy_version_fk FOREIGN KEY(version) REFERENCES background_check_policy_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billable_item_adjustment ADD CONSTRAINT billable_item_adjustment_billable_item_fk FOREIGN KEY(billable_item) REFERENCES billable_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billable_item_adjustment ADD CONSTRAINT billable_item_adjustment_created_by_fk FOREIGN KEY(created_by) REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billable_item ADD CONSTRAINT billable_item_item_fk FOREIGN KEY(item) REFERENCES product_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_account ADD CONSTRAINT billing_account_billing_type_fk FOREIGN KEY(billing_type) REFERENCES billing_billing_type(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_arrears_snapshot$aging_buckets ADD CONSTRAINT billing_arrears_snapshot$aging_buckets_owner_fk 
                FOREIGN KEY(owner) REFERENCES billing_arrears_snapshot(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_arrears_snapshot$aging_buckets ADD CONSTRAINT billing_arrears_snapshot$aging_buckets_value_fk FOREIGN KEY(value) REFERENCES aging_buckets(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_arrears_snapshot ADD CONSTRAINT billing_arrears_snapshot_billing_account_fk FOREIGN KEY(billing_account) REFERENCES billing_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_arrears_snapshot ADD CONSTRAINT billing_arrears_snapshot_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_arrears_snapshot ADD CONSTRAINT billing_arrears_snapshot_total_aging_buckets_fk FOREIGN KEY(total_aging_buckets) REFERENCES aging_buckets(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_bill$line_items ADD CONSTRAINT billing_bill$line_items_owner_fk FOREIGN KEY(owner) REFERENCES billing_bill(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_bill$warnings ADD CONSTRAINT billing_bill$warnings_owner_fk FOREIGN KEY(owner) REFERENCES billing_bill(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_bill ADD CONSTRAINT billing_bill_billing_account_fk FOREIGN KEY(billing_account) REFERENCES billing_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_bill ADD CONSTRAINT billing_bill_billing_cycle_fk FOREIGN KEY(billing_cycle) REFERENCES billing_billing_cycle(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_bill ADD CONSTRAINT billing_bill_lease_fk FOREIGN KEY(lease) REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_billing_cycle ADD CONSTRAINT billing_billing_cycle_billing_type_fk FOREIGN KEY(billing_type) REFERENCES billing_billing_type(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_billing_cycle ADD CONSTRAINT billing_billing_cycle_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_billing_cycle_stats ADD CONSTRAINT billing_billing_cycle_stats_billing_cycle_fk 
                FOREIGN KEY(billing_cycle) REFERENCES billing_billing_cycle(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_debit_credit_link ADD CONSTRAINT billing_debit_credit_link_credit_item_fk FOREIGN KEY(credit_item) REFERENCES billing_invoice_line_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_debit_credit_link ADD CONSTRAINT billing_debit_credit_link_debit_item_fk FOREIGN KEY(debit_item) REFERENCES billing_invoice_line_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_invoice_charge_tax ADD CONSTRAINT billing_invoice_charge_tax_tax_fk FOREIGN KEY(tax) REFERENCES tax(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_invoice_line_item$taxes ADD CONSTRAINT billing_invoice_line_item$taxes_owner_fk FOREIGN KEY(owner) REFERENCES billing_invoice_line_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_invoice_line_item$taxes ADD CONSTRAINT billing_invoice_line_item$taxes_value_fk FOREIGN KEY(value) REFERENCES billing_invoice_charge_tax(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_adjustment_fk FOREIGN KEY(adjustment) REFERENCES lease_adjustment(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_ar_code_fk FOREIGN KEY(ar_code) REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_billing_account_fk FOREIGN KEY(billing_account) REFERENCES billing_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_billing_cycle_fk FOREIGN KEY(billing_cycle) REFERENCES billing_billing_cycle(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_deposit_fk FOREIGN KEY(deposit) REFERENCES deposit(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_payment_record_fk FOREIGN KEY(payment_record) REFERENCES payment_record(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_product_charge_fk 
                FOREIGN KEY(product_charge) REFERENCES billing_invoice_line_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE boiler ADD CONSTRAINT boiler_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE boiler ADD CONSTRAINT boiler_maintenance_contract_contractor_fk FOREIGN KEY(maintenance_contract_contractor) REFERENCES vendor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE boiler ADD CONSTRAINT boiler_warranty_contract_contractor_fk FOREIGN KEY(warranty_contract_contractor) REFERENCES vendor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE boilerwarranty$items ADD CONSTRAINT boilerwarranty$items_owner_fk FOREIGN KEY(owner) REFERENCES boiler(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE boilerwarranty$items ADD CONSTRAINT boilerwarranty$items_value_fk FOREIGN KEY(value) REFERENCES warranty_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE building$media ADD CONSTRAINT building$media_owner_fk FOREIGN KEY(owner) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE building$media ADD CONSTRAINT building$media_value_fk FOREIGN KEY(value) REFERENCES media(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE building_amenity ADD CONSTRAINT building_amenity_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE building ADD CONSTRAINT building_complex_fk FOREIGN KEY(complex) REFERENCES complex(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE building ADD CONSTRAINT building_info_address_country_fk FOREIGN KEY(info_address_country) REFERENCES country(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE building ADD CONSTRAINT building_info_address_province_fk FOREIGN KEY(info_address_province) REFERENCES province(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE building ADD CONSTRAINT building_marketing_fk FOREIGN KEY(marketing) REFERENCES marketing(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE building_merchant_account ADD CONSTRAINT building_merchant_account_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE building_merchant_account ADD CONSTRAINT building_merchant_account_merchant_account_fk FOREIGN KEY(merchant_account) REFERENCES merchant_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE building ADD CONSTRAINT building_property_manager_fk FOREIGN KEY(property_manager) REFERENCES property_manager(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE buildingcontacts$organization_contacts ADD CONSTRAINT buildingcontacts$organization_contacts_owner_fk FOREIGN KEY(owner) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE buildingcontacts$organization_contacts ADD CONSTRAINT buildingcontacts$organization_contacts_value_fk 
                FOREIGN KEY(value) REFERENCES organization_contact(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE buildingcontacts$property_contacts ADD CONSTRAINT buildingcontacts$property_contacts_owner_fk FOREIGN KEY(owner) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE buildingcontacts$property_contacts ADD CONSTRAINT buildingcontacts$property_contacts_value_fk FOREIGN KEY(value) REFERENCES property_contact(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE campaign$audience ADD CONSTRAINT campaign$audience_owner_fk FOREIGN KEY(owner) REFERENCES campaign(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE campaign$audience ADD CONSTRAINT campaign$audience_value_fk FOREIGN KEY(value) REFERENCES recipient(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE campaign$media ADD CONSTRAINT campaign$media_owner_fk FOREIGN KEY(owner) REFERENCES campaign(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE campaign$media ADD CONSTRAINT campaign$media_value_fk FOREIGN KEY(value) REFERENCES communication_media(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE campaign$schedule ADD CONSTRAINT campaign$schedule_owner_fk FOREIGN KEY(owner) REFERENCES campaign(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE campaign_history ADD CONSTRAINT campaign_history_campaign_fk FOREIGN KEY(campaign) REFERENCES phone_call_campaign(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE campaign_history ADD CONSTRAINT campaign_history_tenant_fk FOREIGN KEY(tenant) REFERENCES customer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE campaign ADD CONSTRAINT campaign_message_fk FOREIGN KEY(message) REFERENCES message(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE charge_line_list$charges ADD CONSTRAINT charge_line_list$charges_owner_fk FOREIGN KEY(owner) REFERENCES charge_line_list(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE charge_line_list$charges ADD CONSTRAINT charge_line_list$charges_value_fk FOREIGN KEY(value) REFERENCES charge_line(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE charges ADD CONSTRAINT charges_application_charges_fk FOREIGN KEY(application_charges) REFERENCES charge_line_list(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE charges ADD CONSTRAINT charges_application_fk FOREIGN KEY(application) REFERENCES online_application(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE charges ADD CONSTRAINT charges_monthly_charges_fk FOREIGN KEY(monthly_charges) REFERENCES charge_line_list(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE charges ADD CONSTRAINT charges_one_time_charges_fk FOREIGN KEY(one_time_charges) REFERENCES charge_line_list(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE charges ADD CONSTRAINT charges_payment_split_charges_fk FOREIGN KEY(payment_split_charges) REFERENCES tenant_charge_list(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE charges ADD CONSTRAINT charges_prorated_charges_fk FOREIGN KEY(prorated_charges) REFERENCES charge_line_list(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE city_intro_page$content ADD CONSTRAINT city_intro_page$content_owner_fk FOREIGN KEY(owner) REFERENCES city_intro_page(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE city_intro_page$content ADD CONSTRAINT city_intro_page$content_value_fk FOREIGN KEY(value) REFERENCES html_content(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE city_intro_page ADD CONSTRAINT city_intro_page_province_fk FOREIGN KEY(province) REFERENCES province(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE city ADD CONSTRAINT city_province_fk FOREIGN KEY(province) REFERENCES province(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_favorited_messages ADD CONSTRAINT communication_favorited_messages_message_fk FOREIGN KEY(message) REFERENCES communication_message(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_favorited_messages ADD CONSTRAINT communication_favorited_messages_person_fk FOREIGN KEY(person) REFERENCES communication_person(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_message ADD CONSTRAINT communication_message_destination_fk FOREIGN KEY(destination) REFERENCES communication_person(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_message ADD CONSTRAINT communication_message_parent_fk FOREIGN KEY(parent) REFERENCES communication_message(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_message ADD CONSTRAINT communication_message_sender_fk FOREIGN KEY(sender) REFERENCES communication_person(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE company$contacts ADD CONSTRAINT company$contacts_owner_fk FOREIGN KEY(owner) REFERENCES company(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE company$contacts ADD CONSTRAINT company$contacts_value_fk FOREIGN KEY(value) REFERENCES organization_contacts(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE company$emails ADD CONSTRAINT company$emails_owner_fk FOREIGN KEY(owner) REFERENCES company(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE company$emails ADD CONSTRAINT company$emails_value_fk FOREIGN KEY(value) REFERENCES company_email(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE company$phones ADD CONSTRAINT company$phones_owner_fk FOREIGN KEY(owner) REFERENCES company(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE company$phones ADD CONSTRAINT company$phones_value_fk FOREIGN KEY(value) REFERENCES company_phone(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE concession ADD CONSTRAINT concession_catalog_fk FOREIGN KEY(catalog) REFERENCES product_catalog(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE concession_v ADD CONSTRAINT concession_v_holder_fk FOREIGN KEY(holder) REFERENCES concession(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE concession_v ADD CONSTRAINT concession_v_product_code_fk FOREIGN KEY(product_code) REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE contract ADD CONSTRAINT contract_contractor_fk FOREIGN KEY(contractor) REFERENCES vendor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE crm_role$behaviors ADD CONSTRAINT crm_role$behaviors_owner_fk FOREIGN KEY(owner) REFERENCES crm_role(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE crm_role$rls ADD CONSTRAINT crm_role$rls_owner_fk FOREIGN KEY(owner) REFERENCES crm_role(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE crm_role$rls ADD CONSTRAINT crm_role$rls_value_fk FOREIGN KEY(value) REFERENCES crm_role(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE crm_user_buildings ADD CONSTRAINT crm_user_buildings_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE crm_user_buildings ADD CONSTRAINT crm_user_buildings_usr_fk FOREIGN KEY(usr) REFERENCES crm_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE crm_user_credential$rls ADD CONSTRAINT crm_user_credential$rls_owner_fk FOREIGN KEY(owner) REFERENCES crm_user_credential(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE crm_user_credential$rls ADD CONSTRAINT crm_user_credential$rls_value_fk FOREIGN KEY(value) REFERENCES crm_role(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE crm_user_credential ADD CONSTRAINT crm_user_credential_usr_fk FOREIGN KEY(usr) REFERENCES crm_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_accepted_terms ADD CONSTRAINT customer_accepted_terms_customer_fk FOREIGN KEY(customer) REFERENCES customer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_credit_check ADD CONSTRAINT customer_credit_check_background_check_policy_fk 
                FOREIGN KEY(background_check_policy) REFERENCES background_check_policy_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_credit_check ADD CONSTRAINT customer_credit_check_created_by_fk FOREIGN KEY(created_by) REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_credit_check ADD CONSTRAINT customer_credit_check_screening_fk FOREIGN KEY(screening) REFERENCES customer_screening(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_screening_income ADD CONSTRAINT customer_screening_income_details_fk FOREIGN KEY(details) REFERENCES customer_screening_income_info(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_screening_income_info ADD CONSTRAINT customer_screening_income_info_address_country_fk FOREIGN KEY(address_country) REFERENCES country(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_screening_income_info ADD CONSTRAINT customer_screening_income_info_address_province_fk 
                FOREIGN KEY(address_province) REFERENCES province(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_screening_income ADD CONSTRAINT customer_screening_income_owner_fk FOREIGN KEY(owner) REFERENCES customer_screening_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_screening_personal_asset ADD CONSTRAINT customer_screening_personal_asset_owner_fk FOREIGN KEY(owner) REFERENCES customer_screening_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_screening ADD CONSTRAINT customer_screening_screene_fk FOREIGN KEY(screene) REFERENCES customer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_screening_v ADD CONSTRAINT customer_screening_v_current_address_country_fk FOREIGN KEY(current_address_country) REFERENCES country(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_screening_v ADD CONSTRAINT customer_screening_v_current_address_province_fk FOREIGN KEY(current_address_province) REFERENCES province(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_screening_v ADD CONSTRAINT customer_screening_v_holder_fk FOREIGN KEY(holder) REFERENCES customer_screening(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_screening_v ADD CONSTRAINT customer_screening_v_legal_questions_fk 
                FOREIGN KEY(legal_questions) REFERENCES customer_screening_legal_questions(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_screening_v ADD CONSTRAINT customer_screening_v_previous_address_country_fk FOREIGN KEY(previous_address_country) REFERENCES country(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_screening_v ADD CONSTRAINT customer_screening_v_previous_address_province_fk FOREIGN KEY(previous_address_province) REFERENCES province(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_user_credential ADD CONSTRAINT customer_user_credential_usr_fk FOREIGN KEY(usr) REFERENCES customer_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer ADD CONSTRAINT customer_user_id_fk FOREIGN KEY(user_id) REFERENCES customer_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE dashboard_metadata ADD CONSTRAINT dashboard_metadata_owner_user_id_fk FOREIGN KEY(owner_user_id) REFERENCES crm_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE deposit ADD CONSTRAINT deposit_billable_item_fk FOREIGN KEY(billable_item) REFERENCES billable_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE deposit_lifecycle$interest_adjustments ADD CONSTRAINT deposit_lifecycle$interest_adjustments_owner_fk 
                FOREIGN KEY(owner) REFERENCES deposit_lifecycle(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE deposit_lifecycle$interest_adjustments ADD CONSTRAINT deposit_lifecycle$interest_adjustments_value_fk 
                FOREIGN KEY(value) REFERENCES deposit_interest_adjustment(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE deposit_lifecycle ADD CONSTRAINT deposit_lifecycle_billing_account_fk FOREIGN KEY(billing_account) REFERENCES billing_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE deposit ADD CONSTRAINT deposit_lifecycle_fk FOREIGN KEY(lifecycle) REFERENCES deposit_lifecycle(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE deposit_policy_item ADD CONSTRAINT deposit_policy_item_policy_fk FOREIGN KEY(policy) REFERENCES deposit_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE deposit_policy_item ADD CONSTRAINT deposit_policy_item_product_code_fk FOREIGN KEY(product_code) REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE digital_signature ADD CONSTRAINT digital_signature_person_fk FOREIGN KEY(person) REFERENCES customer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE elevator ADD CONSTRAINT elevator_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE elevator ADD CONSTRAINT elevator_maintenance_contract_contractor_fk FOREIGN KEY(maintenance_contract_contractor) REFERENCES vendor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE elevator ADD CONSTRAINT elevator_warranty_contract_contractor_fk FOREIGN KEY(warranty_contract_contractor) REFERENCES vendor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE elevatorwarranty$items ADD CONSTRAINT elevatorwarranty$items_owner_fk FOREIGN KEY(owner) REFERENCES elevator(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE elevatorwarranty$items ADD CONSTRAINT elevatorwarranty$items_value_fk FOREIGN KEY(value) REFERENCES warranty_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE email_template ADD CONSTRAINT email_template_policy_fk FOREIGN KEY(policy) REFERENCES email_templates_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE emergency_contact ADD CONSTRAINT emergency_contact_address_country_fk FOREIGN KEY(address_country) REFERENCES country(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE emergency_contact ADD CONSTRAINT emergency_contact_address_province_fk FOREIGN KEY(address_province) REFERENCES province(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE emergency_contact ADD CONSTRAINT emergency_contact_customer_fk FOREIGN KEY(customer) REFERENCES customer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE employee$employees ADD CONSTRAINT employee$employees_owner_fk FOREIGN KEY(owner) REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE employee$employees ADD CONSTRAINT employee$employees_value_fk FOREIGN KEY(value) REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE employee$portfolios ADD CONSTRAINT employee$portfolios_owner_fk FOREIGN KEY(owner) REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE employee$portfolios ADD CONSTRAINT employee$portfolios_value_fk FOREIGN KEY(value) REFERENCES portfolio(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE employee ADD CONSTRAINT employee_manager_fk FOREIGN KEY(manager) REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE employee ADD CONSTRAINT employee_user_id_fk FOREIGN KEY(user_id) REFERENCES crm_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE floorplan$media ADD CONSTRAINT floorplan$media_owner_fk FOREIGN KEY(owner) REFERENCES floorplan(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE floorplan$media ADD CONSTRAINT floorplan$media_value_fk FOREIGN KEY(value) REFERENCES media(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE floorplan_amenity ADD CONSTRAINT floorplan_amenity_floorplan_fk FOREIGN KEY(floorplan) REFERENCES floorplan(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE floorplan ADD CONSTRAINT floorplan_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE floorplan ADD CONSTRAINT floorplan_counters_fk FOREIGN KEY(counters) REFERENCES floorplan_counters(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE gadget_content$news ADD CONSTRAINT gadget_content$news_owner_fk FOREIGN KEY(owner) REFERENCES gadget_content(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE gadget_content$news ADD CONSTRAINT gadget_content$news_value_fk FOREIGN KEY(value) REFERENCES news(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE gadget_content$testimonials ADD CONSTRAINT gadget_content$testimonials_owner_fk FOREIGN KEY(owner) REFERENCES gadget_content(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE gadget_content$testimonials ADD CONSTRAINT gadget_content$testimonials_value_fk FOREIGN KEY(value) REFERENCES testimonial(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE gadget_content ADD CONSTRAINT gadget_content_html_content_fk FOREIGN KEY(html_content) REFERENCES html_content(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE gl_code ADD CONSTRAINT gl_code_gl_code_category_fk FOREIGN KEY(gl_code_category) REFERENCES gl_code_category(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE home_page_gadget ADD CONSTRAINT home_page_gadget_content_fk FOREIGN KEY(content) REFERENCES gadget_content(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE html_content ADD CONSTRAINT html_content_locale_fk FOREIGN KEY(locale) REFERENCES available_locale(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE id_assignment_item ADD CONSTRAINT id_assignment_item_policy_fk FOREIGN KEY(policy) REFERENCES id_assignment_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE identification_document ADD CONSTRAINT identification_document_id_type_fk FOREIGN KEY(id_type) REFERENCES identification_document_type(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE identification_document_type ADD CONSTRAINT identification_document_type_policy_fk 
                FOREIGN KEY(policy) REFERENCES application_documentation_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE insurance_certificate ADD CONSTRAINT insurance_certificate_client_fk FOREIGN KEY(client) REFERENCES insurance_tenant_sure_client(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE insurance_certificate ADD CONSTRAINT insurance_certificate_tenant_fk FOREIGN KEY(tenant) REFERENCES lease_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE insurance_tenant_sure_client ADD CONSTRAINT insurance_tenant_sure_client_tenant_fk FOREIGN KEY(tenant) REFERENCES lease_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE insurance_tenant_sure_report ADD CONSTRAINT insurance_tenant_sure_report_insurance_fk FOREIGN KEY(insurance) REFERENCES insurance_certificate(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE insurance_tenant_sure_transaction ADD CONSTRAINT insurance_tenant_sure_transaction_insurance_fk 
                FOREIGN KEY(insurance) REFERENCES insurance_certificate(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE insurance_tenant_sure_transaction ADD CONSTRAINT insurance_tenant_sure_transaction_payment_method_fk 
                FOREIGN KEY(payment_method) REFERENCES payment_method(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE invoice_adjustment_sub_line_item ADD CONSTRAINT invoice_adjustment_sub_line_item_billable_item_adjustment_fk 
                FOREIGN KEY(billable_item_adjustment) REFERENCES billable_item_adjustment(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE invoice_adjustment_sub_line_item ADD CONSTRAINT invoice_adjustment_sub_line_item_line_item_fk 
                FOREIGN KEY(line_item) REFERENCES billing_invoice_line_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE invoice_charge_sub_line_item ADD CONSTRAINT invoice_charge_sub_line_item_billable_item_fk FOREIGN KEY(billable_item) REFERENCES billable_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE invoice_charge_sub_line_item ADD CONSTRAINT invoice_charge_sub_line_item_line_item_fk FOREIGN KEY(line_item) REFERENCES billing_invoice_line_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE invoice_concession_sub_line_item ADD CONSTRAINT invoice_concession_sub_line_item_concession_fk FOREIGN KEY(concession) REFERENCES concession(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE invoice_concession_sub_line_item ADD CONSTRAINT invoice_concession_sub_line_item_line_item_fk 
                FOREIGN KEY(line_item) REFERENCES billing_invoice_line_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE issue_classification ADD CONSTRAINT issue_classification_subject_details_fk FOREIGN KEY(subject_details) REFERENCES issue_subject_details(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE issue_repair_subject ADD CONSTRAINT issue_repair_subject_issue_element_fk FOREIGN KEY(issue_element) REFERENCES issue_element(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE issue_subject_details ADD CONSTRAINT issue_subject_details_subject_fk FOREIGN KEY(subject) REFERENCES issue_repair_subject(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE late_fee_item ADD CONSTRAINT late_fee_item_policy_fk FOREIGN KEY(policy) REFERENCES lease_billing_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lead ADD CONSTRAINT lead_agent_fk FOREIGN KEY(agent) REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lead ADD CONSTRAINT lead_floorplan_fk FOREIGN KEY(floorplan) REFERENCES floorplan(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lead_guest ADD CONSTRAINT lead_guest_lead_fk FOREIGN KEY(lead) REFERENCES lead(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lead ADD CONSTRAINT lead_lease_fk FOREIGN KEY(lease) REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_adjustment ADD CONSTRAINT lease_adjustment_billing_account_fk FOREIGN KEY(billing_account) REFERENCES billing_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_adjustment ADD CONSTRAINT lease_adjustment_code_fk FOREIGN KEY(code) REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_adjustment ADD CONSTRAINT lease_adjustment_created_by_fk FOREIGN KEY(created_by) REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_adjustment_policy_item$taxes ADD CONSTRAINT lease_adjustment_policy_item$taxes_owner_fk 
                FOREIGN KEY(owner) REFERENCES lease_adjustment_policy_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_adjustment_policy_item$taxes ADD CONSTRAINT lease_adjustment_policy_item$taxes_value_fk FOREIGN KEY(value) REFERENCES tax(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_adjustment_policy_item ADD CONSTRAINT lease_adjustment_policy_item_code_fk FOREIGN KEY(code) REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_adjustment_policy_item ADD CONSTRAINT lease_adjustment_policy_item_policy_fk FOREIGN KEY(policy) REFERENCES lease_adjustment_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_application ADD CONSTRAINT lease_application_decided_by_fk FOREIGN KEY(decided_by) REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_application ADD CONSTRAINT lease_application_lease_fk FOREIGN KEY(lease) REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_application ADD CONSTRAINT lease_application_online_application_fk FOREIGN KEY(online_application) REFERENCES master_online_application(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease ADD CONSTRAINT lease_billing_account_fk FOREIGN KEY(billing_account) REFERENCES billing_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_billing_type_policy_item ADD CONSTRAINT lease_billing_type_policy_item_lease_billing_policy_fk 
                FOREIGN KEY(lease_billing_policy) REFERENCES lease_billing_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease ADD CONSTRAINT lease_current_term_fk FOREIGN KEY(current_term) REFERENCES lease_term(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease ADD CONSTRAINT lease_next_term_fk FOREIGN KEY(next_term) REFERENCES lease_term(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_participant ADD CONSTRAINT lease_participant_customer_fk FOREIGN KEY(customer) REFERENCES customer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_participant ADD CONSTRAINT lease_participant_lease_fk FOREIGN KEY(lease) REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease ADD CONSTRAINT lease_previous_term_fk FOREIGN KEY(previous_term) REFERENCES lease_term(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term ADD CONSTRAINT lease_term_lease_fk FOREIGN KEY(lease) REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_participant ADD CONSTRAINT lease_term_participant_application_fk FOREIGN KEY(application) REFERENCES online_application(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_participant ADD CONSTRAINT lease_term_participant_credit_check_fk FOREIGN KEY(credit_check) REFERENCES customer_credit_check(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_participant ADD CONSTRAINT lease_term_participant_lease_participant_fk FOREIGN KEY(lease_participant) REFERENCES lease_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_participant ADD CONSTRAINT lease_term_participant_lease_term_v_fk FOREIGN KEY(lease_term_v) REFERENCES lease_term_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_participant ADD CONSTRAINT lease_term_participant_screening_fk FOREIGN KEY(screening) REFERENCES customer_screening(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_participant ADD CONSTRAINT lease_term_participant_tenant_fk FOREIGN KEY(tenant) REFERENCES lease_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_v ADD CONSTRAINT lease_term_v_holder_fk FOREIGN KEY(holder) REFERENCES lease_term(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_v ADD CONSTRAINT lease_term_v_lease_products_service_item_fk FOREIGN KEY(lease_products_service_item) REFERENCES billable_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_vlease_products$concessions ADD CONSTRAINT lease_term_vlease_products$concessions_owner_fk FOREIGN KEY(owner) REFERENCES lease_term_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_vlease_products$concessions ADD CONSTRAINT lease_term_vlease_products$concessions_value_fk FOREIGN KEY(value) REFERENCES concession(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_vlease_products$feature_items ADD CONSTRAINT lease_term_vlease_products$feature_items_owner_fk 
                FOREIGN KEY(owner) REFERENCES lease_term_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_vlease_products$feature_items ADD CONSTRAINT lease_term_vlease_products$feature_items_value_fk 
                FOREIGN KEY(value) REFERENCES billable_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease ADD CONSTRAINT lease_unit_fk FOREIGN KEY(unit) REFERENCES apt_unit(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_documentation$co_application ADD CONSTRAINT legal_documentation$co_application_owner_fk 
                FOREIGN KEY(owner) REFERENCES legal_documentation(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_documentation$co_application ADD CONSTRAINT legal_documentation$co_application_value_fk 
                FOREIGN KEY(value) REFERENCES legal_terms_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_documentation$guarantor_application ADD CONSTRAINT legal_documentation$guarantor_application_owner_fk 
                FOREIGN KEY(owner) REFERENCES legal_documentation(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_documentation$guarantor_application ADD CONSTRAINT legal_documentation$guarantor_application_value_fk 
                FOREIGN KEY(value) REFERENCES legal_terms_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_documentation$lease ADD CONSTRAINT legal_documentation$lease_owner_fk FOREIGN KEY(owner) REFERENCES legal_documentation(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_documentation$lease ADD CONSTRAINT legal_documentation$lease_value_fk FOREIGN KEY(value) REFERENCES legal_terms_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_documentation$main_application ADD CONSTRAINT legal_documentation$main_application_owner_fk 
                FOREIGN KEY(owner) REFERENCES legal_documentation(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_documentation$main_application ADD CONSTRAINT legal_documentation$main_application_value_fk 
                FOREIGN KEY(value) REFERENCES legal_terms_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_documentation$payment_authorization ADD CONSTRAINT legal_documentation$payment_authorization_owner_fk 
                FOREIGN KEY(owner) REFERENCES legal_documentation(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_documentation$payment_authorization ADD CONSTRAINT legal_documentation$payment_authorization_value_fk 
                FOREIGN KEY(value) REFERENCES legal_terms_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_terms_content ADD CONSTRAINT legal_terms_content_locale_fk FOREIGN KEY(locale) REFERENCES available_locale(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_terms_descriptor$content ADD CONSTRAINT legal_terms_descriptor$content_owner_fk FOREIGN KEY(owner) REFERENCES legal_terms_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_terms_descriptor$content ADD CONSTRAINT legal_terms_descriptor$content_value_fk FOREIGN KEY(value) REFERENCES legal_terms_content(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE locker_area ADD CONSTRAINT locker_area_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE locker ADD CONSTRAINT locker_locker_area_fk FOREIGN KEY(locker_area) REFERENCES locker_area(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance ADD CONSTRAINT maintenance_contract_contractor_fk FOREIGN KEY(contract_contractor) REFERENCES vendor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request_category$sub_categories ADD CONSTRAINT maintenance_request_category$sub_categories_owner_fk 
                FOREIGN KEY(owner) REFERENCES maintenance_request_category(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request_category$sub_categories ADD CONSTRAINT maintenance_request_category$sub_categories_value_fk 
                FOREIGN KEY(value) REFERENCES maintenance_request_category(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request ADD CONSTRAINT maintenance_request_category_fk FOREIGN KEY(category) REFERENCES maintenance_request_category(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request ADD CONSTRAINT maintenance_request_issue_classification_fk FOREIGN KEY(issue_classification) REFERENCES issue_classification(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request ADD CONSTRAINT maintenance_request_lease_participant_fk FOREIGN KEY(lease_participant) REFERENCES lease_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request_category ADD CONSTRAINT maintenance_request_category_level_fk 
                FOREIGN KEY(level) REFERENCES maintenance_request_category_level(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE marketing$ad_blurbs ADD CONSTRAINT marketing$ad_blurbs_owner_fk FOREIGN KEY(owner) REFERENCES marketing(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE marketing$ad_blurbs ADD CONSTRAINT marketing$ad_blurbs_value_fk FOREIGN KEY(value) REFERENCES advertising_blurb(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE news ADD CONSTRAINT news_locale_fk FOREIGN KEY(locale) REFERENCES available_locale(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE notes_and_attachments ADD CONSTRAINT notes_and_attachments_crmuser_fk FOREIGN KEY(crmuser) REFERENCES crm_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE nsf_fee_item ADD CONSTRAINT nsf_fee_item_policy_fk FOREIGN KEY(policy) REFERENCES lease_billing_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE online_application$signatures ADD CONSTRAINT online_application$signatures_owner_fk FOREIGN KEY(owner) REFERENCES online_application(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE online_application$signatures ADD CONSTRAINT online_application$signatures_value_fk FOREIGN KEY(value) REFERENCES digital_signature(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE online_application$steps ADD CONSTRAINT online_application$steps_owner_fk FOREIGN KEY(owner) REFERENCES online_application(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE online_application$steps ADD CONSTRAINT online_application$steps_value_fk FOREIGN KEY(value) REFERENCES application_wizard_step(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE online_application ADD CONSTRAINT online_application_customer_fk FOREIGN KEY(customer) REFERENCES customer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE online_application ADD CONSTRAINT online_application_master_online_application_fk 
                FOREIGN KEY(master_online_application) REFERENCES master_online_application(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE organization_contact ADD CONSTRAINT organization_contact_person_fk FOREIGN KEY(person) REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE organization_contacts$contact_list ADD CONSTRAINT organization_contacts$contact_list_owner_fk 
                FOREIGN KEY(owner) REFERENCES organization_contacts(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE organization_contacts$contact_list ADD CONSTRAINT organization_contacts$contact_list_value_fk 
                FOREIGN KEY(value) REFERENCES organization_contact(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE organization_contacts ADD CONSTRAINT organization_contacts_company_role_fk FOREIGN KEY(company_role) REFERENCES company_role(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE owner ADD CONSTRAINT owner_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE owner ADD CONSTRAINT owner_company_fk FOREIGN KEY(company) REFERENCES company(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE owner_group$owner_list ADD CONSTRAINT owner_group$owner_list_owner_fk FOREIGN KEY(owner) REFERENCES owner_group(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE owner_group$owner_list ADD CONSTRAINT owner_group$owner_list_value_fk FOREIGN KEY(value) REFERENCES owner(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE padpolicy_item ADD CONSTRAINT padpolicy_item_debit_type_fk FOREIGN KEY(debit_type) REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE padpolicy_item ADD CONSTRAINT padpolicy_item_padpolicy_fk FOREIGN KEY(padpolicy) REFERENCES padpolicy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE page_caption ADD CONSTRAINT page_caption_locale_fk FOREIGN KEY(locale) REFERENCES available_locale(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE page_content ADD CONSTRAINT page_content_descriptor_fk FOREIGN KEY(descriptor) REFERENCES page_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE page_content ADD CONSTRAINT page_content_image_fk FOREIGN KEY(image) REFERENCES portal_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE page_content ADD CONSTRAINT page_content_locale_fk FOREIGN KEY(locale) REFERENCES available_locale(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE page_descriptor$caption ADD CONSTRAINT page_descriptor$caption_owner_fk FOREIGN KEY(owner) REFERENCES page_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE page_descriptor$caption ADD CONSTRAINT page_descriptor$caption_value_fk FOREIGN KEY(value) REFERENCES page_caption(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE page_meta_tags ADD CONSTRAINT page_meta_tags_locale_fk FOREIGN KEY(locale) REFERENCES available_locale(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE parking ADD CONSTRAINT parking_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE parking_spot ADD CONSTRAINT parking_spot_parking_fk FOREIGN KEY(parking) REFERENCES parking(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_information ADD CONSTRAINT payment_information_application_fk FOREIGN KEY(application) REFERENCES online_application(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_information ADD CONSTRAINT payment_information_payment_method_billing_address_country_fk 
                FOREIGN KEY(payment_method_billing_address_country) REFERENCES country(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_information ADD CONSTRAINT payment_information_payment_method_billing_address_province_fk 
                FOREIGN KEY(payment_method_billing_address_province) REFERENCES province(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_information ADD CONSTRAINT payment_information_payment_method_customer_fk FOREIGN KEY(payment_method_customer) REFERENCES customer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_information ADD CONSTRAINT payment_information_payment_method_details_fk 
                FOREIGN KEY(payment_method_details) REFERENCES payment_payment_details(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_method ADD CONSTRAINT payment_method_billing_address_country_fk FOREIGN KEY(billing_address_country) REFERENCES country(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_method ADD CONSTRAINT payment_method_billing_address_province_fk FOREIGN KEY(billing_address_province) REFERENCES province(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_method ADD CONSTRAINT payment_method_customer_fk FOREIGN KEY(customer) REFERENCES customer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_method ADD CONSTRAINT payment_method_details_fk FOREIGN KEY(details) REFERENCES payment_payment_details(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_method ADD CONSTRAINT payment_method_tenant_fk FOREIGN KEY(tenant) REFERENCES lease_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_aggregated_transfer_fk FOREIGN KEY(aggregated_transfer) REFERENCES aggregated_transfer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_aggregated_transfer_return_fk FOREIGN KEY(aggregated_transfer_return) REFERENCES aggregated_transfer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_billing_account_fk FOREIGN KEY(billing_account) REFERENCES billing_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record_external ADD CONSTRAINT payment_record_external_billing_account_fk FOREIGN KEY(billing_account) REFERENCES billing_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record_external ADD CONSTRAINT payment_record_external_payment_record_fk FOREIGN KEY(payment_record) REFERENCES payment_record(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_lease_term_participant_fk FOREIGN KEY(lease_term_participant) REFERENCES lease_term_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_merchant_account_fk FOREIGN KEY(merchant_account) REFERENCES merchant_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_pad_billing_cycle_fk FOREIGN KEY(pad_billing_cycle) REFERENCES billing_billing_cycle(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_payment_method_fk FOREIGN KEY(payment_method) REFERENCES payment_method(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_preauthorized_payment_fk FOREIGN KEY(preauthorized_payment) REFERENCES preauthorized_payment(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record_processing ADD CONSTRAINT payment_record_processing_aggregated_transfer_fk 
                FOREIGN KEY(aggregated_transfer) REFERENCES aggregated_transfer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record_processing ADD CONSTRAINT payment_record_processing_payment_record_fk 
                FOREIGN KEY(payment_record) REFERENCES payment_record(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_transaction_account$allowed_transaction ADD CONSTRAINT payment_transaction_account$allowed_transaction_owner_fk 
                FOREIGN KEY(owner) REFERENCES payment_transaction_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_transactions_policy$policy_items ADD CONSTRAINT payment_transactions_policy$policy_items_owner_fk 
                FOREIGN KEY(owner) REFERENCES payment_transactions_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_transactions_policy$policy_items ADD CONSTRAINT payment_transactions_policy$policy_items_value_fk 
                FOREIGN KEY(value) REFERENCES payment_transaction_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payments_summary ADD CONSTRAINT payments_summary_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payments_summary ADD CONSTRAINT payments_summary_merchant_account_fk FOREIGN KEY(merchant_account) REFERENCES merchant_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pet_constraints ADD CONSTRAINT pet_constraints_pet_fk FOREIGN KEY(pet) REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pet_policy$pet_constraints ADD CONSTRAINT pet_policy$pet_constraints_owner_fk FOREIGN KEY(owner) REFERENCES pet_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pet_policy$pet_constraints ADD CONSTRAINT pet_policy$pet_constraints_value_fk FOREIGN KEY(value) REFERENCES pet_constraints(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE portal_image_resource ADD CONSTRAINT portal_image_resource_image_resource_fk FOREIGN KEY(image_resource) REFERENCES site_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE portal_image_resource ADD CONSTRAINT portal_image_resource_locale_fk FOREIGN KEY(locale) REFERENCES available_locale(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE portal_image_set$image_set ADD CONSTRAINT portal_image_set$image_set_owner_fk FOREIGN KEY(owner) REFERENCES portal_image_set(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE portal_image_set$image_set ADD CONSTRAINT portal_image_set$image_set_value_fk FOREIGN KEY(value) REFERENCES site_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE portal_image_set ADD CONSTRAINT portal_image_set_locale_fk FOREIGN KEY(locale) REFERENCES available_locale(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE portfolio$buildings ADD CONSTRAINT portfolio$buildings_owner_fk FOREIGN KEY(owner) REFERENCES portfolio(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE portfolio$buildings ADD CONSTRAINT portfolio$buildings_value_fk FOREIGN KEY(value) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE preauthorized_payment ADD CONSTRAINT preauthorized_payment_payment_method_fk FOREIGN KEY(payment_method) REFERENCES payment_method(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE preauthorized_payment ADD CONSTRAINT preauthorized_payment_tenant_fk FOREIGN KEY(tenant) REFERENCES lease_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product_catalog ADD CONSTRAINT product_catalog_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product ADD CONSTRAINT product_catalog_fk FOREIGN KEY(catalog) REFERENCES product_catalog(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product_item ADD CONSTRAINT product_item_code_fk FOREIGN KEY(code) REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product_item ADD CONSTRAINT product_item_product_fk FOREIGN KEY(product) REFERENCES product_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product_tax_policy_item$taxes ADD CONSTRAINT product_tax_policy_item$taxes_owner_fk FOREIGN KEY(owner) REFERENCES product_tax_policy_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product_tax_policy_item$taxes ADD CONSTRAINT product_tax_policy_item$taxes_value_fk FOREIGN KEY(value) REFERENCES tax(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product_tax_policy_item ADD CONSTRAINT product_tax_policy_item_policy_fk FOREIGN KEY(policy) REFERENCES product_tax_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product_tax_policy_item ADD CONSTRAINT product_tax_policy_item_product_code_fk FOREIGN KEY(product_code) REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product_v$concessions ADD CONSTRAINT product_v$concessions_owner_fk FOREIGN KEY(owner) REFERENCES product_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product_v$concessions ADD CONSTRAINT product_v$concessions_value_fk FOREIGN KEY(value) REFERENCES concession(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product_v$features ADD CONSTRAINT product_v$features_owner_fk FOREIGN KEY(owner) REFERENCES product_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product_v$features ADD CONSTRAINT product_v$features_value_fk FOREIGN KEY(value) REFERENCES product(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product_v ADD CONSTRAINT product_v_holder_fk FOREIGN KEY(holder) REFERENCES product(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE property_account_info ADD CONSTRAINT property_account_info_property_fk FOREIGN KEY(property) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE property_phone ADD CONSTRAINT property_phone_provider_fk FOREIGN KEY(provider) REFERENCES phone_provider(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE province ADD CONSTRAINT province_country_fk FOREIGN KEY(country) REFERENCES country(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pt_vehicle ADD CONSTRAINT pt_vehicle_country_fk FOREIGN KEY(country) REFERENCES country(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pt_vehicle ADD CONSTRAINT pt_vehicle_province_fk FOREIGN KEY(province) REFERENCES province(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE recipient$contact_list ADD CONSTRAINT recipient$contact_list_owner_fk FOREIGN KEY(owner) REFERENCES recipient(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE recipient$contact_list ADD CONSTRAINT recipient$contact_list_value_fk FOREIGN KEY(value) REFERENCES contact(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE reservation_schedule ADD CONSTRAINT reservation_schedule_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE reservation_schedule ADD CONSTRAINT reservation_schedule_tenant_fk FOREIGN KEY(tenant) REFERENCES customer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE resident_portal_settings$custom_html ADD CONSTRAINT resident_portal_settings$custom_html_owner_fk 
                FOREIGN KEY(owner) REFERENCES resident_portal_settings(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE resident_portal_settings$custom_html ADD CONSTRAINT resident_portal_settings$custom_html_value_fk 
                FOREIGN KEY(value) REFERENCES html_content(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE resident_portal_settings$proxy_whitelist ADD CONSTRAINT resident_portal_settings$proxy_whitelist_owner_fk 
                FOREIGN KEY(owner) REFERENCES resident_portal_settings(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE roof ADD CONSTRAINT roof_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE roof ADD CONSTRAINT roof_maintenance_contract_contractor_fk FOREIGN KEY(maintenance_contract_contractor) REFERENCES vendor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE roof_segment ADD CONSTRAINT roof_segment_roof_fk FOREIGN KEY(roof) REFERENCES roof(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE roof ADD CONSTRAINT roof_warranty_contract_contractor_fk FOREIGN KEY(warranty_contract_contractor) REFERENCES vendor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE roofwarranty$items ADD CONSTRAINT roofwarranty$items_owner_fk FOREIGN KEY(owner) REFERENCES roof(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE roofwarranty$items ADD CONSTRAINT roofwarranty$items_value_fk FOREIGN KEY(value) REFERENCES warranty_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE showing ADD CONSTRAINT showing_appointment_fk FOREIGN KEY(appointment) REFERENCES appointment(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE showing ADD CONSTRAINT showing_unit_fk FOREIGN KEY(unit) REFERENCES apt_unit(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$banner ADD CONSTRAINT site_descriptor$banner_owner_fk FOREIGN KEY(owner) REFERENCES site_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$banner ADD CONSTRAINT site_descriptor$banner_value_fk FOREIGN KEY(value) REFERENCES portal_image_set(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$city_intro_pages ADD CONSTRAINT site_descriptor$city_intro_pages_owner_fk FOREIGN KEY(owner) REFERENCES site_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$city_intro_pages ADD CONSTRAINT site_descriptor$city_intro_pages_value_fk FOREIGN KEY(value) REFERENCES city_intro_page(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$home_page_gadgets_narrow ADD CONSTRAINT site_descriptor$home_page_gadgets_narrow_owner_fk 
                FOREIGN KEY(owner) REFERENCES site_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$home_page_gadgets_narrow ADD CONSTRAINT site_descriptor$home_page_gadgets_narrow_value_fk 
                FOREIGN KEY(value) REFERENCES home_page_gadget(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$home_page_gadgets_wide ADD CONSTRAINT site_descriptor$home_page_gadgets_wide_owner_fk 
                FOREIGN KEY(owner) REFERENCES site_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$home_page_gadgets_wide ADD CONSTRAINT site_descriptor$home_page_gadgets_wide_value_fk 
                FOREIGN KEY(value) REFERENCES home_page_gadget(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$logo ADD CONSTRAINT site_descriptor$logo_owner_fk FOREIGN KEY(owner) REFERENCES site_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$logo ADD CONSTRAINT site_descriptor$logo_value_fk FOREIGN KEY(value) REFERENCES portal_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$meta_tags ADD CONSTRAINT site_descriptor$meta_tags_owner_fk FOREIGN KEY(owner) REFERENCES site_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$meta_tags ADD CONSTRAINT site_descriptor$meta_tags_value_fk FOREIGN KEY(value) REFERENCES page_meta_tags(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$site_titles ADD CONSTRAINT site_descriptor$site_titles_owner_fk FOREIGN KEY(owner) REFERENCES site_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$site_titles ADD CONSTRAINT site_descriptor$site_titles_value_fk FOREIGN KEY(value) REFERENCES site_titles(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$slogan ADD CONSTRAINT site_descriptor$slogan_owner_fk FOREIGN KEY(owner) REFERENCES site_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$slogan ADD CONSTRAINT site_descriptor$slogan_value_fk FOREIGN KEY(value) REFERENCES html_content(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$social_links ADD CONSTRAINT site_descriptor$social_links_owner_fk FOREIGN KEY(owner) REFERENCES site_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$social_links ADD CONSTRAINT site_descriptor$social_links_value_fk FOREIGN KEY(value) REFERENCES social_link(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor ADD CONSTRAINT site_descriptor_crm_logo_fk FOREIGN KEY(crm_logo) REFERENCES site_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor ADD CONSTRAINT site_descriptor_resident_portal_settings_fk 
                FOREIGN KEY(resident_portal_settings) REFERENCES resident_portal_settings(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor ADD CONSTRAINT site_descriptor_site_palette_fk FOREIGN KEY(site_palette) REFERENCES site_palette(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor ADD CONSTRAINT site_descriptor_update_flag_fk FOREIGN KEY(update_flag) REFERENCES site_descriptor_changes(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_titles ADD CONSTRAINT site_titles_locale_fk FOREIGN KEY(locale) REFERENCES available_locale(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE summary ADD CONSTRAINT summary_application_fk FOREIGN KEY(application) REFERENCES online_application(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE tenant_charge_list$charges ADD CONSTRAINT tenant_charge_list$charges_owner_fk FOREIGN KEY(owner) REFERENCES tenant_charge_list(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE tenant_charge_list$charges ADD CONSTRAINT tenant_charge_list$charges_value_fk FOREIGN KEY(value) REFERENCES tenant_charge(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE tenant_charge ADD CONSTRAINT tenant_charge_tenant_fk FOREIGN KEY(tenant) REFERENCES lease_term_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE testimonial ADD CONSTRAINT testimonial_locale_fk FOREIGN KEY(locale) REFERENCES available_locale(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE unit_availability_status ADD CONSTRAINT unit_availability_status_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE unit_availability_status ADD CONSTRAINT unit_availability_status_complex_fk FOREIGN KEY(complex) REFERENCES complex(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE unit_availability_status ADD CONSTRAINT unit_availability_status_floorplan_fk FOREIGN KEY(floorplan) REFERENCES floorplan(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE unit_availability_status ADD CONSTRAINT unit_availability_status_unit_fk FOREIGN KEY(unit) REFERENCES apt_unit(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE unit_turnover_stats ADD CONSTRAINT unit_turnover_stats_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE vendor$contacts ADD CONSTRAINT vendor$contacts_owner_fk FOREIGN KEY(owner) REFERENCES vendor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE vendor$contacts ADD CONSTRAINT vendor$contacts_value_fk FOREIGN KEY(value) REFERENCES organization_contacts(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE vendor$emails ADD CONSTRAINT vendor$emails_owner_fk FOREIGN KEY(owner) REFERENCES vendor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE vendor$emails ADD CONSTRAINT vendor$emails_value_fk FOREIGN KEY(value) REFERENCES company_email(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE vendor$phones ADD CONSTRAINT vendor$phones_owner_fk FOREIGN KEY(owner) REFERENCES vendor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE vendor$phones ADD CONSTRAINT vendor$phones_value_fk FOREIGN KEY(value) REFERENCES company_phone(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE warranty$items ADD CONSTRAINT warranty$items_owner_fk FOREIGN KEY(owner) REFERENCES warranty(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE warranty$items ADD CONSTRAINT warranty$items_value_fk FOREIGN KEY(value) REFERENCES warranty_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE warranty ADD CONSTRAINT warranty_contract_contractor_fk FOREIGN KEY(contract_contractor) REFERENCES vendor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE yardi_charge_code ADD CONSTRAINT yardi_charge_code_ar_code_fk FOREIGN KEY(ar_code) REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;


        -- Check Constraints
        
         ALTER TABLE aging_buckets ADD CONSTRAINT aging_buckets_ar_code_e_ck 
                CHECK ((ar_code) IN ('AccountCharge', 'AccountCredit', 'AddOn', 'CarryForwardCharge', 'Commercial', 'Deposit', 'ExternalCharge', 'ExternalCredit',
                 'LatePayment', 'Locker', 'NSF', 'OneTime', 'Parking', 'Pet', 'Residential', 'ResidentialShortTerm', 'Utility'));
        ALTER TABLE arcode ADD CONSTRAINT arcode_code_type_e_ck 
                CHECK ((code_type) IN ('AccountCharge', 'AccountCredit', 'AddOn', 'CarryForwardCharge', 'Commercial', 'Deposit', 'ExternalCharge', 'ExternalCredit',
                 'LatePayment', 'Locker', 'NSF', 'OneTime', 'Parking', 'Pet', 'Residential', 'ResidentialShortTerm', 'Utility'));
        ALTER TABLE arpolicy ADD CONSTRAINT arpolicy_credit_debit_rule_e_ck CHECK ((credit_debit_rule) IN ('oldestDebtFirst', 'rentDebtLast'));
        ALTER TABLE lead ADD CONSTRAINT lead_lease_type_e_ck 
                CHECK ((lease_type) IN ('AccountCharge', 'AccountCredit', 'AddOn', 'CarryForwardCharge', 'Commercial', 'Deposit', 'ExternalCharge', 'ExternalCredit',
                 'LatePayment', 'Locker', 'NSF', 'OneTime', 'Parking', 'Pet', 'Residential', 'ResidentialShortTerm', 'Utility'));
        ALTER TABLE lease ADD CONSTRAINT lease_lease_type_e_ck 
                CHECK ((lease_type) IN ('AccountCharge', 'AccountCredit', 'AddOn', 'CarryForwardCharge', 'Commercial', 'Deposit', 'ExternalCharge', 'ExternalCredit',
                 'LatePayment', 'Locker', 'NSF', 'OneTime', 'Parking', 'Pet', 'Residential', 'ResidentialShortTerm', 'Utility'));
        ALTER TABLE padpolicy ADD CONSTRAINT padpolicy_charge_type_e_ck CHECK ((charge_type) IN ('FixedAmount', 'OwingBalance'));
        ALTER TABLE product ADD CONSTRAINT product_code_type_e_ck 
                CHECK ((code_type) IN ('AccountCharge', 'AccountCredit', 'AddOn', 'CarryForwardCharge', 'Commercial', 'Deposit', 'ExternalCharge', 'ExternalCredit',
                 'LatePayment', 'Locker', 'NSF', 'OneTime', 'Parking', 'Pet', 'Residential', 'ResidentialShortTerm', 'Utility'));

        
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        CREATE INDEX billing_invoice_line_item_billing_account_idx ON billing_invoice_line_item USING btree (billing_account);
        CREATE INDEX billing_invoice_line_item_billing_account_discriminator_idx ON billing_invoice_line_item USING btree (billing_account_discriminator);
        CREATE INDEX maintenance_request_category$sub_categories_owner_idx ON maintenance_request_category$sub_categories USING btree (owner);
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.0.9',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
