/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Cleanup pocberkley leftowers
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

        DELETE  FROM _admin_.pad_debit_record 
        WHERE   pad_batch IN (  SELECT id
                                FROM _admin_.pad_batch 
                                WHERE pmc_namespace = 'pocberkly');
        
        DELETE FROM _admin_.pad_batch WHERE pmc_namespace = 'pocberkly';
        
COMMIT;
