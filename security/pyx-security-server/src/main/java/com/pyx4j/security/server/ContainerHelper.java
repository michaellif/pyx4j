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
 * Created on 2011-01-14
 * @author vlads
 */
package com.pyx4j.security.server;

import com.pyx4j.config.server.ServerSideConfiguration;

public class ContainerHelper {

    protected static IContainerHelper containerHelper;

    public static IContainerHelper getContainerHelper() {
        if (containerHelper == null) {
            switch (ServerSideConfiguration.instance().getEnvironmentType()) {
            case LocalJVM:
                containerHelper = new ServletContainerHelper();
                break;
            default:
                containerHelper = new AppengineContainerHelper();
                break;
            }
        }
        return containerHelper;
    }

    public static boolean isDBReadOnly() {
        return getContainerHelper().isDBReadOnly();
    }
}
