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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.crm.rpc.dto.dashboard.DashboardColumnLayoutFormat;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.server.common.security.VistaContext;

public class DashboardMetadataCrudServiceImpl extends AbstractCrudServiceImpl<DashboardMetadata> implements DashboardMetadataCrudService {

    public DashboardMetadataCrudServiceImpl() {
        super(DashboardMetadata.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    public void create(AsyncCallback<Key> callback, DashboardMetadata dashboardMetadata) {
        if (dashboardMetadata.getPrimaryKey() == null) {
            dashboardMetadata.ownerUser().setPrimaryKey(VistaContext.getCurrentUserPrimaryKey());
            super.create(callback, dashboardMetadata);
        } else {
            throw new SecurityViolationException("Trying to overwrite an existing entity");
        }
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        // delete all child gadgets
        DashboardMetadata dm = Persistence.secureRetrieve(DashboardMetadata.class, entityId);
        for (String gadgetId : new DashboardColumnLayoutFormat(dm.encodedLayout().getValue()).gadgetIds()) {
            Util.gadgetStorage().delete(gadgetId);
        }
        super.delete(callback, entityId);
    }

    @Override
    protected void enhanceRetrieved(DashboardMetadata entity, DashboardMetadata dto, RetrieveTraget retrieveTraget) {
        Persistence.service().retrieve(dto.ownerUser());
    }

    @Override
    protected void enhanceListRetrieved(DashboardMetadata entity, DashboardMetadata dto) {
        super.enhanceListRetrieved(entity, dto);
        this.enhanceRetrieved(entity, dto, null);
    }
}
