/**
*** ====================================================================
***
***     Delete empty and unused pmc last accessed in Q3 2013  
***
***	====================================================================
**/

\i remove_pmc_function.sql


-- dees

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('dees');
    
    COMMIT;

-- amstelmanza

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('amstelmanza');
    
    COMMIT;

-- ottawarental

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('ottawarental');
    
    COMMIT;

-- prerak

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('prerak');
    
    COMMIT;

-- brentfletcher

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('brentfletcher');
    
    COMMIT;

-- landlord

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('landlord');
    
    COMMIT;

-- joes

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('joes');
    
    COMMIT;

-- marcy

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('marcy');
    
    COMMIT;

-- halifaxapartments

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('halifaxapartments');
    
    COMMIT;

-- rlt

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('rlt');
    
    COMMIT;

-- altern

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('altern');
    
    COMMIT;

-- bmp

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('bmp');
    
    COMMIT;

-- willetproperties

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('willetproperties');
    
    COMMIT;

-- johnstreet

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('johnstreet');
    
    COMMIT;

-- bajaexec

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('bajaexec');
    
    COMMIT;

-- sf

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('sf');
    
    COMMIT;

-- stjeanmangement

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('stjeanmangement');
    
    COMMIT;

-- torontorelocationservice

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('torontorelocationservice');
    
    COMMIT;

-- nomadhomes

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('nomadhomes');
    
    COMMIT;

-- rockwell

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('rockwell');
    
    COMMIT;

-- my

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('my');
    
    COMMIT;

-- alexholding

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('alexholding');
    
    COMMIT;

-- thienmai

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('thienmai');
    
    COMMIT;

-- maxinetworks

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('maxinetworks');
    
    COMMIT;

-- ricciutirealty

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('ricciutirealty');
    
    COMMIT;

-- ld3mngmnt

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('ld3mngmnt');
    
    COMMIT;


DROP FUNCTION _dba_.remove_pmc(text,boolean);
