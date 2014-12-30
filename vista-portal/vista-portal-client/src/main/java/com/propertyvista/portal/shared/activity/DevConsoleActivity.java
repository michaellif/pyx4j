/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 30, 2014
 * @author vlads
 */
package com.propertyvista.portal.shared.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ContextInitializeEvent;
import com.pyx4j.security.client.ContextInitializeHandler;

import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.ui.DevConsoleView;
import com.propertyvista.shared.rpc.DevConsoleDataTO;
import com.propertyvista.shared.services.dev.DevConsoleService;

public class DevConsoleActivity extends AbstractActivity implements DevConsoleView.DevConsolePresenter {

    private final DevConsoleView view;

    private static final DevConsoleActivity instance = new DevConsoleActivity();

    public static DevConsoleActivity instance() {
        return instance;
    }

    private DevConsoleActivity() {
        view = PortalSite.getViewFactory().getView(DevConsoleView.class);
    }

    public DevConsoleActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        updateView();
        eventBus.addHandler(ContextInitializeEvent.getType(), new ContextInitializeHandler() {

            @Override
            public void onContextInitialize(ContextInitializeEvent event) {
                updateView();
            }
        });
    }

    private void updateView() {
        GWT.<DevConsoleService> create(DevConsoleService.class).obtainData(new DefaultAsyncCallback<DevConsoleDataTO>() {

            @Override
            public void onSuccess(DevConsoleDataTO result) {
                view.setData(result);
            }
        });
    }
}
