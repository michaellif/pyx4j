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
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.essentials.server.AbstractAntiBot.LoginType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.server.EmailValidator;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.onboarding.OnboardingUserPasswordChangeRequestIO;
import com.propertyvista.onboarding.ResponseIO;
import com.propertyvista.operations.domain.security.OnboardingUserCredential;
import com.propertyvista.operations.server.onboarding.rhf.AbstractRequestHandler;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class OnboardingUserPasswordChangeRequestHandler extends AbstractRequestHandler<OnboardingUserPasswordChangeRequestIO> {

    private final static Logger log = LoggerFactory.getLogger(OnboardingUserPasswordChangeRequestHandler.class);

    private static final I18n i18n = I18n.get(OnboardingUserPasswordResetRequestHandler.class);

    public OnboardingUserPasswordChangeRequestHandler() {
        super(OnboardingUserPasswordChangeRequestIO.class);
    }

    @Override
    public ResponseIO execute(OnboardingUserPasswordChangeRequestIO request) {
        log.info("User {} requested {}", new Object[] { request.email().getValue(), "OnboardingUserPasswordChange" });

        ResponseIO response = EntityFactory.create(ResponseIO.class);

        String email = EmailValidator.normalizeEmailAddress(request.email().getValue());
        AbstractAntiBot.assertLogin(LoginType.userLogin, email, null);

        EntityQueryCriteria<OnboardingUser> criteria = EntityQueryCriteria.create(OnboardingUser.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().email(), email));
        List<OnboardingUser> users = Persistence.service().query(criteria);
        if (users.size() != 1) {
            log.debug("Invalid log-in attempt {} rs {}", email, users.size());
            AbstractAntiBot.authenticationFailed(LoginType.userLogin, email);
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

        boolean validateAgainstOnboarding = true;

        if (!cr.pmc().isNull()) {
            Pmc pmc = cr.pmc();

            if (!ServerSideFactory.create(PmcFacade.class).isOnboardingEnabled(pmc)) {
                response.success().setValue(Boolean.FALSE);
                return response;
            }

            if (pmc.status().getValue() != PmcStatus.Created) {
                String curNameSpace = NamespaceManager.getNamespace();

                try {
                    NamespaceManager.setNamespace(pmc.namespace().getValue());

                    EntityQueryCriteria<CrmUserCredential> crmUCrt = EntityQueryCriteria.create(CrmUserCredential.class);
                    crmUCrt.add(PropertyCriterion.eq(crmUCrt.proto().roles().$().behaviors(), VistaCrmBehavior.PropertyVistaAccountOwner));
                    crmUCrt.add(PropertyCriterion.eq(crmUCrt.proto().onboardingUser(), cr.getPrimaryKey()));
                    CrmUserCredential credential = Persistence.service().retrieve(crmUCrt);

                    if (credential != null) {
                        if (!ServerSideFactory.create(PasswordEncryptorFacade.class).checkUserPassword(request.currentPassword().getValue(),
                                credential.credential().getValue())) {
                            AbstractAntiBot.authenticationFailed(LoginType.userLogin, email);
                            log.info("Invalid password for user {}", email);
                            response.success().setValue(Boolean.FALSE);

                            return response;
                        }

                        if (ServerSideFactory.create(PasswordEncryptorFacade.class).checkUserPassword(request.newPassword().getValue(),
                                credential.credential().getValue())) {
                            log.info("Invalid new password for user {}", email);
                            response.errorMessage().setValue(i18n.tr("Your password cannot repeat your previous password"));
                            response.success().setValue(Boolean.FALSE);

                            return response;
                        }

                        credential.credential().setValue(
                                ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(request.newPassword().getValue()));
                        credential.requiredPasswordChangeOnNextLogIn().setValue(Boolean.FALSE);
                        ServerSideFactory.create(AuditFacade.class).credentialsUpdated(credential.user());
                        Persistence.service().persist(credential);
                        Persistence.service().commit();

                        validateAgainstOnboarding = false;
                    }

                } finally {
                    NamespaceManager.setNamespace(curNameSpace);
                }
            }
        }

        if (validateAgainstOnboarding) {
            if (!ServerSideFactory.create(PasswordEncryptorFacade.class).checkUserPassword(request.currentPassword().getValue(), cr.credential().getValue())) {
                AbstractAntiBot.authenticationFailed(LoginType.userLogin, email);
                log.info("Invalid password for user {}", email);
                response.success().setValue(Boolean.FALSE);

                return response;
            }

            if (ServerSideFactory.create(PasswordEncryptorFacade.class).checkUserPassword(request.newPassword().getValue(), cr.credential().getValue())) {
                log.info("Invalid new password for user {}", email);
                response.errorMessage().setValue(i18n.tr("Your password cannot repeat your previous password"));
                response.success().setValue(Boolean.FALSE);

                return response;
            }
        }

        cr.accessKey().setValue(null);
        cr.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(request.newPassword().getValue()));
        cr.passwordUpdated().setValue(new Date());
        cr.requiredPasswordChangeOnNextLogIn().setValue(Boolean.FALSE);
        Persistence.service().persist(cr);
        Persistence.service().commit();

        response.success().setValue(Boolean.TRUE);
        return response;
    }
}
