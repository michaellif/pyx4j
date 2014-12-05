/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 27, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.shared.services.communityevent;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.asset.CommunityEvent;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.rpc.portal.shared.services.communityevent.CommunityEventCrudService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;

public class CommunityEventCrudServiceImpl implements CommunityEventCrudService {

    @Override
    public void init(AsyncCallback<CommunityEvent> callback, com.pyx4j.entity.rpc.AbstractCrudService.InitializationData initializationData) {
        // TODO Auto-generated method stub

    }

    @Override
    public void retrieve(AsyncCallback<CommunityEvent> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        CommunityEvent event = EntityFactory.create(CommunityEvent.class);
        callback.onSuccess(event);

    }

    @Override
    public void create(AsyncCallback<Key> callback, CommunityEvent editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void save(AsyncCallback<Key> callback, CommunityEvent editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<CommunityEvent>> callback, EntityListCriteria<CommunityEvent> criteria) {
        AptUnit unit = ResidentPortalContext.getUnit();
        if (unit == null || unit.isEmpty()) {
            callback.onSuccess(null);
            return;
        }
        EntityQueryCriteria<CommunityEvent> queryCriteria = EntityQueryCriteria.create(CommunityEvent.class);
        queryCriteria.eq(queryCriteria.proto().building(), unit.building());
        queryCriteria.ge(queryCriteria.proto().date(), new LogicalDate());
        queryCriteria.asc(queryCriteria.proto().date());

        List<CommunityEvent> events = Persistence.service().query(queryCriteria);
        if (events == null || events.isEmpty()) {
            callback.onSuccess(null);
            return;
        }

        EntitySearchResult<CommunityEvent> result = new EntitySearchResult<CommunityEvent>();
        for (CommunityEvent e : events) {
            result.add(e);
        }

        //TODO: sort the events and get first 3
        callback.onSuccess(result);
    }
}
