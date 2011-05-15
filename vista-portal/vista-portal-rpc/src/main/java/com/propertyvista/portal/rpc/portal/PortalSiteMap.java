/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.annotations.NavigationItem;
import com.pyx4j.site.rpc.annotations.PlaceProperties;

public class PortalSiteMap {
    public static String ARG_NAME_ITEM_ID = "ItemID";

    @PlaceProperties(caption = "Find an Apartment")
    @NavigationItem(navigLabel = "FindApartment")
    public static class FindApartment extends AppPlace {
    }

    @PlaceProperties(caption = "Residents")
    @NavigationItem(navigLabel = "Residents")
    public static class Residents extends AppPlace {

        @PlaceProperties(caption = "Tenant Profile")
        @NavigationItem(navigLabel = "Profile")
        public static class TenantProfile extends AppPlace {
        }

        @PlaceProperties(caption = "Maintenance")
        @NavigationItem(navigLabel = "Maintenance")
        public static class Maintenance extends AppPlace {
        }

        @PlaceProperties(caption = "Payment")
        @NavigationItem(navigLabel = "Payment")
        public static class Payment extends AppPlace {
        }

        @PlaceProperties(caption = "Lease Application")
        @NavigationItem(navigLabel = "LeaseApp")
        public static class LeaseApplication extends AppPlace {
        }

    }

}
