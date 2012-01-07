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

import com.pyx4j.commons.Key;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.rpc.PmcDTO;
import com.propertyvista.admin.rpc.services.PmcCrudService;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.misc.VistaDataPreloaderParameter;
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
            Persistence.service().persist(entity);

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
            for (int i = 1; i <= DemoData.UserType.PM.getDefaultMax(); i++) {
                String email = DemoData.UserType.PM.getEmail(i);
                UserPreloader.createUser(email, email, VistaBehavior.PROPERTY_MANAGER);
            }
        }
        AbstractDataPreloader preloader = VistaDataPreloaders.productionPmcPreloaders();
        preloader.setParameterValue(VistaDataPreloaderParameter.pmcName.name(), pmc.name().getStringView());
        log.info("Preload {}", preloader.create());
    }

    @Override
    public void retrieve(AsyncCallback<PmcDTO> callback, Key entityId) {
        Pmc entity;
        try {
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            entity = Persistence.service().retrieve(Pmc.class, entityId);
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
            Persistence.service().merge(entity);
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

            EntitySearchResult<Pmc> data = Persistence.secureQuery(c);

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

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        try {
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            Persistence.service().delete(Pmc.class, entityId);
        } finally {
            NamespaceManager.remove();
        }
        callback.onSuccess(true);
    }

}
