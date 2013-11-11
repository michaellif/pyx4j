/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.crm.client.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.CrmRootPane;
import com.propertyvista.crm.client.ui.NotificationsView;

public class NotificationsActivity extends AbstractActivity implements NotificationsView.Presenter {

    private final NotificationsView view;

    public NotificationsActivity(Place place) {
        view = CrmSite.getViewFactory().getView(NotificationsView.class);
        withPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus) {
        container.setWidget(view);

        List<String> notifList = new ArrayList<String>();
//        notifList.add("Message 1");
//        notifList.add("Message 2");

        view.showNotifications(notifList);
        CrmRootPane rootPane = (CrmRootPane) AppSite.instance().getRootPane();
        rootPane.allocateNotificationsSpace(notifList.size());
    }

    public NotificationsActivity withPlace(Place place) {
        return this;
    }

}
