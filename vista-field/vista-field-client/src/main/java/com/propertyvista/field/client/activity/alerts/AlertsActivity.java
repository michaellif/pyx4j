/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.field.client.activity.alerts;

import java.util.Arrays;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.field.client.event.CheckAlertsEvent;
import com.propertyvista.field.client.event.CheckAlertsHandler;
import com.propertyvista.field.client.event.ShowAlertsEvent;
import com.propertyvista.field.client.ui.components.alerts.AlertsInfoView;
import com.propertyvista.field.client.ui.components.alerts.AlertsScreenView;
import com.propertyvista.field.client.ui.viewfactories.FieldViewFactory;

public class AlertsActivity extends AbstractActivity implements CheckAlertsHandler {

    private static class SingletonHolder {
        public static final AlertsActivity INSTANCE = new AlertsActivity();
    }

    public static AlertsActivity instance() {
        return SingletonHolder.INSTANCE;
    }

    private final AlertsScreenView alertsScreen;

    private final AlertsInfoView alertsInfo;

    public AlertsActivity() {
        alertsScreen = FieldViewFactory.instance(AlertsScreenView.class);
        alertsInfo = FieldViewFactory.instance(AlertsInfoView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
    }

    @Override
    public void onCheckAlerts(CheckAlertsEvent event) {
        // TODO add logic to fetch alerts
        String[] alerts = { "Alert #1", "Alert #2", "Alert #3", "Alert #4" };
        alertsScreen.setAlerts(Arrays.asList(alerts));
        alertsInfo.setUnread(alerts.length);
        AppSite.getEventBus().fireEvent(new ShowAlertsEvent());
    }

}
