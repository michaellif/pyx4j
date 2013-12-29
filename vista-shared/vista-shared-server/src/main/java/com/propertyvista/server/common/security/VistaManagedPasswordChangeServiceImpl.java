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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AbstractPasswordChangeService;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.security.shared.SecurityViolationException;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.AbstractUserCredential;

/**
 * Service used by managers,administrator to administer their subordinate users.
 */
public abstract class VistaManagedPasswordChangeServiceImpl<E extends AbstractUserCredential<? extends AbstractUser>> implements AbstractPasswordChangeService {

    private static Logger log = LoggerFactory.getLogger(VistaManagedPasswordChangeServiceImpl.class);

    private static final I18n i18n = I18n.get(VistaManagedPasswordChangeServiceImpl.class);

    protected final Class<E> credentialClass;

    protected VistaManagedPasswordChangeServiceImpl(Class<E> credentialClass) {
        this.credentialClass = credentialClass;
    }

    @Override
    public void changePassword(AsyncCallback<VoidSerializable> callback, PasswordChangeRequest request) {
        if (EntityFactory.getEntityPrototype(credentialClass).user().getValueClass().equals(VistaContext.getCurrentUser().getValueClass())) {
            if (Context.getVisit().getUserVisit().getPrincipalPrimaryKey().equals(request.userPk().getValue())) {
                throw new SecurityViolationException(i18n.tr("Permission denied"));
            }
        }
        E credential = Persistence.service().retrieve(credentialClass, request.userPk().getValue());
        credential.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(request.newPassword().getValue()));
        if (request.requireChangePasswordOnNextSignIn().isBooleanTrue()) {
            credential.requiredPasswordChangeOnNextLogIn().setValue(Boolean.TRUE);
        }
        ServerSideFactory.create(AuditFacade.class).credentialsUpdated(credential.user());
        Persistence.service().persist(credential);
        Persistence.service().commit();
        log.info("password changed by user {} for {}", Context.getVisit().getUserVisit().getEmail(), request.userPk());
        callback.onSuccess(null);
    }

}
