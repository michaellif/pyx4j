/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Fix OFM lease with start date same as creation date
***
***     ===========================================================================================================
**/                                                


BEGIN TRANSACTION;
        
        UPDATE ofm.lease SET creation_date = '23-JUL-2013' WHERE id = 22047;
        
COMMIT;
