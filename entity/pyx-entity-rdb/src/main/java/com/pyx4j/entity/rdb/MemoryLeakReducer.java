/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on May 23, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.reflect.Field;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.management.MBeanServer;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryLeakReducer {

    private static final Logger log = LoggerFactory.getLogger(MemoryLeakReducer.class);

    public static void deregisterDrivers() {
        // Unregister drivers during shutdown.
        // Fix Memory leak that the singleton java.sql.DriverManager can result in.
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        List<Driver> drvCopy = new Vector<Driver>();
        while (drivers.hasMoreElements()) {
            drvCopy.add(drivers.nextElement());
        }
        int count = 0;
        for (Driver d : drvCopy) {
            if (d.getClass().getClassLoader() != ConnectionProvider.class.getClassLoader()) {
                log.debug("do not deregister {}", d.getClass().getName());
                continue;
            }
            try {
                DriverManager.deregisterDriver(d);
                log.info("deregistered driver {}", d.getClass().getName());
                if ("oracle.jdbc.OracleDriver".equals(d.getClass().getName())) {
                    deregisterOracleDiagnosabilityMBean();
                    removeOracledNotificationListeners();
                    stopOracledThreads();
                }
                count++;
            } catch (Throwable e) {
                log.error("deregister error", e);
            }
        }
        if (count == 0) {
            log.warn("filed to find jdbc driver to deregister");
        }
    }

    private static void deregisterOracleDiagnosabilityMBean() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            Hashtable<String, String> keys = new Hashtable<String, String>();
            keys.put("type", "diagnosability");
            keys.put("name", cl.getClass().getName() + "@" + Integer.toHexString(cl.hashCode()).toLowerCase());
            mbs.unregisterMBean(new ObjectName("com.oracle.jdbc", keys));
            log.info("deregistered OracleDiagnosabilityMBean");
        } catch (javax.management.InstanceNotFoundException nf) {
            log.debug("Oracle OracleDiagnosabilityMBean not found ", nf);
        } catch (Throwable e) {
            log.error("Oracle JMX unregister error", e);
        }
    }

    private static void removeOracledNotificationListeners() {
        try {
            Class<?> type = Class.forName("oracle.jdbc.driver.BlockSource$ThreadedCachingBlockSource$BlockReleaserListener");
            Field field = type.getDeclaredField("SOLE_INSTANCE");
            field.setAccessible(true);
            NotificationListener listener = (NotificationListener) field.get(null);
            if (listener != null) {
                MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
                ((NotificationEmitter) mbean).removeNotificationListener(listener, null, null);
                log.info("removed OracleNotificationListeners");
            }
        } catch (Throwable e) {
            log.error("Oracle NotificationListeners removal error", e);
        }
    }

    @SuppressWarnings("deprecation")
    private static void stopOracledThreads() {
        try {
            Class<?> type = Class.forName("oracle.jdbc.driver.BlockSource$ThreadedCachingBlockSource");
            Field field = type.getDeclaredField("RELEASER");
            field.setAccessible(true);
            Thread thread = (Thread) field.get(null);
            if (thread != null) {
                thread.interrupt();
                thread.stop();
                log.info("stopped RELEASER OracleThread");
            }
        } catch (Throwable e) {
            log.error("Oracle RELEASER Thread stop error", e);
        }
    }

}
