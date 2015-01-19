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
package com.propertyvista.portal.shared.activity.communication;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.security.client.ClientContext;

import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.ui.communication.CommunicationAlertView;

public class CommunicationAlertActivity extends AbstractActivity implements CommunicationAlertView.CommunicationPresenter {

    private final CommunicationAlertView view;

    public CommunicationAlertActivity(Place place) {
        view = PortalSite.getViewFactory().getView(CommunicationAlertView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {

        if (ClientContext.getUserVisit() == null || ClientContext.getUserVisit().getPrincipalPrimaryKey() == null) {
            return;
        }
        panel.setWidget(view);
        view.populate(null);
    }
}
