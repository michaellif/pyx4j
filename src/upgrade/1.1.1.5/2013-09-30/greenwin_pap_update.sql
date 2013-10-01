/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Greenwin pap update
***
***     ======================================================================================================================
**/


CREATE TABLE _dba_.tmp_pap
(       pap_id                          BIGINT,
        payment_id                      BIGINT,
        cycle_id                        BIGINT
        );
        
SET client_encoding TO 'latin1';
        
COPY _dba_.tmp_pap FROM '/home/akinareevski/greenwin_pap.csv' CSV HEADER;        
          

BEGIN TRANSACTION;
        
        
        
        UPDATE  greenwin.payment_record AS pr
        SET     preauthorized_payment = t.pap_id,
                pad_billing_cycle = t.cycle_id
        FROM    _dba_.tmp_pap t
        WHERE   pr.id = t.payment_id
        AND     pr.pad_billing_cycle IS NULL
        AND     pr.preauthorized_payment IS NULL;
        

COMMIT;

TRUNCATE TABLE _dba_.tmp_pap;

/** Same thing for Sterling **/

COPY _dba_.tmp_pap FROM '/home/akinareevski/sterling_pap.csv' CSV HEADER;        
          

BEGIN TRANSACTION;
        
        
        
        UPDATE  sterling.payment_record AS pr
        SET     preauthorized_payment = t.pap_id,
                pad_billing_cycle = t.cycle_id
        FROM    (SELECT t.payment_id,t.pap_id,
                        bc.id AS cycle_id
                FROM    _dba_.tmp_pap t
                JOIN    sterling.payment_record p ON (t.payment_id = p.id)
                JOIN    sterling.lease_term_participant ltp ON (p.lease_term_participant = ltp.id)
                JOIN    sterling.lease_participant lp ON (ltp.lease_participant = lp.id)
                JOIN    sterling.lease l ON (lp.lease = l.id)
                JOIN    sterling.apt_unit a ON (l.unit = a.id)
                JOIN    sterling.building b ON (a.building = b.id)
                JOIN    sterling.billing_billing_cycle bc ON (bc.building = b.id)
                WHERE   bc.billing_cycle_start_date = '01-OCT-2013') AS t
        WHERE   pr.id = t.payment_id;
        

COMMIT; 

-- DROP TABLE _dba_.tmp_pap;   
