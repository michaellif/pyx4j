/**
*** ====================================================================
***
***     Delete empty and unused pmc last accessed in Q3 2013  
***
***	====================================================================
**/

\i remove_pmc_function.sql


-- holla

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('holla');
    
    COMMIT;

-- sunrise

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('sunrise');
    
    COMMIT;

-- asenseoforder

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('asenseoforder');
    
    COMMIT;

-- arxcapital

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('arxcapital');
    
    COMMIT;

-- integral

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('integral');
    
    COMMIT;

-- whitestone

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('whitestone');
    
    COMMIT;

-- yorkwestdevelopments

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('yorkwestdevelopments');
    
    COMMIT;

-- ace

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('ace');
    
    COMMIT;

-- alistings

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('alistings');
    
    COMMIT;

-- denver

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('denver');
    
    COMMIT;

-- jcpropertymanagement

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('jcpropertymanagement');
    
    COMMIT;

-- brody

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('brody');
    
    COMMIT;

-- lhoa

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('lhoa');
    
    COMMIT;

-- mannyg

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('mannyg');
    
    COMMIT;

-- beachtowers

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('beachtowers');
    
    COMMIT;

-- sarah

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('sarah');
    
    COMMIT;

-- primecorp

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('primecorp');
    
    COMMIT;

-- ppm

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('ppm');
    
    COMMIT;

-- richmondcoachhouse

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('richmondcoachhouse');
    
    COMMIT;

-- cindyhamel

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('cindyhamel');
    
    COMMIT;

-- peitest

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('peitest');
    
    COMMIT;

-- joblins

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('joblins');
    
    COMMIT;

-- coalharbourproperties

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('coalharbourproperties');
    
    COMMIT;

-- newdundee

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('newdundee');
    
    COMMIT;

-- richman

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('richman');
    
    COMMIT;

-- d131margaretstreet

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('d131margaretstreet');
    
    COMMIT;

-- vistapacific

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('vistapacific');
    
    COMMIT;

-- getlet

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('getlet');
    
    COMMIT;

-- chapman

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('chapman');
    
    COMMIT;

-- springvilla

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('springvilla');
    
    COMMIT;


-- sun

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('sun');
    
    COMMIT;
    

-- kandrusky

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('kandrusky');
    
    COMMIT;


DROP FUNCTION _dba_.remove_pmc(text,boolean);
