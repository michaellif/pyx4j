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

import com.pyx4j.commons.Key;
import com.pyx4j.site.rpc.AppPlace;
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
    }

    /** This is the place when user must enter credentials and pass the humand verification to send the password request */
    @PlaceProperties(caption = "Reset Password")
    public static class PasswordResetRequest extends AppPlace implements PublicPlace {
    }

    /** This is the place wher user gets redirected to change lost/forgotten password via email (from LoginWithToken) */
    @PlaceProperties(caption = "Reset Password")
    public static class PasswordReset extends AppPlace implements PublicPlace {
    }

    @PlaceProperties(caption = "Resident Registration")
    public static class Registration extends AppPlace implements PublicPlace {
    }

    public static class PortalTermsAndConditions extends AppPlace implements PublicPlace {
    }

    @PlaceProperties(navigLabel = "Select Lease", caption = "Select Lease")
    public static class LeaseContextSelection extends AppPlace {
    }

    @PlaceProperties(caption = "Change Password")
    public static class PasswordChange extends AppPlace {
    }

    public static class PotentialTenants extends AppPlace {
    }

    @PlaceProperties(navigLabel = "", caption = "")
    public static class NotificationPlace extends AppPlace implements PublicPlace {

    }

    @PlaceProperties(navigLabel = "Dashboard", caption = "Dashboard")
    public static class Resident extends AppPlace {

        @PlaceProperties(navigLabel = "My Profile")
        public static class ProfileViewer extends AppPlace {
        }

        public static class ProfileEditor extends AppPlace {
        }

        @PlaceProperties(navigLabel = "My Account")
        public static class Account extends AppPlace {
        }

        public static class Financial extends AppPlace {

            // Billing* are relevant for users who's financials managed by PV */
            public static class BillSummary extends AppPlace {
            }

            public static class BillingHistory extends AppPlace {

                public static class ViewBill extends AppPlace {
                }
            }

            // this one is for tenants from Yardi integrated accounts
            public static class FinancialSummary extends AppPlace {

            }

            public static class PayNow extends AppPlace {
            }

            public static class PaymentSubmitted extends AppPlace {

                public PaymentSubmitted() {
                }

                public PaymentSubmitted(Key paymentRecordID) {
                    formPlace(paymentRecordID);
                }
            }

            @PlaceProperties(caption = "Pre-Authorized Payments", navigLabel = "Auto Pay")
            public static class PreauthorizedPayments extends AppPlace {

                @PlaceProperties(caption = "New Pre-Authorized Payment")
                public static class NewPreauthorizedPayment extends AppPlace {
                }

                @PlaceProperties(caption = "Pre-Authorized Payment Submitted")
                public static class PreauthorizedPaymentSubmitted extends AppPlace {

                    public PreauthorizedPaymentSubmitted() {
                    }

                    public PreauthorizedPaymentSubmitted(Key preauthorizedPaymentID) {
                        formPlace(preauthorizedPaymentID);
                    }
                }
            }
        }

        public static class PaymentMethods extends AppPlace {

            public static class NewPaymentMethod extends AppPlace {
            }

            public static class EditPaymentMethod extends AppPlace {
            }

            public static class ViewPaymentMethod extends AppPlace {
            }

            public static class PaymentMethodSubmitted extends AppPlace {

                public PaymentMethodSubmitted() {
                }

                public PaymentMethodSubmitted(Key preauthorizedPaymentID) {
                    formPlace(preauthorizedPaymentID);
                }
            }
        }

        public static class Maintenance extends AppPlace {

            public static class NewMaintenanceRequest extends AppPlace {
            }

            public static class ViewMaintenanceRequest extends AppPlace {
            }

            public static class EditMaintenanceRequest extends AppPlace {
            }
        }

        @PlaceProperties(navigLabel = "Comm Center", caption = "Communication Center")
        public static class CommunicationCenter extends AppPlace {
        }

        @PlaceProperties(navigLabel = "My Perks & Offers")
        public static class Offers extends AppPlace {
        }

        public static class TenantInsurance extends AppPlace {

            /** this place is displayed to people who don't have tenant insurance */
            public static class ProvideTenantInsurance extends AppPlace {

            }

            public static class TenantSure {

                @PlaceProperties(caption = "Tenant Sure Contact Info")
                public static class About extends AppPlace implements PublicPlace {

                }

                @PlaceProperties(caption = "TenantSure FAQ")
                public static class Faq extends AppPlace {

                }

                @PlaceProperties(navigLabel = "TenantSure Management", caption = "TenantSure Management")
                public static class Management extends AppPlace {

                    @PlaceProperties(caption = "Update Credit Card")
                    public static class UpdateCreditCard extends AppPlace {

                    }

                }

                @PlaceProperties(navigLabel = "Get TenantSure", caption = "Get TenantSure")
                public static class TenantSurePurchase extends AppPlace {

                }

            }

            public static class Other {

                /** This place is for updating insurance other than TenantSure */
                public static class UploadCertificate extends AppPlace {

                }

            }

            @PlaceProperties(caption = "Tenant Insurance")
            public static class CoveredByOtherTenant extends AppPlace {

            }
        }
    }
}
