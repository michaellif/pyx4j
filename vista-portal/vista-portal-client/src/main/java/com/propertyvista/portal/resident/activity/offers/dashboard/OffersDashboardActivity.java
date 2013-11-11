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
package com.propertyvista.portal.resident.activity.offers.dashboard;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.offers.dashboard.OffersDashboardView;
import com.propertyvista.portal.resident.ui.offers.dashboard.OffersDashboardView.OffersDashboardPresenter;
import com.propertyvista.portal.shared.activity.SecurityAwareActivity;

public class OffersDashboardActivity extends SecurityAwareActivity implements OffersDashboardPresenter {

    private final OffersDashboardView view = ResidentPortalSite.getViewFactory().getView(OffersDashboardView.class);

    public OffersDashboardActivity(Place place) {
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
        view.setPresenter(this);

    }

}
