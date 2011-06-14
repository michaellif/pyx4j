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
package com.propertyvista.admin.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.propertyvista.admin.client.ui.SettingsView;
import com.propertyvista.admin.client.viewfactories.AdminVeiwFactory;

public class SettingsActivity extends AbstractActivity {

    private final SettingsView view;

    public SettingsActivity(Place place) {
        view = (SettingsView) AdminVeiwFactory.instance(SettingsView.class);
        assert (view != null);
    }

    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus) {
        container.setWidget(view);

    }

    public SettingsActivity withPlace(Place place) {
        return this;
    }

}
