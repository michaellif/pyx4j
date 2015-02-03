/**
***     ======================================================================================================================
***
***             Rename Sterling buildings
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

    UPDATE  sterling.building 
    SET     property_code = 'xave2175'
    WHERE   property_code = 'aven2175';
    
    UPDATE  sterling.building 
    SET     property_code = 'xave2177'
    WHERE   property_code = 'aven2177';
    
    UPDATE  sterling.building 
    SET     property_code = 'xave2181'
    WHERE   property_code = 'aven2181';
    
    UPDATE  sterling.building 
    SET     property_code = 'xwils166'
    WHERE   property_code = 'wils0166';
    
COMMIT;
