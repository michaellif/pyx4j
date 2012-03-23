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

        bind(dtoProto.role(), dboProto.behavior());
        bind(dtoProto.onboardingAccountId(), dboProto.onboardingAccountId());
        bind(dtoProto.enabled(), dboProto.enabled());
        bind(dtoProto.requireChangePasswordOnNextLogIn(), dboProto.requiredPasswordChangeOnNextLogIn());

    }

    @Override
    protected void persist(OnboardingUserCredential dbo, OnboardingUserDTO dto) {
        dbo.user().email().setValue(PasswordEncryptor.normalizeEmailAddress(dto.email().getValue()));
        Persistence.service().merge(dbo.user());

        if (dbo.getPrimaryKey() == null) {
            dbo.credential().setValue(PasswordEncryptor.encryptPassword(dto.password().getValue()));
        }
        dbo.setPrimaryKey(dbo.user().getPrimaryKey());
        Persistence.service().merge(dbo);
    }

}
