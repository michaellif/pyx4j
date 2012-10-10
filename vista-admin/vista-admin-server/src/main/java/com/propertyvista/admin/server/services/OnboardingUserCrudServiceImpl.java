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
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityFromatUtils;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.admin.rpc.OnboardingUserDTO;
import com.propertyvista.admin.rpc.services.OnboardingUserCrudService;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.server.common.security.PasswordEncryptor;

public class OnboardingUserCrudServiceImpl extends AbstractCrudServiceDtoImpl<OnboardingUserCredential, OnboardingUserDTO> implements OnboardingUserCrudService {

    public OnboardingUserCrudServiceImpl() {
        super(OnboardingUserCredential.class, OnboardingUserDTO.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.firstName(), dboProto.user().firstName());
        bind(dtoProto.lastName(), dboProto.user().lastName());
        bind(dtoProto.name(), dboProto.user().name());
        bind(dtoProto.email(), dboProto.user().email());
        bind(dtoProto.created(), dboProto.user().created());
        bind(dtoProto.updated(), dboProto.user().updated());
        bind(dtoProto.credentialUpdated(), dboProto.credentialUpdated());

        bind(dtoProto.role(), dboProto.behavior());
        bind(dtoProto.onboardingAccountId(), dboProto.onboardingAccountId());
        bind(dtoProto.enabled(), dboProto.enabled());
        bind(dtoProto.requireChangePasswordOnNextLogIn(), dboProto.requiredPasswordChangeOnNextLogIn());

        bind(dtoProto.pmc(), dboProto.pmc());
        bind(dtoProto.onboardingAccountId(), dboProto.onboardingAccountId());
        bind(dtoProto.pmcStatus(), dboProto.pmc().status());

    }

    @Override
    protected void enhanceRetrieved(OnboardingUserCredential entity, OnboardingUserDTO dto, RetrieveTraget retrieveTraget) {
        if (!entity.pmc().isNull()) {
            dto.onboardingAccountId().setValue(entity.pmc().onboardingAccountId().getValue());
        }
    }

    @Override
    protected void enhanceListRetrieved(OnboardingUserCredential entity, OnboardingUserDTO dto) {
        if (!entity.pmc().isNull()) {
            dto.onboardingAccountId().setValue(entity.pmc().onboardingAccountId().getValue());
            dto.pmcStatus().setValue(entity.pmc().status().getValue());
            dto.pmc().set(entity.pmc());
        }
    }

    @Override
    protected void retrievedForList(OnboardingUserCredential entity) {
        Persistence.service().retrieve(entity.user());
    }

    @Override
    protected void retrievedSingle(OnboardingUserCredential entity, RetrieveTraget retrieveTraget) {
        Persistence.service().retrieve(entity.user());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void persist(OnboardingUserCredential dbo, OnboardingUserDTO dto) {
        dbo.user().email().setValue(PasswordEncryptor.normalizeEmailAddress(dto.email().getValue()));
        dbo.user().name().setValue(EntityFromatUtils.nvl_concat(" ", dbo.user().firstName(), dbo.user().lastName()));
        Persistence.service().merge(dbo.user());

        if (dbo.getPrimaryKey() == null) {
            dbo.credential().setValue(PasswordEncryptor.encryptPassword(dto.password().getValue()));
        }
        dbo.setPrimaryKey(dbo.user().getPrimaryKey());
        if (!dbo.pmc().isNull()) {
            Persistence.service().retrieve(dbo.pmc());
            dbo.onboardingAccountId().setValue(dbo.pmc().onboardingAccountId().getValue());
        }
        Persistence.service().merge(dbo);

        if (dbo.pmc().getPrimaryKey() != null) {
            Pmc pmc = Persistence.service().retrieve(Pmc.class, dbo.pmc().getPrimaryKey());

            if (pmc.status().getValue() != PmcStatus.Created) {
                // Update existing CRM user

                final String namespace = NamespaceManager.getNamespace();
                try {
                    NamespaceManager.setNamespace(pmc.namespace().getValue());

                    CrmUser crmUser = Persistence.service().retrieve(CrmUser.class, dbo.crmUser().getValue());
                    crmUser.email().setValue(dbo.user().email().getValue());
                    Persistence.service().persist(crmUser);

                    EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
                    criteria.add(PropertyCriterion.eq(criteria.proto().user(), dbo.crmUser().getValue()));
                    Employee emp = Persistence.service().retrieve(criteria);
                    emp.email().setValue(dbo.user().email().getValue());
                    Persistence.service().persist(emp);
                } finally {
                    NamespaceManager.setNamespace(namespace);
                }
            }
        }
    }
}
