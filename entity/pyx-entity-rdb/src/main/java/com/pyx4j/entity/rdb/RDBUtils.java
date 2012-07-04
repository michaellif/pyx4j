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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.rdb.cfg.Configuration.MultitenancyType;
import com.pyx4j.entity.rdb.mapping.TableMetadata;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.impl.EntityClassFinder;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.server.contexts.NamespaceManager;

public class RDBUtils implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(RDBUtils.class);

    private String connectionNamespace;

    private Connection connection = null;

    public RDBUtils() {
    }

    private Connection connection() {
        if (connection == null) {
            connection = ((EntityPersistenceServiceRDB) Persistence.service()).getAministrationConnection();
            if (connectionNamespace != null) {
                setConnectionNamespace(connectionNamespace);
            }
        }
        return connection;
    }

    public void setConnectionNamespace(String connectionNamespace) {
        this.connectionNamespace = connectionNamespace;
        if ((connection != null) && ((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
            String sql = ((EntityPersistenceServiceRDB) Persistence.service()).getDialect().sqlChangeConnectionNamespace(connectionNamespace);
            try {
                if (sql == null) {
                    connection.setCatalog(connectionNamespace);
                } else {
                    SQLUtils.execute(connection, sql);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void close() {
        try {
            // Correction for connection validation infrastructure in C3P0
            setConnectionNamespace(null);
        } catch (Throwable e) {
            log.error("Error reseting connection namespace", e);
        }
        SQLUtils.closeQuietly(connection);
        connection = null;
    }

    public boolean isTableExists(String tableName) throws SQLException {
        return TableMetadata.isTableExists(((EntityPersistenceServiceRDB) Persistence.service()).getDialect(), connection(), connectionNamespace, tableName);
    }

    public void dropTable(String tableName) throws SQLException {
        List<String> sqls = new Vector<String>();
        sqls.add("drop table " + tableName);
        SQLUtils.execute(connection(), sqls);
    }

    public void execute(List<String> sqls) throws SQLException {
        SQLUtils.execute(connection(), sqls);
    }

    public void execute(String sql) throws SQLException {
        SQLUtils.execute(connection(), sql);
    }

    public static void ensureNamespace() {
        RDBUtils utils = new RDBUtils();
        Connection connection = null;
        try {
            if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                connection = utils.connection();
                String storedNamespaceName = NamespaceManager.getNamespace();
                boolean schemaExists = false;
                ResultSet rs = null;
                try {
                    DatabaseMetaData dbMeta = connection.getMetaData();
                    if (dbMeta.storesLowerCaseIdentifiers()) {
                        storedNamespaceName = storedNamespaceName.toLowerCase(Locale.ENGLISH);
                    } else if (dbMeta.storesUpperCaseIdentifiers()) {
                        storedNamespaceName = storedNamespaceName.toUpperCase(Locale.ENGLISH);
                    }
                    rs = dbMeta.getSchemas(null, storedNamespaceName);
                    if (rs.next()) {
                        schemaExists = true;
                    }
                } finally {
                    SQLUtils.closeQuietly(rs);
                }

                if (!schemaExists) {
                    switch (((EntityPersistenceServiceRDB) Persistence.service()).getDatabaseType()) {
                    case PostgreSQL:
                        utils.execute("CREATE SCHEMA " + storedNamespaceName);
                        break;
                    case MySQL:
                        utils.execute("CREATE DATABASE " + storedNamespaceName + "  DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci");
                        break;
                    default:
                        throw new Error("Unsupported dialect");
                    }
                }
            }
        } catch (SQLException e) {
            throw new Error(e);
        } finally {
            utils.close();
        }
    }

    public static void resetDatabase() {
        long start = System.currentTimeMillis();
        RDBUtils utils = new RDBUtils();
        try {
            log.debug("recreate DB/SCHEMAs");
            switch (((EntityPersistenceServiceRDB) Persistence.service()).getDatabaseType()) {
            case PostgreSQL:
                DatabaseMetaData dbMeta = utils.connection().getMetaData();
                ResultSet rs = null;
                try {
                    rs = dbMeta.getSchemas();
                    while (rs.next()) {
                        String schema = rs.getString(1).toLowerCase(Locale.ENGLISH);
                        if (schema.equals("information_schema") || schema.startsWith("pg_") || schema.startsWith("_dba")) {
                            continue;
                        }
                        utils.execute("DROP SCHEMA " + schema + " CASCADE");
                    }
                } finally {
                    SQLUtils.closeQuietly(rs);
                }
                utils.execute("CREATE SCHEMA public");
                break;
            case MySQL:
                String dbName = ((EntityPersistenceServiceRDB) Persistence.service()).getDatabaseName();
                utils.execute("DROP DATABASE " + dbName);
                utils.execute("CREATE DATABASE " + dbName + "  DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci");
                break;
            default:
                throw new Error("Unsupported dialect");
            }
            ((EntityPersistenceServiceRDB) Persistence.service()).resetMapping();
            log.info("Database '{}' recreated in {}", ((EntityPersistenceServiceRDB) Persistence.service()).getDatabaseName(), TimeUtils.secSince(start));
        } catch (SQLException e) {
            throw new Error(e);
        } finally {
            utils.close();
        }
    }

    public static void dropAllEntityTables() {
        long start = System.currentTimeMillis();
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
            if (!EntityPersistenceServiceRDB.allowNamespaceUse(entityClass)) {
                continue;
            }
            srv.count(EntityQueryCriteria.create(entityClass));
            countTotal++;
        }
        log.info("Total of {} tables created in {}", countTotal, TimeUtils.secSince(start));
    }

    public static void initNameSpaceSpecificEntityTables() {
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
            Table table = entityClass.getAnnotation(Table.class);
            if ((table == null) || !NamespaceManager.getNamespace().equals(table.namespace())) {
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
            if (!EntityPersistenceServiceRDB.allowNamespaceUse(entityClass)) {
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
