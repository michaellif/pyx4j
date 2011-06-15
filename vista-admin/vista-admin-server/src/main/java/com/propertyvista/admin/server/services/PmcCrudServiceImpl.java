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
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.PmcCrudService;
import com.propertyvista.common.domain.DemoData;
import com.propertyvista.common.domain.VistaBehavior;
import com.propertyvista.portal.rpc.corp.PmcAccountCreationRequest;
import com.propertyvista.portal.server.preloader.PortalSitePreload;
import com.propertyvista.portal.server.preloader.PreloadUsers;
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
        //TODO remove this if
        if (!pmc.email().isNull()) {
            PreloadUsers.createUser(pmc.email().getValue(), pmc.password().getValue(), VistaBehavior.PROPERTY_MANAGER);
        }

        if (ApplicationMode.isDevelopment()) {
            for (int i = 1; i <= DemoData.MAX_PROPERTY_MANAGER; i++) {
                String email = DemoData.CRM_PROPERTY_MANAGER_USER_PREFIX + CommonsStringUtils.d000(i) + DemoData.USERS_DOMAIN;
                PreloadUsers.createUser(email, email, VistaBehavior.PROPERTY_MANAGER);
            }
        }

        log.info("Preload {}", new PortalSitePreload().create());
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
    public void search(AsyncCallback<EntitySearchResult<PmcDTO>> callback, EntitySearchCriteria<PmcDTO> criteria) {
        EntitySearchResult<PmcDTO> result;
        try {
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            EntitySearchCriteria<Pmc> c = EntitySearchCriteria.create(Pmc.class);
            c.setPageNumber(criteria.getPageNumber());
            c.setPageSize(criteria.getPageSize());
            // TODO enhanceSearchCriteria

            EntitySearchResult<Pmc> data = EntityServicesImpl.secureSearch(c);

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
