/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Remove starligh pmc
***
***     ===========================================================================================================
**/              

BEGIN TRANSACTION;

    SELECT * FROM _dba_.remove_pmc('starlight');

COMMIT;


DROP FUNCTION _dba_.remove_pmc(text);
