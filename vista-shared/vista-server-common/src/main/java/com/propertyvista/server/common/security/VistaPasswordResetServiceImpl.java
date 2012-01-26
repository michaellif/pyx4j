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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.security.rpc.PasswordResetService;
import com.pyx4j.security.server.AuthenticationServiceImpl;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.domain.security.AbstractUser;
import com.propertyvista.server.domain.security.AbstractUserCredential;

public abstract class VistaPasswordResetServiceImpl<E extends AbstractUserCredential<? extends AbstractUser>> implements PasswordResetService {

    private static Logger log = LoggerFactory.getLogger(VistaPasswordResetServiceImpl.class);

    private static final I18n i18n = I18n.get(VistaPasswordResetServiceImpl.class);

    protected final Class<E> credentialClass;

    protected VistaPasswordResetServiceImpl(Class<E> credentialClass) {
        this.credentialClass = credentialClass;
    }

    @Override
    public void resetPassword(AsyncCallback<AuthenticationResponse> callback, PasswordChangeRequest request) {
        E cr = Persistence.service().retrieve(credentialClass, VistaContext.getCurrentUserPrimaryKey());
        if (cr == null) {
            throw new UserRuntimeException(i18n.tr("Invalid User Account. Please Contact Support"));
        }
        if (!cr.enabled().isBooleanTrue()) {
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }

        cr.accessKey().setValue(null);
        cr.credential().setValue(PasswordEncryptor.encryptPassword(request.newPassword().getValue()));
        Persistence.service().persist(cr);
        log.info("password changed by user {}", Context.getVisit().getUserVisit().getEmail(), VistaContext.getCurrentUserPrimaryKey());

        // logout
        VistaLifecycle.endSession();
        callback.onSuccess(AuthenticationServiceImpl.createAuthenticationResponse(null));
    }
}
