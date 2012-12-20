/**
***     ===================================================================
***
***             Remove commercial unit product item type
***             
***     ===================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.remove_commercial_unit() RETURNS VOID AS
$$
DECLARE
        v_schema_name   VARCHAR(64);
BEGIN
        FOR v_schema_name IN 
        SELECT namespace FROM _admin_.admin_pmc
        WHERE status != 'Created'
        ORDER BY 1
        LOOP
                EXECUTE 'DELETE FROM '||v_schema_name||'.product_item_type '
                ||'WHERE service_type = ''commercialUnit'' ';
        END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

SELECT _dba_.remove_commercial_unit();

DROP FUNCTION _dba_.remove_commercial_unit();

-- COMMIT;
