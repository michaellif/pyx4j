/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             1.1.1.5 migration. Nice and easy for a change
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

        ALTER TABLE _admin_.admin_pmc_yardi_credential ALTER COLUMN  property_code TYPE VARCHAR(4000);
        ALTER TABLE _admin_.audit_record ALTER COLUMN details TYPE VARCHAR(10000);
       
       
COMMIT;
