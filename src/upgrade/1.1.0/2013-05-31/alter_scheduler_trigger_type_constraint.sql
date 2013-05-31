/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Alter scheduler_trigger_trigger_type constraint on _admin_.scheduler_trigger_table
***
***     ======================================================================================================================
**/


BEGIN TRANSACTION;

        ALTER TABLE _admin_.scheduler_trigger DROP CONSTRAINT scheduler_trigger_trigger_type_e_ck;

        ALTER TABLE _admin_.scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
        CHECK (trigger_type IN ('billing','cleanup','depositInterestAdjustment','depositRefund','equifaxRetention','initializeFutureBillingCycles',
        'leaseActivation','leaseCompletion','leaseRenewal','paymentsBmoReceive','paymentsIssue','paymentsPadProcesAcknowledgment',
        'paymentsPadProcesReconciliation','paymentsPadReceiveAcknowledgment','paymentsPadReceiveReconciliation','paymentsPadSend',
        'paymentsScheduledCreditCards','paymentsScheduledEcheck','paymentsTenantSure','paymentsUpdate','tenantSureCancellation',
        'tenantSureHQUpdate','tenantSureReports','tenantSureTransactionReports','test','updateArrears','updatePaymentsSummary',
        'vistaBusinessReport','yardiARDateVerification','yardiImportProcess'));
        
COMMIT;
