/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             update sterling autopay agreement
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

    UPDATE  sterling.autopay_agreement  AS aa
    SET     effective_from = '01-JUL-2014'
    FROM    sterling.lease_participant lp,
            sterling.lease l,
            sterling.apt_unit a,
            sterling.building b
    WHERE   b.property_code IN ('maxw0131','west2292')
    AND     b.id = a.building
    AND     a.id = l.unit
    AND     l.id = lp.lease
    AND     lp.id = aa.tenant
    AND     effective_from = '01-AUG-2014';
    
COMMIT;
