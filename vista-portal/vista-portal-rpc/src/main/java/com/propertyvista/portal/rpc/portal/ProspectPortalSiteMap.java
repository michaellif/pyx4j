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
import com.pyx4j.site.rpc.annotations.PlaceProperties;
import com.pyx4j.site.shared.meta.PublicPlace;

public class ProspectPortalSiteMap extends PortalSiteMap {

    public static class TermsAndConditions extends AppPlace implements PublicPlace {
    }

    @PlaceProperties(caption = "Prospect Registration")
    public static class Registration extends AppPlace implements PublicPlace {
    }

    public static class Status extends AppPlace {
    }

    public static class Application extends AppPlace {

        public static class UnitStep extends AppPlace implements Step {
        }

        public static class OptionsStep extends AppPlace implements Step {
        }

        public static class PersonalInfoAStep extends AppPlace implements Step {
        }

        public static class PersonalInfoBStep extends AppPlace implements Step {
        }

        public static class FinancialStep extends AppPlace implements Step {
        }

        public static class PeopleStep extends AppPlace implements Step {
        }

        public static class ContactsStep extends AppPlace implements Step {
        }

        public static class PmcCustomStep extends AppPlace implements Step {
        }

        public static class SummaryStep extends AppPlace implements Step {
        }

        public static class PaymentStep extends AppPlace implements Step {
        }

    }

}
