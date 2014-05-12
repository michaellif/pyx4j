/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Update function for phone and fax numbers
***
***     ===========================================================================================================
**/                                                

CREATE OR REPLACE FUNCTION _dba_.update_phone_numbers(v_schema_name TEXT) RETURNS VOID AS
$$
BEGIN
    
    -- appointment
    
    EXECUTE 'UPDATE '||v_schema_name||'.appointment '
            ||'SET  phone = regexp_replace(phone,''\D'','''',''g'') ';
            
    -- company_phone
    
    EXECUTE 'UPDATE '||v_schema_name||'.company_phone '
            ||'SET  phone = regexp_replace(phone,''\D'','''',''g'') ';
            
    -- customer
    
    EXECUTE 'UPDATE '||v_schema_name||'.customer '
            ||'SET  person_home_phone = regexp_replace(person_home_phone,''\D'','''',''g''), '
            ||'     person_mobile_phone = regexp_replace(person_mobile_phone,''\D'','''',''g''), '
            ||'     person_work_phone = regexp_replace(person_work_phone,''\D'','''',''g'') ';
            
    -- customer_screening_income_info
    
    EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_income_info '
            ||'SET  supervisor_phone = regexp_replace(supervisor_phone,''\D'','''',''g'') ';
            
            
    -- customer_screening_v
    
    EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_v '
            ||'SET  current_address_manager_phone = regexp_replace(current_address_manager_phone,''\D'','''',''g''), '
            ||'     previous_address_manager_phone = regexp_replace(previous_address_manager_phone,''\D'','''',''g'') ';
 
    
    -- emergency_contact
    
    EXECUTE 'UPDATE '||v_schema_name||'.emergency_contact '
            ||'SET  home_phone = regexp_replace(home_phone,''\D'','''',''g''), '
            ||'     mobile_phone = regexp_replace(mobile_phone,''\D'','''',''g''), '
            ||'     work_phone = regexp_replace(work_phone,''\D'','''',''g'') ';
            
    
    -- employee
    
    EXECUTE 'UPDATE '||v_schema_name||'.employee '
            ||'SET  home_phone = regexp_replace(home_phone,''\D'','''',''g''), '
            ||'     mobile_phone = regexp_replace(mobile_phone,''\D'','''',''g''), '
            ||'     work_phone = regexp_replace(work_phone,''\D'','''',''g'') ';
            
    
    -- iagree
    
    EXECUTE 'UPDATE '||v_schema_name||'.iagree '
            ||'SET  person_home_phone = regexp_replace(person_home_phone,''\D'','''',''g''), '
            ||'     person_mobile_phone = regexp_replace(person_mobile_phone,''\D'','''',''g''), '
            ||'     person_work_phone = regexp_replace(person_work_phone,''\D'','''',''g'') ';
            
    
     -- ilsprofile_building
    
    EXECUTE 'UPDATE '||v_schema_name||'.ilsprofile_building '
            ||'SET  preferred_contacts_phone_value = regexp_replace(preferred_contacts_phone_value,''\D'','''',''g'') ';
    
    
    -- lead_guest
    
    EXECUTE 'UPDATE '||v_schema_name||'.lead_guest '
            ||'SET  person_home_phone = regexp_replace(person_home_phone,''\D'','''',''g''), '
            ||'     person_mobile_phone = regexp_replace(person_mobile_phone,''\D'','''',''g''), '
            ||'     person_work_phone = regexp_replace(person_work_phone,''\D'','''',''g'') ';
            
    
    -- maintenance_request
    
    EXECUTE 'UPDATE '||v_schema_name||'.maintenance_request '
            ||'SET  reporter_phone = regexp_replace(reporter_phone,''\D'','''',''g'') ';
    
    
    -- marketing
    
    EXECUTE 'UPDATE '||v_schema_name||'.marketing '
            ||'SET  marketing_contacts_phone_value = regexp_replace(marketing_contacts_phone_value,''\D'','''',''g'') ';
    
    
    
    -- n4_policy
    
    EXECUTE 'UPDATE '||v_schema_name||'.n4_policy '
            ||'SET  fax_number = regexp_replace(fax_number,''\D'','''',''g''), '
            ||'     phone_number = regexp_replace(phone_number,''\D'','''',''g'') ';
            
    
     -- owner
    
    EXECUTE 'UPDATE '||v_schema_name||'.owner '
            ||'SET  person_home_phone = regexp_replace(person_home_phone,''\D'','''',''g''), '
            ||'     person_mobile_phone = regexp_replace(person_mobile_phone,''\D'','''',''g''), '
            ||'     person_work_phone = regexp_replace(person_work_phone,''\D'','''',''g'') ';
            
            
     -- payment_payment_details
    
    EXECUTE 'UPDATE '||v_schema_name||'.payment_payment_details '
            ||'SET  bank_phone = regexp_replace(bank_phone,''\D'','''',''g'') ';
            
            
    -- pmc_company_info_contact
    
    EXECUTE 'UPDATE '||v_schema_name||'.pmc_company_info_contact '
            ||'SET  phone_number = regexp_replace(phone_number,''\D'','''',''g'') ';
            
            
    -- property_contact
    
    EXECUTE 'UPDATE '||v_schema_name||'.property_contact '
            ||'SET  phone_number = regexp_replace(phone_number,''\D'','''',''g'') ';
    
    
    -- yardi_service_request
    
    EXECUTE 'UPDATE '||v_schema_name||'.yardi_service_request '
            ||'SET  requestor_phone = regexp_replace(requestor_phone,''\D'','''',''g'') ';
            
END;
$$
LANGUAGE plpgsql VOLATILE;
