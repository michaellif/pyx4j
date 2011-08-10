/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity;

import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.tenant.ptapp.ApplicationWizardStep;
import com.propertyvista.portal.ptapp.client.PtAppSite;
import com.propertyvista.portal.ptapp.client.ui.MainNavigView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.PtAppViewFactory;

public class MainNavigActivity extends AbstractActivity implements MainNavigView.MainNavigPresenter {

    private final MainNavigView view;

    public MainNavigActivity(Place place) {
        view = (MainNavigView) PtAppViewFactory.instance(MainNavigView.class);
        assert (view != null);
        view.setPresenter(this);
        withPlace(place);
    }

    public MainNavigActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void navigTo(Place place) {
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public String getNavigLabel(AppPlace place) {
        return AppSite.getHistoryMapper().getPlaceInfo(place).getNavigLabel();
    }

    @Override
    public List<ApplicationWizardStep> getWizardSteps() {
        return PtAppSite.getWizardManager().getApplicationWizardSteps();
    }

    @Override
    public Place getWhere() {
        return AppSite.getPlaceController().getWhere();
    }

}
