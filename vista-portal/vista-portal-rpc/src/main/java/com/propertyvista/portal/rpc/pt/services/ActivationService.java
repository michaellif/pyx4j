/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 13, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.pt.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.pt.AccountCreationRequest;
import com.propertyvista.portal.rpc.pt.PasswordChangeRequest;
import com.propertyvista.portal.rpc.pt.PasswordRetrievalRequest;

import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationResponse;

public interface ActivationService extends IService {

    /**
     * Verify that Unit exists before we allow to start Application process, e.g.
     * createAccount
     */
    public void unitExists(AsyncCallback<Boolean> callback, UnitSelectionCriteria request);

    public void createAccount(AsyncCallback<AuthenticationResponse> callback, AccountCreationRequest request);

    /**
     * Request E-mail to be sent to customer with 'token' for PasswordReset.
     * 
     * E-mail is sent if no exception thrown.
     */
    public void passwordReminder(AsyncCallback<VoidSerializable> callback, PasswordRetrievalRequest request);

    public static final String PASSWORD_TOKEN = "token";

    /**
     * Reset password in the system based on token received in E-mail
     */
    public void passwordReset(AsyncCallback<AuthenticationResponse> callback, PasswordChangeRequest request);

}
