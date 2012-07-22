/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;

import com.propertyvista.portal.rpc.shared.dto.AccountRecoveryOptionsDTO;

public interface AbstractAccountRecoveryOptionsService extends IService {

    public void obtainRecoveryOptions(AsyncCallback<AccountRecoveryOptionsDTO> callback, AuthenticationRequest request);

    public void updateRecoveryOptions(AsyncCallback<AuthenticationResponse> callback, AccountRecoveryOptionsDTO request);

}
