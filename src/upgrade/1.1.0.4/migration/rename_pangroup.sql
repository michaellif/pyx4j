/**
***     ======================================================================================================================
***
***             @version $Revision: 12919 $ ($Author: akinareevski $) $Date: 2013-05-28 16:26:54 -0400 (Tue, 28 May 2013) $
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
        
