/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             update greenwin autopay agreement
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

    UPDATE  greenwin.autopay_agreement  AS aa
    SET     effective_from = '01-JUL-2014'
    FROM    greenwin.lease_participant lp,
            greenwin.lease l,
            greenwin.apt_unit a,
            greenwin.building b
    WHERE   b.property_code IN ('chan0286','chan0294','west0093','erb0285','rose0001')
    AND     b.id = a.building
    AND     a.id = l.unit
    AND     l.id = lp.lease
    AND     lp.id = aa.tenant
    AND     effective_from = '01-AUG-2014'
    
COMMIT;
