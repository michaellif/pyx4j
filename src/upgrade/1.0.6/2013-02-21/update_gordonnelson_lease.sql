/**     =============================================
***
***             Update gordonnelson existing lease
***
***     ============================================
**/


BEGIN TRANSACTION;

UPDATE  gordonnelson.lease 
SET     lease_to = '2014-01-01',
        expected_move_out = '2014-01-01'
WHERE   id = 413;

UPDATE  gordonnelson.lease_term 
SET     term_to = '2014-01-01' 
WHERE   lease = 413;

COMMIT;
