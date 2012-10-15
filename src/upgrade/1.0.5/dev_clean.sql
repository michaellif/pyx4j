/**
***	=======================================================================
***	@version $Revision$ ($Author$) $Date$
***
***		Remove data from development enviroment preload that is not part of production data
***
***	=======================================================================
**/

BEGIN TRANSACTION;

CREATE OR REPLACE FUNCTION _dba_.dev_drop() RETURNS VOID AS
$$
DECLARE
	v_schema_name			VARCHAR(64);
	v_table_name			VARCHAR(64);
	v_void				CHAR(1);
BEGIN
	FOR v_schema_name IN
	SELECT namespace FROM _admin_.admin_pmc WHERE status IN ('Active','Suspended')
	LOOP
		-- income related tables
		FOREACH v_table_name IN ARRAY
		ARRAY[ 'income_info_other',
			   'income_info_self_employed',
			   'income_info_social_services',
			   'income_info_seasonally_employed',
			   'income_info_student_income',
			   'income_info_employer',
			   'personal_income',
			   'person_screening_personal_asset']
		LOOP
			SELECT * INTO v_void FROM _dba_.truncate_schema_table(v_schema_name,v_table_name, TRUE) ;
		END LOOP;

		-- All the rest  screening tables  asumend not existen in DB

		FOREACH v_table_name IN ARRAY
		ARRAY[	'person_screening',
			    'legal_questions']
		LOOP
			SELECT * INTO v_void FROM _dba_.truncate_schema_table(v_schema_name,v_table_name, TRUE) ;
		END LOOP;

        EXECUTE 'UPDATE '||v_schema_name||'.tenant SET screening = NULL';
        EXECUTE 'UPDATE '||v_schema_name||'.guarantor SET screening = NULL';


	END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;


SELECT * FROM _dba_.dev_drop();
DROP FUNCTION _dba_.dev_drop();

COMMIT;


