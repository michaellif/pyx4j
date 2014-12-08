/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 23, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.property.asset.CommunityEvent;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.extra.CommunityEventsView;
import com.propertyvista.portal.resident.ui.extra.CommunityEventsView.CommunityEventsPresenter;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.shared.services.communityevent.CommunityEventCrudService;

public class CommunityEventsActivity extends AbstractActivity implements CommunityEventsPresenter {

    private final CommunityEventsView view;

    private final CommunityEventCrudService communityEventService = (CommunityEventCrudService) GWT.create(CommunityEventCrudService.class);

    public CommunityEventsActivity(Place place) {
        view = ResidentPortalSite.getViewFactory().getView(CommunityEventsView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setPresenter(this);
        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));

        communityEventService.list(new DefaultAsyncCallback<EntitySearchResult<CommunityEvent>>() {

            @Override
            public void onSuccess(EntitySearchResult<CommunityEvent> result) {
                view.populateCommunityEvents(result == null || result.getData() == null ? null : result.getData());
                AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
            }
        }, EntityListCriteria.create(CommunityEvent.class));
    }

    @Override
    public void showEvent(Key eventId) {
        AppSite.getPlaceController().goTo(new PortalSiteMap.CommunityEvent().formPlace(eventId));
    }

}
