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

import java.util.HashMap;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.annotations.NavigationItem;
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.SiteMap;

public class CrmSiteMap implements SiteMap {

    public static String ARG_NAME_ITEM_ID = "itemID";

    public static String ARG_NAME_PARENT_ID = "parentID";

    public static String ARG_VALUE_NEW_ITEM = "new";

    public static AppPlace formItemPlace(AppPlace itemPlace, String itemID) {
        HashMap<String, String> args = new HashMap<String, String>();
        args.put(CrmSiteMap.ARG_NAME_ITEM_ID, itemID);
        itemPlace.setArgs(args);
        return itemPlace;
    }

    public static AppPlace formNewItemPlace(AppPlace itemPlace, String parentID) {
        HashMap<String, String> args = new HashMap<String, String>();
        args.put(CrmSiteMap.ARG_NAME_ITEM_ID, ARG_VALUE_NEW_ITEM);
        args.put(CrmSiteMap.ARG_NAME_PARENT_ID, parentID);
        itemPlace.setArgs(args);
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
        public static class Buildings extends AppPlace {
        }

        @PlaceProperties(caption = "Units")
        @NavigationItem(navigLabel = "Units")
        public static class Units extends AppPlace {
        }

        @PlaceProperties(caption = "Parking Spots")
        @NavigationItem(navigLabel = "Parking Spots")
        public static class ParkingSpots extends AppPlace {
        }

        @PlaceProperties(caption = "Lockers")
        @NavigationItem(navigLabel = "Lockers")
        public static class Lockers extends AppPlace {
        }

        @PlaceProperties(caption = "Arrears")
        @NavigationItem(navigLabel = "Arrears")
        public static class Arrears extends AppPlace {
        }

        @PlaceProperties(caption = "Budgets")
        @NavigationItem(navigLabel = "Budgets")
        public static class Budgets extends AppPlace {
        }

        @PlaceProperties(caption = "Purchase Orders")
        @NavigationItem(navigLabel = "Purchase Orders")
        public static class PurchaseOrders extends AppPlace {
        }

        @PlaceProperties(caption = "City Orders")
        @NavigationItem(navigLabel = "City Orders")
        public static class CityOrders extends AppPlace {
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

        @PlaceProperties(caption = "Building")
        @NavigationItem(navigLabel = "Building")
        public static class Building extends AppPlace {
        }

        @PlaceProperties(caption = "Unit")
        @NavigationItem(navigLabel = "Unit")
        public static class Unit extends AppPlace {
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

        @PlaceProperties(caption = "Building")
        @NavigationItem(navigLabel = "Building")
        public static class Building extends AppPlace {
        }

        @PlaceProperties(caption = "Unit")
        @NavigationItem(navigLabel = "Unit")
        public static class Unit extends AppPlace {
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

    @NavigationItem(navigLabel = "User Settings")
    public static class Settings extends AppPlace {
    }

    @NavigationItem(navigLabel = "Alerts")
    public static class Alert extends AppPlace {
    }

    @NavigationItem(navigLabel = "Messages")
    public static class Message extends AppPlace {
    }
}
