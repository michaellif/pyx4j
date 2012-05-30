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
import com.pyx4j.site.shared.meta.SiteMap;

public class PortalSiteMap implements SiteMap {

    public static String ARG_PAGE_ID = "item-id";

    public static String ARG_ENTITY_ID = "entity-id";

    public static class Login extends AppPlace {
    }

    public static class LoginWithToken extends AppPlace {
    }

    @PlaceProperties(caption = "Select Lease")
    public static class LeaseContextSelection extends AppPlace {
    }

    public static class PasswordResetRequest extends AppPlace {
    }

    public static class PasswordReset extends AppPlace {
    }

    public static class PasswordChange extends AppPlace {
    }

    public static class Landing extends AppPlace {
    }

    @PlaceProperties(caption = "Potential Tenants")
    public static class PotentialTenants extends AppPlace {
    }

    @PlaceProperties(caption = "Dashboard")
    @NavigationItem(navigLabel = "Dashboard")
    public static class Residents extends AppPlace {

        @PlaceProperties(caption = "Personal Information")
        @NavigationItem(navigLabel = "Personal Information")
        public static class PersonalInformation extends AppPlace {
        }

        @PlaceProperties(caption = "Bill Summary")
        @NavigationItem(navigLabel = "Bill Summary")
        public static class BillSummary extends AppPlace {
        }

        @PlaceProperties(caption = "Billing History")
        @NavigationItem(navigLabel = "Billing History")
        public static class BillingHistory extends AppPlace {

            public static class ViewBill extends AppPlace {
            }
        }

        @PlaceProperties(caption = "Payment Methods")
        @NavigationItem(navigLabel = "Payment Methods")
        public static class PaymentMethods extends AppPlace {

            public static class NewPaymentMethod extends AppPlace {
            }

            public static class EditPaymentMethod extends AppPlace {
            }
        }

        @PlaceProperties(caption = "Maintenance")
        @NavigationItem(navigLabel = "Maintenance")
        public static class Maintenance extends AppPlace {

            public static class NewMaintenanceRequest extends AppPlace {
            }

            public static class EditMaintenanceRequest extends AppPlace {
            }
        }

        @PlaceProperties(caption = "My Insurance")
        @NavigationItem(navigLabel = "My Insurance")
        public static class Insurance extends AppPlace {

        }
    }
}
