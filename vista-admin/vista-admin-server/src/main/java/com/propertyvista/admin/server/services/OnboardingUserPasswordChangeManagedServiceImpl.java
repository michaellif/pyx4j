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

import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.PasswordChangeRequest;

import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.admin.rpc.services.OnboardingUserPasswordChangeManagedService;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.server.common.security.VistaManagedPasswordChangeServiceImpl;

public class OnboardingUserPasswordChangeManagedServiceImpl extends VistaManagedPasswordChangeServiceImpl<OnboardingUserCredential> implements
        OnboardingUserPasswordChangeManagedService {

    private final static Logger log = LoggerFactory.getLogger(OnboardingUserPasswordChangeManagedServiceImpl.class);

    public OnboardingUserPasswordChangeManagedServiceImpl() {
        super(OnboardingUserCredential.class);
    }

    @Override
    public void changePassword(AsyncCallback<VoidSerializable> callback, PasswordChangeRequest request) {
        // TODO USe CRM use if exists
        if (VistaTODO.VISTA_1588) {
            log.warn("TODO - implement CRM User/OnboardingUser synchronization");
        }
        super.changePassword(callback, request);
    }

}
