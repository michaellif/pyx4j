/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity.steps.welcomewizardmockup;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.welcome.WelcomeView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.PtAppViewFactory;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;

public class WelcomeActivity extends AbstractActivity implements WelcomeView.Presenter {

    private final WelcomeView view;

    public WelcomeActivity() {
        this.view = PtAppViewFactory.instance(WelcomeView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setPresenter(this);
    }

    @Override
    public void startTheWizard() {
        AppSite.getPlaceController().goTo(new PtSiteMap.WelcomeWizard.ReviewLease());
    }

}
