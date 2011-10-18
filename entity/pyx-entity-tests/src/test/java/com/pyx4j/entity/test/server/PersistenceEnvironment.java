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
 * Created on Jan 23, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.server;

import com.pyx4j.config.shared.ApplicationBackend.ApplicationBackendType;
import com.pyx4j.entity.server.IEntityPersistenceService;

/**
 * Setup Persistence Tests environment, Required for GAE Persistence JVM test in JUnit.
 */
public abstract class PersistenceEnvironment {

    public enum EnvironmentType {
        LocalJVM, GAEDevelopment, GAESandbox
    }

    public abstract ApplicationBackendType getBackendType();

    public abstract IEntityPersistenceService setupDatastore();

    public abstract void teardownDatastore(IEntityPersistenceService srv);

    public static EnvironmentType getEnvironmentType() {
        SecurityManager sm = System.getSecurityManager();
        if (sm == null) {
            return EnvironmentType.LocalJVM;
        } else if (sm.getClass().getName().startsWith("com.google.appengine.tools.development")) {
            return EnvironmentType.GAEDevelopment;
        } else if (sm.getClass().getName().startsWith("com.google.apphosting.")) {
            return EnvironmentType.GAESandbox;
        } else {
            return EnvironmentType.LocalJVM;
        }
    }
}
