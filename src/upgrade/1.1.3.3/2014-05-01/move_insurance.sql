/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***            Move insurance from greenwin to other pmc
***            To be executed on production on May 1, 2014 
***
***     ======================================================================================================================
**/

\i move_insurance_function.sql

-- Cogir

BEGIN TRANSACTION;

    SELECT _dba_.move_insurance('cogir',ARRAY['rich0033']);
    
COMMIT;

-- DMS

BEGIN TRANSACTION;

    SELECT _dba_.move_insurance('dms',ARRAY['mark0150','mark0155','mark0160','quee0297','firs0053','huro2465']);

COMMIT;

-- Larlyn

BEGIN TRANSACTION;

    SELECT _dba_.move_insurance('larlyn',ARRAY['dale0019','dale0021','belm0545','belm0547','belm0565']);

COMMIT;

-- Sterling 

BEGIN TRANSACTION;

    SELECT _dba_.move_insurance('sterling',ARRAY['aven2171','west2292','dund0015','well0077',
                'well0155','well0080','stjo0016','west2220','west2222',
                'carl0400']);

COMMIT;

DROP FUNCTION _dba_.move_insurance(text,text[]);
