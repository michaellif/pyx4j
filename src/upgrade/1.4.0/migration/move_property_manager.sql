/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.1.4 - move property manager to property contacts
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.move_property_manager(v_schema_name TEXT) RETURNS VOID AS
$$
DECLARE
    
    v_manager_name      VARCHAR(500);
    
BEGIN

    -- insert new entry into property_contact table - as property manager 
    -- no matter if it may already be there
    
    EXECUTE 'INSERT INTO '||v_schema_name||'.property_contact (id,phone_type,'
            ||'name,visibility,email,description) '
            ||'(SELECT  nextval(''public.property_contact_seq'') AS id, '
            ||'         ''mainOffice'' AS phone_type, '
            ||'         pm.name AS name,''internal'' AS visibility,'
            ||'         cu.email AS email, ''Property Manager'' AS description '
            ||'FROM     '||v_schema_name||'.property_manager pm '
            ||'LEFT JOIN '||v_schema_name||'.crm_user cu ON (UPPER(pm.name) = UPPER(cu.name)) '
            ||'WHERE    pm.name IS NOT NULL)';
        
    
    -- insert into buildingcontacts$property_contacts
    
    EXECUTE 'INSERT INTO '||v_schema_name||'.buildingcontacts$property_contacts(id,owner,value) '
            ||'(SELECT  nextval(''public.buildingcontacts$property_contacts_seq'') AS id,'
            ||'         b.id AS owner, c.id AS value '
            ||'FROM     '||v_schema_name||'.building b '
            ||'JOIN     '||v_schema_name||'.property_manager pm ON (pm.id = b.property_manager) '
            ||'JOIN     '||v_schema_name||'.property_contact c ON (pm.name = c.name) '
            ||'WHERE    c.description = ''Property Manager'' ) ';
    

END;
$$
LANGUAGE plpgsql VOLATILE;
    
    
