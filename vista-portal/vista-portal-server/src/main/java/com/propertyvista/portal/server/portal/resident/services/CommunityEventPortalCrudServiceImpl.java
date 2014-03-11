/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 10, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.resident.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.asset.CommunityEvent;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.rpc.portal.resident.dto.CommunityEventsGadgetDTO;
import com.propertyvista.portal.rpc.portal.resident.services.CommunityEventPortalCrudService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;

public class CommunityEventPortalCrudServiceImpl extends AbstractCrudServiceImpl<CommunityEvent> implements CommunityEventPortalCrudService {

    public CommunityEventPortalCrudServiceImpl() {
        super(CommunityEvent.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();

    }

    @Override
    public void retreiveCommunityEvents(AsyncCallback<CommunityEventsGadgetDTO> callback) {
        AptUnit unit = ResidentPortalContext.getUnit();
        if (unit == null || unit.isEmpty()) {
            callback.onSuccess(null);
            return;
        }

        EntityQueryCriteria<CommunityEvent> criteria = EntityQueryCriteria.create(CommunityEvent.class);
        criteria.eq(criteria.proto().building(), unit.building());
        criteria.ge(criteria.proto().date(), new LogicalDate());
        criteria.asc(criteria.proto().date());
        List<CommunityEvent> events = Persistence.service().query(criteria);

        if (events == null || events.isEmpty()) {
            callback.onSuccess(null);
            return;
        }

        CommunityEventsGadgetDTO data = EntityFactory.create(CommunityEventsGadgetDTO.class);
        for (CommunityEvent e : events) {
            data.events().add(e);
        }

        callback.onSuccess(data);
    }
}
