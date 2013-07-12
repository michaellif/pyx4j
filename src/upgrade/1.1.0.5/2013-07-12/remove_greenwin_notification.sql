/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***            remove Greenwin notifications for one employee
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

        DELETE FROM greenwin.notification$buildings WHERE owner = 21;
        DELETE FROM greenwin.notification WHERE id = 21;
        
COMMIT;
