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
 */
package com.propertyvista.crm.server.services.dashboard;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.dashboard.GadgetStorageFacade;
import com.propertyvista.biz.system.VistaContext;
import com.propertyvista.crm.rpc.dto.dashboard.DashboardColumnLayoutFormat;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;

public class DashboardMetadataCrudServiceImpl extends AbstractCrudServiceImpl<DashboardMetadata> implements DashboardMetadataCrudService {

    public DashboardMetadataCrudServiceImpl() {
        super(DashboardMetadata.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected DashboardMetadata init(InitializationData initializationData) {
        DashboardMetadata entity = EntityFactory.create(DashboardMetadata.class);

        entity.encodedLayout().setValue(new DashboardColumnLayoutFormat.Builder(LayoutType.Two11).build().getSerializedForm());

        return entity;
    }

    @Override
    protected void create(DashboardMetadata bo, DashboardMetadata to) {
        bo.ownerUser().setPrimaryKey(VistaContext.getCurrentUserPrimaryKey());
        super.create(bo, to);
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        // delete all child gadgets
        DashboardMetadata dm = Persistence.secureRetrieve(DashboardMetadata.class, entityId);

        GadgetStorageFacade gadgetStorageFacadeFacade = ServerSideFactory.create(GadgetStorageFacade.class);
        for (String gadgetId : new DashboardColumnLayoutFormat(dm.encodedLayout().getValue()).gadgetIds()) {
            gadgetStorageFacadeFacade.delete(gadgetId);
        }
        super.delete(callback, entityId);
    }

    @Override
    protected void enhanceRetrieved(DashboardMetadata bo, DashboardMetadata to, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieve(to.ownerUser());
    }

    @Override
    protected void enhanceListRetrieved(DashboardMetadata entity, DashboardMetadata dto) {
        super.enhanceListRetrieved(entity, dto);
        this.enhanceRetrieved(entity, dto, null);
    }
}
