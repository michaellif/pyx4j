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

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ClientSystemInfo;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.server.contexts.Lifecycle;

import com.propertyvista.ob.rpc.OnboardingApplicationBehavior;
import com.propertyvista.ob.rpc.services.OnboardingAuthenticationService;

public class OnboardingAuthenticationServiceImpl extends com.pyx4j.security.server.AuthenticationServiceImpl implements OnboardingAuthenticationService {

    protected String createSession(ClientSystemInfo clientSystemInfo) {
        UserVisit visit = new UserVisit(null, null);
        Set<Behavior> behaviors = new HashSet<Behavior>();
        behaviors.add(OnboardingApplicationBehavior.sessionActivated);
        String token = Lifecycle.beginSession(visit, behaviors);
        return token;
    }

    @Override
    public void authenticate(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, AuthenticationRequest request) {
        callback.onSuccess(createAuthenticationResponse(createSession(clientSystemInfo)));
    }

    @Override
    public void obtainRecaptchaPublicKey(AsyncCallback<String> callback) {
        callback.onSuccess(((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getReCaptchaPublicKey());
    }

}
