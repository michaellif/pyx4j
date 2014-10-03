/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Fix greenwin customer
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

    UPDATE  greenwin.lease_participant
    SET     customer = 96230
    WHERE   id = 10671;
    
    UPDATE  greenwin.lease_participant
    SET     customer = 10672
    WHERE   id = 96303;
    
COMMIT;
