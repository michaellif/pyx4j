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

import com.propertyvista.crm.rpc.services.LeaseCrudService;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseDTO;

public class LeaseCrudServiceImpl implements LeaseCrudService {

    private final static Logger log = LoggerFactory.getLogger(LeaseCrudServiceImpl.class);

    @Override
    public void create(AsyncCallback<LeaseDTO> callback, LeaseDTO editableEntity) {
        Lease entity = GenericConverter.down(editableEntity, Lease.class);
        PersistenceServicesFactory.getPersistenceService().persist(entity);
        callback.onSuccess(GenericConverter.up(entity, LeaseDTO.class));
    }

    @Override
    public void retrieve(AsyncCallback<LeaseDTO> callback, String entityId) {
        Lease entity = PersistenceServicesFactory.getPersistenceService().retrieve(Lease.class, entityId);
        callback.onSuccess(GenericConverter.up(entity, LeaseDTO.class));
    }

    @Override
    public void save(AsyncCallback<LeaseDTO> callback, LeaseDTO editableEntity) {
        Lease entity = GenericConverter.down(editableEntity, Lease.class);
        PersistenceServicesFactory.getPersistenceService().merge(entity);
        callback.onSuccess(GenericConverter.up(entity, LeaseDTO.class));
    }

    @Override
    public void search(AsyncCallback<EntitySearchResult<LeaseDTO>> callback, EntitySearchCriteria<LeaseDTO> criteria) {
        EntitySearchCriteria<Lease> c = GenericConverter.down(criteria, Lease.class);
        //TODO add building specific criteria
        callback.onSuccess(GenericConverter.up(EntityServicesImpl.secureSearch(c), LeaseDTO.class));
    }
}
