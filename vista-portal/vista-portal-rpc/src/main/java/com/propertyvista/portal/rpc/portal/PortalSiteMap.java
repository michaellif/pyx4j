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
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.annotations.NavigationItem;
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.SiteMap;

public class PortalSiteMap implements SiteMap {

    public static String ARG_PAGE_ID = "item-id";

    public static String ARG_PROPERTY_ID = "property-id";

    public static String ARG_FLOORPLAN_ID = "floorplan-id";

    public static String ARG_MAINTENANCE_ID = "maintenance-id";

    public static class Landing extends AppPlace {
    }

    public static class Page extends AppPlace {
    }

    @PlaceProperties(caption = "Potential Tenants")
    public static class PotentialTenants extends AppPlace {
    }

    @PlaceProperties(caption = "Find an Apartment")
    @NavigationItem(navigLabel = "FindApartment")
    public static class FindApartment extends AppPlace {

        @PlaceProperties(caption = "Found Properties")
        @NavigationItem(navigLabel = "propertyMap")
        public static class PropertyMap extends AppPlace {
        }

        public static class ApartmentDetails extends AppPlace {
        }

        public static class FloorplanDetails extends AppPlace {
        }
    }

    @PlaceProperties(caption = "Residents")
    @NavigationItem(navigLabel = "Residents")
    public static class Residents extends AppPlace {

        @PlaceProperties(caption = "Sign In")
        @NavigationItem(navigLabel = "residentLogin")
        public static class Login extends AppPlace {
        }

        public static class RetirevePassword extends AppPlace {
        }

        public static class LoginInvitation extends AppPlace {
        }

        public static class CreateAccount extends AppPlace {
        }

        public static class PersonalInfo extends AppPlace {
        }

        public static class Maintenance extends AppPlace {
        }

        public static class MaintenanceListHistory extends CrudAppPlace {
        }

        public static class BillingHistory extends AppPlace {
        }

        public static class PaymentMethods extends AppPlace {
        }

        public static class CurrentBill extends AppPlace {
        }

    }

}
