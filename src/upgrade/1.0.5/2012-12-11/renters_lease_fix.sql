/**
***     ========================================================
***             Temporary fix for 3D properties lease that starts
***             and ends in November
***             Lease.lease_to and lease_term.term_to to be 
***             reverted back to Nov. 30, 2012 once the issue 
***             is fixed
***     =========================================================
**/

BEGIN TRANSACTION;

UPDATE  renters.lease
SET     lease_to = '11-DEC-2012'
WHERE   id = 330;

UPDATE  renters.lease_term
SET     term_to = '11-DEC-2012'
WHERE   lease = 330;

-- COMMIT;


