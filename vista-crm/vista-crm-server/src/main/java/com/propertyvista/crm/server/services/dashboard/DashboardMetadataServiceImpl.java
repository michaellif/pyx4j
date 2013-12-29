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
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.biz.dashboard.DashboardManagementFacade;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class DashboardMetadataServiceImpl implements DashboardMetadataService {

    @Override
    public void retrieveMetadata(AsyncCallback<DashboardMetadata> callback, Key dashboardId) {
        assert (dashboardId != null);
        DashboardMetadata dashboardMetadata = ServerSideFactory.create(DashboardManagementFacade.class).retrieveMetadata(
                EntityFactory.createIdentityStub(DashboardMetadata.class, dashboardId));

        Persistence.service().commit();
        callback.onSuccess(dashboardMetadata);
    }

    @Override
    public void saveDashboardMetadata(AsyncCallback<DashboardMetadata> callback, DashboardMetadata dm) {
        DashboardMetadata dashboardMetadata = ServerSideFactory.create(DashboardManagementFacade.class).saveDashboardMetadata(dm);

        Persistence.service().commit();
        callback.onSuccess(dashboardMetadata);
    }

    @Override
    public void takeOwnership(AsyncCallback<VoidSerializable> callback, DashboardMetadata dashboardMetadataStub) {

        SecurityController.assertBehavior(VistaCrmBehavior.DashboardManager);
        DashboardMetadata dashboardMetadata = Persistence.service().retrieve(DashboardMetadata.class, dashboardMetadataStub.getPrimaryKey());
        if (dashboardMetadata == null) {
            throw new Error("dashboard metadata '" + dashboardMetadataStub.getPrimaryKey() + "' was not found");
        }

        Key managersPk = CrmAppContext.getCurrentUserPrimaryKey();

        ServerSideFactory.create(DashboardManagementFacade.class).setDashboardOwner(dashboardMetadata, managersPk);

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void changeOwnership(AsyncCallback<VoidSerializable> callback, DashboardMetadata dashboardMetadataStub, CrmUser updatedOwnerStub) {
        DashboardMetadata dashboardMetadata = Persistence.service().retrieve(DashboardMetadata.class, dashboardMetadataStub.getPrimaryKey());
        if (dashboardMetadata == null) {
            throw new Error("dashboard metadata '" + dashboardMetadataStub.getPrimaryKey() + "' was not found");
        }
        if (!CrmAppContext.getCurrentUserPrimaryKey().equals(dashboardMetadata.ownerUser().getPrimaryKey())) {
            throw new SecurityViolationException("changing owner of a dashboard by not owner is forbidden");
        }
        ServerSideFactory.create(DashboardManagementFacade.class).setDashboardOwner(dashboardMetadata, updatedOwnerStub.getPrimaryKey());
        Persistence.service().commit();
        callback.onSuccess(null);
    }

}
