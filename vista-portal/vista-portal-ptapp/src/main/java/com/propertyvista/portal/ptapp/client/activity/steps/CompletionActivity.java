/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity.steps;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.ptapp.client.ui.steps.CompletionView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.WizardStepsViewFactory;

public class CompletionActivity extends AbstractActivity implements CompletionView.Presenter {

    private final CompletionView view;

    public CompletionActivity(AppPlace place) {
        this.view = (CompletionView) WizardStepsViewFactory.instance(CompletionView.class);
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
