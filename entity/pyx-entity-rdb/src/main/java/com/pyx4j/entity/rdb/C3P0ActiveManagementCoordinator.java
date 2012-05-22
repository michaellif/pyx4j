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
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.mchange.v2.c3p0.PooledDataSource;
import com.mchange.v2.c3p0.management.ActiveManagementCoordinator;
import com.mchange.v2.c3p0.management.C3P0RegistryManager;
import com.mchange.v2.c3p0.management.DynamicPooledDataSourceManagerMBean;
import com.mchange.v2.c3p0.management.ManagementCoordinator;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;

import com.pyx4j.log4j.LoggerConfig;

/**
 * C3P0 JMX names bound to WebApp context name
 * To enable add property to c3p0 config file:
 * com.mchange.v2.c3p0.management.ManagementCoordinator=com.pyx4j.entity.rdb.C3P0ActiveManagementCoordinator
 * 
 */
public class C3P0ActiveManagementCoordinator implements ManagementCoordinator {

    private final String C3P0_REGISTRY_NAME;

    //MT: thread-safe
    final static MLogger logger = MLog.getLogger(ActiveManagementCoordinator.class);

    MBeanServer mbs;

    public C3P0ActiveManagementCoordinator() throws Exception {
        this.mbs = ManagementFactory.getPlatformMBeanServer();
        C3P0_REGISTRY_NAME = "com.mchange.v2.c3p0." + LoggerConfig.getContextName() + ":type=C3P0Registry";
    }

    @Override
    public void attemptManageC3P0Registry() {
        try {
            ObjectName name = new ObjectName(C3P0_REGISTRY_NAME);
            C3P0RegistryManager mbean = new C3P0RegistryManager();
            if (mbs.isRegistered(name)) {
                if (logger.isLoggable(MLevel.WARNING)) {
                    logger.warning("A C3P0Registry mbean is already registered. " + "This probably means that an application using c3p0 was undeployed, "
                            + "but not all PooledDataSources were closed prior to undeployment. "
                            + "This may lead to resource leaks over time. Please take care to close " + "all PooledDataSources.");
                }
                mbs.unregisterMBean(name);
            }
            mbs.registerMBean(mbean, name);
        } catch (Exception e) {
            if (logger.isLoggable(MLevel.WARNING))
                logger.log(MLevel.WARNING, "Failed to set up C3P0RegistryManager mBean. "
                        + "[c3p0 will still function normally, but management via JMX may not be possible.]", e);
        }
    }

    @Override
    public void attemptUnmanageC3P0Registry() {
        try {
            ObjectName name = new ObjectName(C3P0_REGISTRY_NAME);
            if (mbs.isRegistered(name)) {
                mbs.unregisterMBean(name);
                if (logger.isLoggable(MLevel.FINER))
                    logger.log(MLevel.FINER, "C3P0Registry mbean unregistered.");
            } else if (logger.isLoggable(MLevel.FINE))
                logger.fine("The C3P0Registry mbean was not found in the registry, so could not be unregistered.");
        } catch (Exception e) {
            if (logger.isLoggable(MLevel.WARNING))
                logger.log(MLevel.WARNING, "An Exception occurred while trying to unregister the C3P0RegistryManager mBean." + e);
        }
    }

    @Override
    public void attemptManagePooledDataSource(PooledDataSource pds) {
        String name = getPdsObjectNameStr(pds);
        try {
            //PooledDataSourceManager mbean = new PooledDataSourceManager( pds );
            //mbs.registerMBean(mbean, ObjectName.getInstance(name));
            //if (logger.isLoggable(MLevel.FINER))
            //    logger.log(MLevel.FINER, "MBean: " + name + " registered.");

            // DynamicPooledDataSourceManagerMBean registers itself on construction (and logs its own registration)
            DynamicPooledDataSourceManagerMBean mbean = new DynamicPooledDataSourceManagerMBean(pds, name, mbs);
        } catch (Exception e) {
            if (logger.isLoggable(MLevel.WARNING))
                logger.log(MLevel.WARNING, "Failed to set up a PooledDataSourceManager mBean. [" + name + "] "
                        + "[c3p0 will still functioning normally, but management via JMX may not be possible.]", e);
        }
    }

    @Override
    public void attemptUnmanagePooledDataSource(PooledDataSource pds) {
        String nameStr = getPdsObjectNameStr(pds);
        try {
            ObjectName name = new ObjectName(nameStr);
            if (mbs.isRegistered(name)) {
                mbs.unregisterMBean(name);
                if (logger.isLoggable(MLevel.FINER))
                    logger.log(MLevel.FINER, "MBean: " + nameStr + " unregistered.");
            } else if (logger.isLoggable(MLevel.FINE))
                logger.fine("The mbean " + nameStr + " was not found in the registry, so could not be unregistered.");
        } catch (Exception e) {
            if (logger.isLoggable(MLevel.WARNING))
                logger.log(MLevel.WARNING, "An Exception occurred while unregistering mBean. [" + nameStr + "] " + e);
        }
    }

    private String getPdsObjectNameStr(PooledDataSource pds) {
        StringBuilder b = new StringBuilder("com.mchange.v2.c3p0.").append(LoggerConfig.getContextName());
        b.append(":type=PooledDataSource[");
        if (pds.getDataSourceName() != null) {
            b.append(pds.getDataSourceName());
        } else {
            b.append(pds.getIdentityToken());
        }
        b.append("]");
        return b.toString();
    }

}
