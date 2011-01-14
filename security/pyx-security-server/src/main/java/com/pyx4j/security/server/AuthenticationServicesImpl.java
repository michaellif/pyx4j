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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.rpc.shared.IsIgnoreSessionTokenService;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationServices;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.Visit;

/**
 * This implementation does not use DB to load and verify users.
 * 
 * You need to override AuthenticateImpl in App.
 */
public class AuthenticationServicesImpl implements AuthenticationServices {

    private static Logger log = LoggerFactory.getLogger(AuthenticationServicesImpl.class);

    protected static IContainerHelper containerHelper;

    public static IContainerHelper getContainerHelper() {
        if (containerHelper == null) {
            switch (ServerSideConfiguration.instance().getEnvironmentType()) {
            case LocalJVM:
                containerHelper = new ServletContainerHelper();
                break;
            default:
                containerHelper = new AppengineContainerHelper();
                break;
            }
        }
        return containerHelper;
    }

    public static AuthenticationResponse createAuthenticationResponse(String logoutApplicationUrl) {
        AuthenticationResponse ar = new AuthenticationResponse();
        ar.setLogoutURL(logoutApplicationUrl);
        if (Context.getSession() != null) {
            ar.setMaxInactiveInterval(Context.getSession().getMaxInactiveInterval());
            ar.setSessionCookieName(ServerSideConfiguration.instance().getSessionCookieName());
            log.debug("session maxInactiveInterval {} sec", ar.getMaxInactiveInterval());
            switch (ServerSideConfiguration.instance().getEnvironmentType()) {
            case GAEDevelopment:
            case GAESandbox:
                if (ar.getMaxInactiveInterval() <= 0) {
                    ar.setMaxInactiveInterval(24 * Consts.HOURS2SEC);
                    log.debug("session maxInactiveInterval {} sec", ar.getMaxInactiveInterval());
                }
            }
        }
        // Make it serializable by RPC
        Set<Behavior> behaviors = new HashSet<Behavior>();
        behaviors.addAll(SecurityController.getBehaviors());
        ar.setBehaviors(behaviors);

        Visit visit = Context.getVisit();
        if (visit != null) {
            ar.setUserVisit(visit.getUserVisit());

            // TODO This is BAD but no much, we will fix this in next version. We still protected from XSRF
            ar.setSessionToken(visit.getSessionToken());
            ar.setAclTimeStamp(String.valueOf(visit.getAclTimeStamp()));
        }

        if (ServerSideConfiguration.instance().useAppengineGoogleAccounts()) {
            AppengineUserService.updateAuthenticationResponse(ar);
        }

        if (ServerSideConfiguration.instance().datastoreReadOnly()) {
            ar.setDatastoreReadOnly(true);
        }
        return ar;
    }

    public static class GetReadOnlyImpl implements AuthenticationServices.GetReadOnly {

        @Override
        public Boolean execute(VoidSerializable request) {
            return ServerSideConfiguration.instance().datastoreReadOnly() || AppengineHelper.isDBReadOnly();
        }

    }

    public static class GetStatusImpl implements AuthenticationServices.GetStatus, IsIgnoreSessionTokenService {

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
            return getContainerHelper().createLoginURL(request);
        }

    }

    public static class GetGoogleAccountsLogoutUrlImpl implements AuthenticationServices.GetGoogleAccountsLogoutUrl {

        @Override
        public String execute(String request) {
            return getContainerHelper().createLogoutURL(request);
        }

    }

}
