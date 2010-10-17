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
 * Created on 2010-05-21
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.rpc.admin;

import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessServices;
import com.pyx4j.rpc.shared.Service;
import com.pyx4j.rpc.shared.VoidSerializable;

public interface AdminServices {

    public interface CountSessions extends Service<VoidSerializable, String> {

    };

    public interface PurgeExpiredSessions extends DeferredProcessServices.AbstractStartDeferredProcessService {

    };

    public interface PurgeAllSessions extends DeferredProcessServices.AbstractStartDeferredProcessService {

    };

    public interface MemcacheClear extends Service<VoidSerializable, VoidSerializable> {

    };

    public interface MemcacheStatistics extends Service<VoidSerializable, String> {

    };

    public interface NetworkSimulationSet extends EntityServices.Save, IsIgnoreNetworkSimulationService {

    };

    public interface NetworkSimulationRetrieve extends Service<VoidSerializable, NetworkSimulation>, IsIgnoreNetworkSimulationService {
    };
}
