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

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.annotations.NavigationItem;
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.SiteMap;

public class AdminSiteMap implements SiteMap {

    @PlaceProperties(caption = "Login")
    public static class Login extends AppPlace {
    }

    @PlaceProperties(caption = "Retrieve Password")
    public static class RetrievePassword extends AppPlace {
    }

    @PlaceProperties(caption = "Reset Password")
    public static class ResetPassword extends AppPlace {
    }

    @PlaceProperties(caption = "Change Password")
    public static class ChangePassword extends AppPlace {
    }

    public static class Management extends AppPlace {
        @PlaceProperties(caption = "PMC")
        @NavigationItem(navigLabel = "PMCs")
        public static class PMC extends CrudAppPlace {
        }
    }

    public static class Administration extends AppPlace {
        @PlaceProperties(caption = "System Maintenance")
        @NavigationItem(navigLabel = "System Maintenance")
        public static class Maintenance extends CrudAppPlace {
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
