/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Fix issues with units that have an active lease, yet marked as available for rent
***
***     =====================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.fix_unit_availability() RETURNS VOID AS
$$
DECLARE
        v_schema_name   VARCHAR(64);
BEGIN
        
        FOR v_schema_name IN 
        SELECT  a.namespace
        FROM    _admin_.admin_pmc a
        JOIN    _admin_.admin_pmc_vista_features f ON (f.id = a.features)
        WHERE   f.yardi_integration
        AND     a.status = 'Active' 
        LOOP
                EXECUTE 'UPDATE '||v_schema_name||'.apt_unit AS a '
                        ||'SET  _available_for_rent = NULL '
                        ||'FROM '||v_schema_name||'.lease l '
                        ||'WHERE a.id = l.unit '
                        ||'AND  l.status = ''Active'' '
                        ||'AND  a._available_for_rent <= current_date ';
        END LOOP;   
END;
$$
LANGUAGE plpgsql VOLATILE;


BEGIN TRANSACTION;

        SELECT * FROM  _dba_.fix_unit_availability();
        
COMMIT;

DROP FUNCTION _dba_.fix_unit_availability();
       
