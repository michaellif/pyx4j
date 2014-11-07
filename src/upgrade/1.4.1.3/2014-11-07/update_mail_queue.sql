/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             update mail queue status
***
***     ======================================================================================================================
**/


BEGIN TRANSACTION;

    UPDATE  _admin_.outgoing_mail_queue
    SET     status = 'Queued',
            attempts = 10
    WHERE   id_discriminator = 'TenantSure' 
    AND     status = 'GiveUp';
    
COMMIT;
