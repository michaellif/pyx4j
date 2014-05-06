/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             fix_ofm_contacts.sql
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

    UPDATE  ofm.emergency_contact
    SET     address_street1 = NULL,
            relationship = NULL
    WHERE   id = 89;
    
COMMIT;
