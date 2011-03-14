/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc;

import com.pyx4j.rpc.shared.Service;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationResponse;

@Deprecated
public interface ActivationServices {

    @Deprecated
    public interface CreateAccount extends Service<AccountCreationRequest, AuthenticationResponse> {

    };

    /**
     * Request E-mail to be sent to customer with 'token' for PasswordReset.
     */
    @Deprecated
    public interface PasswordReminder extends Service<PasswordRetrievalRequest, VoidSerializable> {

    };

    public static final String PASSWORD_TOKEN = "token";

    /**
     * Reset password in the system base on token received in E-mail
     */
    @Deprecated
    public interface PasswordReset extends Service<PasswordChangeRequest, AuthenticationResponse> {

    };

    @Deprecated
    public interface PasswordChange extends Service<PasswordChangeRequest, VoidSerializable> {

    };

    //    public interface EmailChange extends Service<EmailChangeRequest, VoidSerializable> {
    //
    //    };

}
