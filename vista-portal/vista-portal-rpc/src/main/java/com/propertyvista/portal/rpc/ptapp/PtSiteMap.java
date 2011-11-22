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
package com.propertyvista.portal.rpc.ptapp;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.annotations.NavigationItem;
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.SiteMap;

public class PtSiteMap implements SiteMap {

    public static String ARG_FLOORPLAN_ID = "fp-id";

    public final static String STEP_ARG_NAME = "substep";

    @PlaceProperties(caption = "Application Form")
    public static class Login extends AppPlace implements AuthPublicPlace {
    }

    @PlaceProperties(caption = "Application Form")
    public static class RetrievePassword extends AppPlace implements AuthPublicPlace {
    }

    @PlaceProperties(caption = "Application Form")
    public static class ResetPassword extends AppPlace implements AuthPrivatePlace {
    }

    @PlaceProperties(caption = "Application Form")
    public static class ChangePassword extends AppPlace implements AuthPrivatePlace {
    }

    @NavigationItem(navigLabel = "Apartment")
    @PlaceProperties(caption = "Apartment Info")
    public static class Apartment extends AppPlace implements WizardStepPlace {
    }

    @NavigationItem(navigLabel = "Tenants")
    @PlaceProperties(caption = "Tenants & Occupants")
    public static class Tenants extends AppPlace implements WizardStepPlace {
    }

    @NavigationItem(navigLabel = "Information")
    @PlaceProperties(caption = "Applicant Information")
    public static class Info extends AppPlace implements WizardStepPlace {
    }

    @NavigationItem(navigLabel = "Financial")
    @PlaceProperties(caption = "Financial")
    public static class Financial extends AppPlace implements WizardStepPlace {
    }

    @NavigationItem(navigLabel = "Charges")
    @PlaceProperties(caption = "Charges")
    public static class Charges extends AppPlace implements WizardStepPlace {
    }

    @NavigationItem(navigLabel = "Summary")
    @PlaceProperties(caption = "Summary")
    public static class Summary extends AppPlace implements WizardStepPlace {
    }

    @NavigationItem(navigLabel = "Payment")
    @PlaceProperties(caption = "Payment")
    public static class Payment extends AppPlace implements WizardStepPlace {
    }

    @NavigationItem(navigLabel = "Completion")
    @PlaceProperties(caption = "Completion")
    public static class Completion extends AppPlace {
    }

    @PlaceProperties(caption = "")
    public static class GenericMessage extends AppPlace {
    }
}
