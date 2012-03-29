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
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.dashboard.AbstractMetadataCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.ISharedUserEntity;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.server.common.security.VistaContext;

abstract class AbstractMetadataCrudServiceImpl extends AbstractCrudServiceImpl<DashboardMetadata> implements AbstractMetadataCrudService {

    protected AbstractMetadataCrudServiceImpl() {
        super(DashboardMetadata.class);
    }

    abstract void addTypeCriteria(EntityListCriteria<DashboardMetadata> criteria);

    @Override
    public void list(AsyncCallback<EntitySearchResult<DashboardMetadata>> callback, EntityListCriteria<DashboardMetadata> criteria) {
        criteria.or().left(PropertyCriterion.eq(criteria.proto().user(), CrmAppContext.getCurrentUserPrimaryKey()))
                .right(PropertyCriterion.eq(criteria.proto().isShared(), true));
        addTypeCriteria(criteria);
        super.list(callback, criteria);
    }

    @Override
    public void create(AsyncCallback<DashboardMetadata> callback, DashboardMetadata entity) {
        entity.setPrimaryKey(null);

        if (entity.isShared().isBooleanTrue()) {
            entity.user().setPrimaryKey(ISharedUserEntity.DORMANT_KEY);
        } else {
            entity.user().setPrimaryKey(CrmAppContext.getCurrentUserPrimaryKey());
        }
        super.create(callback, entity);
    }

    @Override
    public void save(AsyncCallback<DashboardMetadata> callback, DashboardMetadata entity) {
        //Assert Permission
        Persistence.secureRetrieve(DashboardMetadata.class, entity.getPrimaryKey());

        // TODO  add proper management of secure adapters

        if (entity.user().getPrimaryKey() != ISharedUserEntity.DORMANT_KEY) {
            entity.user().setPrimaryKey(VistaContext.getCurrentUserPrimaryKey());
        }

        if (entity.isShared().isBooleanTrue()) {
            entity.user().setPrimaryKey(ISharedUserEntity.DORMANT_KEY);
        }

        super.save(callback, entity);
    }
}
