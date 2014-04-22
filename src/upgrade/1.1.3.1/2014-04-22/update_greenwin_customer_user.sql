/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Greenwin user update
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

    UPDATE  greenwin.customer_user
    SET     email = 'warhealertpup@rogers.com'
    WHERE   id = 4148;
    
    UPDATE  greenwin.customer
    SET     person_email = 'warhealertpup@rogers.com'
    WHERE   id = 6620;
    
COMMIT;
