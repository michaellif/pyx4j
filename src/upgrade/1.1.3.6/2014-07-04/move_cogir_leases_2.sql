/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Move cogir leases to a new building - manual part
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

    UPDATE  cogir.lease
    SET     unit = 85653
    WHERE   lease_id = 't0048910';
    
    UPDATE  cogir.lease
    SET     unit = 85709
    WHERE   lease_id = 't0048967';
    
COMMIT;
