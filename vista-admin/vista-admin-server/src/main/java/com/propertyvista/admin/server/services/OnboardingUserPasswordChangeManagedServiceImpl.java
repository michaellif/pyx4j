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
package com.propertyvista.admin.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.security.shared.SecurityViolationException;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.admin.rpc.services.OnboardingUserPasswordChangeManagedService;
import com.propertyvista.biz.system.UserManagementFacade;
import com.propertyvista.server.common.security.VistaManagedPasswordChangeServiceImpl;

public class OnboardingUserPasswordChangeManagedServiceImpl extends VistaManagedPasswordChangeServiceImpl<OnboardingUserCredential> implements
        OnboardingUserPasswordChangeManagedService {

    private final static Logger log = LoggerFactory.getLogger(OnboardingUserPasswordChangeManagedServiceImpl.class);

    private static final I18n i18n = I18n.get(VistaManagedPasswordChangeServiceImpl.class);

    public OnboardingUserPasswordChangeManagedServiceImpl() {
        super(OnboardingUserCredential.class);
    }

    @Override
    public void changePassword(AsyncCallback<VoidSerializable> callback, PasswordChangeRequest request) {
        if (Context.getVisit().getUserVisit().getPrincipalPrimaryKey().equals(request.userPk().getValue())) {
            throw new SecurityViolationException(i18n.tr("Permission denied"));
        }

        ServerSideFactory.create(UserManagementFacade.class).managedSetPassword(OnboardingUserCredential.class, request);
        callback.onSuccess(null);
    }
}
