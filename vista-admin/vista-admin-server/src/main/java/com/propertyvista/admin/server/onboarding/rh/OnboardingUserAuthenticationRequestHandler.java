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
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.essentials.server.AbstractAntiBot.LoginType;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.admin.server.onboarding.OnboardingXMLUtils;
import com.propertyvista.admin.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.onboarding.OnboardingUserAuthenticationRequestIO;
import com.propertyvista.onboarding.OnboardingUserAuthenticationResponseIO;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class OnboardingUserAuthenticationRequestHandler extends AbstractRequestHandler<OnboardingUserAuthenticationRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(OnboardingUserAuthenticationRequestHandler.class);

    public OnboardingUserAuthenticationRequestHandler() {
        super(OnboardingUserAuthenticationRequestIO.class);
    }

    @Override
    public ResponseIO execute(OnboardingUserAuthenticationRequestIO request) {
        log.info("User {} performed {} for email {}", new Object[] { request.email().getValue(), "OnboardingUserAuthentication", request.email().getValue() });

        OnboardingUserAuthenticationResponseIO response = EntityFactory.create(OnboardingUserAuthenticationResponseIO.class);
        response.success().setValue(Boolean.TRUE);

        String email = PasswordEncryptor.normalizeEmailAddress(request.email().getValue());
        AbstractAntiBot.assertLogin(LoginType.userLogin, email, new Pair<String, String>(request.captcha().challenge().getValue(), request.captcha().response()
                .getValue()));

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

        OnboardingUserCredential cr = Persistence.service().retrieve(OnboardingUserCredential.class, user.getPrimaryKey());
        if (cr == null) {
            throw new UserRuntimeException("Invalid User Account. Please Contact Support");
        }

        if (!cr.enabled().isBooleanTrue()) {
            response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.PermissionDenied);
            return response;
        }

        if (cr.pmc().getPrimaryKey() != null) {
            Pmc pmc = Persistence.service().retrieve(Pmc.class, cr.pmc().getPrimaryKey());
            response.onboardingAccountId().set(pmc.onboardingAccountId());

            if (pmc.status().getValue() != PmcStatus.Created) {
                String curNameSpace = NamespaceManager.getNamespace();

                try {
                    NamespaceManager.setNamespace(pmc.namespace().getValue());

                    EntityQueryCriteria<CrmUserCredential> crmUCrt = EntityQueryCriteria.create(CrmUserCredential.class);
                    crmUCrt.add(PropertyCriterion.eq(crmUCrt.proto().roles().$().behaviors(), VistaCrmBehavior.PropertyVistaAccountOwner));
                    crmUCrt.add(PropertyCriterion.eq(crmUCrt.proto().onboardingUser(), user.getPrimaryKey()));

                    CrmUserCredential crmCred = Persistence.service().retrieve(crmUCrt);
                    if (crmCred == null) {
                        response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.PermissionDenied);
                        return response;
                    }

                    if (!PasswordEncryptor.checkPassword(request.password().getValue(), crmCred.credential().getValue())) {
                        if (AbstractAntiBot.authenticationFailed(LoginType.userLogin, email)) {
                            response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.ChallengeVerificationRequired);
                            response.reCaptchaPublicKey().setValue(
                                    ((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getReCaptchaPublicKey());
                            return response;
                        } else {
                            response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.AuthenticationFailed);
                            return response;
                        }
                    }

                    response.role().setValue(OnboardingXMLUtils.convertRole(cr.behavior().getValue()));
                    response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.OK);

                    return response;
                } finally {
                    NamespaceManager.setNamespace(curNameSpace);
                }
            } else {
                if (!PasswordEncryptor.checkPassword(request.password().getValue(), cr.credential().getValue())) {
                    log.info("Invalid password for user {}", email);
                    if (AbstractAntiBot.authenticationFailed(LoginType.userLogin, email)) {
                        response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.ChallengeVerificationRequired);
                        response.reCaptchaPublicKey()
                                .setValue(((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getReCaptchaPublicKey());
                        return response;
                    } else {
                        response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.AuthenticationFailed);
                        return response;
                    }
                }
                if (!cr.accessKey().isNull()) {
                    cr.accessKey().setValue(null);
                    Persistence.service().persist(cr);
                    Persistence.service().commit();
                }
                if (cr.requiredPasswordChangeOnNextLogIn().isBooleanTrue()) {
                    response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.OK_PasswordChangeRequired);
                    return response;
                } else {
                    response.role().setValue(OnboardingXMLUtils.convertRole(cr.behavior().getValue()));
                    response.onboardingAccountId().set(pmc.onboardingAccountId());
                    response.email().setValue(user.email().getValue());
                    response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.OK);
                    return response;
                }
            }

        } else {
            if (!PasswordEncryptor.checkPassword(request.password().getValue(), cr.credential().getValue())) {
                log.info("Invalid password for user {}", email);
                if (AbstractAntiBot.authenticationFailed(LoginType.userLogin, email)) {
                    response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.ChallengeVerificationRequired);
                    response.reCaptchaPublicKey().setValue(((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getReCaptchaPublicKey());
                    return response;
                } else {
                    response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.AuthenticationFailed);
                    return response;
                }
            }
            if (!cr.accessKey().isNull()) {
                cr.accessKey().setValue(null);
                Persistence.service().persist(cr);
                Persistence.service().commit();
            }
            if (cr.requiredPasswordChangeOnNextLogIn().isBooleanTrue()) {
                response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.OK_PasswordChangeRequired);
                response.email().setValue(user.email().getValue());
                return response;
            } else {
                response.role().setValue(OnboardingXMLUtils.convertRole(cr.behavior().getValue()));
                response.onboardingAccountId().set(cr.onboardingAccountId());
                response.email().setValue(user.email().getValue());
                response.status().setValue(OnboardingUserAuthenticationResponseIO.AuthenticationStatusCode.OK);
                return response;
            }
        }
    }
}
