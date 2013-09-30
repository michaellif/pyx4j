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
        

-- COMMIT;

-- DROP TABLE _dba_.tmp_pap;

       
        
