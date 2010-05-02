/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 19, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.server;

import java.util.HashSet;
import java.util.Set;

import com.google.appengine.api.users.UserServiceFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationServices;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Lifecycle;

/**
 * This implementation does not use DB to load and verify users.
 * 
 * You need to override AuthenticateImpl in App.
 */
public class AuthenticationServicesImpl implements AuthenticationServices {

    public static AuthenticationResponse createAuthenticationResponse(String logoutApplicationUrl) {
        AuthenticationResponse ar = new AuthenticationResponse();
        ar.setLogoutURL(logoutApplicationUrl);
        if (Context.getSession() != null) {
            ar.setMaxInactiveInterval(Context.getSession().getMaxInactiveInterval());
        }
        // Make it serializable by RPC
        Set<Behavior> behaviors = new HashSet<Behavior>();
        behaviors.addAll(SecurityController.getBehaviors());
        ar.setBehaviors(behaviors);

        if (Context.getVisit() != null) {
            ar.setUserVisit(Context.getVisit().getUserVisit());
        }

        if (ServerSideConfiguration.instance().useAppengineGoogleAccounts()) {
            AppengineUserService.updateAuthenticationResponse(ar);
        }

        return ar;
    }

    public static class GetStatusImpl implements AuthenticationServices.GetStatus {

        @Override
        public AuthenticationResponse execute(String request) {
            return createAuthenticationResponse(request);
        }

    }

    public static class AuthenticateImpl implements AuthenticationServices.Authenticate {

        @Override
        public AuthenticationResponse execute(AuthenticationRequest request) {
            return createAuthenticationResponse(request.logoutApplicationUrl().getValue());
        }

    }

    public static class LogoutImpl implements AuthenticationServices.Logout {

        @Override
        public AuthenticationResponse execute(String request) {
            Lifecycle.endSession();
            return createAuthenticationResponse(request);
        }

    }

    public static class GetGoogleAccountsLoginUrlImpl implements AuthenticationServices.GetGoogleAccountsLoginUrl {

        @Override
        public String execute(String request) {
            return UserServiceFactory.getUserService().createLoginURL(request);
        }

    }

    public static class GetGoogleAccountsLogoutUrlImpl implements AuthenticationServices.GetGoogleAccountsLogoutUrl {

        @Override
        public String execute(String request) {
            return UserServiceFactory.getUserService().createLogoutURL(request);
        }

    }

}
