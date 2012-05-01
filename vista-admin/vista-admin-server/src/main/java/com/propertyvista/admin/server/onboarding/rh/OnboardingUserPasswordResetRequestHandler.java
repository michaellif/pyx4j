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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Pair;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;

import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.onboarding.OnboardingUserPasswordResetRequestIO;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.server.common.security.PasswordEncryptor;

public class OnboardingUserPasswordResetRequestHandler extends AbstractRequestHandler<OnboardingUserPasswordResetRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(OnboardingUserPasswordResetRequestHandler.class);

    public OnboardingUserPasswordResetRequestHandler() {
        super(OnboardingUserPasswordResetRequestIO.class);
    }

    @Override
    public ResponseIO execute(OnboardingUserPasswordResetRequestIO request) {

        ResponseIO response = EntityFactory.create(ResponseIO.class);
        response.success().setValue(Boolean.TRUE);

        AbstractAntiBot.assertCaptcha(new Pair<String, String>(request.captcha().challenge().getValue(), request.captcha().response().getValue()));

        String email = PasswordEncryptor.normalizeEmailAddress(request.email().getValue());

        EntityQueryCriteria<OnboardingUser> criteria = EntityQueryCriteria.create(OnboardingUser.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        List<OnboardingUser> users = Persistence.service().query(criteria);
        if (users.size() != 1) {
            log.debug("Invalid log-in attempt {} rs {}", email, users.size());
            if (AbstractAntiBot.authenticationFailed(email)) {
                response.success().setValue(Boolean.FALSE);
                return response;
            }
        }

        OnboardingUser user = users.get(0);
        ServerSideFactory.create(CommunicationFacade.class).sendOnboardingPasswordRetrievalToken(user);
        log.debug("pwd change token is sent to {}", user.email().getValue());

        return response;
    }
}
