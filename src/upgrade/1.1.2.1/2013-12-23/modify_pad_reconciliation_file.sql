/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             set column _admin_.pad_reconciliation_file.created NOT NULL
***
***     ======================================================================================================================
**/


BEGIN TRANSACTION;

        UPDATE  _admin_.pad_reconciliation_file
        SET     created = date(substring(file_name, 1,8))
        WHERE   created IS NULL ;
        
        ALTER TABLE _admin_.pad_reconciliation_file ALTER COLUMN created SET NOT NULL;
        
COMMIT;
        
        
