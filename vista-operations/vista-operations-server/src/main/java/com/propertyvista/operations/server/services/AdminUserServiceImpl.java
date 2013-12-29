/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.server.EmailValidator;

import com.propertyvista.operations.domain.security.OperationsUserCredential;
import com.propertyvista.operations.rpc.dto.OperationsUserDTO;
import com.propertyvista.operations.rpc.services.AdminUserService;

public class AdminUserServiceImpl extends AbstractCrudServiceDtoImpl<OperationsUserCredential, OperationsUserDTO> implements AdminUserService {

    public AdminUserServiceImpl() {
        super(OperationsUserCredential.class, OperationsUserDTO.class);
    }

    @Override
    protected void bind() {
        bind(toProto.name(), boProto.user().name());
        bind(toProto.email(), boProto.user().email());
        bind(toProto.created(), boProto.user().created());
        bind(toProto.updated(), boProto.user().updated());

        bind(toProto.enabled(), boProto.enabled());
        bind(toProto.requiredPasswordChangeOnNextLogIn(), boProto.requiredPasswordChangeOnNextLogIn());
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<OperationsUserDTO>> callback, EntityListCriteria<OperationsUserDTO> dtoCriteria) {
        throw new IllegalStateException("this operation is not supported for this service");
    }

    @Override
    public void create(AsyncCallback<Key> callback, OperationsUserDTO dto) {
        throw new IllegalStateException("this operation is not supported for this service");
    }

    @Override
    protected void enhanceRetrieved(OperationsUserCredential bo, OperationsUserDTO to, RetrieveTarget retrieveTarget ) {
        if (!bo.behaviors().isEmpty()) {
            to.role().setValue(bo.behaviors().iterator().next());
        }
    }

    @Override
    protected void retrievedSingle(OperationsUserCredential bo, RetrieveTarget retrieveTarget ) {
        Persistence.service().retrieve(bo.user());
    }

    @Override
    protected void persist(OperationsUserCredential dbo, OperationsUserDTO to) {
        dbo.user().email().setValue(EmailValidator.normalizeEmailAddress(to.email().getValue()));
        Persistence.service().merge(dbo.user());

        // ignore role changes (don't copy role from dto to dbo);

        dbo.setPrimaryKey(dbo.user().getPrimaryKey());
        Persistence.service().merge(dbo);
    }

}
