/*
 * MCO 2
 * Copyright (C) 2008-2011 Amdocs Canada.
 *
 * Created on Nov 4, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityDiff;
import com.pyx4j.entity.shared.utils.SimpleEntityBinder;
import com.pyx4j.security.server.EmailValidator;

import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.operations.domain.security.OperationsUserCredential;
import com.propertyvista.operations.rpc.dto.OperationsUserDTO;
import com.propertyvista.operations.rpc.services.AdminUserCrudService;

public class AdminUserCrudServiceImpl extends AbstractCrudServiceDtoImpl<OperationsUserCredential, OperationsUserDTO> implements AdminUserCrudService {

    public AdminUserCrudServiceImpl() {
        super(new SimpleEntityBinder<OperationsUserCredential, OperationsUserDTO>(OperationsUserCredential.class, OperationsUserDTO.class) {

            @Override
            protected void bind() {
                bind(toProto.name(), boProto.user().name());
                bind(toProto.email(), boProto.user().email());
                bind(toProto.created(), boProto.user().created());
                bind(toProto.updated(), boProto.user().updated());

                bind(toProto.enabled(), boProto.enabled());
                bind(toProto.changePassword(), boProto.requiredPasswordChangeOnNextLogIn());
                bind(toProto.credentialUpdated(), boProto.credentialUpdated());

            }
        });
    }

    @Override
    protected void enhanceRetrieved(OperationsUserCredential bo, OperationsUserDTO to, RetrieveTarget retrieveTarget) {
        if (!bo.behaviors().isEmpty()) {
            to.role().setValue(bo.behaviors().iterator().next());
        }
    }

    @Override
    protected void retrievedForList(OperationsUserCredential entity) {
        Persistence.service().retrieve(entity.user());
    }

    @Override
    protected void retrievedSingle(OperationsUserCredential bo, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieve(bo.user());
    }

    @Override
    protected boolean save(OperationsUserCredential entity, OperationsUserDTO dto) {
        OperationsUserCredential orig = Persistence.secureRetrieve(boClass, entity.getPrimaryKey());

        boolean updated = super.save(entity, dto);

        if (updated) {
            ServerSideFactory.create(AuditFacade.class).updated(entity.user(),
                    CommonsStringUtils.nvl_concat(EntityDiff.getChanges(orig, entity), EntityDiff.getChanges(orig.user(), entity.user()), "\n"));
        }

        return updated;
    }

    @Override
    protected boolean persist(OperationsUserCredential dbo, OperationsUserDTO to) {
        dbo.user().email().setValue(EmailValidator.normalizeEmailAddress(to.email().getValue()));
        Persistence.service().merge(dbo.user());

        dbo.behaviors().clear();
        dbo.behaviors().add(to.role().getValue());

        if (dbo.getPrimaryKey() == null) {
            dbo.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(to.password().getValue()));
        }
        dbo.setPrimaryKey(dbo.user().getPrimaryKey());
        return Persistence.service().merge(dbo);
    }

}
