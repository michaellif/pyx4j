/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

import com.propertyvista.crm.rpc.services.BoilerCrudService;
import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.dto.BoilerDTO;

public class BoilerCrudServiceImpl implements BoilerCrudService {

    private final static Logger log = LoggerFactory.getLogger(BoilerCrudServiceImpl.class);

    @Override
    public void create(AsyncCallback<BoilerDTO> callback, BoilerDTO editableEntity) {
        Boiler entity = GenericConverter.down(editableEntity, Boiler.class);
        PersistenceServicesFactory.getPersistenceService().persist(entity);
        callback.onSuccess(GenericConverter.up(entity, BoilerDTO.class));
    }

    @Override
    public void retrieve(AsyncCallback<BoilerDTO> callback, Key entityId) {
        Boiler entity = PersistenceServicesFactory.getPersistenceService().retrieve(Boiler.class, entityId);
        callback.onSuccess(GenericConverter.up(entity, BoilerDTO.class));
    }

    @Override
    public void save(AsyncCallback<BoilerDTO> callback, BoilerDTO editableEntity) {
        Boiler entity = GenericConverter.down(editableEntity, Boiler.class);
        PersistenceServicesFactory.getPersistenceService().merge(entity);
        callback.onSuccess(GenericConverter.up(entity, BoilerDTO.class));
    }

    @Override
    public void search(AsyncCallback<EntitySearchResult<BoilerDTO>> callback, EntitySearchCriteria<BoilerDTO> criteria) {
        EntitySearchCriteria<Boiler> c = GenericConverter.down(criteria, Boiler.class);
        //TODO add Boiler specific criteria
        callback.onSuccess(GenericConverter.up(EntityServicesImpl.secureSearch(c), BoilerDTO.class));
    }

}
