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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.PmcCrudService;
import com.propertyvista.admin.server.onboarding.PmcNameValidator;
import com.propertyvista.domain.PmcDnsName;
import com.propertyvista.portal.rpc.corp.PmcAccountCreationRequest;
import com.propertyvista.portal.server.preloader.PmcCreator;
import com.propertyvista.server.domain.admin.Pmc;

public class PmcCrudServiceImpl extends AbstractCrudServiceDtoImpl<Pmc, PmcDTO> implements PmcCrudService {

    public PmcCrudServiceImpl() {
        super(Pmc.class, PmcDTO.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.enabled(), dboProto.enabled());
        bind(dtoProto.name(), dboProto.name());
        bind(dtoProto.dnsName(), dboProto.dnsName());
        bind(dtoProto.namespace(), dboProto.namespace());
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
        entity.namespace().setValue(entity.namespace().getValue().toLowerCase(Locale.ENGLISH));
        for (PmcDnsName alias : entity.dnsNameAliases()) {
            alias.dnsName().setValue(alias.dnsName().getValue().toLowerCase(Locale.ENGLISH));
        }
        super.persist(entity, dto);
        CacheService.reset();
    }

    @Override
    public void create(AsyncCallback<PmcDTO> callback, PmcDTO editableEntity) {
        editableEntity.dnsName().setValue(editableEntity.dnsName().getValue().toLowerCase(Locale.ENGLISH));
        editableEntity.namespace().setValue(editableEntity.dnsName().getValue());
        if (!PmcNameValidator.canCreatePmcName(editableEntity.dnsName().getValue())) {
            throw new UserRuntimeException("PMC DNS name is reserved of forbidden");
        }

        super.create(callback, editableEntity);

        try {
            Persistence.service().startBackgroundProcessTransaction();
            PmcCreator.preloadPmc(editableEntity);
            Persistence.service().commit();
        } finally {
            Persistence.service().endTransaction();
        }

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
}
