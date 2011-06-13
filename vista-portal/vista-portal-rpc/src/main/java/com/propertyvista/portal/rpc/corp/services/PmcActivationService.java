/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.corp.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;
import com.pyx4j.security.rpc.AuthenticationResponse;

import com.propertyvista.portal.rpc.corp.PmcAccountCreationRequest;

public interface PmcActivationService extends IService {

    public void createAccount(AsyncCallback<AuthenticationResponse> callback, PmcAccountCreationRequest request);

}
