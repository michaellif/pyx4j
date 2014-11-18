/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Remove greeny1/greeny2 created on production by mistake
***
***     =====================================================================================================================
**/

\i remove_pmc_function.sql

BEGIN TRANSACTION;

    SELECT * FROM _dba_.remove_pmc('greeny1');
    SELECT * FROM _dba_.remove_pmc('greeny2');
    
COMMIT;


DROP FUNCTION _dba_.remove_pmc(text, boolean);
