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
package com.propertyvista.admin.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.admin.domain.security.AdminUserCredential;
import com.propertyvista.admin.rpc.AdminUserDTO;
import com.propertyvista.admin.rpc.services.AdminUserService;
import com.propertyvista.server.common.security.PasswordEncryptor;

public class AdminUserServiceImpl extends AbstractCrudServiceDtoImpl<AdminUserCredential, AdminUserDTO> implements AdminUserService {

    public AdminUserServiceImpl() {
        super(AdminUserCredential.class, AdminUserDTO.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.name(), dboProto.user().name());
        bind(dtoProto.email(), dboProto.user().email());
        bind(dtoProto.created(), dboProto.user().created());
        bind(dtoProto.updated(), dboProto.user().updated());

        bind(dtoProto.enabled(), dboProto.enabled());
        bind(dtoProto.requireChangePasswordOnNextLogIn(), dboProto.requiredPasswordChangeOnNextLogIn());
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<AdminUserDTO>> callback, EntityListCriteria<AdminUserDTO> dtoCriteria) {
        throw new IllegalStateException("this operation is not supported for this service");
    }

    @Override
    public void create(AsyncCallback<AdminUserDTO> callback, AdminUserDTO dto) {
        throw new IllegalStateException("this operation is not supported for this service");
    }

    @Override
    protected void enhanceRetrieved(AdminUserCredential entity, AdminUserDTO dto) {
        if (!entity.behaviors().isEmpty()) {
            dto.role().setValue(entity.behaviors().iterator().next());
        }
    }

    @Override
    protected void retrievedSingle(AdminUserCredential entity, RetrieveTraget retrieveTraget) {
        Persistence.service().retrieve(entity.user());
    }

    @Override
    protected void persist(AdminUserCredential dbo, AdminUserDTO dto) {
        dbo.user().email().setValue(PasswordEncryptor.normalizeEmailAddress(dto.email().getValue()));
        Persistence.service().merge(dbo.user());

        // ignore role changes (don't copy role from dto to dbo);

        dbo.setPrimaryKey(dbo.user().getPrimaryKey());
        Persistence.service().merge(dbo);
    }

}
