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

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.essentials.server.AbstractAntiBot.LoginType;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.security.server.EmailValidator;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.biz.system.UserManagementFacade;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.onboarding.OnboardingUserAuthenticationResponseIO;
import com.propertyvista.onboarding.OnboardingUserPasswordResetRequestIO;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.operations.domain.security.OnboardingUserCredential;
import com.propertyvista.operations.server.onboarding.OnboardingXMLUtils;
import com.propertyvista.operations.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.server.common.security.AccessKey;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class OnboardingUserPasswordResetRequestHandler extends AbstractRequestHandler<OnboardingUserPasswordResetRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(OnboardingUserPasswordResetRequestHandler.class);

    private static final I18n i18n = I18n.get(OnboardingUserPasswordResetRequestHandler.class);

    public OnboardingUserPasswordResetRequestHandler() {
        super(OnboardingUserPasswordResetRequestIO.class);
    }

    @Override
    public ResponseIO execute(OnboardingUserPasswordResetRequestIO request) {
        log.info("API requested {}", new Object[] { "OnboardingUserPasswordReset" });

        OnboardingUserAuthenticationResponseIO response = EntityFactory.create(OnboardingUserAuthenticationResponseIO.class);
        response.success().setValue(Boolean.TRUE);

        AccessKey.TokenParser token = new AccessKey.TokenParser(request.token().getValue());
        String email = EmailValidator.normalizeEmailAddress(token.email);
        AbstractAntiBot.assertLogin(LoginType.accessToken, email, null);

        EntityQueryCriteria<OnboardingUser> criteria = EntityQueryCriteria.create(OnboardingUser.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        List<OnboardingUser> users = Persistence.service().query(criteria);
        if (users.size() != 1) {
            log.debug("Invalid log-in attempt {} rs {}", email, users.size());
            if (AbstractAntiBot.authenticationFailed(LoginType.userLogin, email)) {
                response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.ChallengeVerificationRequired);
                response.reCaptchaPublicKey().setValue(((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getReCaptchaPublicKey());
                return response;
            } else {
                response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.AuthenticationFailed);
                return response;
            }
        }
        OnboardingUser user = users.get(0);

        OnboardingUserCredential credential = Persistence.service().retrieve(OnboardingUserCredential.class, user.getPrimaryKey());
        if (credential == null) {
            throw new UserRuntimeException("Invalid User Account. Please Contact Support");
        }
        if (!credential.enabled().isBooleanTrue()) {
            response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.PermissionDenied);
            return response;
        }

        if (!token.accessKey.equals(credential.accessKey().getValue())) {
            AbstractAntiBot.authenticationFailed(LoginType.accessToken, token.email);
            response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.TokenExpired);
            return response;
        }

        if ((new Date().after(credential.accessKeyExpire().getValue()))) {
            response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.TokenExpired);
            return response;
        }

        boolean checkAgainsOnboarding = true;

        if ((!credential.pmc().isNull()) && (!ServerSideFactory.create(PmcFacade.class).isOnboardingEnabled(credential.pmc()))) {
            response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.PermissionDenied);
            return response;
        }

        if (!credential.pmc().isNull() && (credential.pmc().status().getValue() != PmcStatus.Created)) {
            if (credential.pmc().status().getValue() != PmcStatus.Created) {
                String curNameSpace = NamespaceManager.getNamespace();
                try {
                    NamespaceManager.setNamespace(credential.pmc().namespace().getValue());

                    EntityQueryCriteria<CrmUserCredential> crmUCrt = EntityQueryCriteria.create(CrmUserCredential.class);
                    crmUCrt.add(PropertyCriterion.eq(crmUCrt.proto().roles().$().behaviors(), VistaCrmBehavior.PropertyVistaAccountOwner));
                    crmUCrt.add(PropertyCriterion.eq(crmUCrt.proto().onboardingUser(), credential.getPrimaryKey()));
                    CrmUserCredential crmCredential = Persistence.service().retrieve(crmUCrt);

                    if (crmCredential == null) {
                        throw new UserRuntimeException(i18n.tr("Invalid User Account. Please Contact Support"));
                    }

                    if (!crmCredential.enabled().isBooleanTrue()) {
                        response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.PermissionDenied);
                        return response;
                    }

                    // Verify securityAnswer
                    if ((!crmCredential.securityQuestion().isNull()) && (!crmCredential.securityAnswer().equals(request.securityAnswer()))) {
                        throw new UserRuntimeException(i18n.tr("The answer to security question is incorrect"));
                    }

                    if (ServerSideFactory.create(PasswordEncryptorFacade.class).checkUserPassword(request.newPassword().getValue(),
                            crmCredential.credential().getValue())) {
                        throw new UserRuntimeException(i18n.tr("Your password cannot repeat your previous password"));
                    }

                    checkAgainsOnboarding = false;

                } finally {
                    NamespaceManager.setNamespace(curNameSpace);
                }
            }
        }

        if (checkAgainsOnboarding) {
            // Verify securityAnswer
            if ((!credential.securityQuestion().isNull()) && (!credential.securityAnswer().equals(request.securityAnswer()))) {
                throw new UserRuntimeException(i18n.tr("The answer to security question is incorrect"));
            }
            if (ServerSideFactory.create(PasswordEncryptorFacade.class).checkUserPassword(request.newPassword().getValue(), credential.credential().getValue())) {
                throw new UserRuntimeException(i18n.tr("Your password cannot repeat your previous password"));
            }
        }

        PasswordChangeRequest pchRequest = EntityFactory.create(PasswordChangeRequest.class);
        pchRequest.newPassword().setValue(request.newPassword().getValue());
        pchRequest.userPk().setValue(user.getPrimaryKey());
        ServerSideFactory.create(UserManagementFacade.class).selfChangePassword(OnboardingUserCredential.class, pchRequest);

        response.role().setValue(OnboardingXMLUtils.convertRole(credential.behavior().getValue(), true));
        response.onboardingAccountId().set(credential.onboardingAccountId());
        response.email().setValue(user.email().getValue());
        response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.OK);
        return response;
    }
}
