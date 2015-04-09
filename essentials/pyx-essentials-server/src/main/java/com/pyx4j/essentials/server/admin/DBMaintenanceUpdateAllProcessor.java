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
 * Created on Oct 4, 2010
 * @author vlads
 */
package com.pyx4j.essentials.server.admin;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.essentials.rpc.admin.IDBMaintenanceProcessor;

public class DBMaintenanceUpdateAllProcessor implements IDBMaintenanceProcessor<IEntity> {

    private static final long serialVersionUID = -8817187303509545582L;

    @Override
    public boolean process(IEntity entity) {
        return true;
    }

}
