/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Aug 21, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.admin;

import com.pyx4j.essentials.rpc.admin.DBMaintenanceRequest;
import com.pyx4j.essentials.rpc.admin.DBMaintenanceServices;
import com.pyx4j.essentials.server.deferred.DeferredProcessServicesImpl;

public class DBMaintenanceServicesImpl implements DBMaintenanceServices {

    public static class DBMaintenanceImpl implements DBMaintenanceServices.DBMaintenance {

        @Override
        public String execute(DBMaintenanceRequest request) {
            return DeferredProcessServicesImpl.register(new DBMaintenanceDeferredProcess(request));
        }

    }
}
