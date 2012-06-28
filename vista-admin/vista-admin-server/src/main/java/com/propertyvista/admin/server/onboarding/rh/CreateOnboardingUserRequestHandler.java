/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.onboarding.rh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.onboarding.CreateOnboardingUserRequestIO;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.preloader.OnboardingUserPreloader;

public class CreateOnboardingUserRequestHandler extends AbstractRequestHandler<CreateOnboardingUserRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(CreateOnboardingUserRequestHandler.class);

    public CreateOnboardingUserRequestHandler() {
        super(CreateOnboardingUserRequestIO.class);
    }

    @Override
    public ResponseIO execute(CreateOnboardingUserRequestIO request) {
        log.info("User {} requested {} for email {}", new Object[] { request.onboardingAccountId().getValue(), "CreateOnboardingUser",
                request.email().getValue() });

        OnboardingUserPreloader.createOnboardingUser(request.firstName().getValue(), request.lastName().getValue(), request.email().getValue(), request
                .password().getValue(), VistaOnboardingBehavior.ProspectiveClient, request.onboardingAccountId().getValue());

        ResponseIO response = EntityFactory.create(ResponseIO.class);
        response.success().setValue(Boolean.TRUE);

        return response;
    }
}
