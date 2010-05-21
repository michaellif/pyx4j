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
 * Created on Feb 12, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server;

import java.util.Vector;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rpc.DataPreloaderInfo;
import com.pyx4j.entity.rpc.DatastoreAdminServices;
import com.pyx4j.rpc.shared.VoidSerializable;

public class EssentialsDatastoreAdminServicesImpl implements DatastoreAdminServices {

    public static class RemoveAllDataImpl implements DatastoreAdminServices.RemoveAllData {

        @Override
        public String execute(VoidSerializable request) {
            return ((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getDataPreloaders().delete();
        }
    }

    public static class ResetInitialDataImpl implements DatastoreAdminServices.ResetInitialData {

        @Override
        public String execute(VoidSerializable request) {
            return ((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getDataPreloaders().preloadAll();
        }
    }

    public static class GetPreloadersImpl implements DatastoreAdminServices.GetPreloaders {

        @Override
        public Vector<DataPreloaderInfo> execute(VoidSerializable request) {
            return ((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getDataPreloaders().getDataPreloaderInfo();
        }

    }

    public static class ExectutePreloadersCreateImpl implements DatastoreAdminServices.ExectutePreloadersCreate {

        @Override
        public String execute(Vector<DataPreloaderInfo> request) {
            return ((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getDataPreloaders().exectutePreloadersCreate(request);
        }

    }

    public static class ExectutePreloadersDeleteImpl implements DatastoreAdminServices.ExectutePreloadersDelete {

        @Override
        public String execute(Vector<DataPreloaderInfo> request) {
            return ((EssentialsServerSideConfiguration) ServerSideConfiguration.instance()).getDataPreloaders().exectutePreloadersDelete(request);
        }

    }

}
