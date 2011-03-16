/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-12
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.PtAppWizardManager;
import com.propertyvista.portal.client.ptapp.ui.SecondNavigView;
import com.propertyvista.portal.domain.pt.ApplicationWizardStep;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

public class SecondNavigActivity extends AbstractActivity implements SecondNavigView.SecondNavigPresenter {

    public final static String STEP_ARG_NAME = "substep";

    private final SecondNavigView view;

    @Inject
    public SecondNavigActivity(SecondNavigView view) {
        this.view = view;
        view.setPresenter(this);
    }

    public SecondNavigActivity withPlace(Place place) {
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
    public ApplicationWizardStep getWizardStep() {
        ApplicationWizardStep wizardStep = null;
        String placeId = AppSite.getHistoryMapper().getPlaceId(getWhere());
        if (placeId != null) {
            for (ApplicationWizardStep step : PtAppWizardManager.instance().getApplicationProgress().steps()) {
                if (placeId.equals(step.placeId().getValue())) {
                    wizardStep = step;
                    break;
                }
            }
        }
        return wizardStep;
    }

    @Override
    public AppPlace getWhere() {
        return (AppPlace) AppSite.getPlaceController().getWhere();
    }

}
