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
 * Created on Jan 27, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rpc;

import java.util.Vector;

import com.pyx4j.rpc.shared.Service;
import com.pyx4j.rpc.shared.VoidSerializable;

/**
 * This is mainly development time service.
 */
public interface DatastoreAdminServices {

    public interface RemoveAllData extends Service<VoidSerializable, String> {

    };

    public interface ResetInitialData extends Service<VoidSerializable, String> {

    };

    public interface GetPreloaders extends Service<VoidSerializable, Vector<DataPreloaderInfo>> {

    };

    public interface ExectutePreloaders extends Service<Vector<DataPreloaderInfo>, String> {

    };
}
