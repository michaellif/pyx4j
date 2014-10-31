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
    SET     lease_to = '17-OCT-2014',
            termination_lease_to = '17-OCT-2014',
            expected_move_out = '17-OCT-2014'
    WHERE   id = 105727;
    
    
    UPDATE  ofm.lease_term
    SET     term_to = '17-OCT-2014'
    WHERE   id = 69549;
    
COMMIT;

                                           
