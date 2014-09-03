/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Fix ofm lease 
***
***     ===========================================================================================================
**/                                                     

BEGIN TRANSACTION;
    
    UPDATE  ofm.lease 
    SET     lease_to = 'SEP-03-2014',
            termination_lease_to = 'SEP-03-2014'
    WHERE   id = 58686;
    
    
    UPDATE  ofm.lease_term
    SET     term_to = 'SEP-03-2014'
    WHERE   id = 57936;
    
COMMIT;
