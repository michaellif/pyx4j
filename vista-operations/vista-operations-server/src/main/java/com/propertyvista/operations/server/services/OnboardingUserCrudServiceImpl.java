/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 4, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import java.util.concurrent.Callable;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityFromatUtils;
import com.pyx4j.security.server.EmailValidator;

import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.operations.domain.security.OnboardingUserCredential;
import com.propertyvista.operations.rpc.OnboardingUserDTO;
import com.propertyvista.operations.rpc.services.OnboardingUserCrudService;
import com.propertyvista.server.domain.security.CrmUserCredential;
import com.propertyvista.server.jobs.TaskRunner;

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
        bind(dtoProto.requiredPasswordChangeOnNextLogIn(), dboProto.requiredPasswordChangeOnNextLogIn());

        bind(dtoProto.pmc(), dboProto.pmc());
        bind(dtoProto.onboardingAccountId(), dboProto.onboardingAccountId());
        bind(dtoProto.pmcStatus(), dboProto.pmc().status());

    }

    /**
     * Data from PMC database takes priority
     */
    private void bindCrmUser(final OnboardingUserCredential credential, final OnboardingUserDTO dto) {
        TaskRunner.runInTargetNamespace(dto.pmc(), new Callable<Void>() {
            @Override
            public Void call() {
                CrmUser crmUser = Persistence.service().retrieve(CrmUser.class, credential.crmUser().getValue());
                if (crmUser == null) {
                    throw new UserRuntimeException("CRM User " + credential.crmUser().getValue() + " not found in PMC " + dto.pmc().namespace().getValue());
                }
                CrmUserCredential crmCredential = Persistence.service().retrieve(CrmUserCredential.class, crmUser.getPrimaryKey());

                dto.enabled().setValue(crmCredential.enabled().getValue());
                dto.requiredPasswordChangeOnNextLogIn().setValue(crmCredential.requiredPasswordChangeOnNextLogIn().getValue());
                dto.crmCredentialUpdated().setValue(crmCredential.credentialUpdated().getValue());

                return null;
            }
        });
    }

    @Override
    protected void enhanceRetrieved(OnboardingUserCredential entity, OnboardingUserDTO dto, RetrieveTarget RetrieveTarget) {
        if (!entity.pmc().isNull()) {
            dto.onboardingAccountId().setValue(entity.pmc().onboardingAccountId().getValue());
            if (entity.pmc().status().getValue() != PmcStatus.Created) {
                bindCrmUser(entity, dto);
            }
        }
    }

    @Override
    protected void enhanceListRetrieved(OnboardingUserCredential entity, OnboardingUserDTO dto) {
        if (!entity.pmc().isNull()) {
            dto.onboardingAccountId().setValue(entity.pmc().onboardingAccountId().getValue());
            dto.pmcStatus().setValue(entity.pmc().status().getValue());
            dto.pmc().set(entity.pmc());
            if (entity.pmc().status().getValue() != PmcStatus.Created) {
                bindCrmUser(entity, dto);
            }
        }
    }

    @Override
    protected void retrievedForList(OnboardingUserCredential entity) {
        Persistence.service().retrieve(entity.user());
    }

    @Override
    protected void retrievedSingle(OnboardingUserCredential entity, RetrieveTarget RetrieveTarget) {
        Persistence.service().retrieve(entity.user());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void persist(final OnboardingUserCredential dbo, final OnboardingUserDTO dto) {
        dbo.user().email().setValue(EmailValidator.normalizeEmailAddress(dto.email().getValue()));
        dbo.user().name().setValue(EntityFromatUtils.nvl_concat(" ", dbo.user().firstName(), dbo.user().lastName()));
        Persistence.service().merge(dbo.user());

        if (dbo.getPrimaryKey() == null) {
            dbo.credential().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).encryptUserPassword(dto.password().getValue()));
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
                TaskRunner.runInTargetNamespace(pmc, new Callable<Void>() {
                    @Override
                    public Void call() {
                        CrmUser crmUser = Persistence.service().retrieve(CrmUser.class, dbo.crmUser().getValue());
                        crmUser.email().setValue(dbo.user().email().getValue());
                        Persistence.service().persist(crmUser);
                        CrmUserCredential crmCredential = Persistence.service().retrieve(CrmUserCredential.class, crmUser.getPrimaryKey());
                        crmCredential.enabled().setValue(dbo.enabled().getValue());
                        crmCredential.requiredPasswordChangeOnNextLogIn().setValue(dbo.requiredPasswordChangeOnNextLogIn().getValue());
                        crmCredential.credentialUpdated().setValue(dto.crmCredentialUpdated().getValue());
                        Persistence.service().persist(crmCredential);

                        EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
                        criteria.add(PropertyCriterion.eq(criteria.proto().user(), dbo.crmUser().getValue()));
                        Employee emp = Persistence.service().retrieve(criteria);
                        emp.email().setValue(dbo.user().email().getValue());
                        Persistence.service().persist(emp);
                        return null;
                    }
                });
            }
        }
    }
}
