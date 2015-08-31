/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-06-13
 * @author vlads
 */
package com.pyx4j.security.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.shared.ClientSystemInfo;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.ServiceExecution;
import com.pyx4j.rpc.shared.ServiceExecution.OperationType;
import com.pyx4j.rpc.shared.VoidSerializable;

public interface AuthenticationService extends IService {

    public void getSystemReadOnlyStatus(AsyncCallback<Boolean> callback);

    public void verifyVersion(AsyncCallback<VoidSerializable> callback, ClientSystemInfo clientSystemInfo);

    public void authenticate(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, String sessionToken);

    public void authenticate(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, AuthenticationRequest request);

    public void obtainRecaptchaPublicKey(AsyncCallback<String> callback);

    @ServiceExecution(operationType = OperationType.NonBlocking)
    public void keepSessionAlive(AsyncCallback<VoidSerializable> callback);

    public void logout(AsyncCallback<AuthenticationResponse> callback);

    /**
     * Get GoogleAccounts or JSP login page URL
     */
    public void getLoginUrl(AsyncCallback<String> callback, final String destinationURLComponent);

    public void getLogoutUrl(AsyncCallback<String> callback, final String destinationURLComponent);

    public static final String AUTH_TOKEN_ARG = "atoken";

    /**
     * Request E-mail to be sent to customer with 'token' for PasswordReset.
     *
     * E-mail is sent if no exception thrown.
     */
    public void requestPasswordReset(AsyncCallback<VoidSerializable> callback, PasswordRetrievalRequest request);

    /**
     * Login with temporary token that identifies the user and gives him privileges for resetting his password.
     *
     * @param callback
     * @param clientSystemInfo
     * @param accessToken
     *            temporary token used to give the user limited access that allows only to reset his password.
     */
    public void authenticateWithToken(AsyncCallback<AuthenticationResponse> callback, ClientSystemInfo clientSystemInfo, String accessToken);

}
