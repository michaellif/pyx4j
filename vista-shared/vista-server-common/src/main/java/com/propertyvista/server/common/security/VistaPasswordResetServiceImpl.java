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
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AbstractPasswordResetService;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.security.rpc.PasswordResetQuestion;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.AbstractUserCredential;

public abstract class VistaPasswordResetServiceImpl<E extends AbstractUserCredential<? extends AbstractUser>> implements AbstractPasswordResetService {

    private static Logger log = LoggerFactory.getLogger(VistaPasswordResetServiceImpl.class);

    private static final I18n i18n = I18n.get(VistaPasswordResetServiceImpl.class);

    protected final Class<E> credentialClass;

    protected VistaPasswordResetServiceImpl(Class<E> credentialClass) {
        this.credentialClass = credentialClass;
    }

    protected abstract AuthenticationResponse authenticate(E credentials);

    @Override
    public void obtainPasswordResetQuestion(AsyncCallback<PasswordResetQuestion> callback) {
        PasswordResetQuestion response = EntityFactory.create(PasswordResetQuestion.class);
        E credentials = Persistence.service().retrieve(credentialClass, VistaContext.getCurrentUserPrimaryKey());
        if (credentials == null) {
            throw new UserRuntimeException(i18n.tr("Invalid User Account. Please Contact Support"));
        }
        response.securityQuestion().setValue(credentials.securityQuestion().getValue());
        callback.onSuccess(response);
    }

    @Override
    public void resetPassword(AsyncCallback<AuthenticationResponse> callback, PasswordChangeRequest request) {
        E credentials = Persistence.service().retrieve(credentialClass, VistaContext.getCurrentUserPrimaryKey());
        if (credentials == null) {
            throw new UserRuntimeException(i18n.tr("Invalid User Account. Please Contact Support"));
        }
        if (!credentials.enabled().isBooleanTrue()) {
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }

        if ((!credentials.securityQuestion().isNull()) && (!credentials.securityAnswer().equals(request.securityAnswer()))) {
            throw new UserRuntimeException(i18n.tr("The answer to security question is incorrect"));
        }

        credentials.accessKey().setValue(null);
        credentials.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(request.newPassword().getValue()));
        credentials.passwordUpdated().setValue(new Date());
        credentials.requiredPasswordChangeOnNextLogIn().setValue(Boolean.FALSE);
        Persistence.service().persist(credentials);
        Persistence.service().commit();
        log.info("password changed by user {} {}", Context.getVisit().getUserVisit().getEmail(), VistaContext.getCurrentUserPrimaryKey());

        callback.onSuccess(authenticate(credentials));
    }

}
