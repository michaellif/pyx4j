/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.shared.ClientSystemInfo;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.server.LocalService;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.admin.rpc.services.AdminAuthenticationService;
import com.propertyvista.domain.security.VistaAdminBehavior;
import com.propertyvista.onboarding.RequestMessageIO;

public class OnboardingSecurity {

    private final static Logger log = LoggerFactory.getLogger(OnboardingSecurity.class);

    public static boolean enter(RequestMessageIO requestMessage) {
        try {
            //AbstractAntiBot.assertLogin(requestMessage.interfaceEntity().getValue(), null);

            AuthenticationRequest request = EntityFactory.create(AuthenticationRequest.class);
            request.email().setValue(requestMessage.interfaceEntity().getValue());
            request.password().setValue(requestMessage.interfaceEntityPassword().getValue());

            final boolean[] rc = { false };
            // This does the actual authentication; will throw an exception in case of failure
            LocalService.create(AdminAuthenticationService.class).authenticate(new AsyncCallback<AuthenticationResponse>() {
                @Override
                public void onFailure(Throwable caught) {

                    rc[0] = false;
                }

                @Override
                public void onSuccess(AuthenticationResponse result) {
                    // Our wicket session authentication simply returns true, so this call will just create wicket session
                    rc[0] = SecurityController.checkBehavior(VistaAdminBehavior.OnboardingApi);
                }
            }, new ClientSystemInfo(), request);

            return rc[0];
        } catch (Throwable e) {
            log.error("", e);
            return false;
        }
        //return "rossul".equals(requestMessage.interfaceEntity().getValue()) && "secret".equals(requestMessage.interfaceEntityPassword().getValue());
    }

    public static void exit() {
        LocalService.create(AdminAuthenticationService.class).logout(new AsyncCallback<AuthenticationResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                log.error(caught.getMessage());
            }

            @Override
            public void onSuccess(AuthenticationResponse result) {

            }
        });
    }
}
