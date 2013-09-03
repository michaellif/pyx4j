/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Function to monitor payment records in status received - temporary
***
***     =====================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.count_received_payments() 
RETURNS TABLE ( pmc             VARCHAR(120),
                row_count       BIGINT) AS
$$
DECLARE
        v_schema_name           VARCHAR(64);
        v_sql                   TEXT;
BEGIN
        FOR v_schema_name IN
        SELECT namespace FROM _admin_.admin_pmc
        WHERE status != 'Created'
        ORDER BY 1
        LOOP
                v_sql :=  'SELECT '||quote_literal(v_schema_name)||' AS pmc,COUNT(p.id) '
                ||' FROM '||v_schema_name||'.payment_record p '
                ||'JOIN '||v_schema_name||'.payment_method pm ON (p.payment_method = pm.id) '
                ||'WHERE p.payment_status = ''Received'' '
                ||'AND   pm.payment_type IN (''DirectBanking'',''Echeck'') ';
                
   
                EXECUTE  v_sql INTO pmc,row_count;
                RETURN NEXT;

        END LOOP; 
END;
$$
LANGUAGE plpgsql VOLATILE;

SELECT * FROM _dba_.count_received_payments() WHERE row_count > 0;

DROP FUNCTION _dba_.count_received_payments();
