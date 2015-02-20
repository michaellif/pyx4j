/**
*** ====================================================================
***
***     Delete empty and unused pmc last accessed in Q2 2013  
***
***	====================================================================
**/

\i remove_pmc_function.sql


-- molinelli

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('molinelli');
    
    COMMIT;
    
-- habitat

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('habitat');
    
    COMMIT;
    
-- barrierentals

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('barrierentals');
    
    COMMIT;
    
-- ip

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('ip');
    
    COMMIT;
  
/*
-- demopv

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('demopv');
    
    COMMIT;
*/

-- maksym

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('maksym');
    
    COMMIT;
    
-- vf

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('vf');
    
    COMMIT;
    
-- performancepm

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('performancepm');
    
    COMMIT;
    
-- prueba

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('prueba');
    
    COMMIT;
    
-- mendes

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('mendes');
    
    COMMIT;
    
-- banmanproperties

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('banmanproperties');
    
    COMMIT;
    
-- wise

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('wise');
    
    COMMIT;
    
-- solitude

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('solitude');
    
    COMMIT;
    
-- dreamrentals

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('dreamrentals');
    
    COMMIT;
    

-- shimmo

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('shimmo');
    
    COMMIT;
    
-- evvo

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('evvo');
    
    COMMIT;
    
-- properinvest

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('properinvest');
    
    COMMIT;
    
-- zgrant

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('zgrant');
    
    COMMIT;
    
-- erik

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('erik');
    
    COMMIT;
    
-- kingdomproperties

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('kingdomproperties');
    
    COMMIT;
    
-- apmcornwall

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('apmcornwall');
    
    COMMIT;
    
-- karansujay

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('karansujay');
    
    COMMIT;
    
-- pondarealty

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('pondarealty');
    
    COMMIT;
    
-- stewartmanagement

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('stewartmanagement');
    
    COMMIT;

-- portland

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('portland');
    
    COMMIT;
    
-- morgan

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('morgan');
    
    COMMIT;
    
-- arnemann

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('arnemann');
    
    COMMIT;
    
-- highlandplace

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('highlandplace');
    
    COMMIT;
    
-- dbc

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('dbc');
    
    COMMIT;
    
-- freeintern

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('freeintern');
    
    COMMIT;
    
-- citygateproperties

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('citygateproperties');
    
    COMMIT;
    
-- padpicker

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('padpicker');
    
    COMMIT;
    
-- rooof

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('rooof');
    
    COMMIT;
    
-- isitest

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('isitest');
    
    COMMIT;
    
-- paget

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('paget');
    
    COMMIT;

-- lbenterprise

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('lbenterprise');
    
    COMMIT;
    
-- rwrealestate

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('rwrealestate');
    
    COMMIT;
    
-- yawateg

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('yawateg');
    
    COMMIT;
    
-- westwoodridge

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('westwoodridge');
    
    COMMIT;
    
-- te

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('te');
    
    COMMIT;
    
-- harry

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('harry');
    
    COMMIT;
    
-- vicotest

    BEGIN TRANSACTION;
    
        SELECT * FROM _dba_.remove_pmc('vicotest');
    
    COMMIT;
    

DROP FUNCTION _dba_.remove_pmc(text,boolean);
