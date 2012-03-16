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
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.PmcCrudService;
import com.propertyvista.domain.DemoData;
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
    public void create(AsyncCallback<PmcDTO> callback, PmcDTO editableEntity) {
        try {
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            editableEntity.dnsName().setValue(editableEntity.dnsName().getValue().toLowerCase(Locale.ENGLISH));

            super.create(callback, editableEntity);

            preloadPmc(editableEntity);

        } finally {
            NamespaceManager.remove();
        }
    }

    private static void preloadPmc(PmcDTO pmc) {
        NamespaceManager.setNamespace(pmc.dnsName().getValue());

        AbstractDataPreloader preloader = VistaDataPreloaders.productionPmcPreloaders();
        preloader.setParameterValue(VistaDataPreloaderParameter.pmcName.name(), pmc.name().getStringView());
        log.info("Preload {}", preloader.create());

        CrmRole defaultRole = CrmRolesPreloader.getDefaultRole();
        UserPreloader.createCrmUser(pmc.email().getValue(), pmc.email().getValue(), pmc.password().getValue(), defaultRole);
        if (ApplicationMode.isDevelopment()) {
            for (int i = 1; i <= DemoData.UserType.PM.getDefaultMax(); i++) {
                String email = DemoData.UserType.PM.getEmail(i);
                UserPreloader.createCrmUser(email, email, email, defaultRole);
            }
        }

    }

    @Override
    public void retrieve(AsyncCallback<PmcDTO> callback, Key entityId, RetrieveTraget retrieveTraget) {
        try {
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            super.retrieve(callback, entityId, retrieveTraget);
        } finally {
            NamespaceManager.remove();
        }
    }

    @Override
    public void save(AsyncCallback<PmcDTO> callback, PmcDTO editableEntity) {
        try {
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            super.save(callback, editableEntity);
        } finally {
            NamespaceManager.remove();
        }
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<PmcDTO>> callback, EntityListCriteria<PmcDTO> criteria) {
        try {
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            super.list(callback, criteria);
        } finally {
            NamespaceManager.remove();
        }
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        try {
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            super.delete(callback, entityId);
        } finally {
            NamespaceManager.remove();
        }
    }
}
