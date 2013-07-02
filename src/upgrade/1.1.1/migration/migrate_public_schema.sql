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

 -- Sequences to drop
 DROP SEQUENCE communication_favorited_messages_seq;
 DROP SEQUENCE portal_preferences_seq;

 
COMMIT;

SET client_min_messages = 'notice';


