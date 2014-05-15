/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Terminate woodbuffaloproperties lease
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

    UPDATE  woodbuffaloproperties.lease_term
    SET     term_to = '2014-05-14',
            actual_term_to = '2014-05-14'
    WHERE   lease = 49339;
    
    
    UPDATE  woodbuffaloproperties.lease
    SET     lease_to = '2014-05-14',
            termination_lease_to = '2014-05-14',
            actual_move_out = '2014-05-14'
    WHERE   id = 49339;
    
COMMIT;
