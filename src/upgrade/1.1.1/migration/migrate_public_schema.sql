/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Migration script for public schema - generated with by _dba_.generate_sql_sequences function
***
***     ===========================================================================================================
**/                                                     

SET client_min_messages = 'error';
SET search_path = 'public';

BEGIN TRANSACTION;

 -- renamed sequences 

 ALTER SEQUENCE ortal_image_resource_seq RENAME TO portal_logo_image_resource_seq;

 -- Sequences to drop
 DROP SEQUENCE communication_favorited_messages_seq;
 DROP SEQUENCE portal_preferences_seq;

 -- New sequences
 CREATE SEQUENCE site_descriptor$pmc_info_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
  
 -- Change owner to vista
 ALTER SEQUENCE portal_logo_image_resource_seq OWNER TO vista ;
 ALTER SEQUENCE site_descriptor$pmc_info_seq OWNER TO vista ;

COMMIT;

SET client_min_messages = 'notice';


