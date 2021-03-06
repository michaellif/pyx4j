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
 * Created on Aug 21, 2010
 * @author vlads
 */
package com.pyx4j.essentials.j2se;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.Credentials;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.impl.EntityImplGenerator;
import com.pyx4j.rpc.j2se.J2SEService;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;

public class J2SEServiceConnector extends J2SEService {

    private static final Logger log = LoggerFactory.getLogger(J2SEServiceConnector.class);

    protected AuthenticationResponse auth;

    public J2SEServiceConnector(String name) {
        super(null);
        setName(name);
        EntityImplGenerator.generateOnce(false);
    }

    public void openGooleSession(Credentials credentials, GoogleAccountType googleAccountType, boolean developerAdmin) {
        super.googleLogin(credentials.userName, credentials.password, googleAccountType, developerAdmin);
    }

    public void openApplicationSession(Credentials credentials) {
        AuthenticationRequest authenticationRequest = EntityFactory.create(AuthenticationRequest.class);
        authenticationRequest.email().setValue(credentials.userName);
        authenticationRequest.password().setValue(credentials.password);
        //auth = super.execute(AuthenticationServices.Authenticate.class, authenticationRequest);
        sessionToken = auth.getSessionToken();
    }

    public void getApplicationSession() {
        //auth = super.execute(AuthenticationServices.GetStatus.class, null);
        sessionToken = auth.getSessionToken();
        log.debug("got behaviors {}", auth.getBehaviors());
    }

    @Override
    public void logout() {
        //super.execute(AuthenticationServices.Logout.class, null);
        if (auth != null) {
            super.removeCookie(auth.getSessionCookieName());
            auth = null;
            sessionToken = null;
        }
    }
}
