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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.onboarding.CreateOnboardingUserRequestIO;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.domain.security.OnboardingUserCredential;

public class CreateOnboardingUserRequestHandler extends AbstractRequestHandler<CreateOnboardingUserRequestIO> {

    public CreateOnboardingUserRequestHandler() {
        super(CreateOnboardingUserRequestIO.class);
    }

    @Override
    public ResponseIO execute(CreateOnboardingUserRequestIO request) {

        OnboardingUser user = EntityFactory.create(OnboardingUser.class);
        user.name().set(request.name());
        user.email().setValue(PasswordEncryptor.normalizeEmailAddress(request.email().getValue()));
        Persistence.service().persist(user);

        OnboardingUserCredential credential = EntityFactory.create(OnboardingUserCredential.class);
        credential.setPrimaryKey(user.getPrimaryKey());

        credential.user().set(user);
        credential.credential().setValue(PasswordEncryptor.encryptPassword(request.password().getValue()));
        credential.enabled().setValue(Boolean.TRUE);
        credential.behavior().setValue(VistaOnboardingBehavior.ProspectiveClient);
        credential.onboardingAccountId().set(request.onboardingAccountId());

        Persistence.service().persist(credential);
        Persistence.service().commit();

        ResponseIO response = EntityFactory.create(ResponseIO.class);
        response.success().setValue(Boolean.TRUE);
        response.requestId().setValue(request.requestId().getValue());
        return response;
    }
}
