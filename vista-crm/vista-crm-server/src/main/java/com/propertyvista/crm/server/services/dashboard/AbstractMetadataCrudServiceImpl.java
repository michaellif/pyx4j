/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

import com.propertyvista.crm.rpc.services.dashboard.AbstractMetadataCrudService;
import com.propertyvista.crm.server.services.GenericCrudServiceImpl;
import com.propertyvista.domain.dashboard.DashboardMetadata;

abstract class AbstractMetadataCrudServiceImpl extends GenericCrudServiceImpl<DashboardMetadata> implements AbstractMetadataCrudService {

    protected AbstractMetadataCrudServiceImpl() {
        super(DashboardMetadata.class);
    }

    abstract void addTypeCriteria(EntitySearchCriteria<DashboardMetadata> criteria);

    @Override
    public void search(AsyncCallback<EntitySearchResult<DashboardMetadata>> callback, EntitySearchCriteria<DashboardMetadata> criteria) {
        addTypeCriteria(criteria);

        //TODO add or for public or private user keys

        callback.onSuccess(EntityServicesImpl.secureSearch(criteria));
    }
}
