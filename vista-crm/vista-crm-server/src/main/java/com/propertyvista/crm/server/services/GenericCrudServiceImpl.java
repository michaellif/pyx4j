/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-31
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

import com.propertyvista.crm.rpc.services.AbstractCrudService;
import com.propertyvista.domain.marketing.yield.Concession;

/**
 * DBO - Data Base Object
 */
public abstract class GenericCrudServiceImpl<DBO extends IEntity> implements AbstractCrudService<DBO> {

    protected Class<DBO> dboClass;

    protected GenericCrudServiceImpl(Class<DBO> dboClass) {
        this.dboClass = dboClass;
    }

    @Override
    public void create(AsyncCallback<DBO> callback, DBO entity) {
        PersistenceServicesFactory.getPersistenceService().persist(entity);
        callback.onSuccess(entity);
    }

    @Override
    public void retrieve(AsyncCallback<DBO> callback, Key entityId) {
        callback.onSuccess(PersistenceServicesFactory.getPersistenceService().retrieve(dboClass, entityId));
    }

    @Override
    public void save(AsyncCallback<DBO> callback, DBO entity) {
        PersistenceServicesFactory.getPersistenceService().merge(entity);
        callback.onSuccess(entity);
    }

    @Override
    public void search(AsyncCallback<EntitySearchResult<DBO>> callback, EntitySearchCriteria<DBO> criteria) {
        callback.onSuccess(EntityServicesImpl.secureSearch(criteria));
    }
}
