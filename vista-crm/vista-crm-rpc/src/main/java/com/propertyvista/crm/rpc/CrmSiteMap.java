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
        public static class Complex extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Building")
        @NavigationItem(navigLabel = "Buildings")
        public static class Building extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Unit")
        @NavigationItem(navigLabel = "Units")
        public static class Unit extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Arrear")
        @NavigationItem(navigLabel = "Arrears")
        public static class Arrear extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Budget")
        @NavigationItem(navigLabel = "Budgets")
        public static class Budget extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Purchase Order")
        @NavigationItem(navigLabel = "Purchase Orders")
        public static class PurchaseOrder extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "City Order")
        @NavigationItem(navigLabel = "City Orders")
        public static class CityOrder extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Elevator")
        @NavigationItem(navigLabel = "Elevators")
        public static class Elevator extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Boiler")
        @NavigationItem(navigLabel = "Boilers")
        public static class Boiler extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Roof")
        @NavigationItem(navigLabel = "Roofs")
        public static class Roof extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Parking")
        @NavigationItem(navigLabel = "Parkings")
        public static class Parking extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Parking Spot")
        @NavigationItem(navigLabel = "Parking Spots")
        public static class ParkingSpot extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Locker Area")
        @NavigationItem(navigLabel = "Locker Areas")
        public static class LockerArea extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Locker")
        @NavigationItem(navigLabel = "Lockers")
        public static class Locker extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Unit Item")
        @NavigationItem(navigLabel = "Unit Items")
        public static class UnitItem extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Unit Occupancy")
        @NavigationItem(navigLabel = "Unit Occupancies")
        public static class UnitOccupancy extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Floorplan")
        @NavigationItem(navigLabel = "Floorplan")
        public static class Floorplan extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Service")
        @NavigationItem(navigLabel = "Services")
        public static class Service extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Feature")
        @NavigationItem(navigLabel = "Feature")
        public static class Feature extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Concession")
        @NavigationItem(navigLabel = "Concessions")
        public static class Concession extends CRMCrudAppPlace {
        }
    }

    public static class Tenants extends AppPlace {

        @PlaceProperties(caption = "Tenant")
        @NavigationItem(navigLabel = "Tenants")
        public static class Tenant extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Guarantor")
        @NavigationItem(navigLabel = "Guarantors")
        public static class Guarantor extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Screening")
        @NavigationItem(navigLabel = "Screenings")
        public static class Screening extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Tenant On Lease")
        @NavigationItem(navigLabel = "Tenants On Lease")
        public static class TenantInLease extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Lease")
        @NavigationItem(navigLabel = "Leases")
        public static class Lease extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Lease Application")
        @NavigationItem(navigLabel = "Lease Applications")
        public static class LeaseApplication extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Bill")
        @NavigationItem(navigLabel = "Bills")
        public static class Bill extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Payment")
        @NavigationItem(navigLabel = "Payments")
        public static class Payment extends CRMCrudAppPlace {
        }

        public static class EquifaxResult extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Maintenance Request")
        @NavigationItem(navigLabel = "Maintenance Requests")
        public static class MaintenanceRequest extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Former Tenant")
        @NavigationItem(navigLabel = "Former Tenants")
        public static class PastTenant extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Former Lease")
        @NavigationItem(navigLabel = "Former Leases")
        public static class PastLease extends CRMCrudAppPlace {
        }

    }

    public static class Marketing extends AppPlace {

        @PlaceProperties(caption = "Inquiry")
        @NavigationItem(navigLabel = "Inquiry")
        public static class Inquiry extends CRMCrudAppPlace {

        }

        @I18nComment("Potential customer")
        @PlaceProperties(caption = "Lead")
        @NavigationItem(navigLabel = "Leads")
        public static class Lead extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Appointment")
        @NavigationItem(navigLabel = "Appointments")
        public static class Appointment extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Showing")
        @NavigationItem(navigLabel = "Showings")
        public static class Showing extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Potential Tenant")
        @NavigationItem(navigLabel = "Potential Tenants")
        public static class FutureTenant extends CRMCrudAppPlace {
        }
    }

    public static class LegalAndCollections extends AppPlace {
    }

    public static class Finance extends AppPlace {

        @PlaceProperties(caption = "BillingRun")
        @NavigationItem(navigLabel = "BillingRuns")
        public static class BillingRun extends CRMCrudAppPlace {
        }
    }

    public static class Organization extends AppPlace {

        @PlaceProperties(caption = "Employee")
        @NavigationItem(navigLabel = "Employees")
        public static class Employee extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Portfolio")
        @NavigationItem(navigLabel = "Portfolios")
        public static class Portfolio extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Vendor")
        @NavigationItem(navigLabel = "Vendors")
        public static class Vendor extends CRMCrudAppPlace {
        }
    }

    @PlaceProperties(caption = "Report")
    public static class Report extends CRMCrudAppPlace {

        @PlaceProperties(caption = "Reports")
        @NavigationItem(navigLabel = "Manage Reports")
        public static class Management extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Edit")
        @NavigationItem(navigLabel = "Edit")
        public static class Edit extends CRMCrudAppPlace {
        }
    }

    @PlaceProperties(caption = "Dashboard")
    public static class Dashboard extends AppPlace {

        @PlaceProperties(caption = "Dashboards")
        @NavigationItem(navigLabel = "Manage Dashboards")
        public static class Management extends CRMCrudAppPlace {

        }

        @PlaceProperties(caption = "Edit")
        @NavigationItem(navigLabel = "Edit")
        public static class Edit extends CRMCrudAppPlace {
        }
    }

    @NavigationItem(navigLabel = "User Account")
    public static class Account extends CRMCrudAppPlace {
    }

    @NavigationItem(navigLabel = "Administration")
    public static class Settings extends AppPlace {

        @NavigationItem(navigLabel = "Policies")
        public static class Policies extends AppPlace {

            @PlaceProperties(caption = "Misc")
            @NavigationItem(navigLabel = "Misc")
            public static class Misc extends CRMCrudAppPlace {

            }

            @PlaceProperties(caption = "Pet Policy")
            @NavigationItem(navigLabel = "Pet Policy")
            public static class PetPolicy extends CRMCrudAppPlace {

            }

            @PlaceProperties(caption = "Application Documentation")
            @NavigationItem(navigLabel = "Application Documentation")
            public static class ApplicationDocumentation extends CRMCrudAppPlace {

            }

            @PlaceProperties(caption = "Lease Terms")
            @NavigationItem(navigLabel = "Lease Terms")
            public static class LeaseTerms extends CRMCrudAppPlace {

            }

            @PlaceProperties(caption = "Email Templates")
            @NavigationItem(navigLabel = "Email Templates")
            public static class EmailTemplates extends CRMCrudAppPlace {

            }

            @PlaceProperties(caption = "Product Taxes")
            @NavigationItem(navigLabel = "Product Taxes")
            public static class ProductTax extends CRMCrudAppPlace {

            }

//            @PlaceProperties(caption = "Adjustment Taxes")
//            @NavigationItem(navigLabel = "Adjustment Taxes")
//            public static class AdjustmentTax extends CRMCrudAppPlace {
//
//            }

            @PlaceProperties(caption = "Adjustment Taxes")
            @NavigationItem(navigLabel = "Adjustment Taxes")
            public static class LeaseAdjustment extends CRMCrudAppPlace {

            }

            @PlaceProperties(caption = "Deposits")
            @NavigationItem(navigLabel = "Deposits")
            public static class Deposits extends CRMCrudAppPlace {

            }

            @PlaceProperties(caption = "Background Check")
            @NavigationItem(navigLabel = "Background Check")
            public static class BackgroundCheck extends CRMCrudAppPlace {

            }

            @PlaceProperties(caption = "Lease Billing")
            @NavigationItem(navigLabel = "Lease Billing")
            public static class LeaseBilling extends CRMCrudAppPlace {

            }

            @PlaceProperties(caption = "ID Assignment")
            @NavigationItem(navigLabel = "ID Assignment")
            public static class IdAssignment extends CRMCrudAppPlace {

            }
        }

        @PlaceProperties(caption = "Lease Terms")
        @NavigationItem(navigLabel = "Lease Terms")
        public static class LeaseTerms extends CRMCrudAppPlace {

        }

        @PlaceProperties(caption = "User Role")
        @NavigationItem(navigLabel = "User Roles")
        public static class UserRole extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Product Dictionary")
        @NavigationItem(navigLabel = "Product Dictionary")
        public static class ProductDictionary extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Service Item Type")
        @NavigationItem(navigLabel = "Service Item Type")
        public static class ServiceItemType extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Feature Item Type")
        @NavigationItem(navigLabel = "Feature Item Type")
        public static class FeatureItemType extends CRMCrudAppPlace {

        }

        @PlaceProperties(caption = "Content")
        @NavigationItem(navigLabel = "Content")
        public static class Content extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Page")
        @NavigationItem(navigLabel = "Page")
        public static class Page extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Tax")
        @NavigationItem(navigLabel = "Taxes")
        public static class Tax extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "GL Code Category")
        @NavigationItem(navigLabel = "GL Codes")
        public static class GlCodeCategory extends CRMCrudAppPlace {
        }

        @PlaceProperties(caption = "Lease Adjustment Reason")
        @NavigationItem(navigLabel = "Lease Adjustment Reason")
        public static class LeaseAdjustmentReason extends CRMCrudAppPlace {
        }
    }

    @NavigationItem(navigLabel = "Alerts")
    public static class Alert extends AppPlace {
    }

    @NavigationItem(navigLabel = "Messages")
    public static class Message extends AppPlace {
    }

}
