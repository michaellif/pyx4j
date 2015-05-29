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
 * Created on May 20, 2012
 * @author vlads
 */
package com.pyx4j.entity.rdb;

import com.mchange.v2.c3p0.management.ActiveManagementCoordinator;

/**
 * C3P0 JMX names bound to WebApp context name
 * To enable add property to c3p0 config file:
 * com.mchange.v2.c3p0.management.ManagementCoordinator=com.pyx4j.entity.rdb.C3P0ActiveManagementCoordinator
 *
 * @deprecated fixed in c3p0-0.9.5 http://sourceforge.net/p/c3p0/bugs/121/
 */
@Deprecated
public class C3P0ActiveManagementCoordinator extends ActiveManagementCoordinator {

    public C3P0ActiveManagementCoordinator() throws Exception {
        super();
    }

}
