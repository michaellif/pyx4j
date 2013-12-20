/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Add a space to greenwin building.info_address_postal_code (apparently, should be fixed on prod automatically)
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

        UPDATE  greenwin.building
        SET     info_address_postal_code = regexp_replace (info_address_postal_code, '^([A-Z][0-9][A-Z])',E'\\1 ')
        WHERE   info_address_postal_code !~ ' ';
        
COMMIT;
