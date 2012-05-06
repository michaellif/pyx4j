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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AbstractPasswordChangeService;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.domain.security.AbstractUser;
import com.propertyvista.domain.security.AbstractUserCredential;

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
        AbstractAntiBot.assertLogin(credential.user().email().getValue(), null);
        if (!PasswordEncryptor.checkPassword(request.currentPassword().getValue(), credential.credential().getValue())) {
            log.info("Invalid password for user {}", Context.getVisit().getUserVisit().getEmail());
            if (AbstractAntiBot.authenticationFailed(Context.getVisit().getUserVisit().getEmail())) {
                throw new ChallengeVerificationRequired(i18n.tr("Too Many Failed Log In Attempts"));
            } else {
                throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
        }
        if (PasswordEncryptor.checkPassword(request.newPassword().getValue(), credential.credential().getValue())) {
            throw new UserRuntimeException(i18n.tr("Your password cannot repeat your previous password"));
        }
        credential.accessKey().setValue(null);
        credential.credential().setValue(PasswordEncryptor.encryptPassword(request.newPassword().getValue()));
        credential.credentialUpdated().setValue(new Date());
        credential.requiredPasswordChangeOnNextLogIn().setValue(Boolean.FALSE);
        Persistence.service().persist(credential);
        Persistence.service().commit();
        log.info("password changed by user {} {}", Context.getVisit().getUserVisit().getEmail(), VistaContext.getCurrentUserPrimaryKey());
        callback.onSuccess(null);
    }
}
