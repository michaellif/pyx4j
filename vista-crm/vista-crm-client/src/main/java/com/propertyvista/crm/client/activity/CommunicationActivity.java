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
 */
package com.propertyvista.crm.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.communication.CommunicationAlertView;

public class CommunicationActivity extends AbstractActivity implements CommunicationAlertView.CommunicationPresenter {

    private final CommunicationAlertView view;

    public CommunicationActivity(Place place) {
        view = CrmSite.getViewFactory().getView(CommunicationAlertView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {

        panel.setWidget(view);
        view.populate(null);
    }
}
