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

import com.propertyvista.crm.rpc.services.TenantCrudService;
import com.propertyvista.domain.tenant.Tenant;

public class TenantCrudServiceImpl implements TenantCrudService {

    private final static Logger log = LoggerFactory.getLogger(TenantCrudServiceImpl.class);

    @Override
    public void create(AsyncCallback<Tenant> callback, Tenant editableEntity) {
        PersistenceServicesFactory.getPersistenceService().persist(editableEntity);
        callback.onSuccess(editableEntity);
    }

    @Override
    public void retrieve(AsyncCallback<Tenant> callback, String entityId) {
        callback.onSuccess(PersistenceServicesFactory.getPersistenceService().retrieve(Tenant.class, entityId));
    }

    @Override
    public void save(AsyncCallback<Tenant> callback, Tenant editableEntity) {
        PersistenceServicesFactory.getPersistenceService().merge(editableEntity);
        callback.onSuccess(editableEntity);
    }

    @Override
    public void search(AsyncCallback<EntitySearchResult<Tenant>> callback, EntitySearchCriteria<Tenant> criteria) {
        EntitySearchCriteria<Tenant> c = GenericConverter.down(criteria, Tenant.class);
        //TODO add Tenant specific criteria
        callback.onSuccess(EntityServicesImpl.secureSearch(c));
    }
}
