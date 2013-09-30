/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Greenwin lease term participant temporary update
***
***     ======================================================================================================================
**/

/*
SELECT  ltp.id,ltp.participant_role
FROM    greenwin.lease_term_participant ltp
JOIN    greenwin.lease_participant lp ON (lp.id = ltp.lease_participant)
WHERE   lp.participant_id = 'r0016230'
ORDER  BY ltp.id DESC LIMIT 1;
*/

BEGIN TRANSACTION;

        UPDATE  greenwin.lease_term_participant
        SET     participant_role = 'CoApplicant'
        WHERE   id = 166103;

        UPDATE  greenwin.lease_term_participant
        SET     participant_role = 'CoApplicant'
        WHERE   id = 95193;
        
COMMIT;
        
        

