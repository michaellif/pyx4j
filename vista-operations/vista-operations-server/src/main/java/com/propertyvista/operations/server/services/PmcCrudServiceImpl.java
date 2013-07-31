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
package com.propertyvista.operations.server.services;

import java.util.Locale;
import java.util.concurrent.Callable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.utils.EntityDiff;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.server.EmailValidator;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.OperationsTriggerFacade;
import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.biz.system.PmcNameValidator;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.pmc.PmcDnsName;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.ob.server.PmcActivationDeferredProcess;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.Trigger;
import com.propertyvista.operations.domain.vista2pmc.DefaultPaymentFees;
import com.propertyvista.operations.rpc.PmcDTO;
import com.propertyvista.operations.rpc.services.PmcCrudService;
import com.propertyvista.server.jobs.TaskRunner;

public class PmcCrudServiceImpl extends AbstractCrudServiceDtoImpl<Pmc, PmcDTO> implements PmcCrudService {

    public PmcCrudServiceImpl() {
        super(Pmc.class, PmcDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void retrievedSingle(Pmc entity, RetrieveTarget RetrieveTarget) {
        Persistence.service().retrieveMember(entity.equifaxInfo());
        Persistence.service().retrieveMember(entity.equifaxFee());
        Persistence.service().retrieveMember(entity.yardiCredentials());

        for (PmcYardiCredential yardiCredential : entity.yardiCredentials()) {
            if (!yardiCredential.password().encrypted().isNull()) {
                yardiCredential.password().obfuscatedNumber().setValue("**");
            } else if (!yardiCredential.password().number().isNull()) {
                yardiCredential.password().obfuscatedNumber().setValue("##");
            }
        }

    }

    @Override
    protected void enhanceRetrieved(Pmc entity, PmcDTO dto, RetrieveTarget RetrieveTarget) {
        super.enhanceRetrieved(entity, dto, RetrieveTarget);

        dto.vistaCrmUrl().setValue(VistaDeployment.getBaseApplicationURL(entity, VistaApplication.crm, true));
        dto.residentPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(entity, VistaApplication.residentPortal, false));
        dto.prospectPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(entity, VistaApplication.prospect, true));

        dto.defaultPaymentFees().set(Persistence.service().retrieve(EntityQueryCriteria.create(DefaultPaymentFees.class)));
        Persistence.service().retrieveMember(entity.paymentTypeInfo());
        dto.paymentTypeInfo().set(entity.paymentTypeInfo());

    }

    @Override
    protected void persist(Pmc entity, PmcDTO dto) {
        if (!PmcNameValidator.isDnsNameValid(entity.dnsName().getValue())) {
            throw new UserRuntimeException("PMC DNS name is not valid");
        }
        if (!PmcNameValidator.isDnsNameValid(entity.namespace().getValue())) {
            throw new UserRuntimeException("PMC namespace is not valid");
        }

        Pmc orig = Persistence.secureRetrieve(Pmc.class, entity.getPrimaryKey());
        retrievedSingle(orig, RetrieveTarget.Edit);

        entity.dnsName().setValue(entity.dnsName().getValue().toLowerCase(Locale.ENGLISH));
        entity.namespace().setValue(entity.namespace().getValue().toLowerCase(Locale.ENGLISH).replace('-', '_'));

        for (PmcDnsName alias : entity.dnsNameAliases()) {
            alias.dnsName().setValue(alias.dnsName().getValue().toLowerCase(Locale.ENGLISH));
        }

        if (entity.features().yardiIntegration().getValue(false)) {
            entity.features().defaultProductCatalog().setValue(Boolean.TRUE);
        }

        for (PmcYardiCredential yardiCredential : entity.yardiCredentials()) {
            encryptPassword(yardiCredential);
        }

        super.persist(entity, dto);

        ServerSideFactory.create(AuditFacade.class).updated(entity, EntityDiff.getChanges(orig, entity, entity.updated()));

        TaskRunner.runInTargetNamespace(entity, new Callable<Void>() {
            @Override
            public Void call() {
                CacheService.reset();
                return null;
            }
        });
    }

    @Override
    protected void create(Pmc entity, PmcDTO dto) {
        entity.dnsName().setValue(entity.dnsName().getValue().toLowerCase(Locale.ENGLISH));
        entity.namespace().setValue(entity.dnsName().getValue().replace('-', '_'));
        if (!PmcNameValidator.canCreatePmcName(entity.dnsName().getValue(), null)) {
            throw new UserRuntimeException("PMC DNS name is reserved of forbidden");
        }
        for (PmcYardiCredential yardiCredential : entity.yardiCredentials()) {
            encryptPassword(yardiCredential);
        }
        ServerSideFactory.create(PmcFacade.class).create(entity);

        OnboardingUser user = dto.onboardingUser();
        user.pmc().set(entity);
        user.email().setValue(EmailValidator.normalizeEmailAddress(dto.onboardingUser().email().getValue()));
        Persistence.service().persist(user);
    }

    private void encryptPassword(PmcYardiCredential yardiCredential) {
        if (!yardiCredential.password().newNumber().isNull()) {
            ServerSideFactory.create(PasswordEncryptorFacade.class).encryptPassword(yardiCredential.password(),
                    yardiCredential.password().newNumber().getValue());
        } else if (yardiCredential.getPrimaryKey() != null) {
            PmcYardiCredential orig = Persistence.service().retrieve(PmcYardiCredential.class, yardiCredential.getPrimaryKey());
            if (!orig.password().number().isNull()) {
                ServerSideFactory.create(PasswordEncryptorFacade.class).encryptPassword(yardiCredential.password(), orig.password().number().getValue());
                yardiCredential.password().number().setValue(null);
            } else {
                yardiCredential.password().encrypted().setValue(orig.password().encrypted().getValue());
            }
        }
    }

    @Override
    public void resetCache(AsyncCallback<VoidSerializable> callback, Key entityId) {
        Pmc pmc = Persistence.service().retrieve(Pmc.class, entityId);
        TaskRunner.runInTargetNamespace(pmc, new Callable<Void>() {
            @Override
            public Void call() {
                CacheService.reset();
                return null;
            }
        });
        callback.onSuccess(null);
    }

    @Override
    public void activate(AsyncCallback<String> callback, Key entityId) {
        SecurityController.assertPermission(EntityPermission.permissionUpdate(Pmc.class));
        callback.onSuccess(DeferredProcessRegistry.fork(new PmcActivationDeferredProcess(EntityFactory.createIdentityStub(Pmc.class, entityId)),
                ThreadPoolNames.IMPORTS));
    }

    @Override
    public void suspend(AsyncCallback<VoidSerializable> callback, Key entityId) {
        SecurityController.assertPermission(EntityPermission.permissionUpdate(Pmc.class));

        Pmc pmc = Persistence.service().retrieve(entityClass, entityId);
        pmc.status().setValue(PmcStatus.Suspended);
        Persistence.service().persist(pmc);
        Persistence.service().commit();
        CacheService.reset();

        callback.onSuccess(null);

    }

    @Override
    public void cancelPmc(AsyncCallback<VoidSerializable> callback, Key entityId) {
        SecurityController.assertPermission(EntityPermission.permissionUpdate(Pmc.class));

        Pmc pmc = Persistence.service().retrieve(entityClass, entityId);

        ServerSideFactory.create(PmcFacade.class).cancelPmc(pmc);
        Persistence.service().commit();
        CacheService.reset();

        ServerSideFactory.create(AuditFacade.class).info("PMC {0} Cancelled by {1} ", pmc.namespace().getValue(), Context.getVisit().getUserVisit().getEmail());

        pmc = Persistence.service().retrieve(entityClass, entityId);
        callback.onSuccess(null);
    }

    @Override
    public void runPmcProcess(AsyncCallback<Run> callback, Key entityId, PmcProcessType processType, LogicalDate executionDate) {
        Trigger trigger;
        {
            EntityQueryCriteria<Trigger> criteria = EntityQueryCriteria.create(Trigger.class);
            criteria.eq(criteria.proto().triggerType(), processType);
            trigger = Persistence.service().retrieve(criteria);
            if (trigger == null) {
                throw new UserRuntimeException("The Trigger " + processType + " not found");
            }
        }
        Pmc pmc = Persistence.service().retrieve(entityClass, entityId);

        Run run = ServerSideFactory.create(OperationsTriggerFacade.class).startProcess(trigger, pmc, executionDate);
        callback.onSuccess(run.<Run> createIdentityStub());
    }
}
