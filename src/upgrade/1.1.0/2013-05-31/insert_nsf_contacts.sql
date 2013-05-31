/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Insert notifications
***
***     ======================================================================================================================
**/


BEGIN TRANSACTION;

        INSERT INTO pangroup.property_contact (id,phone_type,name,visibility,email,description) VALUES
        (nextval('public.property_contact_seq'),'administrator','Kristian Pandurov','internal','kristian@pangroup.ca','NSF_NOTIFICATIONS');
        
        
        INSERT INTO  pangroup.buildingcontacts$property_contacts (id,owner,value) 
        (SELECT nextval('public.buildingcontacts$property_contacts_seq'),b.id,c.id
        FROM    pangroup.building b, pangroup.property_contact c
        WHERE   c.description = 'NSF_NOTIFICATIONS');
        
COMMIT;


BEGIN TRANSACTION;

        INSERT INTO cogir.property_contact (id,phone_type,name,visibility,email,description) VALUES
        (nextval('public.property_contact_seq'),'administrator','Susan Gilis','internal','SGillis@cogir.net','NSF_NOTIFICATIONS');
        
        
        INSERT INTO  cogir.buildingcontacts$property_contacts (id,owner,value) 
        (SELECT nextval('public.buildingcontacts$property_contacts_seq'),b.id,c.id
        FROM    cogir.building b, cogir.property_contact c
        WHERE   c.description = 'NSF_NOTIFICATIONS');
        
COMMIT;

BEGIN TRANSACTION;

        INSERT INTO sterling.property_contact (id,phone_type,name,visibility,email,description) VALUES
        (nextval('public.property_contact_seq'),'administrator','Soma Kanth','internal','sri@sterlingkaramar.com','NSF_NOTIFICATIONS');
        
        INSERT INTO sterling.property_contact (id,phone_type,name,visibility,email,description) VALUES
        (nextval('public.property_contact_seq'),'administrator','Kyrsten Lingenfelter','internal','kyrsten@sterlingkaramar.com','NSF_NOTIFICATIONS');
        
        
        INSERT INTO  sterling.buildingcontacts$property_contacts (id,owner,value) 
        (SELECT nextval('public.buildingcontacts$property_contacts_seq'),b.id,c.id
        FROM    sterling.building b, sterling.property_contact c
        WHERE   c.description = 'NSF_NOTIFICATIONS');
        
COMMIT;

BEGIN TRANSACTION;

        INSERT INTO greenwin.property_contact (id,phone_type,name,visibility,email,description) VALUES
        (nextval('public.property_contact_seq'),'administrator','Glory Singh','internal','GSingh@greenwin.ca','NSF_NOTIFICATIONS');
        
        
        INSERT INTO  greenwin.buildingcontacts$property_contacts (id,owner,value) 
        (SELECT nextval('public.buildingcontacts$property_contacts_seq'),b.id,c.id
        FROM    greenwin.building b, greenwin.property_contact c
        WHERE   c.description = 'NSF_NOTIFICATIONS');
        
COMMIT;

BEGIN TRANSACTION;

        INSERT INTO gateway.property_contact (id,phone_type,name,visibility,email,description) VALUES
        (nextval('public.property_contact_seq'),'administrator','Diane Trent','internal','DTrent@gatewaypm.com','NSF_NOTIFICATIONS');
        
        INSERT INTO gateway.property_contact (id,phone_type,name,visibility,email,description) VALUES
        (nextval('public.property_contact_seq'),'administrator','Eva Drab','internal','Edrab@gatewaypm.com','NSF_NOTIFICATIONS');
        
        
        INSERT INTO  gateway.buildingcontacts$property_contacts (id,owner,value) 
        (SELECT nextval('public.buildingcontacts$property_contacts_seq'),b.id,c.id
        FROM    gateway.building b, gateway.property_contact c
        WHERE   c.description = 'NSF_NOTIFICATIONS');
        
COMMIT;

BEGIN TRANSACTION;

        INSERT INTO larlyn.property_contact (id,phone_type,name,visibility,email,description) VALUES
        (nextval('public.property_contact_seq'),'administrator','Brenda Anderson','internal','banderson@larlyn.com','NSF_NOTIFICATIONS');
        
        
        INSERT INTO  larlyn.buildingcontacts$property_contacts (id,owner,value) 
        (SELECT nextval('public.buildingcontacts$property_contacts_seq'),b.id,c.id
        FROM    larlyn.building b, larlyn.property_contact c
        WHERE   c.description = 'NSF_NOTIFICATIONS');
        
COMMIT;
        
        
