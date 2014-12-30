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
package com.propertyvista.crm.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.DevConsoleView;
import com.propertyvista.crm.client.ui.DevConsoleView.DevConsolePresenter;

public class DevConsoleActivity extends AbstractActivity implements DevConsolePresenter {

    private final DevConsoleView view;

    public DevConsoleActivity() {
        view = CrmSite.getViewFactory().getView(DevConsoleView.class);
        //view.setPresenter(this);
    }

    public DevConsoleActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

}
