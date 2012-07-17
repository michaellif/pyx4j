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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.crm.rpc.services.security.CrmAccountRecoveryOptionsUserService;
import com.propertyvista.domain.security.SecurityQuestion;
import com.propertyvista.portal.rpc.shared.dto.AccountRecoveryOptionsDTO;
import com.propertyvista.server.common.security.VistaContext;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class CrmAccountRecoveryOptionsUserServiceImpl implements CrmAccountRecoveryOptionsUserService {

    private static Logger log = LoggerFactory.getLogger(CrmAccountRecoveryOptionsUserServiceImpl.class);

    @Override
    public void obtainRecoveryOptions(AsyncCallback<AccountRecoveryOptionsDTO> callback, AuthenticationRequest request) {
        AccountRecoveryOptionsDTO result = EntityFactory.create(AccountRecoveryOptionsDTO.class);
        result.securityQuestionsSuggestions().addAll(Persistence.service().query(EntityQueryCriteria.create(SecurityQuestion.class)));

        CrmUserCredential credential = Persistence.service().retrieve(CrmUserCredential.class, VistaContext.getCurrentUserPrimaryKey());

        result.securityQuestion().setValue(credential.securityQuestion().getValue());
        result.securityAnswer().setValue(credential.securityAnswer().getValue());
        result.recoveryEmail().setValue(credential.recoveryEmail().getValue());

        callback.onSuccess(result);
    }

    @Override
    public void updateRecoveryOptions(AsyncCallback<VoidSerializable> callback, AccountRecoveryOptionsDTO request) {
        CrmUserCredential credential = Persistence.service().retrieve(CrmUserCredential.class, VistaContext.getCurrentUserPrimaryKey());

        credential.securityQuestion().setValue(request.securityQuestion().getValue());
        credential.securityAnswer().setValue(request.securityAnswer().getValue());
        credential.recoveryEmail().setValue(request.recoveryEmail().getValue());

        ServerSideFactory.create(AuditFacade.class).credentialsUpdated(credential.user());
        Persistence.service().persist(credential);
        Persistence.service().commit();
        log.info("AccountRecoveryOptions changed by user {} {}", Context.getVisit().getUserVisit().getEmail(), VistaContext.getCurrentUserPrimaryKey());

        callback.onSuccess(null);
    }

}
