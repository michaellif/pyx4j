/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.propertyvista.portal.client.ui.LeaseTermsView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;

public class LeaseTermsActivity extends AbstractActivity {

    private final LeaseTermsView view;

    public LeaseTermsActivity(Place place) {
        this.view = (LeaseTermsView) PortalViewFactory.instance(LeaseTermsView.class);
        withPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    public LeaseTermsActivity withPlace(Place place) {
        return this;
    }

}
