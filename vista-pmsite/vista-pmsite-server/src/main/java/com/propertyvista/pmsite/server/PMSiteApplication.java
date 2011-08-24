/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 22, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.pmsite.server;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

import com.propertyvista.pmsite.server.pages.FindApartmentPage;
import com.propertyvista.pmsite.server.pages.LandingPage;
import com.propertyvista.pmsite.server.pages.ResidentsPage;
import com.propertyvista.pmsite.server.pages.StaticPage;

public class PMSiteApplication extends WebApplication {

    @Override
    public Class<? extends Page> getHomePage() {
        return LandingPage.class;
    }

    @Override
    protected void init() {
        mountBookmarkablePage("findapt", FindApartmentPage.class);
        mountBookmarkablePage("residents", ResidentsPage.class);
        mountBookmarkablePage("page", StaticPage.class);
    }

}
