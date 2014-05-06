/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Terminate OFM lease
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

    UPDATE  ofm.lease_term
    SET     term_to = '2014-05-05'
    WHERE   lease = 48520;
    
    
    UPDATE  ofm.lease
    SET     completion = 'Termination',
            lease_to = '2014-05-05'
    WHERE   id = 48520;
    
COMMIT;
