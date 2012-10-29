/**
***     ======================================================================
***
***             Fix for yet another orphaned record in waterfront schema
***
***     ======================================================================
**/


BEGIN TRANSACTION;

        DELETE FROM waterfront.product_item a 
        WHERE productdiscriminator = 'service' 
        AND NOT EXISTS (SELECT id FROM waterfront.service_v WHERE id = a.product);



