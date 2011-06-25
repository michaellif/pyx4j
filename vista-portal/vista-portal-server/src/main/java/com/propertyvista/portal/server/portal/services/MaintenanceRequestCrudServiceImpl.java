/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

import com.propertyvista.portal.domain.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.services.MaintenanceRequestCrudService;

//TODO the whole implementation is TBD
public class MaintenanceRequestCrudServiceImpl implements MaintenanceRequestCrudService {

    @Override
    public void create(AsyncCallback<MaintenanceRequestDTO> callback, MaintenanceRequestDTO editableEntity) {
        callback.onSuccess(editableEntity);

    }

    @Override
    public void retrieve(AsyncCallback<MaintenanceRequestDTO> callback, Key entityId) {
        MaintenanceRequestDTO entity = EntityFactory.create(MaintenanceRequestDTO.class);
        entity.id().setValue(new Key(1));
        callback.onSuccess(entity);

    }

    @Override
    public void save(AsyncCallback<MaintenanceRequestDTO> callback, MaintenanceRequestDTO editableEntity) {
        callback.onSuccess(editableEntity);

    }

    @Override
    public void search(AsyncCallback<EntitySearchResult<MaintenanceRequestDTO>> callback, EntitySearchCriteria<MaintenanceRequestDTO> criteria) {
        EntitySearchResult<MaintenanceRequestDTO> result = new EntitySearchResult();
        MaintenanceRequestDTO entity = EntityFactory.create(MaintenanceRequestDTO.class);
        entity.id().setValue(new Key(1));
        result.add(entity);
        callback.onSuccess(result);
    }
}
