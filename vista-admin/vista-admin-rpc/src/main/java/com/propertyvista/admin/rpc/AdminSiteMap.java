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
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.PublicPlace;
import com.pyx4j.site.shared.meta.SigningOutPlace;
import com.pyx4j.site.shared.meta.SiteMap;

public class AdminSiteMap implements SiteMap {

    public static class Login extends AppPlace implements PublicPlace {
    }

    /**
     * Used while user is waiting for authentication required for password reset.
     */
    public static class LoginWithToken extends AppPlace implements PublicPlace {

    }

    public static class SigningOut extends AppPlace implements SigningOutPlace {
    }

    public static class PasswordResetRequest extends AppPlace implements PublicPlace {

    }

    @PlaceProperties(caption = "Reset Password")
    public static class PasswordReset extends AppPlace {
    }

    @PlaceProperties(caption = "Change Password")
    public static class PasswordChange extends AppPlace {
    }

    public static class Management extends AppPlace {
        @PlaceProperties(navigLabel = "Property Management Companies (PMCs)")
        public static class PMC extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Onboarding Users")
        public static class OnboardingUser extends CrudAppPlace {
        }

        public static class OnboardingMerchantAccounts extends CrudAppPlace {

        }

        @PlaceProperties(navigLabel = "Triggers")
        public static class Trigger extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "Runs")
        public static class Run extends CrudAppPlace {
        }

        public static class BillingSetup extends CrudAppPlace {
            // set default place type as Viewer one (we have no lister for this item!)
            public BillingSetup() {
                formViewerPlace(new Key(-1));
            }
        }
    }

    public static class Security extends AppPlace {

        @PlaceProperties(navigLabel = "Audit Records")
        public static class AuditRecord extends CrudAppPlace {

        }
    }

    public static class Legal extends AppPlace {

        @PlaceProperties(navigLabel = "Tenant Portal Terms and Conditions", caption = "Tenant Portal Terms and Conditions")
        public static class PortalTerms extends CrudAppPlace {
        }

        @PlaceProperties(navigLabel = "PMC Terms and Conditions", caption = "PMC Terms and Conditions")
        public static class PmcTerms extends CrudAppPlace {
        }
    }

    public static class Administration extends AppPlace {

        @PlaceProperties(navigLabel = "System Maintenance", caption = "System Maintenance")
        public static class Maintenance extends CrudAppPlace {
            // set default place type as Viewer one (we have no lister for this item!)
            public Maintenance() {
                formViewerPlace(new Key(-1));
            }
        }

        @PlaceProperties(navigLabel = "Simulations")
        public static class Simulation extends CrudAppPlace {

            public Simulation() {
                formViewerPlace(new Key(-1));
            }
        }

        public static class SimulatedDataPreload extends CrudAppPlace {

            public SimulatedDataPreload() {
                formViewerPlace(new Key(-1));
            }

        }

        public static class PadSimulation extends CrudAppPlace {

            @PlaceProperties(navigLabel = "Simulated PAD", caption = "Simulated PAD")
            public static class PadSimFile extends CrudAppPlace {
            }

            @PlaceProperties(navigLabel = "PAD Batches", caption = "PAD Batch")
            public static class PadSimBatch extends CrudAppPlace {
            }
        }

        @PlaceProperties(navigLabel = "Users", caption = "User")
        public static class AdminUsers extends CrudAppPlace {
        }
    }

    @PlaceProperties(navigLabel = "Alerts")
    public static class Alert extends AppPlace {
    }

    @PlaceProperties(navigLabel = "Messages")
    public static class Message extends AppPlace {
    }

    public static class Settings extends AppPlace {
    }

    @PlaceProperties(navigLabel = "Accounts")
    public static class Account extends CrudAppPlace {
    }
}
