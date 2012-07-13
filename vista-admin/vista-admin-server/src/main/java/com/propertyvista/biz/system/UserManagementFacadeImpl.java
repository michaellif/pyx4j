/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 11, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.biz.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.domain.security.AbstractUser;
import com.propertyvista.domain.security.AbstractUserCredential;
import com.propertyvista.server.common.security.PasswordEncryptor;

public class UserManagementFacadeImpl implements UserManagementFacade {

    private static Logger log = LoggerFactory.getLogger(UserManagementFacadeImpl.class);

    @Override
    public <E extends AbstractUserCredential<? extends AbstractUser>> void selfChangePassword(Class<E> credentialClass, PasswordChangeRequest request) {
        // TODO Auto-generated method stub

    }

    @Override
    public <E extends AbstractUserCredential<? extends AbstractUser>> void managedSetPassword(Class<E> credentialClass, PasswordChangeRequest request) {

        E credential = Persistence.service().retrieve(credentialClass, request.userPk().getValue());
        credential.credential().setValue(PasswordEncryptor.encryptPassword(request.newPassword().getValue()));
        if (request.requireChangePasswordOnNextSignIn().isBooleanTrue()) {
            credential.requiredPasswordChangeOnNextLogIn().setValue(Boolean.TRUE);
        }
        Persistence.service().persist(credential);
        Persistence.service().commit();

        log.info("password changed by user {} for {}", Context.getVisit().getUserVisit().getEmail(), request.userPk());
    }

}
