/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.security;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationRequest;

import com.propertyvista.crm.rpc.services.security.CrmAccountRecoveryOptionsUserService;
import com.propertyvista.domain.security.SecurityQuestion;
import com.propertyvista.portal.rpc.shared.dto.AccountRecoveryOptionsDTO;

public class CrmAccountRecoveryOptionsUserServiceImpl implements CrmAccountRecoveryOptionsUserService {

    @Override
    public void obtainRecoveryOptions(AsyncCallback<AccountRecoveryOptionsDTO> callback, AuthenticationRequest request) {
        AccountRecoveryOptionsDTO result = EntityFactory.create(AccountRecoveryOptionsDTO.class);
        result.securityQuestions().addAll(Persistence.service().query(EntityQueryCriteria.create(SecurityQuestion.class)));
        callback.onSuccess(result);
    }

    @Override
    public void updateRecoveryOptions(AsyncCallback<VoidSerializable> callback, AccountRecoveryOptionsDTO request) {
        // TODO Auto-generated method stub
    }

}
