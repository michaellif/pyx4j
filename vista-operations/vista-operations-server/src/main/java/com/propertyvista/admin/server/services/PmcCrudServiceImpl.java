/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.services;

import java.util.Locale;
import java.util.UUID;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.PmcCrudService;
import com.propertyvista.admin.server.onboarding.PmcNameValidator;
import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.biz.system.UserManagementFacade;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.pmc.PmcDnsName;
import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.ob.server.PmcActivationDeferredProcess;

public class PmcCrudServiceImpl extends AbstractCrudServiceDtoImpl<Pmc, PmcDTO> implements PmcCrudService {

    public PmcCrudServiceImpl() {
        super(Pmc.class, PmcDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void retrievedSingle(Pmc entity, RetrieveTraget retrieveTraget) {
        Persistence.service().retrieveMember(entity.equifaxInfo());
        Persistence.service().retrieveMember(entity.equifaxFee());
        Persistence.service().retrieveMember(entity.yardiCredential());
    }

    @Override
    protected void enhanceRetrieved(Pmc entity, PmcDTO dto, RetrieveTraget retrieveTraget) {
        super.enhanceRetrieved(entity, dto, retrieveTraget);

        dto.vistaCrmUrl().setValue(VistaDeployment.getBaseApplicationURL(entity, VistaBasicBehavior.CRM, true));
        dto.residentPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(entity, VistaBasicBehavior.TenantPortal, false));
        dto.prospectPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(entity, VistaBasicBehavior.ProspectiveApp, true));
    }

    @Override
    protected void persist(Pmc entity, PmcDTO dto) {
        if (!PmcNameValidator.isDnsNameValid(entity.dnsName().getValue())) {
            throw new UserRuntimeException("PMC DNS name is not valid");
        }
        if (!PmcNameValidator.isDnsNameValid(entity.namespace().getValue())) {
            throw new UserRuntimeException("PMC namespace is not valid");
        }
        entity.dnsName().setValue(entity.dnsName().getValue().toLowerCase(Locale.ENGLISH));
        entity.namespace().setValue(entity.namespace().getValue().toLowerCase(Locale.ENGLISH).replace('-', '_'));

        if (entity.onboardingAccountId().isNull()) {
            entity.onboardingAccountId().setValue(UUID.randomUUID().toString());
        }

        for (PmcDnsName alias : entity.dnsNameAliases()) {
            alias.dnsName().setValue(alias.dnsName().getValue().toLowerCase(Locale.ENGLISH));
        }
        super.persist(entity, dto);

        // Ppopagate onboardingAccountId to accounts
        EntityQueryCriteria<OnboardingUserCredential> criteria = EntityQueryCriteria.create(OnboardingUserCredential.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().pmc(), entity));
        for (OnboardingUserCredential cred : Persistence.service().query(criteria)) {
            if (!cred.onboardingAccountId().equals(entity.onboardingAccountId())) {
                cred.onboardingAccountId().setValue(entity.onboardingAccountId().getValue());
                Persistence.service().persist(cred);
            }
        }

        CacheService.reset();
    }

    @Override
    protected void create(Pmc entity, PmcDTO dto) {
        entity.dnsName().setValue(entity.dnsName().getValue().toLowerCase(Locale.ENGLISH));
        entity.namespace().setValue(entity.dnsName().getValue().replace('-', '_'));
        if (!PmcNameValidator.canCreatePmcName(entity.dnsName().getValue(), null)) {
            throw new UserRuntimeException("PMC DNS name is reserved of forbidden");
        }
        ServerSideFactory.create(PmcFacade.class).create(entity);

        OnboardingUserCredential cred;
        if (dto.createPmcForExistingOnboardingUserRequest().isNull()) {
            cred = ServerSideFactory.create(UserManagementFacade.class).createOnboardingUser(dto.person().name().firstName().getValue(),
                    dto.person().name().lastName().getValue(), dto.email().getValue(), dto.password().getValue(), VistaOnboardingBehavior.ProspectiveClient,
                    null);

        } else {
            EntityQueryCriteria<OnboardingUserCredential> criteria = EntityQueryCriteria.create(OnboardingUserCredential.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), dto.createPmcForExistingOnboardingUserRequest()));
            cred = Persistence.service().retrieve(criteria);
            if (cred == null) {
                throw new UserRuntimeException("failed to create PMC because existing onboarding user with key = '"
                        + dto.createPmcForExistingOnboardingUserRequest().getPrimaryKey() + "' was not found");
            }
        }
        cred.pmc().set(entity);
        Persistence.service().persist(cred);
    }

    @Override
    public void resetCache(AsyncCallback<VoidSerializable> callback, Key entityId) {
        final String namespace = NamespaceManager.getNamespace();
        Pmc pmc = Persistence.service().retrieve(entityClass, entityId);
        NamespaceManager.setNamespace(pmc.namespace().getValue());
        try {
            CacheService.reset();
        } finally {
            NamespaceManager.setNamespace(namespace);
        }
        callback.onSuccess(null);
    }

    @Override
    public void activate(AsyncCallback<String> callback, Key entityId) {
        SecurityController.assertPermission(EntityPermission.permissionUpdate(Pmc.class));
        callback.onSuccess(DeferredProcessRegistry.fork(new PmcActivationDeferredProcess(EntityFactory.createIdentityStub(Pmc.class, entityId)),
                ThreadPoolNames.IMPORTS));
    }

    @Override
    public void suspend(AsyncCallback<PmcDTO> callback, Key entityId) {
        SecurityController.assertPermission(EntityPermission.permissionUpdate(Pmc.class));

        Pmc pmc = Persistence.service().retrieve(entityClass, entityId);
        pmc.status().setValue(PmcStatus.Suspended);
        Persistence.service().persist(pmc);
        Persistence.service().commit();
        CacheService.reset();

        PmcDTO dto = createDTO(pmc);
        enhanceRetrieved(pmc, dto, null);

        callback.onSuccess(dto);

    }

    @Override
    public void cancelPmc(AsyncCallback<PmcDTO> callback, Key entityId) {
        SecurityController.assertPermission(EntityPermission.permissionUpdate(Pmc.class));

        Pmc pmc = Persistence.service().retrieve(entityClass, entityId);

        ServerSideFactory.create(PmcFacade.class).cancelPmc(pmc);
        Persistence.service().commit();
        CacheService.reset();

        ServerSideFactory.create(AuditFacade.class).info("PMC {0} Cancelled by {1} ", pmc.namespace().getValue(), Context.getVisit().getUserVisit().getEmail());

        pmc = Persistence.service().retrieve(entityClass, entityId);

        if (pmc != null) {
            PmcDTO dto = createDTO(pmc);
            enhanceRetrieved(pmc, dto, null);
            callback.onSuccess(dto);
        } else {
            callback.onSuccess(null);
        }
    }
}
