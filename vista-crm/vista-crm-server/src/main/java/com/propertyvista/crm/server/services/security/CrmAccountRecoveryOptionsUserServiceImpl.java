/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.security;

import static com.pyx4j.commons.CommonsStringUtils.isEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.essentials.server.AbstractAntiBot.LoginType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.VistaContext;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.crm.rpc.services.security.CrmAccountRecoveryOptionsUserService;
import com.propertyvista.domain.security.CrmUserCredential;
import com.propertyvista.domain.security.SecurityQuestion;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.portal.rpc.shared.dto.AccountRecoveryOptionsDTO;

public class CrmAccountRecoveryOptionsUserServiceImpl implements CrmAccountRecoveryOptionsUserService {

    private static final I18n i18n = I18n.get(CrmAccountRecoveryOptionsUserServiceImpl.class);

    private static Logger log = LoggerFactory.getLogger(CrmAccountRecoveryOptionsUserServiceImpl.class);

    @Override
    public void obtainRecoveryOptions(AsyncCallback<AccountRecoveryOptionsDTO> callback, AuthenticationRequest request) {
        AccountRecoveryOptionsDTO result = EntityFactory.create(AccountRecoveryOptionsDTO.class);
        result.securityQuestionsSuggestions().addAll(Persistence.service().query(EntityQueryCriteria.create(SecurityQuestion.class)));

        CrmUserCredential credential = Persistence.service().retrieve(CrmUserCredential.class, VistaContext.getCurrentUserPrimaryKey());

        if (!SecurityController.check(VistaBasicBehavior.CRMSetupAccountRecoveryOptionsRequired)) {
            // Verify password
            Persistence.service().retrieve(credential.user());
            AbstractAntiBot.assertLogin(LoginType.userLogin, credential.user().email().getValue(), null);
            if (!ServerSideFactory.create(PasswordEncryptorFacade.class).checkUserPassword(request.password().getValue(), credential.credential().getValue())) {
                log.info("Invalid password for user {}", ServerContext.getVisit().getUserVisit().getEmail());
                if (AbstractAntiBot.authenticationFailed(LoginType.userLogin, ServerContext.getVisit().getUserVisit().getEmail())) {
                    throw new ChallengeVerificationRequired();
                } else {
                    throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
                }
            }
        }

        result.useSecurityQuestionChallengeForPasswordReset().setValue(
                !isEmpty(credential.securityQuestion().getValue()) || SecurityController.check(VistaBasicBehavior.CRMPasswordChangeRequiresSecurityQuestion));

        result.securityQuestion().setValue(credential.securityQuestion().getValue());
        result.securityAnswer().setValue(credential.securityAnswer().getValue());
        result.recoveryEmail().setValue(credential.recoveryEmail().getValue());

        callback.onSuccess(result);
    }

    @Override
    public void updateRecoveryOptions(AsyncCallback<VoidSerializable> callback, AccountRecoveryOptionsDTO request) {
        CrmUserCredential credentials = Persistence.service().retrieve(CrmUserCredential.class, VistaContext.getCurrentUserPrimaryKey());

        if (!SecurityController.check(VistaBasicBehavior.CRMSetupAccountRecoveryOptionsRequired)) {
            // Verify password
            Persistence.service().retrieve(credentials.user());
            AbstractAntiBot.assertLogin(LoginType.userLogin, credentials.user().email().getValue(), null);
            if (!ServerSideFactory.create(PasswordEncryptorFacade.class).checkUserPassword(request.password().getValue(), credentials.credential().getValue())) {
                log.info("Invalid password for user {}", ServerContext.getVisit().getUserVisit().getEmail());
                if (AbstractAntiBot.authenticationFailed(LoginType.userLogin, ServerContext.getVisit().getUserVisit().getEmail())) {
                    throw new ChallengeVerificationRequired();
                } else {
                    throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
                }
            }
        }

        if (request.useSecurityQuestionChallengeForPasswordReset().getValue(false)
                || SecurityController.check(VistaBasicBehavior.CRMPasswordChangeRequiresSecurityQuestion)) {
            assertIsDefined(request.securityQuestion());
            assertIsDefined(request.securityAnswer());
        }

        credentials.securityQuestion().setValue(request.securityQuestion().getValue());
        credentials.securityAnswer().setValue(request.securityAnswer().getValue());
        credentials.recoveryEmail().setValue(request.recoveryEmail().getValue());

        ServerSideFactory.create(AuditFacade.class).credentialsUpdated(credentials.user());
        Persistence.service().persist(credentials);
        Persistence.service().commit();
        log.info("AccountRecoveryOptions changed by user {} {}", ServerContext.getVisit().getUserVisit().getEmail(), VistaContext.getCurrentUserPrimaryKey());

        if (SecurityController.check(VistaBasicBehavior.CRMSetupAccountRecoveryOptionsRequired)) {
            ServerContext.getVisit().setAclRevalidationRequired();
        }
        callback.onSuccess(null);
    }

    private void assertIsDefined(IPrimitive<String> str) {
        if (isEmpty(str.getValue())) {
            throw new UserRuntimeException(true, i18n.tr("\"{0}\" is required", str.getMeta().getCaption()));
        }
    }

}
