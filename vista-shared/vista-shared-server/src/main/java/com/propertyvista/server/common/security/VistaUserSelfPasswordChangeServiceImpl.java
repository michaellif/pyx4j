/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.security;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.essentials.server.AbstractAntiBot.LoginType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AbstractPasswordChangeService;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.VistaContext;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.AbstractUserCredential;

/**
 * Service used by User them self to change password
 */
public abstract class VistaUserSelfPasswordChangeServiceImpl<E extends AbstractUserCredential<? extends AbstractUser>> implements AbstractPasswordChangeService {

    private static Logger log = LoggerFactory.getLogger(VistaUserSelfPasswordChangeServiceImpl.class);

    private static final I18n i18n = I18n.get(VistaUserSelfPasswordChangeServiceImpl.class);

    protected final Class<E> credentialClass;

    protected VistaUserSelfPasswordChangeServiceImpl(Class<E> credentialClass) {
        this.credentialClass = credentialClass;
    }

    @Override
    public void changePassword(AsyncCallback<VoidSerializable> callback, PasswordChangeRequest request) {
        E credential = Persistence.service().retrieve(credentialClass, VistaContext.getCurrentUserPrimaryKey());
        if (!credential.enabled().isBooleanTrue()) {
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        Persistence.service().retrieve(credential.user());
        AbstractAntiBot.assertLogin(LoginType.userLogin, credential.user().email().getValue(), null);
        if (!ServerSideFactory.create(PasswordEncryptorFacade.class)
                .checkUserPassword(request.currentPassword().getValue(), credential.credential().getValue())) {
            log.info("Invalid password for user {}", Context.getVisit().getUserVisit().getEmail());
            if (AbstractAntiBot.authenticationFailed(LoginType.userLogin, Context.getVisit().getUserVisit().getEmail())) {
                throw new ChallengeVerificationRequired(i18n.tr("Too Many Failed Log In Attempts"));
            } else {
                throw new UserRuntimeException(true, AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
        }
        if (ServerSideFactory.create(PasswordEncryptorFacade.class).checkUserPassword(request.newPassword().getValue(), credential.credential().getValue())) {
            throw new UserRuntimeException(true, i18n.tr("Your password cannot repeat your previous password"));
        }
        credential.accessKey().setValue(null);
        credential.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(request.newPassword().getValue()));
        credential.passwordUpdated().setValue(new Date());
        credential.requiredPasswordChangeOnNextLogIn().setValue(Boolean.FALSE);

        ServerSideFactory.create(AuditFacade.class).credentialsUpdated(credential.user());

        Persistence.service().persist(credential);
        Persistence.service().commit();
        log.info("password changed by user {} {}", Context.getVisit().getUserVisit().getEmail(), VistaContext.getCurrentUserPrimaryKey());
        callback.onSuccess(null);
    }
}
