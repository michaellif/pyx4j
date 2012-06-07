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

import java.util.List;
import java.util.Locale;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.OnboardingMerchantAccount;
import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.admin.domain.pmc.PmcDnsName;
import com.propertyvista.admin.domain.security.OnboardingUserCredential;
import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.PmcCrudService;
import com.propertyvista.admin.server.onboarding.PmcNameValidator;
import com.propertyvista.admin.server.preloader.OnboardingUserPreloader;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.security.VistaOnboardingBehavior;
import com.propertyvista.portal.rpc.corp.PmcAccountCreationRequest;
import com.propertyvista.portal.server.preloader.PmcCreator;

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
        bind(dtoProto.created(), dboProto.created());
    }

    @Override
    public void createAccount(AsyncCallback<PmcDTO> callback, PmcAccountCreationRequest request) {
        // TODO Auto-generated method stub
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
    public void create(AsyncCallback<PmcDTO> callback, PmcDTO editableEntity) {
        editableEntity.dnsName().setValue(editableEntity.dnsName().getValue().toLowerCase(Locale.ENGLISH));
        editableEntity.namespace().setValue(editableEntity.dnsName().getValue().replace('-', '_'));
        editableEntity.status().setValue(PmcStatus.Created);
        if (!PmcNameValidator.canCreatePmcName(editableEntity.dnsName().getValue(), null)) {
            throw new UserRuntimeException("PMC DNS name is reserved of forbidden");
        }

        super.create(callback, editableEntity);

        EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
        criteria.or(PropertyCriterion.eq(criteria.proto().dnsName(), editableEntity.dnsName().getValue()),
                PropertyCriterion.eq(criteria.proto().namespace(), editableEntity.namespace().getValue()));
        Pmc pmc = Persistence.service().retrieve(criteria);

        OnboardingUserCredential cred = OnboardingUserPreloader.createOnboardingUser(editableEntity.person().name().firstName().getValue(), editableEntity
                .person().name().lastName().getValue(), editableEntity.email().getValue(), editableEntity.password().getValue(),
                VistaOnboardingBehavior.ProspectiveClient, null);

        cred.pmc().set(pmc);
        Persistence.service().persist(cred);
        Persistence.service().commit();
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

            EntityQueryCriteria<OnboardingMerchantAccount> onbMrchAccCrt = EntityQueryCriteria.create(OnboardingMerchantAccount.class);
            credentialCrt.add(PropertyCriterion.eq(onbMrchAccCrt.proto().pmc(), pmc));
            List<OnboardingMerchantAccount> onbMrchAccs = Persistence.service().query(onbMrchAccCrt);

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
    protected void enhanceRetrieved(Pmc entity, PmcDTO dto) {
        super.enhanceRetrieved(entity, dto);

        dto.vistaCrmUrl().setValue(VistaDeployment.getBaseApplicationURL(entity, VistaBasicBehavior.CRM, true));
        dto.residentPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(entity, VistaBasicBehavior.TenantPortal, true));
        dto.prospectPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(entity, VistaBasicBehavior.ProspectiveApp, true));
    }
}
