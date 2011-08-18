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
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceServicesFactory {

    private static final Logger log = LoggerFactory.getLogger(PersistenceServicesFactory.class);

    private static IEntityPersistenceService instance;

    public final static String GAE_IMPL_CLASS = "com.pyx4j.entity.gae.EntityPersistenceServiceGAE";

    public final static String RDBMS_IMPL_CLASS = "com.pyx4j.entity.rdb.EntityPersistenceServiceRDB";

    @SuppressWarnings("unchecked")
    public static synchronized IEntityPersistenceService getPersistenceService() {
        if (instance == null) {
            Class<? extends IEntityPersistenceService> serviceClass;
            try {
                serviceClass = (Class<? extends IEntityPersistenceService>) Class.forName(GAE_IMPL_CLASS);
            } catch (ClassNotFoundException e) {
                try {
                    serviceClass = (Class<? extends IEntityPersistenceService>) Class.forName(RDBMS_IMPL_CLASS);
                } catch (ClassNotFoundException e2) {
                    throw new RuntimeException("PersistenceService not found");
                }
            }
            try {
                instance = serviceClass.newInstance();
            } catch (Throwable e) {
                log.error("creating PersistenceService from [" + serviceClass.getName() + "]", e);
                throw new RuntimeException("PersistenceService not available");
            }
        }
        return instance;
    }

    public static synchronized void dispose() {
        if (instance != null) {
            try {
                if (instance instanceof IEntityPersistenceServiceExt) {
                    ((IEntityPersistenceServiceExt) instance).dispose();
                }
            } finally {
                instance = null;
            }
        }
    }

    public static synchronized void deregister() {
        if (instance != null) {
            try {
                if (instance instanceof IEntityPersistenceServiceExt) {
                    ((IEntityPersistenceServiceExt) instance).deregister();
                }
            } finally {
                instance = null;
            }
        }
    }
}
