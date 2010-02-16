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
 * Created on Feb 16, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.server;

import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.examples.domain.ExamplesBehavior;
import com.pyx4j.rpc.shared.ServiceExecutePermission;
import com.pyx4j.security.server.AppengineAclBuilder;
import com.pyx4j.security.shared.CoreBehavior;

class ExamplesAccessControlList extends AppengineAclBuilder {

    ExamplesAccessControlList() {

        grant(new ServiceExecutePermission("*"));
        // TODO Remove
        grant(new EntityPermission("*", EntityPermission.READ));

        grant(ExamplesBehavior.CRM_ADMIN, new EntityPermission("*", "*"));
        grant(ExamplesBehavior.CRM_EMPLOYEE, new EntityPermission("*", "*"));
        grant(ExamplesBehavior.CRM_CUSTOMER, new EntityPermission("*", "*"));

        grant(CoreBehavior.DEVELOPER, new ServiceExecutePermission("*"));
        grant(CoreBehavior.DEVELOPER, new EntityPermission("*", "*"));

        freeze();
    }
}
