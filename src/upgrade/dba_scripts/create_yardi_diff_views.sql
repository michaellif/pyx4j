/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Comparison views for Yardi import/charges testting on env 33
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.create_comparison_views(v_schema_name TEXT, v_batch_id TEXT) RETURNS VOID AS
$$
BEGIN

        -- Create a vista transactions viev
        
        EXECUTE 'CREATE OR REPLACE VIEW _admin_.'||v_schema_name||'_transactions AS '
                ||'(SELECT      l.lease_id AS client_id, '
                ||'             pr.amount,pmd.bank_id,pmd.branch_transit_number,'
                ||'             pmd.account_no_number AS account_number, '
                ||'             b.property_code '
                ||'FROM         '||v_schema_name||'.payment_record pr '
                ||'JOIN         '||v_schema_name||'.payment_method pm ON (pm.id = pr.payment_method) '
                ||'JOIN         '||v_schema_name||'.payment_payment_details pmd ON (pm.details = pmd.id) '
                ||'JOIN         '||v_schema_name||'.lease_term_participant ltp ON (ltp.id = pr.lease_term_participant) '
                ||'JOIN         '||v_schema_name||'.lease_participant lp ON (lp.id = ltp.lease_participant) '
                ||'JOIN         '||v_schema_name||'.lease l ON (lp.lease = l.id) '
                ||'JOIN         '||v_schema_name||'.apt_unit a ON (a.id = l.unit) '
                ||'JOIN         '||v_schema_name||'.building b ON (b.id = a.building) '
                ||'WHERE        pr.created_date >= ''2013-04-28'' )';
                
        EXECUTE 'ALTER VIEW _admin_.'||v_schema_name||'_transactions OWNER TO vista';       
                
        -- Join view 
        
        EXECUTE 'CREATE OR REPLACE VIEW _admin_.'||v_schema_name||'_diff_join  AS '
                ||'(SELECT      CASE WHEN a.client_id IS NULL THEN b.client_id ELSE a.client_id END AS client_id, '
                ||'             c.property_code, '
                ||'             a.amount AS yardi_amount, '
                ||'             b.amount AS vista_amount, '
                ||'             abs(COALESCE(a.amount,0) - COALESCE(b.amount,0)) AS delta, '
                ||'             CASE WHEN a.bank_id IS NULL THEN b.bank_id ELSE a.bank_id END AS bank_id, '
                ||'             CASE WHEN a.branch_transit_number IS NULL THEN b.branch_transit_number ELSE a.branch_transit_number END AS branch_transit_number, '
                ||'             CASE WHEN a.account_number IS NULL THEN b.account_number ELSE a.account_number END AS account_number   '          
                ||'     FROM (  SELECT  client_id,amount,bank_id,branch_transit_number,account_number '
                ||'             FROM    _admin_.test_yardi_eft '
                ||'             WHERE batch_id = '||quote_literal(v_batch_id)||' '
                ||'             EXCEPT '
                ||'             SELECT  client_id,amount,bank_id,branch_transit_number,account_number '
                ||'             FROM    _admin_.'||v_schema_name||'_transactions) AS a '
                ||' FULL OUTER JOIN (   SELECT  client_id,amount,bank_id,branch_transit_number,account_number '
                ||'                     FROM    _admin_.'||v_schema_name||'_transactions '
                ||'                     EXCEPT '
                ||'                     SELECT  client_id,amount,bank_id,branch_transit_number,account_number '
                ||'                     FROM    _admin_.test_yardi_eft '
                ||'                     WHERE batch_id = '||quote_literal(v_batch_id)||') b ' 
                ||' ON   (a.client_id = b.client_id AND a.bank_id = b.bank_id AND a.branch_transit_number = b.branch_transit_number AND a.account_number = b.account_number) '
                ||'LEFT JOIN         _admin_.'||v_schema_name||'_transactions c ON (b.client_id = c.client_id)) ';
                
                
        EXECUTE 'ALTER VIEW _admin_.'||v_schema_name||'_diff_join OWNER TO vista';
                
END;
$$
LANGUAGE plpgsql VOLATILE;
 
