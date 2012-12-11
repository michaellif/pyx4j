/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.ob.client.mvp.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.ob.client.views.OnboardingViewFactory;
import com.propertyvista.ob.client.views.PmcAccountCreationRequestView;
import com.propertyvista.ob.rpc.OnboardingSiteMap;
import com.propertyvista.ob.rpc.dto.PmcAccountCreationRequest;
import com.propertyvista.ob.rpc.services.OnboardingPublicActivationService;
import com.propertyvista.ob.rpc.services.PmcRegistrationService;

public class PmcAccountCreationRequestActivity extends AbstractActivity implements PmcAccountCreationRequestView.Presenter {

    private final PmcAccountCreationRequestView view;

    private final OnboardingPublicActivationService onboardingPublicActivationService;

    private final PmcRegistrationService pmcRegistrationService;

    public PmcAccountCreationRequestActivity(AppPlace place) {
        view = OnboardingViewFactory.instance(PmcAccountCreationRequestView.class);
        onboardingPublicActivationService = GWT.<OnboardingPublicActivationService> create(OnboardingPublicActivationService.class);
        pmcRegistrationService = GWT.<PmcRegistrationService> create(PmcRegistrationService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        view.setPmcAccountCreationRequest(EntityFactory.create(PmcAccountCreationRequest.class));
        panel.setWidget(view);
    }

    @Override
    public void checkDns(AsyncCallback<Boolean> callback, String dnsName) {
        onboardingPublicActivationService.checkDNSAvailability(callback, dnsName);
    }

    @Override
    public void createAccount() {
        pmcRegistrationService.createAccount(new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String deferredCorellationId) {
                goToProgressPlace(deferredCorellationId);
            }
        }, view.getPmcAccountCreationRequest());
    }

    @Override
    public void populate() {
        // TODO Auto-generated method stub
    }

    @Override
    @Deprecated
    public void refresh() {
        // TODO Auto-generated method stub

    }

    private void goToProgressPlace(String deferredCorellationId) {
        AppPlace progressPlace = new OnboardingSiteMap.PmcAccountCreationProgress();
        progressPlace.placeArg("id", deferredCorellationId);
        AppSite.getPlaceController().goTo(progressPlace);
    }

}
