/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 19, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.propertyvista.portal.client.ui.BillingHistoryView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;

public class BillingHistoryActivity extends AbstractActivity {
    private final BillingHistoryView view;

    public BillingHistoryActivity(Place place) {
        this.view = (BillingHistoryView) PortalViewFactory.instance(BillingHistoryView.class);
        withPlace(place);
    }

    public BillingHistoryActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);

    }

}
