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

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.admin.server.onboarding.OnboardingXMLUtils;
import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.onboarding.OnboardingUserAuthenticationResponseIO;
import com.propertyvista.onboarding.OnboardingUserPasswordResetRequestIO;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.server.common.security.AccessKey;
import com.propertyvista.server.common.security.PasswordEncryptor;

public class OnboardingUserPasswordResetRequestHandler extends AbstractRequestHandler<OnboardingUserPasswordResetRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(OnboardingUserPasswordResetRequestHandler.class);

    private static final I18n i18n = I18n.get(OnboardingUserPasswordResetRequestHandler.class);

    public OnboardingUserPasswordResetRequestHandler() {
        super(OnboardingUserPasswordResetRequestIO.class);
    }

    @Override
    public ResponseIO execute(OnboardingUserPasswordResetRequestIO request) {
        log.info("User {} requested {}", new Object[] { request.onboardingAccountId().getValue(), "OnboardingUserPasswordReset" });

        OnboardingUserAuthenticationResponseIO response = EntityFactory.create(OnboardingUserAuthenticationResponseIO.class);
        response.success().setValue(Boolean.TRUE);

        AccessKey.TokenParser token = new AccessKey.TokenParser(request.token().getValue());
        String email = PasswordEncryptor.normalizeEmailAddress(token.email);
        AbstractAntiBot.assertLogin(email, null);

        EntityQueryCriteria<OnboardingUser> criteria = EntityQueryCriteria.create(OnboardingUser.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        List<OnboardingUser> users = Persistence.service().query(criteria);
        if (users.size() != 1) {
            log.debug("Invalid log-in attempt {} rs {}", email, users.size());
            if (AbstractAntiBot.authenticationFailed(email)) {
                response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.ChallengeVerificationRequired);
                response.reCaptchaPublicKey().setValue(((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getReCaptchaPublicKey());
                return response;
            } else {
                response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.AuthenticationFailed);
                return response;
            }
        }
        OnboardingUser user = users.get(0);

        OnboardingUserCredential cr = Persistence.service().retrieve(OnboardingUserCredential.class, user.getPrimaryKey());
        if (cr == null) {
            throw new UserRuntimeException("Invalid User Account. Please Contact Support");
        }
        if (!cr.enabled().isBooleanTrue()) {
            response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.PermissionDenied);
            return response;
        }

        if (!token.accessKey.equals(cr.accessKey().getValue())) {
            AbstractAntiBot.authenticationFailed(token.email);
            response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.TokenExpired);
            return response;
        }

        if ((new Date().after(cr.accessKeyExpire().getValue()))) {
            response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.TokenExpired);
            return response;
        }

        if (PasswordEncryptor.checkPassword(request.newPassword().getValue(), cr.credential().getValue())) {
            throw new UserRuntimeException(i18n.tr("Your password cannot repeat your previous password"));
        }

        cr.accessKey().setValue(null);
        cr.credential().setValue(PasswordEncryptor.encryptPassword(request.newPassword().getValue()));
        cr.credentialUpdated().setValue(new Date());
        cr.requiredPasswordChangeOnNextLogIn().setValue(Boolean.FALSE);
        Persistence.service().persist(cr);
        Persistence.service().commit();

        response.role().setValue(OnboardingXMLUtils.convertRole(cr.behavior().getValue()));
        response.onboardingAccountId().set(cr.onboardingAccountId());
        response.email().setValue(user.email().getValue());
        response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.OK);
        return response;
    }
}
