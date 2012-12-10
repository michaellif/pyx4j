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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.client.DeferredProcessDialog;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.ob.rpc.dto.PmcAccountCreationRequest;
import com.propertyvista.ob.rpc.services.OnboardingAuthenticationService;
import com.propertyvista.ob.rpc.services.PmcRegistrationService;

/**
 * flow Execution Example
 */
class OnboardingFlowSample {

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
        PmcAccountCreationRequest request = EntityFactory.create(PmcAccountCreationRequest.class);

        String id = "p" + String.valueOf(System.currentTimeMillis());
        request.name().setValue(id);
        request.dnsName().setValue(id);
        request.firstName().setValue("F");
        request.lastName().setValue("L");
        request.email().setValue(id + "@pyx4j.com");

        AsyncCallback<String> callback = new DefaultAsyncCallback<String>() {
            @Override
            public void onSuccess(String deferredCorrelationId) {
                doTestPmcCreationStep3(deferredCorrelationId);
            }
        };
        GWT.<PmcRegistrationService> create(PmcRegistrationService.class).createAccount(callback, request);
    }

    private void doTestPmcCreationStep3(String deferredCorrelationId) {
        DeferredProcessDialog d = new DeferredProcessDialog("PMC Activation", "Activating PMC ...", false) {
            @Override
            public void onDeferredSuccess(DeferredProcessProgressResponse result) {
                super.onDeferredSuccess(result);
            }
        };
        d.show();
        d.startProgress(deferredCorrelationId);
    }
}
