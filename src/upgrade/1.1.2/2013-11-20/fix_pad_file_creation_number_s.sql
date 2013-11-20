/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _admin_.pad_file.file_creation_s bugfix
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

        UPDATE  _admin_.pad_file 
        SET     file_creation_number_s = _dba_.convert_id_to_string(file_creation_number) 
        WHERE   file_creation_number_s IS NULL;
        
COMMIT;
