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
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.WizardStep;
import com.propertyvista.portal.client.ptapp.WizardStep.Status;
import com.propertyvista.portal.client.ptapp.ui.MainNavigView;
import com.propertyvista.portal.rpc.pt.SiteMap;

import com.pyx4j.site.client.place.AppPlaceListing;
import com.pyx4j.site.rpc.AppPlace;

public class MainNavigActivity extends AbstractActivity implements MainNavigView.Presenter {

    private final MainNavigView view;

    private final PlaceController placeController;

    private final AppPlaceListing appPlaceListing;

    @Inject
    public MainNavigActivity(MainNavigView view, PlaceController placeController, AppPlaceListing appPlaceListing) {
        this.view = view;
        this.placeController = placeController;
        this.appPlaceListing = appPlaceListing;
        view.setPresenter(this);
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
        placeController.goTo(place);
    }

    @Override
    public String getNavigLabel(AppPlace place) {
        return appPlaceListing.getPlaceInfo(place).getNavigLabel();
    }

    @Override
    public List<WizardStep> getWizardSteps() {
        List<WizardStep> steps = new ArrayList<WizardStep>();

        // TODO call PtAppWizardManager

        steps.add(new WizardStep(new SiteMap.Apartment(), WizardStep.Status.notVisited));
        steps.add(new WizardStep(new SiteMap.Tenants(), WizardStep.Status.notVisited));
        steps.add(new WizardStep(new SiteMap.Info(), WizardStep.Status.notVisited));
        steps.add(new WizardStep(new SiteMap.Financial(), WizardStep.Status.notVisited));
        steps.add(new WizardStep(new SiteMap.Pets(), WizardStep.Status.notVisited));
        steps.add(new WizardStep(new SiteMap.Charges(), WizardStep.Status.notVisited));
        steps.add(new WizardStep(new SiteMap.Summary(), WizardStep.Status.notVisited));
        steps.add(new WizardStep(new SiteMap.Payment(), WizardStep.Status.notVisited));

        boolean visited = false;

        for (int i = steps.size() - 1; i >= 0; i--) {
            if (steps.get(i).getPlace().equals(placeController.getWhere())) {
                steps.get(i).setStatus(Status.current);
                visited = true;
            } else if (i % 2 == 0) {
                steps.get(i).setStatus(visited == true ? Status.complete : Status.notVisited);
            } else {
                steps.get(i).setStatus(visited == true ? Status.hasAlert : Status.notVisited);
            }
        }

        return steps;
    }
}
