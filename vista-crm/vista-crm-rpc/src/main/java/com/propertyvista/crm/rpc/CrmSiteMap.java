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
import com.pyx4j.site.rpc.CrudAppPlace;
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
        public static class Complex extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Building")
        @NavigationItem(navigLabel = "Buildings")
        public static class Building extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Unit")
        @NavigationItem(navigLabel = "Units")
        public static class Unit extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Arrear")
        @NavigationItem(navigLabel = "Arrears")
        public static class Arrear extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Budget")
        @NavigationItem(navigLabel = "Budgets")
        public static class Budget extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Purchase Order")
        @NavigationItem(navigLabel = "Purchase Orders")
        public static class PurchaseOrder extends CrudAppPlace {
        }

        @PlaceProperties(caption = "City Order")
        @NavigationItem(navigLabel = "City Orders")
        public static class CityOrder extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Elevator")
        @NavigationItem(navigLabel = "Elevators")
        public static class Elevator extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Boiler")
        @NavigationItem(navigLabel = "Boilers")
        public static class Boiler extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Roof")
        @NavigationItem(navigLabel = "Roofs")
        public static class Roof extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Parking")
        @NavigationItem(navigLabel = "Parkings")
        public static class Parking extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Parking Spot")
        @NavigationItem(navigLabel = "Parking Spots")
        public static class ParkingSpot extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Locker Area")
        @NavigationItem(navigLabel = "Locker Areas")
        public static class LockerArea extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Locker")
        @NavigationItem(navigLabel = "Lockers")
        public static class Locker extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Unit Item")
        @NavigationItem(navigLabel = "Unit Items")
        public static class UnitItem extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Unit Occupancy")
        @NavigationItem(navigLabel = "Unit Occupancies")
        public static class UnitOccupancy extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Floorplan")
        @NavigationItem(navigLabel = "Floorplan")
        public static class Floorplan extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Service")
        @NavigationItem(navigLabel = "Services")
        public static class Service extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Feature")
        @NavigationItem(navigLabel = "Feature")
        public static class Feature extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Concession")
        @NavigationItem(navigLabel = "Concessions")
        public static class Concession extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Maintenance Request")
        @NavigationItem(navigLabel = "Maintenance Requests")
        public static class MaintenanceRequest extends CrudAppPlace {
        }
    }

    public static class Tenants extends AppPlace {

        @PlaceProperties(caption = "Tenant")
        @NavigationItem(navigLabel = "Tenants")
        public static class Tenant extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Guarantor")
        @NavigationItem(navigLabel = "Guarantors")
        public static class Guarantor extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Screening")
        @NavigationItem(navigLabel = "Screenings")
        public static class Screening extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Tenant On Lease")
        @NavigationItem(navigLabel = "Tenants On Lease")
        public static class TenantInLease extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Lease")
        @NavigationItem(navigLabel = "Leases")
        public static class Lease extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Bill")
        @NavigationItem(navigLabel = "Bills")
        public static class Bill extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Application")
        @NavigationItem(navigLabel = "Applications")
        public static class MasterApplication extends CrudAppPlace {
        }

        public static class Application extends CrudAppPlace {
        }

        public static class EquifaxResult extends CrudAppPlace {
        }
    }

    public static class Marketing extends AppPlace {

        @PlaceProperties(caption = "Inquiry")
        @NavigationItem(navigLabel = "Inquiry")
        public static class Inquiry extends CrudAppPlace {

        }

        @I18nComment("Potential customer")
        @PlaceProperties(caption = "Lead")
        @NavigationItem(navigLabel = "Leads")
        public static class Lead extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Appointment")
        @NavigationItem(navigLabel = "Appointments")
        public static class Appointment extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Showing")
        @NavigationItem(navigLabel = "Showings")
        public static class Showing extends CrudAppPlace {
        }
    }

    public static class LegalAndCollections extends AppPlace {
    }

    public static class Finance extends AppPlace {
    }

    public static class Organization extends AppPlace {

        @PlaceProperties(caption = "Employee")
        @NavigationItem(navigLabel = "Employees")
        public static class Employee extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Portfolio")
        @NavigationItem(navigLabel = "Portfolios")
        public static class Portfolio extends CrudAppPlace {
        }
    }

    @PlaceProperties(caption = "Report")
    public static class Report extends CrudAppPlace {

        @PlaceProperties(caption = "Reports")
        @NavigationItem(navigLabel = "Manage Reports")
        public static class Management extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Edit")
        @NavigationItem(navigLabel = "Edit")
        public static class Edit extends CrudAppPlace {
        }
    }

    @PlaceProperties(caption = "Dashboard")
    public static class Dashboard extends CrudAppPlace {

        @PlaceProperties(caption = "Dashboards")
        @NavigationItem(navigLabel = "Manage Dashboards")
        public static class Management extends CrudAppPlace {

        }

        @PlaceProperties(caption = "Edit")
        @NavigationItem(navigLabel = "Edit")
        public static class Edit extends CrudAppPlace {
        }
    }

    @NavigationItem(navigLabel = "User Account")
    public static class Account extends CrudAppPlace {
    }

    @NavigationItem(navigLabel = "Administration")
    public static class Settings extends AppPlace {

        @NavigationItem(navigLabel = "Policies")
        public static class Policies extends AppPlace {

            @PlaceProperties(caption = "Misc")
            @NavigationItem(navigLabel = "Misc")
            public static class Misc extends CrudAppPlace {

            }

            @PlaceProperties(caption = "Pet Policy")
            @NavigationItem(navigLabel = "Pet Policy")
            public static class PetPolicy extends CrudAppPlace {

            }

            @PlaceProperties(caption = "Application Documentation")
            @NavigationItem(navigLabel = "Application Documentation")
            public static class ApplicationDocumentation extends CrudAppPlace {

            }

            @PlaceProperties(caption = "Lease Terms")
            @NavigationItem(navigLabel = "Lease Terms")
            public static class LeaseTerms extends CrudAppPlace {

            }

            @PlaceProperties(caption = "Email Templates")
            @NavigationItem(navigLabel = "Email Templates")
            public static class EmailTemplates extends CrudAppPlace {

            }
        }

        @PlaceProperties(caption = "Lease Terms")
        @NavigationItem(navigLabel = "Lease Terms")
        public static class LeaseTerms extends CrudAppPlace {

        }

        @PlaceProperties(caption = "Policy Management")
        @NavigationItem(navigLabel = "Policy Management")
        public static class Policy extends AppPlace {
        }

        @PlaceProperties(caption = "User Role")
        @NavigationItem(navigLabel = "User Roles")
        public static class UserRole extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Product Dictionary")
        @NavigationItem(navigLabel = "Product Dictionary")
        public static class ProductDictionary extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Service Item Type")
        @NavigationItem(navigLabel = "Service Item Type")
        public static class ServiceItemType extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Feature Item Type")
        @NavigationItem(navigLabel = "Feature Item Type")
        public static class FeatureItemType extends CrudAppPlace {

        }

        @PlaceProperties(caption = "Content")
        @NavigationItem(navigLabel = "Content")
        public static class Content extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Page")
        @NavigationItem(navigLabel = "Page")
        public static class Page extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Tax")
        @NavigationItem(navigLabel = "Taxes")
        public static class Tax extends CrudAppPlace {
        }

        @PlaceProperties(caption = "GlCode")
        @NavigationItem(navigLabel = "GlCodes")
        public static class GlCode extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Charge Code")
        @NavigationItem(navigLabel = "Charge Codes")
        public static class ChargeCode extends CrudAppPlace {
        }
    }

    @NavigationItem(navigLabel = "Alerts")
    public static class Alert extends AppPlace {
    }

    @NavigationItem(navigLabel = "Messages")
    public static class Message extends AppPlace {
    }

}
