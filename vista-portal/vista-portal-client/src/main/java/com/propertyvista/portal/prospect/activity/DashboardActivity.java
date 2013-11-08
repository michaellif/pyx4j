/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.prospect.activity;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.propertyvista.portal.prospect.ui.DashboardView;
import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.activity.SecurityAwareActivity;

public class DashboardActivity extends SecurityAwareActivity implements DashboardView.DashboardPresenter {

    private final DashboardView view;

    public DashboardActivity(Place place) {
        this.view = PortalSite.getViewFactory().instantiate(DashboardView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
        view.setPresenter(this);

        populate();
    }

    private void populate() {

    }

}
