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

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.essentials.server.AbstractAntiBot.LoginType;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.onboarding.OnboardingUserPasswordResetQuestionResponseIO;
import com.propertyvista.onboarding.OnboardingUserTokenValidationRequestIO;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.server.common.security.AccessKey;
import com.propertyvista.server.common.security.PasswordEncryptor;

public class OnboardingUserTokenValidationRequestHandler extends AbstractRequestHandler<OnboardingUserTokenValidationRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(OnboardingUserTokenValidationRequestHandler.class);

    public OnboardingUserTokenValidationRequestHandler() {
        super(OnboardingUserTokenValidationRequestIO.class);
    }

    @Override
    public ResponseIO execute(OnboardingUserTokenValidationRequestIO request) {
        log.info("API requested {}", new Object[] { "OnboardingUserTokenValidation" });

        OnboardingUserPasswordResetQuestionResponseIO response = EntityFactory.create(OnboardingUserPasswordResetQuestionResponseIO.class);

        AccessKey.TokenParser token = new AccessKey.TokenParser(request.token().getValue());
        String email = PasswordEncryptor.normalizeEmailAddress(token.email);
        AbstractAntiBot.assertLogin(LoginType.accessToken, email, null);

        EntityQueryCriteria<OnboardingUser> criteria = EntityQueryCriteria.create(OnboardingUser.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        List<OnboardingUser> users = Persistence.service().query(criteria);
        if (users.size() != 1) {
            log.debug("Invalid log-in attempt {} rs {}", email, users.size());
            response.success().setValue(Boolean.FALSE);
            return response;

        }
        OnboardingUser user = users.get(0);

        OnboardingUserCredential cr = Persistence.service().retrieve(OnboardingUserCredential.class, user.getPrimaryKey());
        if (cr == null) {
            throw new UserRuntimeException("Invalid User Account. Please Contact Support");
        }
        if (!cr.enabled().isBooleanTrue()) {
            response.success().setValue(Boolean.FALSE);
            return response;
        }

        if (!token.accessKey.equals(cr.accessKey().getValue())) {
            AbstractAntiBot.authenticationFailed(token.email);
            response.success().setValue(Boolean.FALSE);
            return response;
        }

        if ((new Date().after(cr.accessKeyExpire().getValue()))) {
            response.success().setValue(Boolean.FALSE);
            return response;
        }

        response.success().setValue(Boolean.TRUE);

        // TODO If CRM user exists send the question
        response.securityQuestion().setValue(null);

        return response;

    }
}
