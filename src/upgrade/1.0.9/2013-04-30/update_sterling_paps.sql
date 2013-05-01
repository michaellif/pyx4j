/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Update payment_status on sterling PAPs 
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

        UPDATE  sterling.payment_record
        SET     payment_status = 'Submitted'
        WHERE   id IN (263,264)
        AND     payment_status = 'Scheduled';


COMMIT;
