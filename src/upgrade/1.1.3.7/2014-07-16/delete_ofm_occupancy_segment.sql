/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     DELETE extra ofm occupancy segment
***
***     ===========================================================================================================
**/                                              

BEGIN TRANSACTION;

    DELETE FROM ofm.apt_unit_occupancy_segment
    WHERE   unit = 67161
    AND     status = 'available'
    AND     date_from = '2014-07-01';
    
COMMIT;

