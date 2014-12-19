/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2011
 * @author vadims
 */
package com.propertyvista.portal.shared.activity.communityevent;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.property.asset.CommunityEvent;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.shared.services.communityevent.CommunityEventCrudService;
import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.ui.communityevent.CommunityEventPageView;
import com.propertyvista.portal.shared.ui.communityevent.CommunityEventPageView.CommunityEventPagePresenter;

public class CommunityEventPageActivity extends AbstractActivity implements CommunityEventPagePresenter {

    private final CommunityEventPageView view;

    private final Key entityId;

    private final CommunityEventCrudService communityEventService = (CommunityEventCrudService) GWT.create(CommunityEventCrudService.class);

    public CommunityEventPageActivity(AppPlace place) {
        view = PortalSite.getViewFactory().getView(CommunityEventPageView.class);
        this.entityId = place.getItemId();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setPresenter(this);

        assert (entityId != null);

        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));

        communityEventService.retrieve(new DefaultAsyncCallback<CommunityEvent>() {

            @Override
            public void onSuccess(CommunityEvent result) {
                view.populate(result);
                AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));

            }
        }, entityId, RetrieveTarget.View);
    }

    @Override
    public void showEvent(Key eventId) {
        AppSite.getPlaceController().goTo(new PortalSiteMap.CommunityEvent().formPlace(eventId));
    }

    @Override
    public void edit() {
        // TODO Auto-generated method stub

    }

    @Override
    public void save() {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub

    }

}
