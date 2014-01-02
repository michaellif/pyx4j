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
package com.propertyvista.ob.server.services;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ClientSystemInfo;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;
import com.pyx4j.rpc.shared.IgnoreSessionToken;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.shared.AclRevalidator;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Lifecycle;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.ob.rpc.dto.OnboardingApplicationStatus;
import com.propertyvista.ob.rpc.dto.OnboardingUserVisit;
import com.propertyvista.ob.rpc.services.OnboardingAuthenticationService;
import com.propertyvista.server.TaskRunner;

public class OnboardingAuthenticationServiceImpl extends com.pyx4j.security.server.AuthenticationServiceImpl implements OnboardingAuthenticationService,
        AclRevalidator {

    protected VistaApplication getVistaApplication() {
        return VistaApplication.onboarding;
    }

    protected VistaBasicBehavior getApplicationBehavior() {
        return VistaBasicBehavior.Onboarding;
    }

    @Override
    public void obtainRecaptchaPublicKey(AsyncCallback<String> callback) {
        callback.onSuccess(((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getReCaptchaPublicKey());
    }

    protected String createSession(ClientSystemInfo clientSystemInfo) {
        OnboardingUserVisit visit = new OnboardingUserVisit();
        visit.setStatus(OnboardingApplicationStatus.starting);
        Set<Behavior> behaviors = new HashSet<Behavior>();
        behaviors.add(getVistaApplication());
        behaviors.add(getApplicationBehavior());
        String token = Lifecycle.beginSession(visit, behaviors);
        return token;
    }

    @Override
    public void authenticate(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, AuthenticationRequest request) {
        callback.onSuccess(createAuthenticationResponse(createSession(clientSystemInfo)));
    }

    @Override
    @IgnoreSessionToken
    public void authenticate(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, String sessionToken) {
        assertClientSystemInfo(clientSystemInfo);
        AuthenticationResponse ar = createAuthenticationResponse(sessionToken);

        // Case of application reload
        final OnboardingUserVisit visit = Context.getUserVisit(OnboardingUserVisit.class);
        if (visit != null) {
            TaskRunner.runInOperationsNamespace(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
                    criteria.eq(criteria.proto().namespace(), visit.pmcNamespace);
                    Pmc pmc = Persistence.service().retrieve(criteria);
                    if (pmc != null) {
                        switch (pmc.status().getValue()) {
                        case Active:
                            visit.status = OnboardingApplicationStatus.accountCreated;
                            break;
                        default:
                            break;
                        }
                    }
                    return null;
                }

            });

        }

        callback.onSuccess(ar);
    }

    @Override
    public Set<Behavior> getCurrentBehaviours(Key principalPrimaryKey, Set<Behavior> currentBehaviours, long aclTimeStamp) {
        return currentBehaviours;
    }
}
