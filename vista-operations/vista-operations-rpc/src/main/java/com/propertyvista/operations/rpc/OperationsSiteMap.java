/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.rpc;

import com.pyx4j.commons.Key;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.PublicPlace;
import com.pyx4j.site.shared.meta.SiteMap;

import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.operations.domain.legal.VistaTerms.Target;

public class OperationsSiteMap implements SiteMap {

    public interface DevelopmentOnlyPlace {

    }

    public static class Login extends AppPlace implements PublicPlace {
        {
            setStable(false);
        }
    }

    /**
     * Used while user is waiting for authentication required for password reset.
     */
    public static class LoginWithToken extends AppPlace implements PublicPlace {

    }

    public static class PasswordResetRequest extends AppPlace implements PublicPlace {

    }

    @PlaceProperties(caption = "Reset Password")
    public static class PasswordReset extends AppPlace {
        {
            setStable(false);
        }
    }

    @PlaceProperties(caption = "Change Password")
    public static class PasswordChange extends AppPlace {
    }

    public static class Management extends AppPlace {

        @PlaceProperties(navigLabel = "PMCs")
        public static class PMC extends OperationsCrudAppPlace {
        }

        public static class EquifaxApproval extends OperationsCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "PMC Merchant Accounts")
        public static class PmcMerchantAccount extends OperationsCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Triggers")
        public static class Trigger extends OperationsCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Trigger Runs")
        public static class TriggerRun extends OperationsCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Trigger Runs Data")
        public static class TriggerRunData extends OperationsCrudAppPlace {
        }

        public static class BillingSetup extends OperationsCrudAppPlace {
            // set default place type as Viewer one (we have no lister for this item!)
            public BillingSetup() {
                formViewerPlace(new Key(-1));
            }
        }

        @PlaceProperties(navigLabel = "Credit Check Transactions")
        public static class CreditCheckTransaction extends OperationsCrudAppPlace {
        }
    }

    public static class FundsTransfer extends AppPlace {

        @PlaceProperties(navigLabel = "Card Transaction Records")
        public static class CardTransactionRecord extends OperationsCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Direct Debit Records")
        public static class DirectDebitRecord extends OperationsCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Funds Transfer Files")
        public static class FundsTransferFile extends OperationsCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Funds Transfer Records")
        public static class FundsTransferRecord extends OperationsCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Funds Transfer Batches")
        public static class FundsTransferBatch extends OperationsCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Funds Reconciliation Files")
        public static class FundsReconciliationFile extends OperationsCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Funds Reconciliation Summaries")
        public static class FundsReconciliationSummary extends OperationsCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Funds Reconciliation Records")
        public static class FundsReconciliationRecord extends OperationsCrudAppPlace {
        }
    }

    public static class Security extends AppPlace {

        @PlaceProperties(navigLabel = "Audit Records")
        public static class AuditRecord extends OperationsCrudAppPlace {

        }
    }

    public static class Legal extends AppPlace {

        public static abstract class VistaTermsAccess extends OperationsCrudAppPlace {
            private final Target target;

            public VistaTermsAccess(Target target) {
                this.target = target;
            }

            public VistaTerms.Target getTarget() {
                return target;
            }
        }

        @PlaceProperties(caption = "T&C PMC Vista Service")
        public static class PmcTerms extends VistaTermsAccess {
            public PmcTerms() {
                super(Target.PmcPropertyVistaService);
            }
        }

        @PlaceProperties(caption = "T&C PMC PaymentPad")
        public static class PmcPaymentPadTerms extends VistaTermsAccess {
            public PmcPaymentPadTerms() {
                super(Target.PmcPaymentPad);
            }
        }

        @PlaceProperties(caption = "T&C PMC Caledon")
        public static class PmcCaledonTermsTemplate extends VistaTermsAccess {
            public PmcCaledonTermsTemplate() {
                super(Target.PmcCaledonTemplate);
            }
        }

        @PlaceProperties(caption = "T&C PMC Caledon Sole Proprietorship Section")
        public static class PmcCaledonSoleProprietorshipSection extends VistaTermsAccess {
            public PmcCaledonSoleProprietorshipSection() {
                super(Target.PmcCaledonSoleProprietorshipSection);
            }
        }

        @PlaceProperties(caption = "Resident Portal Terms And Conditions")
        public static class ResidentPortalTermsAndConditions extends VistaTermsAccess {
            public ResidentPortalTermsAndConditions() {
                super(Target.ResidentPortalTermsAndConditions);
            }
        }

        @PlaceProperties(caption = "Resident Portal Privacy Policy")
        public static class ResidentPortalPrivacyPolicy extends VistaTermsAccess {
            public ResidentPortalPrivacyPolicy() {
                super(Target.ResidentPortalPrivacyPolicy);
            }
        }

        @PlaceProperties(caption = "Prospect Portal Terms And Conditions")
        public static class ProspectPortalTermsAndConditions extends VistaTermsAccess {
            public ProspectPortalTermsAndConditions() {
                super(Target.ProspectPortalTermsAndConditions);
            }
        }

        @PlaceProperties(caption = "Prospect Portal Privacy Policy")
        public static class ProspectPortalPrivacyPolicy extends VistaTermsAccess {
            public ProspectPortalPrivacyPolicy() {
                super(Target.ProspectPortalPrivacyPolicy);
            }
        }

        @PlaceProperties(caption = "Tenant Billing Terms")
        public static class TenantBillingTerms extends VistaTermsAccess {
            public TenantBillingTerms() {
                super(Target.TenantBillingTerms);
            }
        }

        @PlaceProperties(caption = "Tenant eCheck Pre-Authorized Payment Terms")
        public static class TenantPreAuthorizedPaymentECheck extends VistaTermsAccess {
            public TenantPreAuthorizedPaymentECheck() {
                super(Target.TenantPreAuthorizedPaymentECheckTerms);
            }
        }

        @PlaceProperties(caption = "Tenant Card Pre-Authorized Payment Terms")
        public static class TenantPreAuthorizedPaymentCreditCard extends VistaTermsAccess {
            public TenantPreAuthorizedPaymentCreditCard() {
                super(Target.TenantPreAuthorizedPaymentCardTerms);
            }
        }

        @PlaceProperties(caption = "Tenant Convenience Fee Terms")
        public static class TenantCaledonConvenienceFee extends VistaTermsAccess {
            public TenantCaledonConvenienceFee() {
                super(Target.TenantPaymentWebPaymentFeeTerms);
            }
        }

        @PlaceProperties(caption = "TenantSure PreAuthorized Agreement")
        public static class TenantSurePapAgreement extends VistaTermsAccess {
            public TenantSurePapAgreement() {
                super(Target.TenantSurePreAuthorizedPaymentsAgreement);
            }
        }
    }

    public static class Administration extends AppPlace {

        @PlaceProperties(navigLabel = "System Maintenance", caption = "System Maintenance")
        public static class Maintenance extends OperationsCrudAppPlace {
            // set default place type as Viewer one (we have no lister for this item!)
            public Maintenance() {
                formViewerPlace(new Key(-1));
            }
        }

        @PlaceProperties(navigLabel = "Simulations")
        public static class Simulation extends OperationsCrudAppPlace {

            public Simulation() {
                formViewerPlace(new Key(-1));
            }
        }

        @PlaceProperties(navigLabel = "Users", caption = "User")
        public static class AdminUsers extends OperationsCrudAppPlace {
        }

        public static class EncryptedStorage extends OperationsCrudAppPlace {
        }

        public static class OperationsAlert extends OperationsCrudAppPlace {
        }

        public static class TenantSure extends OperationsCrudAppPlace {
        }

        public static class OutgoingMail extends OperationsCrudAppPlace {
        }
    }

    public static class Simulator extends AppPlace {

        public static class CardServiceSimulation extends OperationsCrudAppPlace implements DevelopmentOnlyPlace {

            @PlaceProperties(caption = "Simulated Card Service Configuration", navigLabel = "Cards Service Config")
            public static class CardServiceSimulatorConfiguration extends OperationsCrudAppPlace implements DevelopmentOnlyPlace {

            }

            @PlaceProperties(caption = "Simulated Card", navigLabel = "Cards")
            public static class CardServiceSimulationCard extends OperationsCrudAppPlace implements DevelopmentOnlyPlace {

            }

            @PlaceProperties(caption = "Simulated Card Transaction", navigLabel = "Card Transactions")
            public static class CardServiceSimulationTransaction extends OperationsCrudAppPlace implements DevelopmentOnlyPlace {

            }

            @PlaceProperties(caption = "Simulated Card Merchant", navigLabel = "Card Merchants")
            public static class CardServiceSimulationMerchantAccount extends OperationsCrudAppPlace implements DevelopmentOnlyPlace {

            }

            @PlaceProperties(caption = "Simulated Cards Reconciliation", navigLabel = "Cards Reconciliations")
            public static class CardServiceSimulationReconciliation extends OperationsCrudAppPlace implements DevelopmentOnlyPlace {

            }
        }

        public static class PadSimulation extends CrudAppPlace {

            @PlaceProperties(caption = "Simulated Funds Transfer", navigLabel = "Funds Transfer")
            public static class PadSimFile extends OperationsCrudAppPlace implements DevelopmentOnlyPlace {
            }

            @PlaceProperties(caption = "PAD Batch")
            public static class PadSimBatch extends OperationsCrudAppPlace implements DevelopmentOnlyPlace {
            }

        }

        public static class SimulatedDataPreload extends OperationsCrudAppPlace implements DevelopmentOnlyPlace {

            public SimulatedDataPreload() {
                formViewerPlace(new Key(-1));
            }

        }

        @PlaceProperties(caption = "Simulated Direct Banking Record", navigLabel = "Direct Banking Record")
        public static class DirectBankingSimRecord extends OperationsCrudAppPlace implements DevelopmentOnlyPlace {

        }

        @PlaceProperties(caption = "Simulated Direct Banking File", navigLabel = "Direct Banking File")
        public static class DirectBankingSimFile extends OperationsCrudAppPlace implements DevelopmentOnlyPlace {

        }

    }

    @PlaceProperties(navigLabel = "Accounts")
    public static class Account extends OperationsCrudAppPlace {
    }
}
