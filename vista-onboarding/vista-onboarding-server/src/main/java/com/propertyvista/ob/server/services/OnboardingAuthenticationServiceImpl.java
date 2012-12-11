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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ClientSystemInfo;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;
import com.pyx4j.rpc.shared.IgnoreSessionToken;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.shared.AclRevalidator;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Lifecycle;

import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.security.onboarding.OnboardingApplicationBehavior;
import com.propertyvista.ob.rpc.services.OnboardingAuthenticationService;

public class OnboardingAuthenticationServiceImpl extends com.pyx4j.security.server.AuthenticationServiceImpl implements OnboardingAuthenticationService,
        AclRevalidator {

    public final static String accountCreatedAttr = OnboardingAuthenticationServiceImpl.class.getName() + ".accountCreated";

    protected VistaBasicBehavior getApplicationBehavior() {
        return VistaBasicBehavior.Onboarding;
    }

    @Override
    public void obtainRecaptchaPublicKey(AsyncCallback<String> callback) {
        callback.onSuccess(((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getReCaptchaPublicKey());
    }

    protected String createSession(ClientSystemInfo clientSystemInfo) {
        UserVisit visit = new UserVisit(null, null);
        Set<Behavior> behaviors = new HashSet<Behavior>();
        behaviors.add(getApplicationBehavior());
        String token = Lifecycle.beginSession(visit, behaviors);
        return token;
    }

    @Override
    public void authenticate(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, AuthenticationRequest request) {
        callback.onSuccess(createAuthenticationResponse(createSession(clientSystemInfo)));
    }

    public static void addSessionBehavior(OnboardingApplicationBehavior behaviour) {
        Set<Behavior> behaviors = new HashSet<Behavior>();
        behaviors.addAll(SecurityController.getBehaviors());
        behaviors.add(behaviour);
        Lifecycle.changeSession(behaviors);
    }

    @Override
    @IgnoreSessionToken
    public void authenticate(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, String sessionToken) {
        assertClientSystemInfo(clientSystemInfo);
        if ((Context.getVisit() != null) && !SecurityController.checkBehavior(OnboardingApplicationBehavior.accountCreated)) {
            if ((Context.getVisit().getAttribute(accountCreatedAttr) == Boolean.TRUE)) {
                addSessionBehavior(OnboardingApplicationBehavior.accountCreationRequested);
            }
        }
        callback.onSuccess(createAuthenticationResponse(sessionToken));
    }

    @Override
    public Set<Behavior> getCurrentBehaviours(Key principalPrimaryKey, Set<Behavior> currentBehaviours, long aclTimeStamp) {
        if ((Context.getVisit().getAttribute(accountCreatedAttr) == Boolean.TRUE)
                && (!currentBehaviours.contains(OnboardingApplicationBehavior.accountCreated))) {
            Set<Behavior> behaviors = new HashSet<Behavior>();
            behaviors.addAll(currentBehaviours);
            behaviors.add(OnboardingApplicationBehavior.accountCreated);
            return behaviors;
        } else {
            return currentBehaviours;
        }
    }
}
