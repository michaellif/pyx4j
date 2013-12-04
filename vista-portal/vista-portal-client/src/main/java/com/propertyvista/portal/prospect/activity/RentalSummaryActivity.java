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
 * @version $Id$
 */
package com.propertyvista.portal.prospect.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent.ChangeType;

import com.propertyvista.portal.prospect.ProspectPortalSite;
import com.propertyvista.portal.prospect.events.ApplicationWizardStateChangeEvent;
import com.propertyvista.portal.prospect.events.ApplicationWizardStateChangeHandler;
import com.propertyvista.portal.prospect.ui.RentalSummaryView;
import com.propertyvista.portal.prospect.ui.RentalSummaryView.RentalSummaryPresenter;

public class RentalSummaryActivity extends AbstractActivity implements RentalSummaryPresenter {

    private final RentalSummaryView view;

    public RentalSummaryActivity(Place place) {
        view = ProspectPortalSite.getViewFactory().getView(RentalSummaryView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
        eventBus.addHandler(ApplicationWizardStateChangeEvent.getType(), new ApplicationWizardStateChangeHandler() {
            @Override
            public void onStateChange(ApplicationWizardStateChangeEvent event) {
                if (event.getChangeType() == ApplicationWizardStateChangeEvent.ChangeType.termChange) {
                    view.populate(event.getApplicationWizard().getValue());
                }
            }
        });

    }
}
