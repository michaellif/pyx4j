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
import com.pyx4j.site.rpc.NotificationAppPlace;
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.PublicPlace;
import com.pyx4j.site.shared.meta.SiteMap;

public class PtSiteMap implements SiteMap {

    public static String ARG_FLOORPLAN_ID = "fp-id";

    public final static String STEP_ARG_NAME = "substep";

    @PlaceProperties(caption = "Welcome to Move-In Wizard")
    public static class Login extends AppPlace implements PublicPlace {
    }

    /**
     * Used while user is waiting for authentication required for password reset or when new application is created for him.
     */
    public static class LoginWithToken extends AppPlace implements PublicPlace {
    }

    public static class PasswordResetRequest extends AppPlace implements PublicPlace {
    }

    public static class PasswordReset extends AppPlace {
    }

    public static class PasswordChange extends AppPlace {
    }

    @PlaceProperties(caption = "Please Select an Application")
    public static class ApplicationSelectionRequired extends AppPlace {
    }

    @PlaceProperties(caption = "Apartment Info")
    public static class Apartment extends AppPlace implements WizardStepPlace {
    }

    public static class Tenants extends AppPlace implements WizardStepPlace {
    }

    @PlaceProperties(caption = "Information")
    public static class Info extends AppPlace implements WizardStepPlace {
    }

    public static class Financial extends AppPlace implements WizardStepPlace {
    }

    public static class Charges extends AppPlace implements WizardStepPlace {
    }

    @PlaceProperties(caption = "Application/Offer To Lease Summary")
    public static class Summary extends AppPlace implements WizardStepPlace {
    }

    public static class Payment extends AppPlace implements WizardStepPlace {
    }

    public static class Completion extends AppPlace {
    }

    public static class ApplicationStatus extends AppPlace {
    }

    public static class Notification extends NotificationAppPlace {
    }

    // FIXME this is map of Mockup Move-In AKA Welcome AKA Wizard for approved tenants, needs review for real implementation    
    public static class WelcomeWizard extends AppPlace {

        @PlaceProperties(caption = "Congratulations! You have been APPROVED")
        public static class Welcome extends AppPlace {

        }

        @PlaceProperties(navigLabel = "Lease", caption = "Residential Tenancy Agreement")
        public static class ReviewLease extends AppPlace implements WizardStepPlace {

        }

        @PlaceProperties(caption = "Tenant Insurance")
        public static class Insurance extends AppPlace implements WizardStepPlace {

        }

        @PlaceProperties(navigLabel = "Schedule Move-In Day", caption = "Move-In Day Scheduler")
        public static class MoveInSchedule extends AppPlace implements WizardStepPlace {

        }

        public static class Completion extends AppPlace {

        }
    }
}
