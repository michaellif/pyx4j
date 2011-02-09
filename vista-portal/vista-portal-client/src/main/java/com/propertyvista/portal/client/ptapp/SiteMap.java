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
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp;

import com.pyx4j.site.client.NavigationItem;
import com.pyx4j.site.client.place.AppPlace;

public class SiteMap {

    @NavigationItem(caption = "Application Form")
    public static class SignIn extends AppPlace {
    }

    @NavigationItem(navigLabel = "Apartment", caption = "Apartment Info", topLevel = true)
    public static class Apartment extends AppPlace {
    }

    @NavigationItem(navigLabel = "Tenants", caption = "Tenants", topLevel = true)
    public static class Tenants extends AppPlace {
    }

    @NavigationItem(navigLabel = "Info", caption = "Info", topLevel = true)
    public static class Info extends AppPlace {
    }

    @NavigationItem(navigLabel = "Financial", caption = "Financial", topLevel = true)
    public static class Financial extends AppPlace {
    }

    @NavigationItem(navigLabel = "Pets", caption = "Pets", topLevel = true)
    public static class Pets extends AppPlace {
    }

    @NavigationItem(navigLabel = "Payments", caption = "Payments", topLevel = true)
    public static class Payments extends AppPlace {
    }

    @NavigationItem(navigLabel = "Summary", caption = "Summary", topLevel = true)
    public static class Summary extends AppPlace {
    }

}
