/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 28, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.NavigSettingsView;
import com.propertyvista.crm.client.ui.NavigSettingsView.NavigSettingsPresenter;

public class NavigSettingsActivity extends AbstractActivity implements NavigSettingsPresenter {

    private final NavigSettingsView view;

    private Place place;

    public NavigSettingsActivity() {
        view = CrmSite.getViewFactory().getView(NavigSettingsView.class);
    }

    public void withPlace(Place place) {
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        if (place instanceof AppPlace) {
            view.select((AppPlace) place);
        }
    }

}
