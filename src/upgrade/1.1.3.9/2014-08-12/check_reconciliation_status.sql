/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***     
***     Check that reconciliation status is in line with payment status
***
***     ===========================================================================================================
**/                                   

CREATE OR REPLACE FUNCTION _dba_.check_reconciliation_status() 
RETURNS TABLE   (   pmc             VARCHAR(64),
                    property_code   VARCHAR(64),
                    lease_id        VARCHAR(64),
                    transaction_id  VARCHAR(64),
                    payment_date    DATE,
                    amount          NUMERIC(18,2),
                    merchant_terminal_id    VARCHAR(64),
                    reconciliation_status   VARCHAR(64),
                    processing_status       BOOLEAN,
                    payment_status  VARCHAR(64),
                    reason_code     VARCHAR(15),
                    reason_text     VARCHAR(120))
AS 
$$
DECLARE
    
    v_schema_name           VARCHAR(64);
    
BEGIN

    FOR pmc, transaction_id, payment_date, amount,
        merchant_terminal_id, reconciliation_status,
        processing_status, reason_code, reason_text IN 
    SELECT  a.namespace AS pmc, r.transaction_id, 
            r.payment_date,r.amount, r.merchant_terminal_id,
            r.reconciliation_status, r.processing_status,
            r.reason_code, r.reason_text
    FROM    _admin_.funds_reconciliation_record_record r
    JOIN    _admin_.admin_pmc_merchant_account_index m ON (r.merchant_terminal_id = m.merchant_terminal_id)
    JOIN    _admin_.admin_pmc a ON (a.id = m.pmc)
    WHERE   r.processing_status
    AND     r.reconciliation_status IN ('RETURNED', 'REJECTED')
    AND     r.payment_date >= '01-AUG-2014'
    LOOP
        
        EXECUTE 'SELECT b.property_code, l.lease_id, p.payment_status '
                ||'FROM     '||pmc||'.payment_record p '
                ||'JOIN     '||pmc||'.lease_term_participant ltp ON (ltp.id = p.lease_term_participant) '
                ||'JOIN     '||pmc||'.lease_participant lp ON (lp.id = ltp.lease_participant) '
                ||'JOIN     '||pmc||'.lease l ON (l.id = lp.lease) '
                ||'JOIN     '||pmc||'.apt_unit au ON (au.id = l.unit) '
                ||'JOIN     '||pmc||'.building b ON (b.id = au.building) '
                ||'WHERE    p.id = '||transaction_id::bigint 
        INTO    property_code, lease_id, payment_status ;
        
        RETURN NEXT;
        
    END LOOP;    
    
END;
$$
LANGUAGE plpgsql VOLATILE;
