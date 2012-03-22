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

import com.propertyvista.admin.rpc.OnboardingUserDTO;
import com.propertyvista.admin.rpc.services.OnboardingUserCrudService;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.domain.security.AdminUserCredential;
import com.propertyvista.server.domain.security.OnboardingUserCredential;

public class OnboardingUserCrudServiceImpl extends AbstractCrudServiceDtoImpl<OnboardingUserCredential, OnboardingUserDTO> implements OnboardingUserCrudService {

    public OnboardingUserCrudServiceImpl() {
        super(OnboardingUserCredential.class, OnboardingUserDTO.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.name(), dboProto.user().name());
        bind(dtoProto.email(), dboProto.user().email());
        bind(dtoProto.created(), dboProto.user().created());
        bind(dtoProto.updated(), dboProto.user().updated());

        bind(dtoProto.enabled(), dboProto.enabled());
        bind(dtoProto.updated(), dboProto.updated());
        bind(dtoProto.requireChangePasswordOnNextLogIn(), dboProto.requiredPasswordChangeOnNextLogIn());

    }

    @Override
    protected void persist(OnboardingUserCredential credential, OnboardingUserDTO dto) {
        dto.email().setValue(PasswordEncryptor.normalizeEmailAddress(dto.email().getValue()));
        Persistence.service().merge(credential.user());

        credential.setPrimaryKey(credential.user().getPrimaryKey());

        AdminUserCredential crs = Persistence.service().retrieve(AdminUserCredential.class, credential.getPrimaryKey());
        if ((crs == null) || (crs.isNull())) {
            credential.credential().setValue(PasswordEncryptor.encryptPassword(dto.password().getValue()));
        }
        Persistence.service().merge(credential);
    }

}
