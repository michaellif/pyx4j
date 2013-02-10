/*
 * MCO 2
 * Copyright (C) 2008-2011 Amdocs Canada.
 *
 * Created on Nov 4, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.operations.domain.security.OperationsUserCredential;
import com.propertyvista.operations.rpc.OperationsUserDTO;
import com.propertyvista.operations.rpc.services.AdminUserCrudService;
import com.propertyvista.server.common.security.PasswordEncryptor;

public class AdminUserCrudServiceImpl extends AbstractCrudServiceDtoImpl<OperationsUserCredential, OperationsUserDTO> implements AdminUserCrudService {

    public AdminUserCrudServiceImpl() {
        super(OperationsUserCredential.class, OperationsUserDTO.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.name(), dboProto.user().name());
        bind(dtoProto.email(), dboProto.user().email());
        bind(dtoProto.created(), dboProto.user().created());
        bind(dtoProto.updated(), dboProto.user().updated());

        bind(dtoProto.enabled(), dboProto.enabled());
        bind(dtoProto.requiredPasswordChangeOnNextLogIn(), dboProto.requiredPasswordChangeOnNextLogIn());
        bind(dtoProto.credentialUpdated(), dboProto.credentialUpdated());

    }

    @Override
    protected void enhanceRetrieved(OperationsUserCredential entity, OperationsUserDTO dto, RetrieveTraget retrieveTraget) {
        if (!entity.behaviors().isEmpty()) {
            dto.role().setValue(entity.behaviors().iterator().next());
        }
    }

    @Override
    protected void retrievedForList(OperationsUserCredential entity) {
        Persistence.service().retrieve(entity.user());
    }

    @Override
    protected void retrievedSingle(OperationsUserCredential entity, RetrieveTraget retrieveTraget) {
        Persistence.service().retrieve(entity.user());
    }

    @Override
    protected void persist(OperationsUserCredential dbo, OperationsUserDTO dto) {
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
