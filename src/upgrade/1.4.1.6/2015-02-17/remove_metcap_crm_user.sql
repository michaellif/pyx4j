/**
***     ===========================================================================================================
***
***     Remove metcap initial user (email mm@pyx4j.com)
***
***     ===========================================================================================================
**/                                                     

BEGIN TRANSACTION;

    DELETE  FROM metcap.crm_user_credential$rls
    WHERE   owner = 843;
    
    DELETE  FROM metcap.crm_user_credential 
    WHERE   usr = 843;
    
    DELETE  FROM metcap.employee
    WHERE   user_id = 843;
    
    DELETE  FROM metcap.crm_user
    WHERE   id = 843;
    
COMMIT;
