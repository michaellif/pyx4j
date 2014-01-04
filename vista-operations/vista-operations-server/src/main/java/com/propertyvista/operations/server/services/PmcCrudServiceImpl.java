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
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
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
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.pmc.PmcDnsName;
import com.propertyvista.domain.pmc.PmcEquifaxInfo;
import com.propertyvista.domain.security.OnboardingUser;
import com.propertyvista.domain.security.PasswordIdentity;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.ob.server.PmcActivationDeferredProcess;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.Trigger;
import com.propertyvista.operations.domain.scheduler.TriggerPmcSelectionType;
import com.propertyvista.operations.domain.vista2pmc.DefaultPaymentFees;
import com.propertyvista.operations.rpc.dto.PmcDTO;
import com.propertyvista.operations.rpc.services.PmcCrudService;
import com.propertyvista.server.TaskRunner;

public class PmcCrudServiceImpl extends AbstractCrudServiceDtoImpl<Pmc, PmcDTO> implements PmcCrudService {

    public PmcCrudServiceImpl() {
        super(Pmc.class, PmcDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected PmcDTO init(InitializationData initializationData) {
        PmcDTO entity = EntityFactory.create(PmcDTO.class);

        entity.features().occupancyModel().setValue(Boolean.TRUE);
        entity.features().productCatalog().setValue(Boolean.TRUE);
        entity.features().leases().setValue(Boolean.TRUE);
        entity.features().onlineApplication().setValue(Boolean.FALSE);
        entity.features().defaultProductCatalog().setValue(true);
        entity.features().yardiIntegration().setValue(Boolean.FALSE);
        entity.features().countryOfOperation().setValue(CountryOfOperation.Canada);
        entity.features().tenantSureIntegration().setValue(Boolean.TRUE);

        return entity;
    }

    @Override
    protected void retrievedSingle(Pmc bo, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieveMember(bo.equifaxInfo());
        Persistence.service().retrieveMember(bo.equifaxFee());
        Persistence.service().retrieveMember(bo.yardiCredentials());

        for (PmcYardiCredential yardiCredential : bo.yardiCredentials()) {
            setOfuscatedPassword(yardiCredential.password());
        }
        setOfuscatedPassword(bo.equifaxInfo().memberNumber());
        setOfuscatedPassword(bo.equifaxInfo().securityCode());
    }

    private void setOfuscatedPassword(PasswordIdentity passwordDescr) {
        if (!passwordDescr.encrypted().isNull()) {
            passwordDescr.obfuscatedNumber().setValue("**");
        } else if (!passwordDescr.number().isNull()) {
            passwordDescr.obfuscatedNumber().setValue("##");
        }
    }

    @Override
    protected void enhanceRetrieved(Pmc bo, PmcDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        to.vistaCrmUrl().setValue(VistaDeployment.getBaseApplicationURL(bo, VistaApplication.crm, true));
        to.residentPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(bo, VistaApplication.resident, false));
        to.prospectPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(bo, VistaApplication.prospect, true));

        to.defaultPaymentFees().set(Persistence.service().retrieve(EntityQueryCriteria.create(DefaultPaymentFees.class)));
        Persistence.service().retrieveMember(bo.paymentTypeInfo());
        to.paymentTypeInfo().set(bo.paymentTypeInfo());

    }

    @Override
    protected void persist(Pmc bo, PmcDTO to) {
        if (!PmcNameValidator.isDnsNameValid(bo.dnsName().getValue())) {
            throw new UserRuntimeException("PMC DNS name is not valid");
        }
        if (!PmcNameValidator.isDnsNameValid(bo.namespace().getValue())) {
            throw new UserRuntimeException("PMC namespace is not valid");
        }

        Pmc orig = Persistence.secureRetrieve(Pmc.class, bo.getPrimaryKey());
        retrievedSingle(orig, RetrieveTarget.Edit);

        bo.dnsName().setValue(bo.dnsName().getValue().toLowerCase(Locale.ENGLISH));
        bo.namespace().setValue(bo.namespace().getValue().toLowerCase(Locale.ENGLISH).replace('-', '_'));

        for (PmcDnsName alias : bo.dnsNameAliases()) {
            alias.dnsName().setValue(alias.dnsName().getValue().toLowerCase(Locale.ENGLISH));
        }

        if (bo.features().yardiIntegration().getValue(false)) {
            bo.features().defaultProductCatalog().setValue(Boolean.TRUE);
        }

        for (PmcYardiCredential yardiCredential : bo.yardiCredentials()) {
            encryptPassword(yardiCredential);
        }
        encryptPassword(bo.equifaxInfo());

        super.persist(bo, to);

        ServerSideFactory.create(AuditFacade.class).updated(bo, EntityDiff.getChanges(orig, bo));

        TaskRunner.runInTargetNamespace(bo, new Callable<Void>() {
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
        if (!PmcNameValidator.canCreatePmcName(entity.dnsName().getValue(), null)) {
            throw new UserRuntimeException("PMC DNS name is reserved of forbidden");
        }
        for (PmcYardiCredential yardiCredential : entity.yardiCredentials()) {
            encryptPassword(yardiCredential);
        }
        encryptPassword(entity.equifaxInfo());
        ServerSideFactory.create(PmcFacade.class).create(entity);

        OnboardingUser user = dto.onboardingUser();
        user.pmc().set(entity);
        user.email().setValue(EmailValidator.normalizeEmailAddress(dto.onboardingUser().email().getValue()));
        Persistence.service().persist(user);
    }

    private void encryptPassword(PasswordIdentity value, PasswordIdentity originalValue) {
        if (!value.newNumber().isNull()) {
            ServerSideFactory.create(PasswordEncryptorFacade.class).encryptPassword(value, value.newNumber().getValue());
        } else if (!originalValue.isNull()) {
            if (!originalValue.number().isNull()) {
                ServerSideFactory.create(PasswordEncryptorFacade.class).encryptPassword(value, originalValue.number().getValue());
                value.number().setValue(null);
            } else {
                value.encrypted().setValue(originalValue.encrypted().getValue());
            }
        }
    }

    private void encryptPassword(PmcYardiCredential yardiCredential) {
        PmcYardiCredential orig;
        if (yardiCredential.getPrimaryKey() != null) {
            orig = Persistence.service().retrieve(PmcYardiCredential.class, yardiCredential.getPrimaryKey());
        } else {
            orig = EntityFactory.create(PmcYardiCredential.class);
        }

        encryptPassword(yardiCredential.password(), orig.password());
    }

    private void encryptPassword(PmcEquifaxInfo equifaxInfo) {
        PmcEquifaxInfo orig;
        if (equifaxInfo.getPrimaryKey() != null) {
            orig = Persistence.service().retrieve(PmcEquifaxInfo.class, equifaxInfo.getPrimaryKey());
        } else {
            orig = EntityFactory.create(PmcEquifaxInfo.class);
        }
        Persistence.service().retrieve(orig);
        encryptPassword(equifaxInfo.memberNumber(), orig.memberNumber());
        encryptPassword(equifaxInfo.securityCode(), orig.securityCode());
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

        Pmc pmc = Persistence.service().retrieve(boClass, entityId);
        pmc.status().setValue(PmcStatus.Suspended);
        Persistence.service().persist(pmc);
        Persistence.service().commit();
        CacheService.reset();

        callback.onSuccess(null);

    }

    @Override
    public void cancelPmc(AsyncCallback<VoidSerializable> callback, Key entityId) {
        SecurityController.assertPermission(EntityPermission.permissionUpdate(Pmc.class));

        Pmc pmc = Persistence.service().retrieve(boClass, entityId);

        ServerSideFactory.create(PmcFacade.class).cancelPmc(pmc);
        Persistence.service().commit();
        CacheService.reset();

        ServerSideFactory.create(AuditFacade.class).info("PMC {0} Cancelled by {1} ", pmc.namespace().getValue(), Context.getVisit().getUserVisit().getEmail());

        pmc = Persistence.service().retrieve(boClass, entityId);
        callback.onSuccess(null);
    }

    @Override
    public void runPmcProcess(AsyncCallback<Run> callback, Key entityId, PmcProcessType processType, LogicalDate executionDate) {
        Pmc pmc = Persistence.service().retrieve(boClass, entityId);
        Trigger trigger;
        {
            EntityQueryCriteria<Trigger> criteria = EntityQueryCriteria.create(Trigger.class);
            criteria.eq(criteria.proto().triggerType(), processType);
            {
                OrCriterion or1 = criteria.or();
                or1.left().eq(criteria.proto().populationType(), TriggerPmcSelectionType.allPmc);
                OrCriterion or2 = or1.right().or();
                or2.left().eq(criteria.proto().populationType(), TriggerPmcSelectionType.except);
                or2.left().ne(criteria.proto().population(), pmc);
                or2.right().eq(criteria.proto().populationType(), TriggerPmcSelectionType.manual);
                or2.right().eq(criteria.proto().population(), pmc);
            }

            criteria.asc(criteria.proto().id());
            trigger = Persistence.service().retrieve(criteria);
            if (trigger == null) {
                throw new UserRuntimeException("The Trigger " + processType + " not found for this PMC");
            }
        }

        Run run = ServerSideFactory.create(OperationsTriggerFacade.class).startProcess(trigger, pmc, executionDate);
        callback.onSuccess(run.<Run> createIdentityStub());
    }
}
