/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Remove duplicate greenwin lease
***
***     =====================================================================================================================
**/


BEGIN TRANSACTION;

    -- kill lease 108189 and everything associated with it
    
    -- lease_term_participant
    
    DELETE FROM greenwin.lease_term_participant
    WHERE   lease_participant IN    (SELECT id
                                    FROM    greenwin.lease_participant 
                                    WHERE   lease = 108189);
    
    -- lease_participant 
    
    DELETE FROM    greenwin.lease_participant 
    WHERE   lease = 108189;
    

    -- customer 
    
    DELETE FROM greenwin.customer
    WHERE   id = 118204;
    
    
    -- lease_term_v 
    
    DELETE FROM greenwin.lease_term_v 
    WHERE holder IN (SELECT  id 
                    FROM greenwin.lease_term
                    WHERE   lease = 108189);


    -- lease_term$agreement_confirmation_terms
    
    DELETE FROM greenwin.lease_term$agreement_confirmation_terms
    WHERE   owner IN (SELECT  id 
                    FROM greenwin.lease_term
                    WHERE   lease = 108189);
    
    -- lease_term$agreement_legal_terms
    
    DELETE FROM greenwin.lease_term$agreement_legal_terms
    WHERE   owner IN (SELECT  id 
                    FROM greenwin.lease_term
                    WHERE   lease = 108189);
    
    -- lease_term
    
    DELETE FROM greenwin.lease_term
    WHERE   lease = 108189;

    -- lease
    
    DELETE FROM greenwin.lease
    WHERE   id = 108189;
    
COMMIT;
