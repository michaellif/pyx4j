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
package com.propertyvista.portal.tester;

import com.pyx4j.site.rpc.AppPlace;

public class SiteMap {

    public static class Landing extends AppPlace {
    }

    public static class Home extends AppPlace {

        public static class Products extends AppPlace {

        }

        public static class Services extends AppPlace {

        }

    }

    public static class ContactUs extends AppPlace {
    }

    public static class AboutUs extends AppPlace {
    }

    //TODO: antonk: various annotations for generator
    //    @DoNotShowOnNavig
    //    @StaticPage()
    public static class TopRightActions extends AppPlace {
    }

    public static class SignUp extends AppPlace {
    }

    public static class SignUpResult extends AppPlace {
    }
}
