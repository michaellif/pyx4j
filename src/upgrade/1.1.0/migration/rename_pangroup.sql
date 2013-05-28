/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Rename PAN Group Properties to PAN Group Properties Old, including schema and DNS name  
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

        UPDATE  _admin_.admin_pmc 
        SET     name = 'PAN Group Properties Old',
                namespace = 'pangroup_old',
                dns_name = 'pangroup-old'
        WHERE   id = 2;
        
        ALTER SCHEMA pangroup RENAME TO pangroup_old;
        
COMMIT;
        
