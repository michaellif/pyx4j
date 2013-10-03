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
        SET     status = 'Completed',
                completion = 'Termination',
                lease_to = '17-SEP-2013',
                termination_lease_to = '17-SEP-2013'  
        WHERE   id= 346;      
        
COMMIT;

BEGIN TRANSACTION;

        UPDATE  ofm.lease 
        SET     completion = NULL ,
                lease_to = '30-JUN-2013',
                termination_lease_to = NULL, 
                actual_move_out = '30-JUN-2013'  
        WHERE   id= 346;

COMMIT;                                         


BEGIN TRANSACTION;

        
        UPDATE  ofm.lease 
        SET     completion = 'Termination',
                lease_to = '17-SEP-2013',
                termination_lease_to = '17-SEP-2013'  
        WHERE   id= 345;
        
        UPDATE  ofm.lease 
        SET     completion = 'Termination',
                lease_to = '17-SEP-2013',
                termination_lease_to = '17-SEP-2013'  
        WHERE   id= 20066;
        
        
COMMIT;

BEGIN TRANSACTION;
        
        UPDATE  ofm.lease 
        SET     completion = NULL ,
                lease_to = '01-JUL-2013',
                termination_lease_to = NULL, 
                actual_move_out = '01-JUL-2013'  
        WHERE   id= 345;
        
        UPDATE  ofm.lease 
        SET     completion = NULL ,
                lease_to = '30-JUN-2013',
                termination_lease_to = NULL, 
                actual_move_out = '30-JUN-2013'  
        WHERE   id= 20066;
        
 COMMIT;
            
