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
 * @version $Id$
 */
package com.propertyvista.crm.rpc;

import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.annotations.NavigationItem;
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.PublicPlace;
import com.pyx4j.site.shared.meta.SigningOutPlace;
import com.pyx4j.site.shared.meta.SiteMap;

public class CrmSiteMap implements SiteMap {

    @PlaceProperties(caption = "Login")
    public static class Login extends AppPlace implements PublicPlace {
    }

    public static class SigningOut extends AppPlace implements SigningOutPlace {
    }

    /**
     * A place where a unfortunate user can perform request to reset his password.
     */
    public static class PasswordResetRequest extends AppPlace implements PublicPlace {
    }

    /**
     * Used while user is waiting for authentication required for password reset.
     */
    public static class LoginWithToken extends AppPlace implements PublicPlace {

    }

    @PlaceProperties(caption = "Reset Password")
    public static class PasswordReset extends AppPlace {
    }

    @PlaceProperties(caption = "Change Password")
    public static class PasswordChange extends AppPlace {
    }

    public static class Properties extends AppPlace {
        @PlaceProperties(caption = "Complex")
        @NavigationItem(navigLabel = "Complexes")
        public static class Complex extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Building")
        @NavigationItem(navigLabel = "Buildings")
        public static class Building extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Unit")
        @NavigationItem(navigLabel = "Units")
        public static class Unit extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Arrear")
        @NavigationItem(navigLabel = "Arrears")
        public static class Arrear extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Budget")
        @NavigationItem(navigLabel = "Budgets")
        public static class Budget extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Purchase Order")
        @NavigationItem(navigLabel = "Purchase Orders")
        public static class PurchaseOrder extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "City Order")
        @NavigationItem(navigLabel = "City Orders")
        public static class CityOrder extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Elevator")
        @NavigationItem(navigLabel = "Elevators")
        public static class Elevator extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Boiler")
        @NavigationItem(navigLabel = "Boilers")
        public static class Boiler extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Roof")
        @NavigationItem(navigLabel = "Roofs")
        public static class Roof extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Parking")
        @NavigationItem(navigLabel = "Parkings")
        public static class Parking extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Parking Spot")
        @NavigationItem(navigLabel = "Parking Spots")
        public static class ParkingSpot extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Locker Area")
        @NavigationItem(navigLabel = "Locker Areas")
        public static class LockerArea extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Locker")
        @NavigationItem(navigLabel = "Lockers")
        public static class Locker extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Unit Item")
        @NavigationItem(navigLabel = "Unit Items")
        public static class UnitItem extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Unit Occupancy")
        @NavigationItem(navigLabel = "Unit Occupancies")
        public static class UnitOccupancy extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Floorplan")
        @NavigationItem(navigLabel = "Floorplan")
        public static class Floorplan extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Service")
        @NavigationItem(navigLabel = "Services")
        public static class Service extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Feature")
        @NavigationItem(navigLabel = "Feature")
        public static class Feature extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Concession")
        @NavigationItem(navigLabel = "Concessions")
        public static class Concession extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Utility")
        @NavigationItem(navigLabel = "Utilities")
        public static class Utility extends CrmCrudAppPlace {
        }
    }

    public static class Tenants extends AppPlace {

        @PlaceProperties(caption = "Tenant")
        @NavigationItem(navigLabel = "Tenants")
        public static class Tenant extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Guarantor")
        @NavigationItem(navigLabel = "Guarantors")
        public static class Guarantor extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Screening")
        @NavigationItem(navigLabel = "Screenings")
        public static class Screening extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Tenant On Lease")
        @NavigationItem(navigLabel = "Tenants On Lease")
        public static class TenantInLease extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Lease")
        @NavigationItem(navigLabel = "Leases")
        public static class Lease extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Lease2")
        @NavigationItem(navigLabel = "Leases 2")
        public static class Lease2 extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Lease Application")
        @NavigationItem(navigLabel = "Lease Applications")
        public static class LeaseApplication extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Lease Term")
        public static class LeaseTerm extends CrmCrudAppPlace {
        }

        public static class EquifaxResult extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Maintenance Request")
        @NavigationItem(navigLabel = "Maintenance Requests")
        public static class MaintenanceRequest extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Former Tenant")
        @NavigationItem(navigLabel = "Former Tenants")
        public static class PastTenant extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Former Lease")
        @NavigationItem(navigLabel = "Former Leases")
        public static class PastLease extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Former Guarantor")
        @NavigationItem(navigLabel = "Former Guarantors")
        public static class PastGuarantor extends CrmCrudAppPlace {
        }

    }

    public static class Marketing extends AppPlace {

        @PlaceProperties(caption = "Inquiry")
        @NavigationItem(navigLabel = "Inquiry")
        public static class Inquiry extends CrmCrudAppPlace {

        }

        @I18nComment("Potential customer")
        @PlaceProperties(caption = "Lead")
        @NavigationItem(navigLabel = "Leads")
        public static class Lead extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Appointment")
        @NavigationItem(navigLabel = "Appointments")
        public static class Appointment extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Showing")
        @NavigationItem(navigLabel = "Showings")
        public static class Showing extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Potential Tenant")
        @NavigationItem(navigLabel = "Potential Tenants")
        public static class FutureTenant extends CrmCrudAppPlace {
        }
    }

    public static class LegalAndCollections extends AppPlace {
    }

    public static class Finance extends AppPlace {

        @PlaceProperties(caption = "BillingCycle")
        @NavigationItem(navigLabel = "BillingCycles")
        public static class BillingCycle extends CrmCrudAppPlace {

            public static final String ARG_BC_ID = "BcId";

            public static final String ARG_BT_ID = "BtId";

            public static final String ARG_BILL_STATUS = "BillStatus";

            public static class Leases extends CrmCrudAppPlace {
            }

            public static class Bills extends CrmCrudAppPlace {

            }
        }

        @PlaceProperties(caption = "Bill")
        @NavigationItem(navigLabel = "Bills")
        public static class Bill extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Payment")
        @NavigationItem(navigLabel = "Payments")
        public static class Payment extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Lease Adjustment")
        @NavigationItem(navigLabel = "Lease Adjustments")
        public static class LeaseAdjustment extends CrmCrudAppPlace {

        }

        @PlaceProperties(caption = "Lease Deposit")
        @NavigationItem(navigLabel = "Lease Deposits")
        public static class LeaseDeposit extends CrmCrudAppPlace {

        }

        @PlaceProperties(caption = "Aggregated Transfer")
        @NavigationItem(navigLabel = "Aggregated Transfers")
        public static class AggregatedTransfer extends CrmCrudAppPlace {
        }
    }

    public static class Organization extends AppPlace {

        @PlaceProperties(caption = "Employee")
        @NavigationItem(navigLabel = "Employees")
        public static class Employee extends CrmCrudAppPlace {

        }

        @PlaceProperties(caption = "Portfolio")
        @NavigationItem(navigLabel = "Portfolios")
        public static class Portfolio extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Vendor")
        @NavigationItem(navigLabel = "Vendors")
        public static class Vendor extends CrmCrudAppPlace {
        }
    }

    @Deprecated
    @PlaceProperties(caption = "Report")
    public static class Report extends AppPlace {

        @PlaceProperties(caption = "Reports")
        @NavigationItem(navigLabel = "Manage Reports")
        public static class Management extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Edit")
        @NavigationItem(navigLabel = "Edit")
        public static class Edit extends CrmCrudAppPlace {
        }
    }

    public static class Reports extends AppPlace {

    }

    @PlaceProperties(caption = "Dashboard")
    public static class Dashboard extends AppPlace {

        @PlaceProperties(caption = "Dashboards")
        @NavigationItem(navigLabel = "Manage Dashboards")
        public static class Management extends CrmCrudAppPlace {

        }

        @PlaceProperties(caption = "Edit")
        @NavigationItem(navigLabel = "Edit")
        public static class Edit extends CrmCrudAppPlace {
        }
    }

    @NavigationItem(navigLabel = "User Account")
    public static class Account extends CrmCrudAppPlace {

        public static class AccountRecoveryOptionsRequired extends CrmCrudAppPlace {
        }

        public static class AccountRecoveryOptions extends CrmCrudAppPlace {
        }

        public static class LoginAttemptsLog extends CrmCrudAppPlace {
        }

    }

    @NavigationItem(navigLabel = "Administration")
    public static class Settings extends AppPlace {

        @NavigationItem(navigLabel = "Policies")
        public static class Policies extends AppPlace {

            @PlaceProperties(caption = "Dates")
            @NavigationItem(navigLabel = "Dates")
            public static class Dates extends CrmCrudAppPlace {

            }

            @PlaceProperties(caption = "Restrictions")
            @NavigationItem(navigLabel = "Restrictions")
            public static class Restrictions extends CrmCrudAppPlace {

            }

            @PlaceProperties(caption = "Pet Policy")
            @NavigationItem(navigLabel = "Pet Policy")
            public static class PetPolicy extends CrmCrudAppPlace {

            }

            @PlaceProperties(caption = "Application Documentation")
            @NavigationItem(navigLabel = "Application Documentation")
            public static class ApplicationDocumentation extends CrmCrudAppPlace {

            }

            @PlaceProperties(caption = "Legal Documentation - Terms and Conditions")
            @NavigationItem(navigLabel = "Legal Documentation")
            public static class LegalDocumentation extends CrmCrudAppPlace {

            }

            @PlaceProperties(caption = "Email Templates")
            @NavigationItem(navigLabel = "Email Templates")
            public static class EmailTemplates extends CrmCrudAppPlace {

            }

            @PlaceProperties(caption = "Product Taxes")
            @NavigationItem(navigLabel = "Product Taxes")
            public static class ProductTax extends CrmCrudAppPlace {

            }

//            @PlaceProperties(caption = "Adjustment Taxes")
//            @NavigationItem(navigLabel = "Adjustment Taxes")
//            public static class AdjustmentTax extends CRMCrudAppPlace {
//
//            }

            @PlaceProperties(caption = "Lease Adjustment")
            @NavigationItem(navigLabel = "Lease Adjustment")
            public static class LeaseAdjustmentPolicy extends CrmCrudAppPlace {

            }

            @PlaceProperties(caption = "Deposits")
            @NavigationItem(navigLabel = "Deposits")
            public static class Deposits extends CrmCrudAppPlace {

            }

            @PlaceProperties(caption = "Background Check")
            @NavigationItem(navigLabel = "Background Check")
            public static class BackgroundCheck extends CrmCrudAppPlace {

            }

            @PlaceProperties(caption = "Lease Billing")
            @NavigationItem(navigLabel = "Lease Billing")
            public static class LeaseBilling extends CrmCrudAppPlace {

            }

            @PlaceProperties(caption = "ID Assignment")
            @NavigationItem(navigLabel = "ID Assignment")
            public static class IdAssignment extends CrmCrudAppPlace {

            }

            @PlaceProperties(caption = "Aging Priority")
            @NavigationItem(navigLabel = "Aging Priority")
            public static class AR extends CrmCrudAppPlace {

            }
        }

        @PlaceProperties(caption = "User Role")
        @NavigationItem(navigLabel = "User Roles")
        public static class UserRole extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Merchant Account")
        @NavigationItem(navigLabel = "Merchant Accounts")
        public static class MerchantAccount extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Product Dictionary")
        @NavigationItem(navigLabel = "Product Dictionary")
        public static class ProductDictionary extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Service Item Type")
        @NavigationItem(navigLabel = "Service Item Type")
        public static class ServiceItemType extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Feature Item Type")
        @NavigationItem(navigLabel = "Feature Item Type")
        public static class FeatureItemType extends CrmCrudAppPlace {

        }

        @PlaceProperties(caption = "Content")
        @NavigationItem(navigLabel = "Content")
        public static class Content extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Page")
        @NavigationItem(navigLabel = "Page")
        public static class Page extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Home Page Gadgets")
        @NavigationItem(navigLabel = "Home Page Gadgets")
        public static class HomePageGadgets extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Tax")
        @NavigationItem(navigLabel = "Taxes")
        public static class Tax extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "GL Code Category")
        @NavigationItem(navigLabel = "GL Codes")
        public static class GlCodeCategory extends CrmCrudAppPlace {
        }

        @PlaceProperties(caption = "Lease Adjustment Reason")
        @NavigationItem(navigLabel = "Lease Adjustment Reason")
        public static class LeaseAdjustmentReason extends CrmCrudAppPlace {
        }
    }

    @NavigationItem(navigLabel = "Alerts")
    public static class Alert extends AppPlace {
    }

    @NavigationItem(navigLabel = "Messages")
    public static class Message extends AppPlace {
    }

    @NavigationItem(navigLabel = "Runtime Errors")
    public static class RuntimeError extends AppPlace {
    }
}
