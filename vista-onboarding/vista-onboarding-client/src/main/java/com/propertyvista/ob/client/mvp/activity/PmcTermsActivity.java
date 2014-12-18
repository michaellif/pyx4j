/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-12
 * @author ArtyomB
 */
package com.propertyvista.ob.client.mvp.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultRecoverableAsyncCallback;

import com.propertyvista.ob.client.views.OnboardingViewFactory;
import com.propertyvista.ob.client.views.PmcTermsView;
import com.propertyvista.ob.rpc.services.OnboardingPublicActivationService;

public class PmcTermsActivity extends AbstractActivity {

    private static final I18n i18n = I18n.get(PmcTermsActivity.class);

    private final PmcTermsView view;

    private final OnboardingPublicActivationService service;

    public PmcTermsActivity() {
        view = OnboardingViewFactory.instance(PmcTermsView.class);
        service = GWT.<OnboardingPublicActivationService> create(OnboardingPublicActivationService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.populate(i18n.tr("Loading..."));
        panel.setWidget(view);
        service.getPmcAccountTerms(new DefaultRecoverableAsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                view.populate(result);
            }
        });
    }

}
