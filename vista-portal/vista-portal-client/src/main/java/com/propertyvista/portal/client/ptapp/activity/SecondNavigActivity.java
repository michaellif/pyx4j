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

import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.PtAppWizardManager;
import com.propertyvista.portal.client.ptapp.ui.SecondNavigView;
import com.propertyvista.portal.domain.pt.ApplicationWizardStep;
import com.propertyvista.portal.domain.pt.ApplicationWizardSubstep;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

public class SecondNavigActivity extends AbstractActivity implements SecondNavigView.SecondNavigPresenter {

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
        AppSite.instance().getPlaceController().goTo(place);
    }

    @Override
    public String getNavigLabel(AppPlace place) {
        return AppSite.instance().getHistoryMapper().getPlaceInfo(place).getNavigLabel();
    }

    @Override
    public List<ApplicationWizardSubstep> getWizardSubsteps() {
        String token = AppSite.instance().getHistoryMapper().getToken(getWhere());
        if (token == null) {
            return null;
        }
        for (ApplicationWizardStep step : PtAppWizardManager.instance().getApplicationProgress().steps()) {
            if (token.equals(step.placeToken().getValue())) {
                return step.substeps();
            }
        }
        return null;
    }

    @Override
    public Place getWhere() {
        return AppSite.instance().getPlaceController().getWhere();
    }

}
