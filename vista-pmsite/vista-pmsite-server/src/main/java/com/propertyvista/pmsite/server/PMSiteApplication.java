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

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.time.Duration;

import com.pyx4j.config.shared.ApplicationMode;

import com.propertyvista.pmsite.server.pages.AptDetailsPage;
import com.propertyvista.pmsite.server.pages.AptListPage;
import com.propertyvista.pmsite.server.pages.FindAptPage;
import com.propertyvista.pmsite.server.pages.LandingPage;
import com.propertyvista.pmsite.server.pages.ResidentsPage;

public class PMSiteApplication extends WebApplication {

    @Override
    protected void init() {

        super.init();

        if (ApplicationMode.isDevelopment()) {
            getResourceSettings().setResourcePollFrequency(Duration.ONE_SECOND);
        }
//        getPageSettings().addComponentResolver(new I18nMessageResolver());

        mountPage("findapt", FindAptPage.class);

        //TODO        mount(new QueryStringUrlCodingStrategy("aptlist", AptListPage.class));
        mountPage("aptlist", AptListPage.class);

        //TODO        mount(new QueryStringUrlCodingStrategy("aptinfo", AptDetailsPage.class));
        mountPage("aptinfo", AptDetailsPage.class);

        mountPage("residents", ResidentsPage.class);

//TODO        mount(new MixedParamUrlCodingStrategy("cnt", StaticPage.class, PMSiteContentManager.PARAMETER_NAMES));
        mountPage("cnt", FindAptPage.class);

        // add js "virtual" folder
//TODO        getSharedResources().putClassAlias(JSResources.class, "js");

    }

    @Override
    public Class<LandingPage> getHomePage() {
        return LandingPage.class;
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new PMSiteSession(request);
    }

    public static PMSiteApplication get() {
        return (PMSiteApplication) WebApplication.get();
    }

}
