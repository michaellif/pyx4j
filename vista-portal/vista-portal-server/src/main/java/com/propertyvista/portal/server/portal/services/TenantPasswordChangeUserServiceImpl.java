/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 27, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.AbstractAntiBot;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.ChallengeVerificationRequired;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.portal.rpc.portal.services.TenantPasswordChangeUserService;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.common.security.VistaContext;
import com.propertyvista.server.domain.security.TenantUserCredential;

public class TenantPasswordChangeUserServiceImpl implements TenantPasswordChangeUserService {

    private static Logger log = LoggerFactory.getLogger(TenantPasswordChangeUserServiceImpl.class);

    private static final I18n i18n = I18n.get(TenantPasswordChangeUserServiceImpl.class);

    @Override
    public void changePassword(AsyncCallback<VoidSerializable> callback, PasswordChangeRequest request) {
        TenantUserCredential cr = Persistence.service().retrieve(TenantUserCredential.class, VistaContext.getCurrentUserPrimaryKey());
        if (!cr.enabled().isBooleanTrue()) {
            throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
        }
        if (!PasswordEncryptor.checkPassword(request.currentPassword().getValue(), cr.credential().getValue())) {
            log.info("Invalid password for user {}", Context.getVisit().getUserVisit().getEmail());
            if (AbstractAntiBot.authenticationFailed(Context.getVisit().getUserVisit().getEmail())) {
                throw new ChallengeVerificationRequired(i18n.tr("Too Many Failed Log In Attempts"));
            } else {
                throw new UserRuntimeException(AbstractAntiBot.GENERIC_LOGIN_FAILED_MESSAGE);
            }
        }
        cr.accessKey().setValue(null);
        cr.credential().setValue(PasswordEncryptor.encryptPassword(request.newPassword().getValue()));
        cr.requiredPasswordChangeOnNextLogIn().setValue(Boolean.FALSE);
        Persistence.service().persist(cr);
        log.info("password changed by user {}", Context.getVisit().getUserVisit().getEmail(), VistaContext.getCurrentUserPrimaryKey());
        callback.onSuccess(null);
    }

}
