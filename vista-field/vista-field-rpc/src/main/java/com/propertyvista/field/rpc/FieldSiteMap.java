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
package com.propertyvista.field.rpc;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.PublicPlace;
import com.pyx4j.site.shared.meta.SiteMap;

public class FieldSiteMap implements SiteMap {

    public static class Login extends AppPlace implements PublicPlace {
    }

    /**
     * A place where a unfortunate user can perform request to reset his password.
     */
    public static class PasswordResetRequest extends AppPlace implements PublicPlace {
    }

    /**
     * Used while user is waiting for authentication required for password reset.
     */
    public static class LoginWithToken extends AppPlace implements PublicPlace {
    }

    @PlaceProperties(caption = "Reset Password")
    public static class PasswordReset extends AppPlace {
    }

    @PlaceProperties(caption = "Change Password")
    public static class PasswordChange extends AppPlace {
    }

    public static class Properties extends AppPlace {

        @PlaceProperties(navigLabel = "Buildings")
        public static class Building extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Units")
        public static class Unit extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Unit Items")
        public static class UnitItem extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Unit Occupancies")
        public static class UnitOccupancy extends CrudAppPlace {
        }

    }

    public static class Tenants extends AppPlace {

        @PlaceProperties(navigLabel = "Tenants")
        public static class Tenant extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Guarantors")
        public static class Guarantor extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Tenants On Lease")
        public static class TenantInLease extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Leases")
        public static class Lease extends CrudAppPlace {
        }

        public static class LeaseTerm extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Maintenance Requests")
        public static class MaintenanceRequest extends CrudAppPlace {
        }

    }

    @PlaceProperties(navigLabel = "Alerts")
    public static class Alert extends AppPlace {
    }

    @PlaceProperties(navigLabel = "Messages")
    public static class Message extends AppPlace {
    }

    @PlaceProperties(navigLabel = "Runtime Errors")
    public static class RuntimeError extends AppPlace {
    }

    public static class ApplicationSelection extends AppPlace {
    }

    public static class Search extends AppPlace {
    }

    public static class AlertViewer extends AppPlace {
    }

}
