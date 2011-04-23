/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 23, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.sync;

import com.propertyvista.portal.rpc.pt.AccountCreationRequest;
import com.propertyvista.portal.rpc.pt.services.ActivationService;

import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.UnitTestsAsyncCallback;

public class ActivationServiceSync {

    private AuthenticationResponse response;

    public AuthenticationResponse createAccount(AccountCreationRequest request) {
        response = null;

        ActivationService service = TestServiceFactory.create(ActivationService.class);
        service.createAccount(new UnitTestsAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                response = result;
            }
        }, request);

        return response;
    }
}
