/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             greenwin n4 update - not executed on prod yet, maybe never will 
***
***     ======================================================================================================================
**/

/*
SELECT  b.property_code, 
        b.info_address_street_number,
        b.info_address_street_name,
        a.info_unit_number,
        a.info_legal_address_suite_number,
        a.info_legal_address_street_number,
        a.info_legal_address_street_name
FROM    greenwin.building b 
JOIN    greenwin.apt_unit a ON (b.id = a.building)
WHERE   b.property_code IN ('oldc0100','oldc0120','regi0300');
ORDER BY 1, 4;
*/

BEGIN TRANSACTION;

    UPDATE  greenwin.apt_unit AS a
    SET     info_legal_address_street_name = 'Old Carriage Dr.',
            info_legal_address_override = TRUE,
            info_legal_address_suite_number = regexp_replace(info_unit_number,'^0','')
    FROM    greenwin.building b 
    WHERE   a.building = b.id 
    AND     b.property_code IN ('oldc0100','oldc0120');

    UPDATE  greenwin.apt_unit AS a
    SET     info_legal_address_street_name = 'Regina St. N',
            info_legal_address_override = TRUE,
            info_legal_address_suite_number =  regexp_replace(info_unit_number,'^\d+-', ''),
            info_legal_address_street_number = regexp_replace(a.info_unit_number,'-\d+$', '')
    FROM    greenwin.building b 
    WHERE   a.building = b.id 
    AND     b.property_code = 'regi0300';

    UPDATE  greenwin.apt_unit AS a
    SET     info_legal_address_suite_number =  regexp_replace(info_legal_address_suite_number,'^0', '')
    FROM    greenwin.building b 
    WHERE   a.building = b.id 
    AND     b.property_code = 'regi0300';
    
COMMIT;
