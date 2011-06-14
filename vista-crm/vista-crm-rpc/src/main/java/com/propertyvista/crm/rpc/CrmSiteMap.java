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

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.annotations.NavigationItem;
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.SiteMap;

public class CrmSiteMap implements SiteMap {

    @PlaceProperties(caption = "Login")
    public static class Login extends AppPlace {
    }

    @PlaceProperties(caption = "Retrieve Password")
    public static class RetrievePassword extends AppPlace {
    }

    @PlaceProperties(caption = "Reset Password")
    public static class ResetPassword extends AppPlace {
    }

    @PlaceProperties(caption = "Change Password")
    public static class ChangePassword extends AppPlace {
    }

    public static class Properties extends AppPlace {
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

        @PlaceProperties(caption = "Concession")
        @NavigationItem(navigLabel = "Concessions")
        public static class Concession extends CrudAppPlace {

        }

    }

    public static class Tenants extends AppPlace {

        @PlaceProperties(caption = "Tenant")
        @NavigationItem(navigLabel = "Tenants")
        public static class Tenant extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Lease")
        @NavigationItem(navigLabel = "Leases")
        public static class Lease extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Application")
        @NavigationItem(navigLabel = "Applications")
        public static class Application extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Inquiry")
        @NavigationItem(navigLabel = "Inquies")
        public static class Inquiry extends CrudAppPlace {
        }

    }

    public static class Marketing extends AppPlace {
    }

    public static class LegalAndCollections extends AppPlace {
    }

    public static class Finance extends AppPlace {
    }

    @NavigationItem(navigLabel = "Some Report")
    public static class Report extends AppPlace {
    }

    @NavigationItem(navigLabel = "Default Dashboard")
    public static class Dashboard extends AppPlace {
    }

    @NavigationItem(navigLabel = "User Account")
    public static class Account extends AppPlace {
    }

    @NavigationItem(navigLabel = "Administration")
    public static class Settings extends AppPlace {

        @PlaceProperties(caption = "Policy Management")
        @NavigationItem(navigLabel = "Policy Management")
        public static class Policy extends AppPlace {

        }

        @PlaceProperties(caption = "User Role")
        @NavigationItem(navigLabel = "User Role")
        public static class UserRole extends AppPlace {

        }

        @PlaceProperties(caption = "General")
        @NavigationItem(navigLabel = "General")
        public static class General extends AppPlace {

        }

        @PlaceProperties(caption = "Content")
        @NavigationItem(navigLabel = "Content")
        public static class Content extends CrudAppPlace {

        }
    }

    @NavigationItem(navigLabel = "Alerts")
    public static class Alert extends AppPlace {
    }

    @NavigationItem(navigLabel = "Messages")
    public static class Message extends AppPlace {
    }
}
