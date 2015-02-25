/**
*** ====================================================================
***
***     Delete empty and unused pmc last accessed in Q1 2013  
***
***	====================================================================
**/

\i remove_pmc_function.sql


-- waples

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('waples');
    
    COMMIT;
    
-- invigoproperties

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('invigoproperties');
    
    COMMIT;

-- ipm247

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('ipm247');
    
    COMMIT;
    
-- rpm

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('rpm');
    
    COMMIT;
    
-- cpi

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('cpi');
    
    COMMIT;
    
-- aaa

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('aaa');
    
    COMMIT;
    
-- roomsforlease

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('roomsforlease');
    
    COMMIT;
    
-- kevinnorman

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('kevinnorman');
    
    COMMIT;
    
-- cornerstone

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('cornerstone');
    
    COMMIT;
    
-- anderson

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('anderson');
    
    COMMIT;
    
-- cosburnave

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('cosburnave');
    
    COMMIT;
    
-- atyourservice

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('atyourservice');
    
    COMMIT;
    
    
-- aelias

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('aelias');
    
    COMMIT;
    
-- opa

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('opa');
    
    COMMIT;
    

    
-- immomarketing

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('immomarketing');
    
    COMMIT;
    
-- vanak

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('vanak');
    
    COMMIT;
    
-- ropm

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('ropm');
    
    COMMIT;
    
-- gabe

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('gabe');
    
    COMMIT;
    
-- racheleddy

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('racheleddy');
    
    COMMIT;
    
-- akproperties

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('akproperties');
    
    COMMIT;
    
-- kgproperties

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('kgproperties');
    
    COMMIT;
    
-- taran

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('taran');
    
    COMMIT;
    
-- livpm

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('livpm');
    
    COMMIT;
    
-- onehundredba

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('onehundredba');
    
    COMMIT;
    

DROP FUNCTION _dba_.remove_pmc(text,boolean);
