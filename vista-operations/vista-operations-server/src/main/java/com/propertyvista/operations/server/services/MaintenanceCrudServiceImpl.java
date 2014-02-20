/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.DatastoreReadOnlyRuntimeException;
import com.pyx4j.entity.shared.utils.EntityDiff;
import com.pyx4j.essentials.rpc.admin.SystemMaintenanceState;
import com.pyx4j.essentials.server.admin.AdminServiceImpl;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.quartz.SchedulerHelper;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.YardiOperationsFacade;
import com.propertyvista.config.SystemConfig;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.operations.domain.VistaSystemMaintenanceState;
import com.propertyvista.operations.rpc.services.MaintenanceCrudService;

public class MaintenanceCrudServiceImpl extends AdminServiceImpl implements MaintenanceCrudService {

    public MaintenanceCrudServiceImpl() {
    }

    @Override
    public void getSystemReadOnlyStatus(AsyncCallback<Boolean> callback) {
        callback.onSuccess(ServerSideConfiguration.instance().datastoreReadOnly());
    }

    @Override
    public void resetGlobalCache(AsyncCallback<VoidSerializable> callback) {
        CacheService.resetAll();
        ServerSideFactory.create(YardiOperationsFacade.class).restLicenseCache();
        callback.onSuccess(null);
    }

    // Crud service interface implementation:

    @Override
    public void init(AsyncCallback<VistaSystemMaintenanceState> callback, InitializationData initializationData) {
        throw new Error();
    }

    @Override
    public void create(AsyncCallback<Key> callback, VistaSystemMaintenanceState editableEntity) {
        throw new Error();
    }

    @Override
    public void retrieve(AsyncCallback<VistaSystemMaintenanceState> callback, Key entityId, RetrieveTarget retrieveTarget) {
        VistaSystemMaintenanceState state = (VistaSystemMaintenanceState) SystemMaintenance.getSystemMaintenanceInfo();
        state.setPrimaryKey(entityId);
        callback.onSuccess(state);
    }

    @Override
    public void save(AsyncCallback<Key> callback, VistaSystemMaintenanceState state) {
        SystemMaintenanceState origState = SystemMaintenance.getSystemMaintenanceInfo().duplicate(VistaSystemMaintenanceState.class);

        SystemMaintenance.setSystemMaintenanceInfo(state);
        SchedulerHelper.setActive(!VistaDeployment.isVistaStaging());

        try {
            ServerSideFactory.create(AuditFacade.class).record(AuditRecordEventType.System, null, "System {0};\nState changes: {1}",
                    SystemConfig.getLocalHostName(), EntityDiff.getChanges(origState, SystemMaintenance.getSystemMaintenanceInfo()));
        } catch (DatastoreReadOnlyRuntimeException readOnly) {
            //TODO remove this when we have second Audit connection 
            // ignore for now.
        }

        callback.onSuccess(state.getPrimaryKey());
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<VistaSystemMaintenanceState>> callback, EntityListCriteria<VistaSystemMaintenanceState> criteria) {
        throw new Error();
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        throw new Error();
    }

}
