/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

import com.propertyvista.crm.rpc.services.ApplicationCrudService;
import com.propertyvista.dto.ApplicationDTO;
import com.propertyvista.portal.domain.ptapp.Application;

public class ApplicationCrudServiceImpl implements ApplicationCrudService {

    private final static Logger log = LoggerFactory.getLogger(ApplicationCrudServiceImpl.class);

    @Override
    public void create(AsyncCallback<ApplicationDTO> callback, ApplicationDTO editableEntity) {
        Application entity = GenericConverter.down(editableEntity, Application.class);
        PersistenceServicesFactory.getPersistenceService().persist(entity);
        callback.onSuccess(GenericConverter.up(entity, ApplicationDTO.class));
    }

    @Override
    public void retrieve(AsyncCallback<ApplicationDTO> callback, String entityId) {
        Application entity = PersistenceServicesFactory.getPersistenceService().retrieve(Application.class, entityId);
        callback.onSuccess(GenericConverter.up(entity, ApplicationDTO.class));
    }

    @Override
    public void save(AsyncCallback<ApplicationDTO> callback, ApplicationDTO editableEntity) {
        Application entity = GenericConverter.down(editableEntity, Application.class);
        PersistenceServicesFactory.getPersistenceService().merge(entity);
        callback.onSuccess(GenericConverter.up(entity, ApplicationDTO.class));
    }

    @Override
    public void search(AsyncCallback<EntitySearchResult<ApplicationDTO>> callback, EntitySearchCriteria<ApplicationDTO> criteria) {
        EntitySearchCriteria<Application> c = GenericConverter.down(criteria, Application.class);
        //TODO add building specific criteria
        callback.onSuccess(GenericConverter.up(EntityServicesImpl.secureSearch(c), ApplicationDTO.class));
    }
}
