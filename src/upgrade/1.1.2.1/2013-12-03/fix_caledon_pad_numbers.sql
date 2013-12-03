/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Fix incorrect caledon pad file numbers
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

        UPDATE  _admin_.pad_file_creation_number
        SET     number = 119
        WHERE   funds_transfer_type = 'PreAuthorizedDebit'
        AND     number = 120;
        
        UPDATE  _admin_.pad_file_creation_number
        SET     number = 33
        WHERE   funds_transfer_type = 'DirectBankingPayment'
        AND     number = 34;
        
COMMIT;
