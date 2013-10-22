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
    }

    @PlaceProperties(caption = "Change Password")
    public static class PasswordChange extends AppPlace {
    }

    public static class Management extends AppPlace {

        @PlaceProperties(navigLabel = "PMCs")
        public static class PMC extends CrudAppPlace {
        }

        public static class EquifaxApproval extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "PMC Merchant Accounts")
        public static class PmcMerchantAccount extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Triggers")
        public static class Trigger extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Trigger Runs")
        public static class TriggerRun extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Trigger Runs Data")
        public static class TriggerRunData extends CrudAppPlace {
        }

        public static class BillingSetup extends CrudAppPlace {
            // set default place type as Viewer one (we have no lister for this item!)
            public BillingSetup() {
                formViewerPlace(new Key(-1));
            }
        }
    }

    public static class FundsTransfer extends AppPlace {

        @PlaceProperties(navigLabel = "Direct Debit Records")
        public static class DirectDebitRecord extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Funds Transfer Files")
        public static class FundsTransferFile extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Funds Transfer Records")
        public static class FundsTransferRecord extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Funds Transfer Batches")
        public static class FundsTransferBatch extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Funds Reconciliation Files")
        public static class FundsReconciliationFile extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Funds Reconciliation Records")
        public static class FundsReconciliationRecord extends CrudAppPlace {
        }
    }

    public static class Security extends AppPlace {

        @PlaceProperties(navigLabel = "Audit Records")
        public static class AuditRecord extends CrudAppPlace {

        }
    }

    public static class Legal extends AppPlace {

        public static abstract class VistaTermsAccess extends CrudAppPlace {
            private final Target target;

            public VistaTermsAccess(Target target) {
                this.target = target;
            }

            public VistaTerms.Target getTarget() {
                return target;
            }
        }

        @PlaceProperties(caption = "PMC Terms and Conditions")
        public static class PmcTerms extends VistaTermsAccess {
            public PmcTerms() {
                super(Target.PMC);
            }
        }

        @PlaceProperties(caption = "PMC PAD Terms")
        public static class PmcPadTerms extends VistaTermsAccess {
            public PmcPadTerms() {
                super(Target.PmcPaymentPad);
            }
        }

        @PlaceProperties(caption = "Caledon Terms and Conditions")
        public static class PmcCaledonTermsTemplate extends VistaTermsAccess {
            public PmcCaledonTermsTemplate() {
                super(Target.PmcCaledonTemplate);
            }
        }

        @PlaceProperties(caption = "Caledon Sole Propetorship Section Terms")
        public static class PmcCaledonSoleProprietorshipSection extends VistaTermsAccess {
            public PmcCaledonSoleProprietorshipSection() {
                super(Target.PmcCaledonSoleProprietorshipSection);
            }
        }

        @PlaceProperties(caption = "Tenant Terms and Conditions")
        public static class TenantTerms extends VistaTermsAccess {
            public TenantTerms() {
                super(Target.Tenant);
            }
        }

        public static class TenantBillingTerms extends VistaTermsAccess {
            public TenantBillingTerms() {
                super(Target.TenantBilling);
            }
        }

        @PlaceProperties(caption = "Tenant PAD Terms")
        public static class TenantPAD extends VistaTermsAccess {
            public TenantPAD() {
                super(Target.TenantPaymentPad);
            }
        }

        @PlaceProperties(caption = "Tenant Credit Card Terms")
        public static class TenantCC extends VistaTermsAccess {
            public TenantCC() {
                super(Target.TenantPaymentCreditCard);
            }
        }

        @PlaceProperties(caption = "TenantSure PAP Terms")
        public static class TenantSurePapAgreement extends VistaTermsAccess {
            public TenantSurePapAgreement() {
                super(Target.TenantSurePreAuthorizedPaymentsAgreement);
            }
        }
    }

    public static class Administration extends AppPlace {

        @PlaceProperties(navigLabel = "System Maintenance", caption = "System Maintenance")
        public static class Maintenance extends CrudAppPlace {
            // set default place type as Viewer one (we have no lister for this item!)
            public Maintenance() {
                formViewerPlace(new Key(-1));
            }
        }

        @PlaceProperties(navigLabel = "Simulations")
        public static class Simulation extends CrudAppPlace {

            public Simulation() {
                formViewerPlace(new Key(-1));
            }
        }

        @PlaceProperties(navigLabel = "Users", caption = "User")
        public static class AdminUsers extends CrudAppPlace {
        }

        public static class EncryptedStorage extends AppPlace {
        }

    }

    public static class Simulator extends AppPlace {

        public static class CardServiceSimulation extends CrudAppPlace implements DevelopmentOnlyPlace {

            @PlaceProperties(caption = "Simulated Card", navigLabel = "Cards")
            public static class CardServiceSimulationCard extends CrudAppPlace implements DevelopmentOnlyPlace {

            }

            @PlaceProperties(caption = "Simulated Card Transaction", navigLabel = "Card Transactions")
            public static class CardServiceSimulationTransaction extends CrudAppPlace implements DevelopmentOnlyPlace {

            }

            @PlaceProperties(caption = "Simulated Card Merchant", navigLabel = "Card Merchants")
            public static class CardServiceSimulationMerchantAccount extends CrudAppPlace implements DevelopmentOnlyPlace {

            }
        }

        public static class PadSimulation extends CrudAppPlace {

            @PlaceProperties(caption = "Simulated Funds Transfer", navigLabel = "Funds Transfer")
            public static class PadSimFile extends CrudAppPlace implements DevelopmentOnlyPlace {
            }

            @PlaceProperties(caption = "PAD Batch")
            public static class PadSimBatch extends CrudAppPlace implements DevelopmentOnlyPlace {
            }

        }

        public static class SimulatedDataPreload extends CrudAppPlace implements DevelopmentOnlyPlace {

            public SimulatedDataPreload() {
                formViewerPlace(new Key(-1));
            }

        }

        @PlaceProperties(caption = "Simulated Direct Banking Record", navigLabel = "Direct Banking Record")
        public static class DirectBankingSimRecord extends CrudAppPlace implements DevelopmentOnlyPlace {

        }

        @PlaceProperties(caption = "Simulated Direct Banking File", navigLabel = "Direct Banking File")
        public static class DirectBankingSimFile extends CrudAppPlace implements DevelopmentOnlyPlace {

        }

    }

    @PlaceProperties(navigLabel = "Accounts")
    public static class Account extends CrudAppPlace {
    }
}
