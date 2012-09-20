/**
***	======================================================================
***
***		DB Updates performed on September 19, 2012
***
***	======================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.db_migration_2012_09_17() RETURNS VOID
AS
$$
DECLARE
	v_schema_name		VARCHAR(64);
BEGIN
	FOR v_schema_name IN
	SELECT namespace FROM _admin_.admin_pmc 
	WHERE status IN ('Active','Suspended')
	LOOP
		EXECUTE 'UPDATE '||v_schema_name||'.column_descriptor_entity '||
		'SET property_path = ''PaymentsSummary/building/'', '||
		'title = ''Building'' '||
		'WHERE property_path = ''PaymentsSummary/merchantAccount/accountNumber/'' ';
	
		-- Update id_assignment_item
		EXECUTE 'UPDATE '||v_schema_name||'.id_assignment_item '||
		'SET policy = (SELECT MAX(id) FROM '||v_schema_name||'.id_assignment_policy ) '||
		'WHERE target = ''employee'' ';
	
	END LOOP;

END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;
	SELECT _dba_.db_migration_2012_09_17();
COMMIT;

DROP FUNCTION _dba_.db_migration_2012_09_17();
