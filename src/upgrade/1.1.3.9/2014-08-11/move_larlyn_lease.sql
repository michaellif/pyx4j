/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             move larlyn lease from dale0019 to dale0021
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

    UPDATE  larlyn.lease AS l
    SET     unit = a.id 
    FROM    larlyn.apt_unit a 
    JOIN    larlyn.building b ON (a.building = b.id)
    WHERE   l.lease_id = 't0044411'
    AND     b.property_code = 'dale0021'
    AND     a.info_unit_number = '0104';
    
    UPDATE  larlyn.lease_term AS lt
    SET     unit = l.unit 
    FROM    larlyn.lease l
    WHERE   lt.lease = l.id
    AND     l.lease_id = 't0044411';
    
COMMIT;
