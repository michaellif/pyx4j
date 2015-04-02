/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 24, 2012
 * @author ArtyomB
 */
package com.propertyvista.portal.prospect.activity;

import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.tenant.ProspectData;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.prospect.ProspectPortalSite;
import com.propertyvista.portal.prospect.ui.ApplicationContextSelectionView;
import com.propertyvista.portal.prospect.ui.ApplicationContextSelectionView.ApplicationContextSelectionPresenter;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationContextChoiceDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationContextSelectionService;

public class ApplicationContextSelectionActivity extends AbstractActivity implements ApplicationContextSelectionPresenter {

    private final ApplicationContextSelectionView view;

    private final ApplicationContextSelectionService service;

    public ApplicationContextSelectionActivity() {
        this.service = GWT.<ApplicationContextSelectionService> create(ApplicationContextSelectionService.class);
        this.view = ProspectPortalSite.getViewFactory().getView(ApplicationContextSelectionView.class);
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        populate();
    }

    public void populate() {
        service.getApplicationContextChoices(new DefaultAsyncCallback<Vector<OnlineApplicationContextChoiceDTO>>() {
            @Override
            public void onSuccess(Vector<OnlineApplicationContextChoiceDTO> result) {
                view.populate(result);
            }
        });
    }

    @Override
    public void setApplicationContext(OnlineApplication onlineApplication) {
        if (onlineApplication != null) {
            service.setApplicationContext(new DefaultAsyncCallback<VoidSerializable>() {
                @Override
                public void onSuccess(VoidSerializable result) {
                    AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
                }
            }, onlineApplication);
        } else {
            throw new Error("Application wasn't selected");
        }

    }

    @Override
    public void createNewApplication() {
        ProspectData data = EntityFactory.create(ProspectData.class);

        data.ilsBuildingId().setValue(Window.Location.getParameter(ProspectPortalSiteMap.ARG_ILS_BUILDING_ID));
        data.ilsFloorplanId().setValue(Window.Location.getParameter(ProspectPortalSiteMap.ARG_ILS_FLOORPLAN_ID));
        data.ilsUnitId().setValue(Window.Location.getParameter(ProspectPortalSiteMap.ARG_ILS_UNIT_ID));

        service.createNewApplication(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
            }
        }, data);
    }
}
