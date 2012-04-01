/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.rpc;

import com.pyx4j.commons.Key;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.annotations.NavigationItem;
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.PublicPlace;
import com.pyx4j.site.shared.meta.SigningOutPlace;
import com.pyx4j.site.shared.meta.SiteMap;

public class AdminSiteMap implements SiteMap {

    @PlaceProperties(caption = "Login")
    public static class Login extends AppPlace implements PublicPlace {
    }

    /**
     * Used while user is waiting for authentication required for password reset.
     */
    public static class LoginWithToken extends AppPlace implements PublicPlace {

    }

    public static class SigningOut extends AppPlace implements SigningOutPlace {
    }

    @PlaceProperties(caption = "Password Reset Request")
    public static class PasswordResetRequest extends AppPlace implements PublicPlace {

    }

    @PlaceProperties(caption = "Reset Password")
    public static class PasswordReset extends AppPlace {
    }

    @PlaceProperties(caption = "Change Password")
    public static class PasswordChange extends AppPlace {
    }

    public static class Management extends AppPlace {
        @PlaceProperties(caption = "Property Management Company (PMC)")
        @NavigationItem(navigLabel = "Property Management Companies (PMCs)")
        public static class PMC extends CrudAppPlace {
        }

        @PlaceProperties(caption = "Onboarding Users")
        @NavigationItem(navigLabel = "Onboarding Users")
        public static class OnboardingUsers extends CrudAppPlace {

        }
    }

    public static class Administration extends AppPlace {
        @PlaceProperties(caption = "System Maintenance")
        @NavigationItem(navigLabel = "System Maintenance")
        public static class Maintenance extends CrudAppPlace {
            // set default place type as Viewer one (we have no lister for this item!)
            public Maintenance() {
                super(Type.viewer);
                formViewerPlace(new Key(-1));
            }
        }

        @PlaceProperties(caption = "Users")
        @NavigationItem(navigLabel = "Users")
        public static class AdminUsers extends CrudAppPlace {
        }
    }

    @NavigationItem(navigLabel = "Alerts")
    public static class Alert extends AppPlace {
    }

    @NavigationItem(navigLabel = "Messages")
    public static class Message extends AppPlace {
    }

    @NavigationItem(navigLabel = "Settings")
    public static class Settings extends AppPlace {
    }

    @NavigationItem(navigLabel = "Account")
    public static class Account extends AppPlace {
    }
}
