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

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

import com.propertyvista.crm.rpc.services.UnitCrudService;
import com.propertyvista.dto.AptUnitDTO;

public class UnitCrudServiceImpl implements UnitCrudService {

    private final static Logger log = LoggerFactory.getLogger(UnitCrudServiceImpl.class);

    @Override
    public void create(AsyncCallback<AptUnitDTO> callback, AptUnitDTO editableEntity) {
        PersistenceServicesFactory.getPersistenceService().persist(editableEntity);
        callback.onSuccess(editableEntity);
    }

    @Override
    public void retrieve(AsyncCallback<AptUnitDTO> callback, long entityId) {
        AptUnitDTO editableEntity = PersistenceServicesFactory.getPersistenceService().retrieve(AptUnitDTO.class, entityId);
        callback.onSuccess(editableEntity);
    }

    @Override
    public void save(AsyncCallback<AptUnitDTO> callback, AptUnitDTO editableEntity) {
        PersistenceServicesFactory.getPersistenceService().merge(editableEntity);
        callback.onSuccess(editableEntity);
    }

    @Override
    public void search(AsyncCallback<EntitySearchResult<AptUnitDTO>> callback, EntitySearchCriteria<AptUnitDTO> criteria) {
        callback.onSuccess(EntityServicesImpl.secureSearch(criteria));
    }
}
