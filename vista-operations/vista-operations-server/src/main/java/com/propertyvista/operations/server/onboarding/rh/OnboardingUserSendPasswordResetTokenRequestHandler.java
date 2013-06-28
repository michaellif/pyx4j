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
package com.propertyvista.operations.server.onboarding.rh;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Pair;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.essentials.server.AbstractAntiBot.LoginType;
import com.pyx4j.security.server.EmailValidator;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.onboarding.OnboardingUserSendPasswordResetTokenRequestIO;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.operations.domain.security.OnboardingUserCredential;
import com.propertyvista.operations.server.onboarding.rhf.AbstractRequestHandler;

public class OnboardingUserSendPasswordResetTokenRequestHandler extends AbstractRequestHandler<OnboardingUserSendPasswordResetTokenRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(OnboardingUserSendPasswordResetTokenRequestHandler.class);

    public OnboardingUserSendPasswordResetTokenRequestHandler() {
        super(OnboardingUserSendPasswordResetTokenRequestIO.class);
    }

    @Override
    public ResponseIO execute(OnboardingUserSendPasswordResetTokenRequestIO request) {
        log.info("User {} requested {}", new Object[] { request.email().getValue(), "OnboardingUserSendPasswordResetToken" });

        ResponseIO response = EntityFactory.create(ResponseIO.class);
        response.success().setValue(Boolean.TRUE);

        AbstractAntiBot.assertCaptcha(new Pair<String, String>(request.captcha().challenge().getValue(), request.captcha().response().getValue()));

        String email = EmailValidator.normalizeEmailAddress(request.email().getValue());

        EntityQueryCriteria<OnboardingUser> criteria = EntityQueryCriteria.create(OnboardingUser.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        List<OnboardingUser> users = Persistence.service().query(criteria);
        if (users.size() != 1) {
            log.debug("Invalid log-in attempt {} rs {}", email, users.size());
            if (AbstractAntiBot.authenticationFailed(LoginType.userLogin, email)) {
                response.success().setValue(Boolean.FALSE);
                return response;
            }
        }
        OnboardingUser user = users.get(0);

        OnboardingUserCredential credential = Persistence.service().retrieve(OnboardingUserCredential.class, user.getPrimaryKey());
        if (credential == null) {
            throw new UserRuntimeException("Invalid User Account. Please Contact Support");
        }
        if (!credential.enabled().isBooleanTrue()) {
            response.success().setValue(Boolean.FALSE);
            return response;
        }

        if (!credential.pmc().isNull()) {
            if (!ServerSideFactory.create(PmcFacade.class).isOnboardingEnabled(credential.pmc())) {
                response.success().setValue(Boolean.FALSE);
                return response;
            }
        }

        ServerSideFactory.create(CommunicationFacade.class).sendOnboardingPasswordRetrievalToken(user, request.onboardingSystemBaseUrl().getValue());
        log.debug("pwd change token is sent to {}", user.email().getValue());

        return response;
    }
}
