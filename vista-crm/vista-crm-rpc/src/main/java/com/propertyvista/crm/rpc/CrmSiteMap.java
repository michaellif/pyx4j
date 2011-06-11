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

import com.pyx4j.commons.Key;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.annotations.NavigationItem;
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.SiteMap;

public class CrmSiteMap implements SiteMap {

    public static String ARG_NAME_ITEM_ID = "itemID";

    public static String ARG_NAME_PARENT_ID = "parentID";

    public static String ARG_VALUE_NEW_ITEM = "new";

    @Deprecated
    public static AppPlace formItemPlace(AppPlace itemPlace, Key itemID) {
        itemPlace.putArg(CrmSiteMap.ARG_NAME_ITEM_ID, itemID.toString());
        return itemPlace;
    }

    @Deprecated
    public static AppPlace formNewItemPlace(AppPlace itemPlace, Key parentID) {
        itemPlace.putArg(CrmSiteMap.ARG_NAME_ITEM_ID, ARG_VALUE_NEW_ITEM);
        itemPlace.putArg(CrmSiteMap.ARG_NAME_PARENT_ID, parentID.toString());
        return itemPlace;
    }

    //
    // Paces definition:
    //
    @PlaceProperties(caption = "Application Form")
    public static class Login extends AppPlace {
    }

    @PlaceProperties(caption = "Application Form")
    public static class RetrievePassword extends AppPlace {
    }

    @PlaceProperties(caption = "Application Form")
    public static class ResetPassword extends AppPlace {
    }

    @PlaceProperties(caption = "Application Form")
    public static class ChangePassword extends AppPlace {
    }

    public static class Properties extends AppPlace {
        @PlaceProperties(caption = "Buildings")
        @NavigationItem(navigLabel = "Buildings")
        public static class Buildings extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Units")
        @NavigationItem(navigLabel = "Units")
        public static class Units extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Parking Spots")
        @NavigationItem(navigLabel = "Parking Spots")
        public static class ParkingSpots extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Lockers")
        @NavigationItem(navigLabel = "Lockers")
        public static class Lockers extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Arrears")
        @NavigationItem(navigLabel = "Arrears")
        public static class Arrears extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Budgets")
        @NavigationItem(navigLabel = "Budgets")
        public static class Budgets extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Purchase Orders")
        @NavigationItem(navigLabel = "Purchase Orders")
        public static class PurchaseOrders extends CrudAppPlace {
        }

        @PlaceProperties(caption = "City Orders")
        @NavigationItem(navigLabel = "City Orders")
        public static class CityOrders extends CrudAppPlace {
        }
    }

    public static class Tenants extends AppPlace {

        @PlaceProperties(caption = "Current Tenants")
        @NavigationItem(navigLabel = "Current Tenants")
        public static class CurrentTenants extends AppPlace {
        }

        @PlaceProperties(caption = "All Tenants")
        @NavigationItem(navigLabel = "All Tenants")
        public static class AllTenants extends AppPlace {
        }

        @PlaceProperties(caption = "Leases")
        @NavigationItem(navigLabel = "Leases")
        public static class Leases extends AppPlace {
        }

        @PlaceProperties(caption = "Applications")
        @NavigationItem(navigLabel = "Applications")
        public static class Applications extends AppPlace {
        }

        @PlaceProperties(caption = "Inquiries")
        @NavigationItem(navigLabel = "Inquiries")
        public static class Inquiries extends AppPlace {
        }
    }

    public static class Viewers extends AppPlace {

        @PlaceProperties(caption = "Tenant")
        @NavigationItem(navigLabel = "Tenant")
        public static class Tenant extends AppPlace {
        }

        @PlaceProperties(caption = "Lease")
        @NavigationItem(navigLabel = "Lease")
        public static class Lease extends AppPlace {
        }

        @PlaceProperties(caption = "Application")
        @NavigationItem(navigLabel = "Application")
        public static class Application extends AppPlace {
        }

        @PlaceProperties(caption = "Inquiry")
        @NavigationItem(navigLabel = "Inquiry")
        public static class Inquiry extends AppPlace {
        }

        @PlaceProperties(caption = "Elevator")
        @NavigationItem(navigLabel = "Elevator")
        public static class Elevator extends AppPlace {

        }

        @PlaceProperties(caption = "Boiler")
        @NavigationItem(navigLabel = "Boiler")
        public static class Boiler extends AppPlace {

        }

        @PlaceProperties(caption = "Roof")
        @NavigationItem(navigLabel = "Roof")
        public static class Roof extends AppPlace {

        }

        @PlaceProperties(caption = "Parking")
        @NavigationItem(navigLabel = "Parking")
        public static class Parking extends AppPlace {

        }

        @PlaceProperties(caption = "Parking Spot")
        @NavigationItem(navigLabel = "Parking Spot")
        public static class ParkingSpot extends AppPlace {

        }

        @PlaceProperties(caption = "Locker Area")
        @NavigationItem(navigLabel = "Locker Area")
        public static class LockerArea extends AppPlace {

        }

        @PlaceProperties(caption = "Locker")
        @NavigationItem(navigLabel = "Locker")
        public static class Locker extends AppPlace {

        }

        @PlaceProperties(caption = "Unit Item")
        @NavigationItem(navigLabel = "Unit Item")
        public static class UnitItem extends AppPlace {

        }

        @PlaceProperties(caption = "Unit Occupancy")
        @NavigationItem(navigLabel = "Unit Occupancy")
        public static class UnitOccupancy extends AppPlace {

        }

        @PlaceProperties(caption = "Concession")
        @NavigationItem(navigLabel = "Concession")
        public static class Concession extends AppPlace {

        }

    }

    public static class Editors extends AppPlace {

        @PlaceProperties(caption = "Tenant")
        @NavigationItem(navigLabel = "Tenant")
        public static class Tenant extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Lease")
        @NavigationItem(navigLabel = "Lease")
        public static class Lease extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Application")
        @NavigationItem(navigLabel = "Application")
        public static class Application extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Inquiry")
        @NavigationItem(navigLabel = "Inquiry")
        public static class Inquiry extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Elevator")
        @NavigationItem(navigLabel = "Elevator")
        public static class Elevator extends CrudAppPlace {

        }

        @PlaceProperties(caption = "Boiler")
        @NavigationItem(navigLabel = "Boiler")
        public static class Boiler extends CrudAppPlace {

        }

        @PlaceProperties(caption = "Roof")
        @NavigationItem(navigLabel = "Roof")
        public static class Roof extends CrudAppPlace {

        }

        @PlaceProperties(caption = "Parking")
        @NavigationItem(navigLabel = "Parking")
        public static class Parking extends CrudAppPlace {

        }

        @PlaceProperties(caption = "Parking Spot")
        @NavigationItem(navigLabel = "Parking Spot")
        public static class ParkingSpot extends CrudAppPlace {

        }

        @PlaceProperties(caption = "Locker Area")
        @NavigationItem(navigLabel = "Locker Area")
        public static class LockerArea extends CrudAppPlace {

        }

        @PlaceProperties(caption = "Locker")
        @NavigationItem(navigLabel = "Locker")
        public static class Locker extends CrudAppPlace {

        }

        @PlaceProperties(caption = "Unit Item")
        @NavigationItem(navigLabel = "Unit Item")
        public static class UnitItem extends CrudAppPlace {

        }

        @PlaceProperties(caption = "Unit Occupancy")
        @NavigationItem(navigLabel = "Unit Occupancy")
        public static class UnitOccupancy extends CrudAppPlace {

        }

        @PlaceProperties(caption = "Concession")
        @NavigationItem(navigLabel = "Concession")
        public static class Concession extends CrudAppPlace {

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
