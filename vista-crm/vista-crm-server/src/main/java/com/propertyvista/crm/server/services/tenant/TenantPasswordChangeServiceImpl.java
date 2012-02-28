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
package com.propertyvista.crm.server.services.tenant;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.security.shared.SecurityViolationException;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.crm.rpc.services.tenant.TenantPasswordChangeService;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.domain.security.TenantUserCredential;

public class TenantPasswordChangeServiceImpl implements TenantPasswordChangeService {

    @Override
    public void changePassword(AsyncCallback<VoidSerializable> callback, PasswordChangeRequest request) {
        if (Context.getVisit().getUserVisit().getPrincipalPrimaryKey().equals(request.userPk().getValue())) {
            throw new SecurityViolationException("Permission denied");
        }
        TenantUserCredential credential = Persistence.service().retrieve(TenantUserCredential.class, request.userPk().getValue());
        credential.credential().setValue(PasswordEncryptor.encryptPassword(request.newPassword().getValue()));
        if (request.requireChangePasswordOnNextSignIn().isBooleanTrue()) {
            credential.requiredPasswordChangeOnNextLogIn().setValue(Boolean.TRUE);
        }
        Persistence.service().persist(credential);
        Persistence.service().commit();
        callback.onSuccess(null);
    }
}
