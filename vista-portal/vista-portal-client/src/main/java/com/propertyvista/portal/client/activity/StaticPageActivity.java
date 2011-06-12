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
import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.ui.StaticPageView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.domain.site.PageContent;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Landing;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Page;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

public class StaticPageActivity extends AbstractActivity implements StaticPageView.Presenter {

    private final StaticPageView view;

    private String path;

    public StaticPageActivity(Place place) {
        this.view = (StaticPageView) PortalViewFactory.instance(StaticPageView.class);
        withPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);

        panel.setWidget(view);
        PortalSite.getPortalSiteServices().retrieveStaticContent(new DefaultAsyncCallback<PageContent>() {
            @Override
            public void onSuccess(PageContent content) {
                if (content != null && !content.isNull()) {
                    view.setContent(content.content().getStringView());
                }
            }
        }, path);
    }

    public StaticPageActivity withPlace(Place place) {
        if (place instanceof Page) {
            path = PageContent.PATH_SEPARATOR + ((AppPlace) place).getArg(PortalSiteMap.ARG_PAGE_ID);
        } else if (place instanceof Landing) {
            path = PageContent.PATH_SEPARATOR;
        } else {
            path = AppSite.getHistoryMapper().getPlaceId(place);
        }
        return this;
    }

}
