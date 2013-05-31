/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Fix default product catalog for newly created pmcs
***
***     ======================================================================================================================
**/



BEGIN TRANSACTION;

        UPDATE  _admin_.admin_pmc_vista_features f
        SET     default_product_catalog = TRUE
        FROM    _admin_.admin_pmc a 
        WHERE   a.features = f.id 
        AND     a.created > '29-APR-2013'
        AND     COALESCE(f.default_product_catalog,FALSE) = FALSE;
        
COMMIT;

