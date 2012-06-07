/*
 * MCO 2
 * Copyright (C) 2008-2011 Amdocs Canada.
 *
 * Created on Nov 4, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.services;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.admin.domain.security.AdminUserCredential;
import com.propertyvista.admin.rpc.AdminUserDTO;
import com.propertyvista.admin.rpc.services.AdminUserCrudService;
import com.propertyvista.server.common.security.PasswordEncryptor;

public class AdminUserCrudServiceImpl extends AbstractCrudServiceDtoImpl<AdminUserCredential, AdminUserDTO> implements AdminUserCrudService {

    public AdminUserCrudServiceImpl() {
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
    protected void enhanceRetrieved(AdminUserCredential entity, AdminUserDTO dto) {
        if (!entity.behaviors().isEmpty()) {
            dto.role().setValue(entity.behaviors().iterator().next());
        }
    }

    @Override
    protected void retrievedForList(AdminUserCredential entity) {
        Persistence.service().retrieve(entity.user());
    }

    @Override
    protected void retrievedSingle(AdminUserCredential entity, RetrieveTraget retrieveTraget) {
        Persistence.service().retrieve(entity.user());
    }

    @Override
    protected void persist(AdminUserCredential dbo, AdminUserDTO dto) {
        dbo.user().email().setValue(PasswordEncryptor.normalizeEmailAddress(dto.email().getValue()));
        Persistence.service().merge(dbo.user());

        dbo.behaviors().clear();
        dbo.behaviors().add(dto.role().getValue());

        if (dbo.getPrimaryKey() == null) {
            dbo.credential().setValue(PasswordEncryptor.encryptPassword(dto.password().getValue()));
        }
        dbo.setPrimaryKey(dbo.user().getPrimaryKey());
        Persistence.service().merge(dbo);
    }

}
