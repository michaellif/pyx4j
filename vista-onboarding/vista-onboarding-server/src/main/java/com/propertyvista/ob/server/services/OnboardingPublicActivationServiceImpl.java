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
 */
package com.propertyvista.ob.server.services;

import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.rpc.shared.IgnoreSessionToken;

import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.ob.rpc.services.OnboardingPublicActivationService;
import com.propertyvista.operations.domain.legal.LegalDocument;
import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.server.TaskRunner;
import com.propertyvista.server.VistaTermsUtils;

public class OnboardingPublicActivationServiceImpl implements OnboardingPublicActivationService {

    @Override
    public void checkDNSAvailability(AsyncCallback<Boolean> callback, final String dnsName) {
        callback.onSuccess(TaskRunner.runInOperationsNamespace(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                return ServerSideFactory.create(PmcFacade.class).checkDNSAvailability(dnsName);
            }

        }));
    }

    @Override
    @IgnoreSessionToken
    public void getPmcAccountTerms(AsyncCallback<String> callback) {
        String termsContent = null;

        VistaTerms terms = VistaTermsUtils.retrieveVistaTerms(VistaTerms.Target.PmcPropertyVistaService);
        for (LegalDocument doc : terms.version().document()) {
            if (doc.locale().getValue().getLanguage().startsWith("en")) {
                termsContent = doc.content().getValue();
                break;
            }
        }

        if (termsContent == null) {
            throw new RuntimeException("Terms not found");
        }
        callback.onSuccess(termsContent);
    }
}
