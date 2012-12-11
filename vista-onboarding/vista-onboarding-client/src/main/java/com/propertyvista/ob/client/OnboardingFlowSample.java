/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.ob.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.ob.client.forms.PmcAccountCreationRequestForm;
import com.propertyvista.ob.client.forms.PmcAccountCreationRequestForm.DnsCheckRequestHandler;
import com.propertyvista.ob.rpc.dto.PmcAccountCreationRequest;
import com.propertyvista.ob.rpc.services.OnboardingAuthenticationService;
import com.propertyvista.ob.rpc.services.OnboardingPublicActivationService;
import com.propertyvista.ob.rpc.services.PmcRegistrationService;

class OnboardingFlowSample {

    private static final I18n i18n = I18n.get(OnboardingFlowSample.class);

    private final AcceptsOneWidget panel;

    public OnboardingFlowSample(AcceptsOneWidget panel) {
        this.panel = panel;
    }

    void doTestPmcCreationStep1() {
        AsyncCallback<Boolean> callback = new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                doTestPmcCreationStep2();
            }
        };
        ClientContext.authenticate(GWT.<OnboardingAuthenticationService> create(OnboardingAuthenticationService.class), null, callback);
    }

    private void doTestPmcCreationStep2() {

        final AsyncCallback<String> accountRequestCreationCallback = new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String deferredCorrelationId) {
                doTestPmcCreationStep3(deferredCorrelationId);
            }
        };
        // TODO set this handler after it works
        DnsCheckRequestHandler dnsCheckHandler = new DnsCheckRequestHandler() {
            @Override
            public void checkDns(AsyncCallback<Boolean> callback, String dnsName) {
                GWT.<OnboardingPublicActivationService> create(OnboardingPublicActivationService.class).checkDNSAvailability(callback, dnsName);
            }
        };
        PmcAccountCreationRequestForm accountRequestForm = new PmcAccountCreationRequestForm(new DnsCheckRequestHandler() {
            @Override
            public void checkDns(AsyncCallback<Boolean> callback, String dnsName) {
                callback.onSuccess(true);
            }
        }) {
            @Override
            public void onSubmit(PmcAccountCreationRequest accountCreationRequest) {
                GWT.<PmcRegistrationService> create(PmcRegistrationService.class).createAccount(accountRequestCreationCallback, accountCreationRequest);
            }
        };
        accountRequestForm.initContent();
        accountRequestForm.populate(EntityFactory.create(PmcAccountCreationRequest.class));
        panel.setWidget(accountRequestForm);

    }

    private void doTestPmcCreationStep3(String deferredCorrelationId) {
//        stepsProgress.setWidth("40em");
//        stepsProgress.getElement().getStyle().setProperty("marginLeft", "auto");
//        stepsProgress.getElement().getStyle().setProperty("marginRight", "auto");
//
//        panel.setWidget(stepsProgress);
//        stepsProgress.startProgresss(deferredCorrelationId);
    }
}
