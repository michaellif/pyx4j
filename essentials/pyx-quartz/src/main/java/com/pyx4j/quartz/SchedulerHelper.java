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
 * Created on 2011-04-18
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.quartz;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.jdbcjobstore.Constants;
import org.quartz.impl.jdbcjobstore.HSQLDBDelegate;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.impl.jdbcjobstore.StdJDBCDelegate;
import org.quartz.impl.jdbcjobstore.oracle.OracleDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.gwt.server.IOUtils;

public class SchedulerHelper {

    private static final Logger log = LoggerFactory.getLogger(SchedulerHelper.class);

    //SimplePropertiesTriggerPersistenceDelegateSupport.TABLE_SIMPLE_PROPERTIES_TRIGGERS
    protected static final String TABLE_SIMPLE_PROPERTIES_TRIGGERS = "SIMPROP_TRIGGERS";

    private static SchedulerHelper instance;

    private final SchedulerFactory schedulerFactory;

    private final Scheduler scheduler;

    SchedulerHelper() {
        Properties quartzProperties = new Properties();

        InputStream propIn = null;
        try {
            propIn = Thread.currentThread().getContextClassLoader().getResourceAsStream("quartz.properties");
            if (propIn != null) {
                quartzProperties.load(propIn);
            }
        } catch (IOException e) {
            log.error("quartz properties load error", e);
            throw new Error("quartz initialization error");
        } finally {
            IOUtils.closeQuietly(propIn);
        }

        // TODO other default quartz configurations.

        if (ServerSideConfiguration.instance() instanceof ServerSideConfigurationWithQuartz) {
            QuartzConfiguration quartzConfiguration = ((ServerSideConfigurationWithQuartz) ServerSideConfiguration.instance()).quartzConfiguration();
            //TODO our configuration here
        }

        quartzProperties.put(StdSchedulerFactory.PROP_JOB_STORE_CLASS, JobStoreTX.class.getName());

        String delegateProperty = StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".driverDelegateClass";
        Configuration rdbConfiguration = RDBUtils.getRDBConfiguration();
        switch (rdbConfiguration.databaseType()) {
        case HSQLDB:
            quartzProperties.put(delegateProperty, HSQLDBDelegate.class.getName());
            break;
        case MySQL:
            quartzProperties.put(delegateProperty, StdJDBCDelegate.class.getName());
            break;
        case Oracle:
            quartzProperties.put(delegateProperty, OracleDelegate.class.getName());
            break;
        default:
            throw new Error("Unsupporte ddatabaseType " + rdbConfiguration.databaseType());
        }

        // Lets quartz manage its connections pool for now.
        String dataSourceName = "main";
        quartzProperties.put(StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".dataSource", dataSourceName);
        String dsConfigPrefix = StdSchedulerFactory.PROP_DATASOURCE_PREFIX + "." + dataSourceName + ".";
        quartzProperties.put(dsConfigPrefix + StdSchedulerFactory.PROP_DATASOURCE_DRIVER, rdbConfiguration.driverClass());
        quartzProperties.put(dsConfigPrefix + StdSchedulerFactory.PROP_DATASOURCE_URL, rdbConfiguration.connectionUrl());
        quartzProperties.put(dsConfigPrefix + StdSchedulerFactory.PROP_DATASOURCE_USER, rdbConfiguration.userName());
        quartzProperties.put(dsConfigPrefix + StdSchedulerFactory.PROP_DATASOURCE_PASSWORD, rdbConfiguration.password());
        quartzProperties.put(dsConfigPrefix + StdSchedulerFactory.PROP_DATASOURCE_MAX_CONNECTIONS, "4");

        try {
            schedulerFactory = new StdSchedulerFactory(quartzProperties);

            scheduler = schedulerFactory.getScheduler();
            scheduler.start();

        } catch (SchedulerException e) {
            log.error("quartz initialization error", e);
            throw new Error("quartz initialization error", e);
        }

    }

    public static synchronized void init() {
        if (ServerSideConfiguration.instance().isDevelopmentBehavior()) {
            createQuartzTables();
        }

        instance = new SchedulerHelper();
    }

    public static synchronized void shutdown() {
        if (instance != null) {
            // TODO verify all closed and stopped when run in tomcat...
            try {
                if (instance.scheduler != null) {
                    instance.scheduler.shutdown();
                }
            } catch (SchedulerException e) {
                log.error("error", e);
            } finally {
                instance = null;
            }
        }
    }

    private static void createQuartzTables() {
        RDBUtils rdb = null;
        try {
            rdb = new RDBUtils();
            boolean allPresent = true;
            for (String tableName : quartzTables()) {
                if (!rdb.isTableExists(tableName)) {
                    allPresent = false;
                    break;
                }
            }
            if (allPresent) {
                log.debug("All quartz tables are present");
            } else {
                String sqlResourceName;
                switch (RDBUtils.getRDBConfiguration().databaseType()) {
                case HSQLDB:
                    sqlResourceName = "tables_hsqldb.sql";
                    break;
                case MySQL:
                    sqlResourceName = "tables_mysql.sql";
                    break;
                case Oracle:
                    sqlResourceName = "tables_oracle.sql";
                    break;
                default:
                    throw new Error("Unsupporte ddatabaseType " + RDBUtils.getRDBConfiguration().databaseType());
                }

                String text = IOUtils.getTextResource(SchedulerHelper.class.getPackage().getName().replace('.', '/') + "/" + sqlResourceName);
                List<String> sqls = new Vector<String>();
                // split the text to SQL statements, Use ; separator
                List<String> lines = Arrays.asList(text.split(";"));
                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        sqls.add(line);
                    }
                }
                rdb.execute(sqls);
            }
        } catch (SQLException e) {
            throw new Error("quartz tables creation error", e);
        } catch (IOException e) {
            throw new Error("quartz tables creation error", e);
        } finally {
            IOUtils.closeQuietly(rdb);
        }
    }

    public static void dbReset() {
        RDBUtils rdb = null;
        try {
            rdb = new RDBUtils();
            for (String tableName : quartzTables()) {
                if (rdb.isTableExists(tableName)) {
                    log.info("drop table {}", tableName);
                    rdb.dropTable(tableName);
                }
            }
        } catch (SQLException e) {
            throw new Error("quartz tables reset error", e);
        } finally {
            IOUtils.closeQuietly(rdb);
        }
    }

    private static List<String> quartzTables() {
        List<String> v = new ArrayList<String>();

        String prefix = Constants.DEFAULT_TABLE_PREFIX;
        v.add(prefix + Constants.TABLE_FIRED_TRIGGERS);
        v.add(prefix + Constants.TABLE_PAUSED_TRIGGERS);
        v.add(prefix + Constants.TABLE_SCHEDULER_STATE);
        v.add(prefix + Constants.TABLE_LOCKS);
        v.add(prefix + Constants.TABLE_SIMPLE_TRIGGERS);
        v.add(prefix + TABLE_SIMPLE_PROPERTIES_TRIGGERS);
        v.add(prefix + Constants.TABLE_CRON_TRIGGERS);
        v.add(prefix + Constants.TABLE_BLOB_TRIGGERS);
        v.add(prefix + Constants.TABLE_TRIGGERS);
        v.add(prefix + Constants.TABLE_JOB_DETAILS);
        v.add(prefix + Constants.TABLE_CALENDARS);

        return v;
    }

    public static Scheduler getScheduler() {
        return instance.scheduler;
    }

}
