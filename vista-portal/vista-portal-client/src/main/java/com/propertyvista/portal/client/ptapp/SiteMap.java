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

import com.pyx4j.site.client.place.AppPlace;

//TODO Use annotations and generator instead of constructor
public class SiteMap {

    public static class SignIn extends AppPlace {
        public SignIn() {
            super("", "Application Form");
        }
    }

    public static class Apartment extends AppPlace {
        public Apartment() {
            super("Apartment", "Apartment Info");
        }
    }

    public static class Tenants extends AppPlace {
        public Tenants() {
            super("Tenants", "Tenants");
        }
    }

    public static class Info extends AppPlace {
        public Info() {
            super("Info", "Info");
        }
    }

    public static class Financial extends AppPlace {
        public Financial() {
            super("Financial", "Financial");
        }
    }

    public static class Pets extends AppPlace {
        public Pets() {
            super("Pets", "Pets");
        }
    }

    public static class Payments extends AppPlace {
        public Payments() {
            super("Payments", "Payments");
        }
    }

    public static class Summary extends AppPlace {
        public Summary() {
            super("Summary", "Summary");
        }
    }

}
