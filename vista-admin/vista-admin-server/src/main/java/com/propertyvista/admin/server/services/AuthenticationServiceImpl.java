/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.services;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.UserVisit;

import com.propertyvista.admin.rpc.services.AuthenticationService;
import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.server.common.security.VistaLifecycle;

public class AuthenticationServiceImpl extends com.pyx4j.security.server.AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public void authenticate(AsyncCallback<AuthenticationResponse> callback, AuthenticationRequest request) {
        final UserVisit visit = new UserVisit(null, "Admin");

        Set<Behavior> behaviors = new HashSet<Behavior>();
        behaviors.add(VistaBehavior.ADMIN);
        callback.onSuccess(createAuthenticationResponse(VistaLifecycle.beginSession(visit, behaviors)));
    }

    @Override
    public void logout(AsyncCallback<AuthenticationResponse> callback) {
        VistaLifecycle.endSession();
        callback.onSuccess(createAuthenticationResponse(null));
    }

}
