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
 */
package com.propertyvista.server.common.security;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AbstractPasswordChangeService;
import com.pyx4j.security.rpc.PasswordChangeRequest;

import com.propertyvista.biz.system.UserManagementFacade;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.AbstractUserCredential;

/**
 * Service used by User them self to change password
 */
public abstract class VistaUserSelfPasswordChangeServiceImpl<E extends AbstractUserCredential<? extends AbstractUser>> implements AbstractPasswordChangeService {

    protected final Class<E> credentialClass;

    protected VistaUserSelfPasswordChangeServiceImpl(Class<E> credentialClass) {
        this.credentialClass = credentialClass;
    }

    @Override
    public void changePassword(AsyncCallback<VoidSerializable> callback, PasswordChangeRequest request) {
        ServerSideFactory.create(UserManagementFacade.class).selfChangePassword(credentialClass, request);
        callback.onSuccess(null);
    }
}
