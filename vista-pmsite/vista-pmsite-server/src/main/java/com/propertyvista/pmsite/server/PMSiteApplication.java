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

import js.JSResources;

import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;

import com.propertyvista.pmsite.server.model.SearchCriteriaModel;
import com.propertyvista.pmsite.server.pages.AptDetailsPage;
import com.propertyvista.pmsite.server.pages.AptListPage;
import com.propertyvista.pmsite.server.pages.FindAptPage;
import com.propertyvista.pmsite.server.pages.LandingPage;
import com.propertyvista.pmsite.server.pages.ResidentsPage;
import com.propertyvista.pmsite.server.pages.StaticPage;

public class PMSiteApplication extends WebApplication {

    private SearchCriteriaModel searchModel;

    @Override
    public Class<? extends Page> getHomePage() {
        return LandingPage.class;
    }

    @Override
    protected void init() {
        mountBookmarkablePage("findapt", FindAptPage.class);
        mount(new QueryStringUrlCodingStrategy("aptlist", AptListPage.class));
        mount(new QueryStringUrlCodingStrategy("aptinfo", AptDetailsPage.class));

        mountBookmarkablePage("residents", ResidentsPage.class);

        mount(new MixedParamUrlCodingStrategy("cnt", StaticPage.class, PMSiteContentManager.PARAMETER_NAMES));

        // add js "virtual" folder
        getSharedResources().putClassAlias(JSResources.class, "js");

        // create search model
        this.searchModel = new SearchCriteriaModel();
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new PMSiteSession(request);
    }

    public static PMSiteApplication get() {
        return (PMSiteApplication) WebApplication.get();
    }

    public SearchCriteriaModel getSearchModel() {
        return searchModel;
    }
}
