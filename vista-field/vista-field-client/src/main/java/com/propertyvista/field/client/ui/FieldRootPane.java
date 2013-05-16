/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 6, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.field.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.RootPane;
import com.pyx4j.site.client.ui.layout.MobileLayoutPanel;

import com.propertyvista.field.client.FieldSite;
import com.propertyvista.field.client.activity.alerts.AlertsActivity;
import com.propertyvista.field.client.event.AlertsAction;
import com.propertyvista.field.client.event.ChangeAlertsEvent;
import com.propertyvista.field.client.event.ChangeAlertsHandler;
import com.propertyvista.field.client.event.ChangeLayoutEvent;
import com.propertyvista.field.client.event.ChangeLayoutHandler;
import com.propertyvista.field.client.mvp.DetailsActivityMapper;
import com.propertyvista.field.client.mvp.HeaderActivityMapper;
import com.propertyvista.field.client.mvp.ListerActivityMapper;
import com.propertyvista.field.client.mvp.ScreenActivityMapper;
import com.propertyvista.field.client.ui.components.alerts.AlertsInfoView;
import com.propertyvista.field.client.ui.components.alerts.AlertsScreenView;
import com.propertyvista.field.client.ui.components.menu.MenuScreenView;
import com.propertyvista.field.client.ui.viewfactories.FieldViewFactory;

public class FieldRootPane extends RootPane<MobileLayoutPanel> implements IsWidget, ChangeLayoutHandler, ChangeAlertsHandler {

    public FieldRootPane() {
        super(new MobileLayoutPanel());

        bind(new HeaderActivityMapper(), asWidget().getHeaderDisplay());
        bind(new ListerActivityMapper(), asWidget().getListerDisplay());
        bind(new DetailsActivityMapper(), asWidget().getDetailsDisplay());
        bind(new ScreenActivityMapper(), asWidget().getScreenDisplay());

        //set static content
        asWidget().getMenuDisplay().setWidget(FieldViewFactory.instance(MenuScreenView.class));
        asWidget().getAlertsDisplay().setWidget(FieldViewFactory.instance(AlertsScreenView.class));
        asWidget().getAlertsInfoDisplay().setWidget(FieldViewFactory.instance(AlertsInfoView.class));

        AppSite.getEventBus().addHandler(ChangeLayoutEvent.getType(), this);
        AppSite.getEventBus().addHandler(ChangeAlertsEvent.getType(), this);
        AppSite.getEventBus().addHandler(ChangeAlertsEvent.getType(), AlertsActivity.instance());
    }

    @Override
    protected void onPlaceChange(Place place) {
    }

    @Override
    public void onChangeLayout(ChangeLayoutEvent event) {
        switch (event.getAction()) {
        case ShowApplication:
            asWidget().showApplicationContent();
            asWidget().setPageOrientation(FieldSite.getPageOrientation());
            break;
        case ShiftMenu:
            asWidget().shiftMenu();
            break;
        case ShiftAlerts:
            asWidget().shiftAlerts();
            break;
        case SetListerLayout:
            asWidget().setListerLayout(true);
            break;
        case DiscardListerLayout:
            asWidget().setListerLayout(false);
            break;
        case SetListerLayoutAndShiftAlerts:
            asWidget().shiftAlerts();
            asWidget().setListerLayout(true);
            break;
        case DiscardListerLayoutAndShiftAlerts:
            asWidget().shiftAlerts();
            asWidget().setListerLayout(false);
            break;
        case ExpandDetails:
            asWidget().expandDetails(true);
            break;
        case CollapseDetails:
            asWidget().expandDetails(false);
            break;
        default:
            break;
        }
    }

    @Override
    public void onChangeAlerts(ChangeAlertsEvent event) {
        if (AlertsAction.ShowAlerts == event.getAction()) {
            asWidget().showAlerts(true);
        }
    }

}
