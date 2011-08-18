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
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.lister.EntityLister;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.PmcCrudService;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.PreloadConfig;
import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.portal.rpc.corp.PmcAccountCreationRequest;
import com.propertyvista.portal.server.preloader.UserPreloader;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;
import com.propertyvista.server.domain.admin.Pmc;

public class PmcCrudServiceImpl implements PmcCrudService {

    private final static Logger log = LoggerFactory.getLogger(PmcCrudServiceImpl.class);

    @Override
    public void createAccount(AsyncCallback<PmcDTO> callback, PmcAccountCreationRequest request) {
        // TODO Auto-generated method stub
    }

    private static Pmc convertDTO2DBO(PmcDTO src) {
        Pmc dst = EntityFactory.create(Pmc.class);
        dst.setPrimaryKey(src.getPrimaryKey());
        dst.name().setValue(src.name().getValue());
        dst.dnsName().setValue(src.dnsName().getValue().toLowerCase(Locale.ENGLISH));
        return dst;
    }

    private static PmcDTO convertDBO2DTO(Pmc src) {
        PmcDTO dst = EntityFactory.create(PmcDTO.class);
        dst.setPrimaryKey(src.getPrimaryKey());
        dst.name().setValue(src.name().getValue());
        dst.dnsName().setValue(src.dnsName().getValue());
        return dst;
    }

    @Override
    public void create(AsyncCallback<PmcDTO> callback, PmcDTO editableEntity) {
        Pmc entity;
        try {
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            entity = convertDTO2DBO(editableEntity);
            PersistenceServicesFactory.getPersistenceService().persist(entity);

            preloadPmc(editableEntity);

        } finally {
            NamespaceManager.remove();
        }
        callback.onSuccess(convertDBO2DTO(entity));
    }

    private static void preloadPmc(PmcDTO pmc) {
        NamespaceManager.setNamespace(pmc.dnsName().getValue());

        UserPreloader.createUser(pmc.email().getValue(), pmc.password().getValue(), VistaBehavior.PROPERTY_MANAGER);

        if (ApplicationMode.isDevelopment()) {
            PreloadConfig config = PreloadConfig.createDefault();
            for (int i = 1; i <= config.getMaxPropertyManagers(); i++) {
                String email = DemoData.CRM_PROPERTY_MANAGER_USER_PREFIX + CommonsStringUtils.d000(i) + DemoData.USERS_DOMAIN;
                UserPreloader.createUser(email, email, VistaBehavior.PROPERTY_MANAGER);
            }
        }

        log.info("Preload {}", VistaDataPreloaders.productionPmcPreloaders().create());
    }

    @Override
    public void retrieve(AsyncCallback<PmcDTO> callback, Key entityId) {
        Pmc entity;
        try {
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            entity = PersistenceServicesFactory.getPersistenceService().retrieve(Pmc.class, entityId);
        } finally {
            NamespaceManager.remove();
        }
        callback.onSuccess(convertDBO2DTO(entity));
    }

    @Override
    public void save(AsyncCallback<PmcDTO> callback, PmcDTO editableEntity) {
        Pmc entity;
        try {
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            entity = convertDTO2DBO(editableEntity);
            PersistenceServicesFactory.getPersistenceService().merge(entity);
        } finally {
            NamespaceManager.remove();
        }
        callback.onSuccess(convertDBO2DTO(entity));
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<PmcDTO>> callback, EntityListCriteria<PmcDTO> criteria) {
        EntitySearchResult<PmcDTO> result;
        try {
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            EntityListCriteria<Pmc> c = EntityListCriteria.create(Pmc.class);
            c.setPageNumber(criteria.getPageNumber());
            c.setPageSize(criteria.getPageSize());
            // TODO enhanceSearchCriteria

            EntitySearchResult<Pmc> data = EntityLister.secureQuery(c);

            result = new EntitySearchResult<PmcDTO>();
            result.setEncodedCursorReference(data.getEncodedCursorReference());
            result.hasMoreData(data.hasMoreData());
            result.setData(new Vector<PmcDTO>());
            for (Pmc entity : data.getData()) {
                result.getData().add(convertDBO2DTO(entity));
            }
        } finally {
            NamespaceManager.remove();
        }
        callback.onSuccess(result);

    }

}
