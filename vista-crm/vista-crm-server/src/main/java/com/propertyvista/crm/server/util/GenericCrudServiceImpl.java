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
package com.propertyvista.crm.server.util;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IEntity;

/**
 * DBO - Data Base Object
 */
public abstract class GenericCrudServiceImpl<DBO extends IEntity> extends GenericListServiceImpl<DBO> implements AbstractCrudService<DBO> {

    protected GenericCrudServiceImpl(Class<DBO> dboClass) {
        super(dboClass);
    }

    protected void persistDBO(DBO dbo) {
        Persistence.secureSave(dbo);
    }

    @Override
    public void create(AsyncCallback<DBO> callback, DBO entity) {
        persistDBO(entity);
        callback.onSuccess(entity);
    }

    @Override
    public void retrieve(AsyncCallback<DBO> callback, Key entityId) {
        DBO entity = Persistence.secureRetrieve(dboClass, entityId);
        enhanceRetrieve(entity, false);
        callback.onSuccess(entity);
    }

    /**
     * This is empty method. Don't need to call it in Override
     */
    protected void enhanceSave(DBO entity) {
    }

    @Override
    public void save(AsyncCallback<DBO> callback, DBO entity) {
        persistDBO(entity);
        enhanceSave(entity);
        callback.onSuccess(entity);
    }
}
