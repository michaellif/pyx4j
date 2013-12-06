/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     hhproperties - delete buildings
***
***     ===========================================================================================================
**/         

BEGIN TRANSACTION;

        DELETE FROM hhproperties.aging_buckets;
        DELETE FROM hhproperties.billing_arrears_snapshot;
        DELETE FROM hhproperties.buildingcontacts$organization_contacts;
        DELETE FROM hhproperties.product_v$features;
        DELETE FROM hhproperties.product_item;
        DELETE FROM hhproperties.product_v;
        DELETE FROM hhproperties.product;
        DELETE FROM hhproperties.product_catalog;
        DELETE FROM hhproperties.building_merchant_account;
        DELETE FROM hhproperties.building;
        DELETE FROM hhproperties.complex;

COMMIT;
                                                    
