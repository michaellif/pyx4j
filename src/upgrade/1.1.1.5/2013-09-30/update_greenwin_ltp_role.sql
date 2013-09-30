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
SELECT  MAX(ltp.id) AS max_ltp_id,ltp.participant_role,lp.participant_id, l.lease_id
FROM    greenwin.lease_term_participant ltp
JOIN    greenwin.lease_participant lp ON (lp.id = ltp.lease_participant)
JOIN    greenwin.lease l ON (l.id = lp.lease)
WHERE   lp.participant_id = 'r0003236'
--WHERE   ltp.participant_role = 'Dependent'
-- AND     l.lease_id IN ('t0030384','t0012771','t0031766','t0015568','t0007804')
GROUP BY ltp.participant_role,lp.participant_id, l.lease_id
ORDER BY 1;
-- ORDER  BY ltp.id DESC LIMIT 1;

max_ltp_id | participant_role | participant_id | lease_id 
------------+------------------+----------------+----------
      95210 | Dependent        | r0005197       | t0012771
      95212 | Dependent        | r0005199       | t0012771
      95304 | Dependent        | r0006269       | t0015568
      95306 | Dependent        | r0006271       | t0015568
      95308 | Dependent        | r0014348       | t0015568
      95308 | Dependent        | r0003236       | t0007804
      95676 | Dependent        | r0004503       | t0007804
      95678 | Dependent        | r0004833       | t0007804
     114597 | Dependent        | r0015062       | t0030384
     114599 | Dependent        | r0015064       | t0030384
     114601 | Dependent        | r0016332       | t0031766
     114603 | Dependent        | r0016334       | t0031766

*/

BEGIN TRANSACTION;

        UPDATE  greenwin.lease_term_participant
        SET     participant_role = 'CoApplicant'
        WHERE   id = 166103;

        UPDATE  greenwin.lease_term_participant
        SET     participant_role = 'CoApplicant'
        WHERE   id = 95193;
        
COMMIT;

BEGIN TRANSACTION;

        UPDATE  greenwin.lease_term_participant
        SET     participant_role = 'CoApplicant'
        WHERE   id IN (95210,95212,95304,95306,95308,95674,95676,95678,114597,114599,114601,114603)
        AND     participant_role = 'Dependent';

        
COMMIT;


# Revert this action

BEGIN TRANSACTION;

        UPDATE  greenwin.lease_term_participant
        SET     participant_role = 'Dependent'
        WHERE   id IN (95193,95210,95212,95304,95306,95308,95674,95676,95678,114597,114599,114601,114603,166103)
        AND     participant_role = 'CoApplicant';

        
COMMIT;
        
        

