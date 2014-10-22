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

import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.extra.CommunityEventsView;
import com.propertyvista.portal.resident.ui.extra.CommunityEventsView.CommunityEventsPresenter;
import com.propertyvista.portal.rpc.portal.resident.dto.CommunityEventsGadgetDTO;
import com.propertyvista.portal.rpc.portal.resident.services.CommunityEventPortalCrudService;

public class CommunityEventsActivity extends AbstractActivity implements CommunityEventsPresenter {

    private final CommunityEventsView view;

    private final CommunityEventPortalCrudService communityEventService = (CommunityEventPortalCrudService) GWT.create(CommunityEventPortalCrudService.class);

    public CommunityEventsActivity(Place place) {
        view = ResidentPortalSite.getViewFactory().getView(CommunityEventsView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));

        communityEventService.retreiveCommunityEvents(new DefaultAsyncCallback<CommunityEventsGadgetDTO>() {

            @Override
            public void onSuccess(CommunityEventsGadgetDTO result) {
                view.populateCommunityEvents(result);
                AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
            }
        });
    }

}
