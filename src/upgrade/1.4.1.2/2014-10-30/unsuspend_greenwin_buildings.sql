/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             unsuspend greenwin buildings suspended on Oct. 30, 2014
***
***     =====================================================================================================================
**/



BEGIN TRANSACTION;

    UPDATE  greenwin.building AS b
    SET     suspended = FALSE 
    FROM    _admin_.audit_record a 
    WHERE   a.namespace = 'greenwin' 
    AND     a.event = 'Update' 
    AND     DATE_TRUNC('day', a.created) = '30-OCT-2014'
    AND     a.entity_class = 'Building'
    AND     a.details = 'Suspended'
    AND     a.entity_id = b.id 
    ORDER BY created DESC;
    
COMMIT;
