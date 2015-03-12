/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 */
package com.propertyvista.crm.rpc;

import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.NotificationAppPlace;
import com.pyx4j.site.rpc.ReportsAppPlace;
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.PublicPlace;
import com.pyx4j.site.shared.meta.SiteMap;

import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;
import com.propertyvista.domain.reports.CustomerCreditCheckReportMetadata;
import com.propertyvista.domain.reports.EftReportMetadata;
import com.propertyvista.domain.reports.EftVarianceReportMetadata;
import com.propertyvista.domain.reports.ResidentInsuranceReportMetadata;
import com.propertyvista.dto.communication.MessageDTO;

public class CrmSiteMap implements SiteMap {

    public static class Login extends AppPlace implements PublicPlace {
        {
            setStable(false);
        }
    }

    /**
     * A place where a unfortunate user can perform request to reset his password.
     */
    public static class PasswordResetRequest extends AppPlace implements PublicPlace {
        {
            setStable(false);
        }
    }

    /**
     * Used while user is waiting for authentication required for password reset.
     */
    public static class LoginWithToken extends AppPlace implements PublicPlace {
        {
            setStable(false);
        }

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

    public static class Welcome extends AppPlace {
    }

    public static class Properties extends AppPlace {
        @PlaceProperties(navigLabel = "Complexes")
        public static class Complex extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Buildings")
        public static class Building extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Units")
        public static class Unit extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Arrears")
        public static class Arrear extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Budgets")
        public static class Budget extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Purchase Orders")
        public static class PurchaseOrder extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "City Orders")
        public static class CityOrder extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Elevators")
        public static class Elevator extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Boilers")
        public static class Boiler extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Roofs")
        public static class Roof extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Community Events")
        public static class CommunityEvent extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Parkings")
        public static class Parking extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Parking Spots")
        public static class ParkingSpot extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Locker Areas")
        public static class LockerArea extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Lockers")
        public static class Locker extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Unit Items")
        public static class UnitItem extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Unit Occupancies")
        public static class UnitOccupancy extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Floorplan")
        public static class Floorplan extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Services")
        public static class Service extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Feature")
        public static class Feature extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Concessions")
        public static class Concession extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Utilities")
        public static class Utility extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Landlords")
        public static class Landlord extends CrmCrudAppPlace {
        }
    }

    public static class Tenants extends AppPlace {

        @PlaceProperties(navigLabel = "Tenants")
        public static class Tenant extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Guarantors")
        public static class Guarantor extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Screenings")
        public static class Screening extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Tenants On Lease")
        public static class TenantInLease extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Leases")
        public static class Lease extends CrmCrudAppPlace {

            @PlaceProperties(navigLabel = "Credit Transaction")
            public static class InvoiceCredit extends CrmCrudAppPlace {

            }

            @PlaceProperties(navigLabel = "Debit Transaction")
            public static class InvoiceDebit extends CrmCrudAppPlace {

            }
        }

        @PlaceProperties(navigLabel = "Lease Applications")
        public static class LeaseApplication extends CrmCrudAppPlace {
        }

        public static class LeaseTerm extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Maintenance Requests")
        public static class MaintenanceRequest extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Former Tenants")
        public static class FormerTenant extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Former Leases")
        public static class FormerLease extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Former Guarantors")
        public static class FormerGuarantor extends CrmCrudAppPlace {
        }

        public static class CustomerCreditCheckLongReport extends CrmCrudAppPlace {
            public CustomerCreditCheckLongReport() {
                super(Type.viewer);
            }
        }
    }

    public static class Marketing extends AppPlace {

        @PlaceProperties(navigLabel = "Inquiries")
        public static class Inquiry extends CrmCrudAppPlace {
        }

        @I18nComment("Potential customer")
        @PlaceProperties(navigLabel = "Leads")
        public static class Lead extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Appointments")
        public static class Appointment extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Showings")
        public static class Showing extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Potential Tenants")
        public static class PotentialTenant extends CrmCrudAppPlace {
        }
    }

    public static class LegalAndCollections extends AppPlace {

        public static class EvictionCase extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "N4")
        public static class N4Batches extends CrmCrudAppPlace {
        }

        public static class N4BatchItem extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "L1 Create Batch")
        public static class L1GenerationTool extends CrmCrudAppPlace {

        }

    }

    public static class Finance extends AppPlace {

        @PlaceProperties(navigLabel = "Billing Cycles")
        public static class BillingCycle extends CrmCrudAppPlace {

            public static final String ARG_BC_ID = "BcId";

            public static final String ARG_BT_ID = "BtId";

            public static final String ARG_BILL_STATUS = "BillStatus";

            public static class Leases extends CrmCrudAppPlace {
            }

            public static class Bills extends CrmCrudAppPlace {

            }
        }

        @PlaceProperties(navigLabel = "Bills")
        public static class Bill extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Payments")
        public static class Payment extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Lease Adjustments")
        public static class LeaseAdjustment extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Lease Deposits")
        public static class LeaseDeposit extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Aggregated Transfers")
        public static class AggregatedTransfer extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Auto Pays")
        public static class AutoPay extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Auto Pays Review")
        public static class AutoPayReview extends AppPlace {
        }

        @PlaceProperties(navigLabel = "Money In", caption = "Money In - Quick Collect")
        public static class MoneyIn extends AppPlace {

            @PlaceProperties(navigLabel = "Bank Deposit Slip / Batch", caption = "Money In Batch")
            public static class Batch extends CrudAppPlace {

            }

        }
    }

    public static class Organization extends AppPlace {

        @PlaceProperties(navigLabel = "Employees")
        public static class Employee extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Portfolios")
        public static class Portfolio extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Vendors")
        public static class Vendor extends CrmCrudAppPlace {
        }
    }

    public static class Reports extends AppPlace {

        // please try to maintain lexicographic order

        public static class AutoPayChanges extends ReportsAppPlace<AutoPayChangesReportMetadata> {
            public AutoPayChanges() {
                super(AutoPayChangesReportMetadata.class);
            }
        }

        public static class AutoPayReconciliation extends CrmCrudAppPlace {
        }

        public static class Availability extends ReportsAppPlace<AvailabilityReportMetadata> {
            public Availability() {
                super(AvailabilityReportMetadata.class);
            }
        }

        public static class CustomerCreditCheck extends ReportsAppPlace<CustomerCreditCheckReportMetadata> {
            public CustomerCreditCheck() {
                super(CustomerCreditCheckReportMetadata.class);
            }
        }

        @PlaceProperties(navigLabel = "EFT", caption = "EFT")
        public static class Eft extends ReportsAppPlace<EftReportMetadata> {
            public Eft() {
                super(EftReportMetadata.class);
            }
        }

        @PlaceProperties(navigLabel = "EFT Variance", caption = "EFT Variance")
        public static class EftVariance extends ReportsAppPlace<EftVarianceReportMetadata> {
            public EftVariance() {
                super(EftVarianceReportMetadata.class);
            }
        }

        public static class ResidentInsurance extends ReportsAppPlace<ResidentInsuranceReportMetadata> {
            public ResidentInsurance() {
                super(ResidentInsuranceReportMetadata.class);
            }
        }

    }

    public static class Dashboard extends AppPlace {

        @PlaceProperties(navigLabel = "Manage Dashboards", caption = "Dashboard")
        public static class Manage extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Main Dashboard")
        public static class View extends AppPlace {
        }

    }

    @PlaceProperties(navigLabel = "User Account")
    public static class Account extends AppPlace {

        @PlaceProperties(navigLabel = "Profile")
        public static class UserProfile extends CrmCrudAppPlace {
            public UserProfile() {
                super(Type.viewer);
            }
        }

        @PlaceProperties(navigLabel = "Account")
        public static class AccountPreferences extends CrmCrudAppPlace {
            public AccountPreferences() {
                super(Type.viewer);
            }
        }

        public static class AccountRecoveryOptionsRequired extends AppPlace {
        }

        public static class LoginAttemptsLog extends CrmCrudAppPlace {
        }

    }

    public static class Administration extends AppPlace {

        public static class Profile extends AppPlace {

            @PlaceProperties(caption = "Company Info")
            public static class CompanyInfo extends CrmCrudAppPlace {
            }

            @PlaceProperties(caption = "Payment Methods")
            public static class PaymentMethods extends CrmCrudAppPlace {
            }
        }

        public static class Settings extends AppPlace {

            @PlaceProperties(caption = "Online Payment Setup")
            public static class OnlinePaymentSetup extends CrmCrudAppPlace {
            }

            @PlaceProperties(caption = "Message Category")
            public static class MessageCategory extends CrmCrudAppPlace {
            }

            /**
             * This place is for dispatching the Credit Check to correct activity
             */
            @PlaceProperties(caption = "Credit Check", navigLabel = "Credit Check")
            public static class CreditCheck extends AppPlace {

                @PlaceProperties(caption = "Credit Check Setup", navigLabel = "Credit Check Setup")
                public static class Setup extends CrmCrudAppPlace {
                }

                @PlaceProperties(caption = "Credit Check", navigLabel = "Credit Check Status")
                public static class Status extends CrmCrudAppPlace {

                }

            }

            @PlaceProperties(caption = "Internet Listing Service")
            public static class ILSConfig extends CrmCrudAppPlace {
            }
        }

        public static class Security extends AppPlace {

            public static class AuditRecords extends CrmCrudAppPlace {
            }

            @PlaceProperties(navigLabel = "User Roles")
            public static class UserRole extends CrmCrudAppPlace {
            }

            @PlaceProperties(navigLabel = "Assign Reports")
            public static class AssignReports extends CrmCrudAppPlace {
            }

            public static class TenantSecurity extends CrmCrudAppPlace {
            }
        }

        public static class Financial extends AppPlace {

            @PlaceProperties(navigLabel = "Merchant Accounts")
            public static class MerchantAccount extends CrmCrudAppPlace {
            }

            @PlaceProperties(navigLabel = "AR Codes")
            public static class ARCode extends CrmCrudAppPlace {
            }

            @PlaceProperties(navigLabel = "GL Codes", caption = "GL Code Category")
            public static class GlCodeCategory extends CrmCrudAppPlace {
            }

            @PlaceProperties(navigLabel = "Taxes")
            public static class Tax extends CrmCrudAppPlace {
            }

            @PlaceProperties(navigLabel = "Credit Checks")
            public static class CustomerCreditCheck extends CrmCrudAppPlace {
            }
        }

        public static class ContentManagement extends CrmCrudAppPlace {

            public static class General extends CrmCrudAppPlace {

            }

            public static class Website extends CrmCrudAppPlace {

                public static class HomePageGadgets extends CrmCrudAppPlace {
                }

                public static class Page extends CrmCrudAppPlace {
                }

                public static class CityIntroPage extends CrmCrudAppPlace {
                }
            }

            public static class Portal extends CrmCrudAppPlace {

            }
        }

        public static class Policies extends AppPlace {

            @PlaceProperties(caption = "Approval Checklist")
            public static class ApplicationApprovalChecklist extends CrmCrudAppPlace {
            }

            @PlaceProperties(caption = "Documentation")
            public static class ApplicationDocumentation extends CrmCrudAppPlace {
            }

            @PlaceProperties(caption = "AR Settings")
            public static class AR extends CrmCrudAppPlace {
            }

            public static class AutoPay extends CrmCrudAppPlace {
            }

            public static class BackgroundCheck extends CrmCrudAppPlace {
            }

            public static class Billing extends CrmCrudAppPlace {
            }

            public static class Dates extends CrmCrudAppPlace {
            }

            public static class Deposits extends CrmCrudAppPlace {
            }

            @PlaceProperties(navigLabel = "Email")
            public static class EmailTemplates extends CrmCrudAppPlace {
            }

//            @PlaceProperties(navigLabel = "Adjustment Taxes")
//            public static class AdjustmentTax extends CRMCrudAppPlace {
//            }

            @PlaceProperties(navigLabel = "ID Assignment")
            public static class IdAssignment extends CrmCrudAppPlace {
            }

            public static class LeaseAdjustment extends CrmCrudAppPlace {
            }

            public static class LeaseTermination extends CrmCrudAppPlace {

            }

            @PlaceProperties(caption = "Lease Agreement")
            public static class AgreementLegalTerms extends CrmCrudAppPlace {
            }

            @PlaceProperties(caption = "Lease Application")
            public static class LeaseApplicationTerms extends CrmCrudAppPlace {
            }

            @PlaceProperties(caption = "Terms and Conditions")
            public static class LegalDocumentation extends CrmCrudAppPlace {
            }

            @PlaceProperties(caption = "Financial Terms")
            public static class FinancialTerms extends CrmCrudAppPlace {
            }

            @PlaceProperties(caption = "Legal Questions")
            public static class LegalQuestions extends CrmCrudAppPlace {

            }

            public static class MaintenanceRequest extends CrmCrudAppPlace {
            }

            public static class EvictionFlow extends CrmCrudAppPlace {
            }

            @PlaceProperties(caption = "Ontario N4")
            public static class N4 extends CrmCrudAppPlace {
            }

            public static class PaymentTypeSelection extends CrmCrudAppPlace {
            }

            public static class Pet extends CrmCrudAppPlace {
            }

            public static class ProductTax extends CrmCrudAppPlace {
            }

            @PlaceProperties(caption = "Online Availability")
            public static class ProspectPortal extends CrmCrudAppPlace {
            }

            @PlaceProperties(caption = "Communication")
            public static class ResidentPortal extends CrmCrudAppPlace {
            }

            public static class Restrictions extends CrmCrudAppPlace {
            }

            @PlaceProperties(caption = "Renters Insurance")
            public static class TenantInsurance extends CrmCrudAppPlace {
            }

            public static class YardiInterface extends CrmCrudAppPlace {
            }
        }
    }

    @PlaceProperties(navigLabel = "Alerts")
    public static class Notifications extends AppPlace {
    }

    // Communication Started --------------
    @PlaceProperties(navigLabel = "Communication")
    public static class Communication extends AppPlace {
        @PlaceProperties(navigLabel = "Messages")
        public static class Message extends CrmCrudAppPlace {
            private final Object criteria;

            private MessageDTO forwardedMessage;

            public Message() {
                criteria = null;
            }

            public Message(Object criteria) {
                this.criteria = criteria;
            }

            public Object getCriteria() {
                return criteria;
            }

            public void setForwardedMessage(MessageDTO forwardedMessage) {
                this.forwardedMessage = forwardedMessage;
            }

            public MessageDTO getForwardedMessage() {
                return forwardedMessage;
            }
        }

        @PlaceProperties(navigLabel = "Templates")
        public static class BroadcastTemplate extends CrmCrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Events")
        public static class BroadcastEvent extends CrmCrudAppPlace {
        }
    }

    // Communication Ended --------------

    @PlaceProperties(navigLabel = "Runtime Errors")
    public static class RuntimeError extends NotificationAppPlace {
    }
}
