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

import com.pyx4j.site.client.annotations.NavigationItem;
import com.pyx4j.site.client.annotations.PlaceProperties;
import com.pyx4j.site.client.place.AppPlace;

public class SiteMap {

    @PlaceProperties(caption = "Application Form")
    public static class CreateAccount extends AppPlace {
    }

    @PlaceProperties(caption = "Login")
    public static class Login extends AppPlace {
    }

    @PlaceProperties(caption = "Retrieve Password")
    public static class RetrievePassword extends AppPlace {
    }

    @NavigationItem(navigLabel = "Apartment")
    @PlaceProperties(caption = "Apartment Info")
    public static class Apartment extends AppPlace {
    }

    @NavigationItem(navigLabel = "Tenants")
    @PlaceProperties(caption = "Tenants")
    public static class Tenants extends AppPlace {
    }

    @NavigationItem(navigLabel = "Info")
    @PlaceProperties(caption = "Applicant Info")
    public static class Info extends AppPlace {
    }

    @NavigationItem(navigLabel = "Financial")
    @PlaceProperties(caption = "Financial")
    public static class Financial extends AppPlace {
    }

    @NavigationItem(navigLabel = "Pets")
    @PlaceProperties(caption = "Pets")
    public static class Pets extends AppPlace {
    }

    @NavigationItem(navigLabel = "Charges")
    @PlaceProperties(caption = "Charges")
    public static class Charges extends AppPlace {
    }

    @NavigationItem(navigLabel = "Summary")
    @PlaceProperties(caption = "Summary")
    public static class Summary extends AppPlace {
    }

    @NavigationItem(navigLabel = "Payment")
    @PlaceProperties(caption = "Payment")
    public static class Payment extends AppPlace {
    }

    @PlaceProperties(caption = "Privacy policy", staticContent = "privacyPolicy")
    public static class PrivacyPolicy extends AppPlace {
    }

    @PlaceProperties(caption = "Terms and conditions", staticContent = "termsAndConditions")
    public static class TermsAndConditions extends AppPlace {
    }

}
