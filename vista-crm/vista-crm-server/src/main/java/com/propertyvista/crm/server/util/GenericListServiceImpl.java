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
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.lister.EntityLister;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.site.rpc.services.AbstractListService;

/**
 * DBO - Data Base Object
 */
public abstract class GenericListServiceImpl<DBO extends IEntity> implements AbstractListService<DBO> {

    protected Class<DBO> dboClass;

    protected GenericListServiceImpl(Class<DBO> dboClass) {
        this.dboClass = dboClass;
    }

    protected void enhanceRetrieve(DBO entity, boolean fromList) {
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        Persistence.service().delete(dboClass, entityId);
        callback.onSuccess(true);
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<DBO>> callback, EntityListCriteria<DBO> criteria) {
        EntitySearchResult<DBO> result = EntityLister.secureQuery(criteria);
        for (DBO entity : result.getData()) {
            enhanceRetrieve(entity, true);
        }
        callback.onSuccess(result);
    }
}
