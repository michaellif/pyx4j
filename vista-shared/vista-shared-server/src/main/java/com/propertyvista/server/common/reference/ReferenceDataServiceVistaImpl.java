/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 23, 2013
 * @author vlads
 */
package com.propertyvista.server.common.reference;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.ReferenceDataServiceImpl;

import com.propertyvista.domain.site.AvailableLocale;

/**
 * Midnight Solution for Province combo box filter
 * 
 * @author vlads
 * 
 */
public class ReferenceDataServiceVistaImpl extends ReferenceDataServiceImpl {

    @SuppressWarnings("unchecked")
    @Override
    public void query(AsyncCallback<EntitySearchResult<? extends IEntity>> callback, EntityQueryCriteria<? extends IEntity> criteria) {
        if (criteria.getEntityClass() == AvailableLocale.class) {
            EntitySearchResult<IEntity> result = new EntitySearchResult<>();
            result.setData((Vector<IEntity>) Persistence.secureQuery(criteria));
            callback.onSuccess(result);
        } else {
            super.query(callback, criteria);
        }
    }

}
