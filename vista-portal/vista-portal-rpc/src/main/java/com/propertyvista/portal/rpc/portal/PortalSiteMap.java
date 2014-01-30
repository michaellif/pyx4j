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
import com.pyx4j.site.rpc.NotificationAppPlace;
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.PublicPlace;
import com.pyx4j.site.shared.meta.SiteMap;

public class PortalSiteMap implements SiteMap {

    public static String ARG_PAGE_ID = "item-id";

    public static String ARG_ENTITY_ID = "entity-id";

    @PlaceProperties(caption = "Resident Login")
    public static class Login extends AppPlace implements PublicPlace {
        {
            setStable(false);
        }
    }

    public static class LoginWithToken extends AppPlace implements PublicPlace {
        {
            //setStable(false);
        }
    }

    public static class Logout extends AppPlace implements PublicPlace {
        {
            setStable(false);
        }
    }

    /** This is the place when user must enter credentials and pass the humand verification to send the password request */
    @PlaceProperties(caption = "Reset Password")
    public static class PasswordResetRequest extends AppPlace implements PublicPlace {
        {
            setStable(false);
        }
    }

    /** This is the place wher user gets redirected to change lost/forgotten password via email (from LoginWithToken) */
    @PlaceProperties(caption = "Reset Password")
    public static class PasswordReset extends AppPlace implements PublicPlace {
        {
            setStable(false);
        }
    }

    @PlaceProperties(caption = "Change Password")
    public static class PasswordChange extends AppPlace {
    }

    @PlaceProperties(navigLabel = "", caption = "")
    public static class NotificationPlace extends NotificationAppPlace implements PublicPlace {

    }

    public static class PortalTerms {

        public static class PortalTermsAndConditions extends AppPlace implements PublicPlace {
        }

        public static class PortalPrivacyPolicy extends AppPlace implements PublicPlace {
        }

        public static class PMCTermsAndConditions extends AppPlace implements PublicPlace {
        }

        public static class PMCPrivacyPolicy extends AppPlace implements PublicPlace {
        }

        public static class BillingTerms extends AppPlace implements PublicPlace {
        }
    }
}
