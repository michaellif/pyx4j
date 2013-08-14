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
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.essentials.server.admin.AdminServiceImpl;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.quartz.SchedulerHelper;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.operations.rpc.VistaSystemMaintenanceState;
import com.propertyvista.operations.rpc.services.MaintenanceCrudService;
import com.propertyvista.config.VistaDeployment;

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
        callback.onSuccess(null);
    }

    // Crud service interface implementation:

    @Override
    public void create(AsyncCallback<Key> callback, VistaSystemMaintenanceState editableEntity) {
        throw new Error();
    }

    @Override
    public void retrieve(AsyncCallback<VistaSystemMaintenanceState> callback, Key entityId, RetrieveTarget retrieveTarget ) {
        VistaSystemMaintenanceState state = (VistaSystemMaintenanceState) SystemMaintenance.getSystemMaintenanceInfo();
        state.setPrimaryKey(entityId);
        callback.onSuccess(state);
    }

    @Override
    public void save(AsyncCallback<Key> callback, VistaSystemMaintenanceState state) {
        SystemMaintenance.setSystemMaintenanceInfo(state);
        SchedulerHelper.setActive(!VistaDeployment.isVistaStaging());
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
