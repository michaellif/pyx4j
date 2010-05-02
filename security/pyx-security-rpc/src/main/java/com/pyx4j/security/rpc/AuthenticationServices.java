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
 * Created on Jan 18, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.rpc;

import com.pyx4j.rpc.shared.Service;

public interface AuthenticationServices {

    public interface GetStatus extends Service<String, AuthenticationResponse> {

    };

    public interface Authenticate extends Service<AuthenticationRequest, AuthenticationResponse> {

    };

    public interface Logout extends Service<String, AuthenticationResponse> {

    };

    public interface GetGoogleAccountsLoginUrl extends Service<String, String> {

    };

    public interface GetGoogleAccountsLogoutUrl extends Service<String, String> {

    };

}
