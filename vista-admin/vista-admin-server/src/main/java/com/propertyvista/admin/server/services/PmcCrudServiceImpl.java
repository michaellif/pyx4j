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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.PmcCrudService;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.PmcDnsName;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.portal.rpc.corp.PmcAccountCreationRequest;
import com.propertyvista.portal.server.preloader.CrmRolesPreloader;
import com.propertyvista.portal.server.preloader.UserPreloader;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;
import com.propertyvista.server.domain.admin.Pmc;

public class PmcCrudServiceImpl extends AbstractCrudServiceDtoImpl<Pmc, PmcDTO> implements PmcCrudService {

    private final static Logger log = LoggerFactory.getLogger(PmcCrudServiceImpl.class);

    public PmcCrudServiceImpl() {
        super(Pmc.class, PmcDTO.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.name(), dboProto.name());
        bind(dtoProto.dnsName(), dboProto.dnsName());
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
        for (PmcDnsName alias : entity.dnsNameAliases()) {
            alias.dnsName().setValue(alias.dnsName().getValue().toLowerCase(Locale.ENGLISH));
        }
        super.persist(entity, dto);
        CacheService.reset();
    }

    @Override
    public void create(AsyncCallback<PmcDTO> callback, PmcDTO editableEntity) {
        editableEntity.dnsName().setValue(editableEntity.dnsName().getValue().toLowerCase(Locale.ENGLISH));
        super.create(callback, editableEntity);
        preloadPmc(editableEntity);
    }

    private static void preloadPmc(PmcDTO pmc) {
        final String namespace = NamespaceManager.getNamespace();
        NamespaceManager.setNamespace(pmc.dnsName().getValue());
        try {

            AbstractDataPreloader preloader = VistaDataPreloaders.productionPmcPreloaders();
            preloader.setParameterValue(VistaDataPreloaderParameter.pmcName.name(), pmc.name().getStringView());
            log.info("Preload {}", preloader.create());

            CrmRole defaultRole = CrmRolesPreloader.getDefaultRole();
            UserPreloader.createCrmUser(pmc.email().getValue(), pmc.email().getValue(), pmc.password().getValue(), defaultRole);

            // Create support account by default
            UserPreloader.createCrmUser("PropertyVista Support", "support@propertyvista.com", "Vista2012", defaultRole);

            if (ApplicationMode.isDevelopment()) {
                for (int i = 1; i <= DemoData.UserType.PM.getDefaultMax(); i++) {
                    String email = DemoData.UserType.PM.getEmail(i);
                    UserPreloader.createCrmUser(email, email, email, defaultRole);
                }
            }
            Persistence.service().commit();
        } finally {
            NamespaceManager.setNamespace(namespace);
        }
    }

    @Override
    public void resetCache(AsyncCallback<VoidSerializable> callback, Key entityId) {
        final String namespace = NamespaceManager.getNamespace();
        Pmc pmc = Persistence.service().retrieve(entityClass, entityId);
        NamespaceManager.setNamespace(pmc.dnsName().getValue());
        try {
            CacheService.reset();
        } finally {
            NamespaceManager.setNamespace(namespace);
        }
        callback.onSuccess(null);
    }
}
