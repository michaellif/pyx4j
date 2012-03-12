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
package com.pyx4j.entity.rdb;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.rdb.ConnectionProvider.ConnectionTarget;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.dialect.MySQLDialect;
import com.pyx4j.entity.rdb.mapping.TableMetadata;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.impl.EntityClassFinder;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.meta.EntityMeta;

public class RDBUtils implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(RDBUtils.class);

    private final ConnectionProvider connectionProvider;

    public RDBUtils() {
        try {
            connectionProvider = new ConnectionProvider(getRDBConfiguration());
        } catch (SQLException e) {
            log.error("RDB initialization error", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Configuration getRDBConfiguration() {
        IPersistenceConfiguration cfg = ServerSideConfiguration.instance().getPersistenceConfiguration();
        if (cfg == null) {
            throw new RuntimeException("Persistence Configuration is not defined (is null) in class " + ServerSideConfiguration.instance().getClass().getName());
        }
        if (!(cfg instanceof Configuration)) {
            throw new RuntimeException("Invalid RDB configuration class " + cfg);
        }
        return (Configuration) cfg;
    }

    public Connection getConnection() {
        return connectionProvider.getConnection(ConnectionTarget.forUpdate);
    }

    @Override
    public void close() {
        connectionProvider.dispose();
    }

    public boolean isTableExists(String tableName) throws SQLException {
        Connection connection = connectionProvider.getConnection(ConnectionTarget.forRead);
        try {
            return (TableMetadata.getTableMetadata(connection, tableName) != null);
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    public void dropTable(String tableName) throws SQLException {
        Connection connection = connectionProvider.getConnection(ConnectionTarget.forUpdate);
        List<String> sqls = new Vector<String>();
        try {
            sqls.add("drop table " + tableName);
            SQLUtils.execute(connection, sqls);
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    public void execute(List<String> sqls) throws SQLException {
        Connection connection = connectionProvider.getConnection(ConnectionTarget.forUpdate);
        try {
            SQLUtils.execute(connection, sqls);
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    public void execute(String sql) throws SQLException {
        Connection connection = connectionProvider.getConnection(ConnectionTarget.forUpdate);
        try {
            SQLUtils.execute(connection, sql);
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    public static void dropAllEntityTables() {
        long start = System.currentTimeMillis();
        RDBUtils utils = new RDBUtils();
        try {
            if (utils.connectionProvider.getDialect() instanceof MySQLDialect) {
                Configuration cfg = getRDBConfiguration();
                try {
                    utils.execute("DROP DATABASE " + cfg.dbName());
                    utils.execute("CREATE DATABASE " + cfg.dbName() + "  DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci");
                } catch (SQLException e) {
                    throw new Error(e);
                }
                log.info("Database '{}' recreated in {}", cfg.dbName(), TimeUtils.secSince(start));
                return;
            }
        } finally {
            utils.close();
        }

        EntityPersistenceServiceRDB srv = (EntityPersistenceServiceRDB) Persistence.service();
        List<String> allClasses = EntityClassFinder.getEntityClassesNames();
        int countTotal = 0;
        for (String className : allClasses) {
            Class<? extends IEntity> entityClass = ServerEntityFactory.entityClass(className);
            EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
            if (meta.isTransient() || (entityClass.getAnnotation(AbstractEntity.class) != null) || (entityClass.getAnnotation(AbstractEntity.class) != null)) {
                continue;
            }
            if (srv.isTableExists(meta.getEntityClass())) {
                log.info("drop table {}", meta.getEntityClass().getName());
                srv.dropTable(meta.getEntityClass());
                countTotal++;
            }
        }
        log.info("Total of {} tables dropped in {}", countTotal, TimeUtils.secSince(start));
    }

    public static void dropAllForeignKeys() {
        EntityPersistenceServiceRDB srv = (EntityPersistenceServiceRDB) Persistence.service();
        List<String> allClasses = EntityClassFinder.getEntityClassesNames();
        int countTotal = 0;
        long start = System.currentTimeMillis();
        for (String className : allClasses) {
            Class<? extends IEntity> entityClass = ServerEntityFactory.entityClass(className);
            EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
            if (meta.isTransient() || (entityClass.getAnnotation(AbstractEntity.class) != null) || (entityClass.getAnnotation(AbstractEntity.class) != null)) {
                continue;
            }
            if (srv.isTableExists(meta.getEntityClass())) {
                log.info("drop ForeignKeys in {}", meta.getEntityClass().getName());
                countTotal += srv.dropForeignKeys(meta.getEntityClass());
            }
        }
        log.info("Total of {} ForeignKeys dropped in {}", countTotal, TimeUtils.secSince(start));
    }

    public static void initAllEntityTables() {
        int countTotal = 0;
        long start = System.currentTimeMillis();
        EntityPersistenceServiceRDB srv = (EntityPersistenceServiceRDB) Persistence.service();
        List<String> allClasses = EntityClassFinder.getEntityClassesNames();
        for (String className : allClasses) {
            if (className.toLowerCase().contains(".gae")) {
                continue;
            }
            Class<? extends IEntity> entityClass = ServerEntityFactory.entityClass(className);
            EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
            if (meta.isTransient() || entityClass.getAnnotation(AbstractEntity.class) != null || entityClass.getAnnotation(EmbeddedEntity.class) != null) {
                continue;
            }
            srv.count(EntityQueryCriteria.create(entityClass));
            countTotal++;
        }
        log.info("Total of {} tables created in {}", countTotal, TimeUtils.secSince(start));
    }

    public static void deleteFromAllEntityTables() {
        EntityPersistenceServiceRDB srv = (EntityPersistenceServiceRDB) Persistence.service();
        List<String> allClasses = EntityClassFinder.getEntityClassesNames();
        for (String className : allClasses) {
            Class<? extends IEntity> entityClass = ServerEntityFactory.entityClass(className);
            EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
            if (meta.isTransient() || entityClass.getAnnotation(AbstractEntity.class) != null) {
                continue;
            }
            if (srv.isTableExists(meta.getEntityClass())) {
                @SuppressWarnings({ "rawtypes", "unchecked" })
                EntityQueryCriteria<?> criteria = new EntityQueryCriteria(entityClass);
                List<Key> keys = srv.queryKeys(criteria);
                if (keys.size() > 0) {
                    log.info("delete {} rows from table {}", keys.size(), meta.getEntityClass().getName());
                    srv.delete(entityClass, keys);
                }
            }
        }
    }
}
