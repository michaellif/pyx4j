/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity.seps.welcomewizard;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.completion.CompletionView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.WizardStepsViewFactory;

public class CompletionActivity extends AbstractActivity implements CompletionView.Presenter {

    private final CompletionView view;

    public CompletionActivity(AppPlace place) {
        this.view = WizardStepsViewFactory.instance(CompletionView.class);
        withPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    public Activity withPlace(AppPlace place) {
        return this;
    }

}
