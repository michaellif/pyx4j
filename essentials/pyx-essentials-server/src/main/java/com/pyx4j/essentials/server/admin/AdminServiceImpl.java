/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Sep 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.admin;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.rpc.admin.AdminService;
import com.pyx4j.essentials.rpc.admin.SystemMaintenanceState;

public class AdminServiceImpl implements AdminService {

    @Override
    public void getSystemReadOnlyStatus(AsyncCallback<Boolean> callback) {
        callback.onSuccess(ServerSideConfiguration.instance().datastoreReadOnly());
    }

    @Override
    public void setSystemMaintenanceSchedule(AsyncCallback<SystemMaintenanceState> callback) {
        callback.onSuccess(SystemMaintenance.getSystemMaintenanceInfo());
    }

    @Override
    public void scheduleSystemMaintenance(AsyncCallback<SystemMaintenanceState> callback, SystemMaintenanceState state) {
        SystemMaintenance.setSystemMaintenanceInfo(state);
        callback.onSuccess(SystemMaintenance.getSystemMaintenanceInfo());
    }
}
