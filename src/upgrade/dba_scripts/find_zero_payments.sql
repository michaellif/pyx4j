/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Function to find zero payment records 
***
***     ===========================================================================================================
**/  

CREATE OR REPLACE FUNCTION _dba_.find_zero_payments(v_schema_name TEXT) 
RETURNS SETOF RECORD
/*
RETURNS TABLE   (       pmc                     TEXT,
                        new_pap_id              BIGINT,
                        review_of_pap           BIGINT,
                        lease_id                VARCHAR(50),
                        new_amount              NUMERIC(18,2),
                        old_amount              NUMERIC(18,2),
                        new_agreed_price        NUMERIC(18,2),
                        old_agreed_price        NUMERIC(18,2),
                        created_on              TIMESTAMP,
                        updated_on              TIMESTAMP)
*/
AS
$$
DECLARE
        v_sql           TEXT;
BEGIN
        v_sql:=         'WITH t AS      (SELECT  aa.id AS pap,l.lease_id, aa.review_of_pap,'
                        ||'             aac.amount, bi.agreed_price, aa.is_deleted '
                        ||'             FROM    '||v_schema_name||'.autopay_agreement aa '
                        ||'             JOIN    '||v_schema_name||'.lease_participant lp ON (aa.tenant = lp.id) '
                        ||'             JOIN    '||v_schema_name||'.lease l ON (lp.lease = l.id) '
                        ||'             JOIN    '||v_schema_name||'.autopay_agreement_covered_item aac ON (aac.pap = aa.id) '
                        ||'             JOIN    '||v_schema_name||'.billable_item bi ON (aac.billable_item = bi.id) '
                        ||'             JOIN    '||v_schema_name||'.lease_term_v ltv ON (bi.id = lease_products_service_item)), '
                        ||'     l AS    (SELECT entity_id, event, created '
                        ||'             FROM    _admin_.audit_record '
                        ||'             WHERE   entity_class = ''AutopayAgreement'' '
                        ||'             AND     usr IS NULL '
                        ||'             AND     namespace = '||quote_literal(v_schema_name)||') '
                        ||'SELECT       '||quote_literal(v_schema_name)||'::text AS pmc, t1.pap::bigint AS new_pap_id,'
                        ||'             t1.review_of_pap::bigint,t1.lease_id::VARCHAR(50),t1.amount::numeric(18,2) AS new_amount, '
                        ||'             t2.amount::numeric(18,2) AS old_amount,t1.agreed_price::numeric(18,2)  AS new_agreed_price, '
                        ||'             t2.agreed_price::numeric(18,2) AS old_agreed_price, '
                        ||'             l1.created::timestamp AS created_on, l2.created::timestamp AS updated_on '
                        ||'FROM         t AS t1 '
                        ||'JOIN         t AS t2 ON (t1.review_of_pap = t2.pap) '
                        ||'JOIN         l AS l1 ON (l1.entity_id = t1.pap AND l1.event = ''Create'') '
                        ||'JOIN         l AS l2 ON (l2.entity_id = t1.pap AND l2.event = ''Update'' AND l1.created >= l2.created - interval ''30 minutes'') '
                        ||'WHERE        NOT t1.is_deleted '
                        ||'AND          t1.amount = 0 '
                        ||'AND          t1.agreed_price != 0 '
                        ||'AND          t2.amount != 0 ';
                        
        -- EXECUTE v_sql INTO pmc,new_pap_id,review_of_pap,lease_id,new_amount,old_amount,new_agreed_price,old_agreed_price,created_on,updated_on;
        RETURN  QUERY EXECUTE v_sql;
END;
$$
LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE VIEW _dba_.zero_payments AS (
        SELECT * FROM _dba_.find_zero_payments('berkley') AS ( pmc TEXT,new_pap_id BIGINT,review_of_pap BIGINT,lease_id VARCHAR(50),
                                                                new_amount NUMERIC(18,2),old_amount NUMERIC(18,2),new_agreed_price NUMERIC(18,2),
                                                                old_agreed_price NUMERIC(18,2),created_on TIMESTAMP,updated_on TIMESTAMP)
        UNION
        SELECT * FROM _dba_.find_zero_payments('cogir') AS ( pmc TEXT,new_pap_id BIGINT,review_of_pap BIGINT,lease_id VARCHAR(50),
                                                                new_amount NUMERIC(18,2),old_amount NUMERIC(18,2),new_agreed_price NUMERIC(18,2),
                                                                old_agreed_price NUMERIC(18,2),created_on TIMESTAMP,updated_on TIMESTAMP)
        UNION
        SELECT * FROM _dba_.find_zero_payments('greenwin') AS ( pmc TEXT,new_pap_id BIGINT,review_of_pap BIGINT,lease_id VARCHAR(50),
                                                                new_amount NUMERIC(18,2),old_amount NUMERIC(18,2),new_agreed_price NUMERIC(18,2),
                                                                old_agreed_price NUMERIC(18,2),created_on TIMESTAMP,updated_on TIMESTAMP)
        UNION
        SELECT * FROM _dba_.find_zero_payments('larlyn') AS ( pmc TEXT,new_pap_id BIGINT,review_of_pap BIGINT,lease_id VARCHAR(50),
                                                                new_amount NUMERIC(18,2),old_amount NUMERIC(18,2),new_agreed_price NUMERIC(18,2),
                                                                old_agreed_price NUMERIC(18,2),created_on TIMESTAMP,updated_on TIMESTAMP)
        UNION
        SELECT * FROM _dba_.find_zero_payments('metcap') AS ( pmc TEXT,new_pap_id BIGINT,review_of_pap BIGINT,lease_id VARCHAR(50),
                                                                new_amount NUMERIC(18,2),old_amount NUMERIC(18,2),new_agreed_price NUMERIC(18,2),
                                                                old_agreed_price NUMERIC(18,2),created_on TIMESTAMP,updated_on TIMESTAMP)
        UNION
        SELECT * FROM _dba_.find_zero_payments('realstar') AS ( pmc TEXT,new_pap_id BIGINT,review_of_pap BIGINT,lease_id VARCHAR(50),
                                                                new_amount NUMERIC(18,2),old_amount NUMERIC(18,2),new_agreed_price NUMERIC(18,2),
                                                                old_agreed_price NUMERIC(18,2),created_on TIMESTAMP,updated_on TIMESTAMP)
        UNION
        SELECT * FROM _dba_.find_zero_payments('sterling') AS ( pmc TEXT,new_pap_id BIGINT,review_of_pap BIGINT,lease_id VARCHAR(50),
                                                                new_amount NUMERIC(18,2),old_amount NUMERIC(18,2),new_agreed_price NUMERIC(18,2),
                                                                old_agreed_price NUMERIC(18,2),created_on TIMESTAMP,updated_on TIMESTAMP)
        ORDER BY 1
);

                     
                                                                       


