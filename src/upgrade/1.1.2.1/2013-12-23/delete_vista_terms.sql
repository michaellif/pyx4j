/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             delete TenantPaymentCreditCard and TenantPaymentPad from vista_terms
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;

        DELETE FROM _admin_.vista_terms_v$document WHERE id IN (12,13);
        DELETE FROM _admin_.legal_document WHERE id IN (12,13);
        DELETE FROM _admin_.vista_terms_v WHERE holder IN (5,6);
        DELETE FROM _admin_.vista_terms WHERE id IN (5,6);
        
COMMIT;
