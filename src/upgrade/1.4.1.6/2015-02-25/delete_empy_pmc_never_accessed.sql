/**
*** ====================================================================
***
***     Delete empty and unused pmc that were never accessed  
***
***	====================================================================
**/

\i remove_pmc_function.sql


-- drydenrentals

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('drydenrentals');
    
    COMMIT;
    
-- myrentalproperty

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('myrentalproperty');
    
    COMMIT;

-- management

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('management');
    
    COMMIT;
    
-- mamaisondansparis

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('mamaisondansparis');
    
    COMMIT;
    
-- michelle

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('michelle');
    
    COMMIT;
    
-- mwalton

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('mwalton');
    
    COMMIT;
    
-- remus

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('remus');
    
    COMMIT;
    
-- norland

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('norland');
    
    COMMIT;
    
-- coreywalsh

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('coreywalsh');
    
    COMMIT;
    

-- gestion

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('gestion');
    
    COMMIT;
    
-- grandview846

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('grandview846');
    
    COMMIT;
    
-- saeed

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('saeed');
    
    COMMIT;
    
-- creationsbmantel

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('creationsbmantel');
    
    COMMIT;
    
-- esthetician

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('esthetician');
    
    COMMIT;
    
-- kprobbins

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('kprobbins');
    
    COMMIT;
    
-- hoponline

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('hoponline');
    
    COMMIT;
    
-- klassen

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('klassen');
    
    COMMIT;
    
-- latief

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('latief');
    
    COMMIT;
    
-- townline

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('townline');
    
    COMMIT;
    
-- wales

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('wales');
    
    COMMIT;
    
-- werentjakarta

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('werentjakarta');
    
    COMMIT;

    
DROP FUNCTION _dba_.remove_pmc(text,boolean);
