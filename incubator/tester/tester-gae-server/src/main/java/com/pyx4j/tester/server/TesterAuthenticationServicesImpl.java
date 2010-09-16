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
 * Created on 2010-09-16
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.server;

import com.pyx4j.rpc.shared.IsIgnoreSessionTokenService;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationServices;
import com.pyx4j.security.server.AuthenticationServicesImpl;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.server.contexts.Lifecycle;

public class TesterAuthenticationServicesImpl extends AuthenticationServicesImpl {

    public static class AuthenticateImpl implements AuthenticationServices.Authenticate, IsIgnoreSessionTokenService {

        @Override
        public AuthenticationResponse execute(AuthenticationRequest request) {
            Lifecycle.beginSession(new UserVisit(System.currentTimeMillis(), null), null);
            return AuthenticationServicesImpl.createAuthenticationResponse(null);
        }
    }
}
