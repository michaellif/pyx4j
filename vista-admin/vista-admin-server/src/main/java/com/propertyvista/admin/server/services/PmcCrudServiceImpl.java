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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.OnboardingMerchantAccount;
import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.admin.domain.pmc.PmcDnsName;
import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.PmcCrudService;
import com.propertyvista.admin.server.onboarding.PmcNameValidator;
import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.portal.server.preloader.PmcCreator;
import com.propertyvista.preloader.OnboardingUserPreloader;

public class PmcCrudServiceImpl extends AbstractCrudServiceDtoImpl<Pmc, PmcDTO> implements PmcCrudService {

    public PmcCrudServiceImpl() {
        super(Pmc.class, PmcDTO.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.status(), dboProto.status());
        bind(dtoProto.name(), dboProto.name());
        bind(dtoProto.dnsName(), dboProto.dnsName());
        bind(dtoProto.namespace(), dboProto.namespace());
        bind(dtoProto.onboardingAccountId(), dboProto.onboardingAccountId());
        bind(dtoProto.dnsNameAliases(), dboProto.dnsNameAliases());
        bind(dtoProto.features(), dboProto.features());
        bind(dtoProto.created(), dboProto.created());
        bind(dtoProto.updated(), dboProto.updated());
    }

    @Override
    protected void enhanceRetrieved(Pmc entity, PmcDTO dto) {
        super.enhanceRetrieved(entity, dto);

        dto.vistaCrmUrl().setValue(VistaDeployment.getBaseApplicationURL(entity, VistaBasicBehavior.CRM, true));
        dto.residentPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(entity, VistaBasicBehavior.TenantPortal, false));
        dto.prospectPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(entity, VistaBasicBehavior.ProspectiveApp, true));
    }

    @Override
    protected void persist(Pmc entity, PmcDTO dto) {
        entity.dnsName().setValue(entity.dnsName().getValue().toLowerCase(Locale.ENGLISH));
        entity.namespace().setValue(entity.namespace().getValue().toLowerCase(Locale.ENGLISH).replace('-', '_'));
        for (PmcDnsName alias : entity.dnsNameAliases()) {
            alias.dnsName().setValue(alias.dnsName().getValue().toLowerCase(Locale.ENGLISH));
        }
        super.persist(entity, dto);
        CacheService.reset();
    }

    @Override
    protected void create(Pmc entity, PmcDTO dto) {
        entity.dnsName().setValue(entity.dnsName().getValue().toLowerCase(Locale.ENGLISH));
        entity.namespace().setValue(entity.dnsName().getValue().replace('-', '_'));
        entity.status().setValue(PmcStatus.Created);
        if (!PmcNameValidator.canCreatePmcName(entity.dnsName().getValue(), null)) {
            throw new UserRuntimeException("PMC DNS name is reserved of forbidden");
        }
        super.create(entity, dto);
        
        OnboardingUserCredential cred;
        if (dto.createPmcForExistingOnboardingUserRequest().isNull()) {
	        cred = OnboardingUserPreloader.createOnboardingUser(dto.person().name().firstName().getValue(), dto.person().name().lastName()
	                .getValue(), dto.email().getValue(), dto.password().getValue(), VistaOnboardingBehavior.ProspectiveClient, null);
	                	        
        } else {
        	EntityQueryCriteria<OnboardingUserCredential> criteria = EntityQueryCriteria.create(OnboardingUserCredential.class);
        	criteria.add(PropertyCriterion.eq(criteria.proto().user(), dto.createPmcForExistingOnboardingUserRequest()));        	
        	cred = Persistence.service().retrieve(criteria);
        	if (cred == null) {
        		throw new UserRuntimeException("failed to create PMC because existing onboarding user with key = '" + dto.createPmcForExistingOnboardingUserRequest().getPrimaryKey() + "' was not found");        		
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
    public void activate(AsyncCallback<PmcDTO> callback, Key entityId) {
        SecurityController.assertPermission(EntityPermission.permissionUpdate(Pmc.class));
        Pmc pmc = Persistence.service().retrieve(entityClass, entityId);

        if (pmc.status().getValue() == PmcStatus.Created) // First time create preload
        {
            EntityQueryCriteria<OnboardingUserCredential> credentialCrt = EntityQueryCriteria.create(OnboardingUserCredential.class);
            credentialCrt.add(PropertyCriterion.eq(credentialCrt.proto().pmc(), pmc));
            List<OnboardingUserCredential> creds = Persistence.service().query(credentialCrt);

            if (creds.size() == 0) {
                throw new UserRuntimeException("No users for PMC " + pmc.name().getValue());
            }

            OnboardingUserCredential onbUserCred = creds.get(0);

            OnboardingUser onbUser = Persistence.service().retrieve(OnboardingUser.class, onbUserCred.user().getPrimaryKey());

            List<OnboardingMerchantAccount> onbMrchAccs;
            if (pmc.onboardingAccountId().getValue() != null) {
                EntityQueryCriteria<OnboardingMerchantAccount> onbMrchAccCrt = EntityQueryCriteria.create(OnboardingMerchantAccount.class);
                onbMrchAccCrt.add(PropertyCriterion.eq(onbMrchAccCrt.proto().onboardingAccountId(), pmc.onboardingAccountId().getValue()));
                onbMrchAccs = Persistence.service().query(onbMrchAccCrt);
            } else {
                onbMrchAccs = new ArrayList<OnboardingMerchantAccount>();
            }

            try {
                Persistence.service().startBackgroundProcessTransaction();
                PmcCreator.preloadPmc(pmc, onbUser, onbUserCred, onbMrchAccs);
                pmc.status().setValue(PmcStatus.Active);
                Persistence.service().persist(pmc);
                onbUserCred.behavior().setValue(VistaOnboardingBehavior.Client);
                Persistence.service().persist(onbUserCred);
                Persistence.service().persist(onbMrchAccs);

                Persistence.service().commit();
            } finally {
                Persistence.service().endTransaction();
            }
        } else {
            pmc.status().setValue(PmcStatus.Active);
            Persistence.service().persist(pmc);
            Persistence.service().commit();
        }
        CacheService.reset();
        super.retrieve(callback, entityId, RetrieveTraget.View);
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
        enhanceRetrieved(pmc, dto);

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
            enhanceRetrieved(pmc, dto);
            callback.onSuccess(dto);
        } else {
            callback.onSuccess(null);
        }
    }
}
