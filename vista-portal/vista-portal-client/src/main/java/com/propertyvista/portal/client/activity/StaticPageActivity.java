/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 22, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.ui.StaticPageView;
import com.propertyvista.portal.domain.site.PageContent;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class StaticPageActivity extends AbstractActivity implements StaticPageView.Presenter {

    private final StaticPageView view;

    private String pageId;

    @Inject
    public StaticPageActivity(StaticPageView view) {
        this.view = view;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);

        panel.setWidget(view);
        PortalSite.getPortalSiteServices().retrieveStaticContent(new DefaultAsyncCallback<PageContent>() {
            @Override
            public void onSuccess(PageContent content) {
                view.setContent(content.content().getStringView());
            }
        }, pageId);
    }

    public StaticPageActivity withPlace(Place place) {
        pageId = ((AppPlace) place).getArgs().get(PortalSiteMap.ARG_PAGE_ID);
        return this;
    }

}
