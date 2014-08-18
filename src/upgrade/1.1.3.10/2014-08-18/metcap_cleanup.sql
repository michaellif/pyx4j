/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Metcap cleanup
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

    -- building_merchant_account : delete all
    
    DELETE FROM metcap.building_merchant_account;
    
    
    -- payment_method
    
    UPDATE  metcap.payment_method AS pm 
    SET     is_deleted = TRUE
    FROM    metcap.customer c 
    JOIN    metcap.lease_participant lp ON (c.id = lp.customer)
    JOIN    metcap.lease l ON (l.id = lp.lease)
    JOIN    metcap.apt_unit a ON (a.id = l.unit)
    JOIN    metcap.building b ON (b.id = a.building)
    WHERE   pm.id_discriminator = 'LeasePaymentMethod' 
    AND     c.id = pm.customer 
    AND     b.property_code NOT IN ('rann0001','lake0245','silv1315',
    'east0350','east0340','darc7110');
    
-- COMMIT;
    
    
