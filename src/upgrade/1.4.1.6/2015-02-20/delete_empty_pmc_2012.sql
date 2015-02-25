/**
*** ====================================================================
***
***     Delete empty and unused pmc last accessed in 2012  
***
***	====================================================================
**/

\i remove_pmc_function.sql


-- ensoholdings

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('ensoholdings');
    
    COMMIT;

-- ziyufan

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('ziyufan');
    
    COMMIT;

-- tamburellorealty

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('tamburellorealty');
    
    COMMIT;

-- cactus

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('cactus');
    
    COMMIT;

-- londonrealtor

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('londonrealtor');
    
    COMMIT;

-- kyte

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('kyte');
    
    COMMIT;

-- amke

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('amke');
    
    COMMIT;

-- kumabe

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('kumabe');
    
    COMMIT;

-- citifiedresident

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('citifiedresident');
    
    COMMIT;

-- cleeves

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('cleeves');
    
    COMMIT;
    
-- homelife

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('homelife');
    
    COMMIT;

-- bsh

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('bsh');
    
    COMMIT;

-- ewan

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('ewan');
    
    COMMIT;

-- redbrick

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('redbrick');
    
    COMMIT;
    
-- phanley2000

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('phanley2000');
    
    COMMIT;

-- sppropertymgmt

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('sppropertymgmt');
    
    COMMIT;

-- flynnproperties

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('flynnproperties');
    
    COMMIT;

-- bob44

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('bob44');
    
    COMMIT;

-- appleridgehomes

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('appleridgehomes');
    
    COMMIT;
    
-- garyreedproperties

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('garyreedproperties');
    
    COMMIT;
    
-- tim

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('tim');
    
    COMMIT;
    
-- kirshenblatt

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('kirshenblatt');
    
    COMMIT;
    
DROP FUNCTION _dba_.remove_pmc(text,boolean);
