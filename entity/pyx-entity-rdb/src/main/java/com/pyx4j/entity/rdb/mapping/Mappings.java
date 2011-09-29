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
 * Created on 2010-07-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.Trace;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.rdb.ConnectionProvider;
import com.pyx4j.entity.rdb.ConnectionProvider.ConnectionTarget;
import com.pyx4j.entity.rdb.SQLUtils;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;

public class Mappings {

    private static final Logger log = LoggerFactory.getLogger(Mappings.class);

    private final ConnectionProvider connectionProvider;

    private final Map<Class<? extends IEntity>, TableModel> tables = new Hashtable<Class<? extends IEntity>, TableModel>();

    private final Set<String> usedTableNames = new HashSet<String>();

    private final Set<String> sequences;

    private static final Map<Class<? extends IEntity>, Object> entityLocks = new Hashtable<Class<? extends IEntity>, Object>();

    public static final boolean traceInit = false;

    public Mappings(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;

        if (connectionProvider.getDialect().isSequencesBaseIdentity()) {
            sequences = new HashSet<String>();
            initSequences();
        } else {
            sequences = null;
        }
    }

    //TODO fix connection
    public TableModel getTableModel(Class<? extends IEntity> entityClass) {
        return ensureTable(null, connectionProvider.getDialect(), EntityFactory.getEntityMeta(entityClass));
    }

    public TableModel ensureTable(Connection connection, Dialect dialect, EntityMeta entityMeta) {
        if (entityMeta.isTransient()) {
            throw new Error("Can't operate on Transient Entity " + entityMeta.getEntityClass().getName());
        }
        TableModel model = tables.get(entityMeta.getEntityClass());
        if (model != null) {
            return model;
        }

        // Avoid lock on EntityClass
        Object entityTypeLock;
        synchronized (entityLocks) {
            entityTypeLock = entityLocks.get(entityMeta.getEntityClass());
            if (entityTypeLock == null) {
                entityTypeLock = new Object();
                entityLocks.put(entityMeta.getEntityClass(), entityTypeLock);
            }
        }

        if (traceInit) {
            log.trace(Trace.enter() + "ensureTable {} lock {}", entityMeta.getPersistenceName(), System.identityHashCode(entityTypeLock));
        }

        synchronized (entityTypeLock) {
            if (traceInit) {
                log.trace(Trace.id() + "ensureTable {} obtained lock {}", entityMeta.getPersistenceName(), System.identityHashCode(entityTypeLock));
            }

            // Got the lock, see if model already created
            model = tables.get(entityMeta.getEntityClass());
            if (model == null) {
                model = new TableModel(dialect, this, entityMeta);
                if (usedTableNames.contains(model.getTableName().toLowerCase(Locale.ENGLISH))) {
                    log.warn("redefining/extending table {} for class {}", model.getTableName(), entityMeta.getEntityClass());
                }
                for (MemberOperationsMeta member : model.operationsMeta().getCollectionMembers()) {
                    if (usedTableNames.contains(member.sqlName().toLowerCase(Locale.ENGLISH))) {
                        log.warn("redefining/extending table {} for member {}", member.sqlName(), member.getMemberPath());
                    }
                }
                try {
                    model.ensureExists(connection, dialect);
                } catch (SQLException e) {
                    log.error("table creation error", e);
                    throw new RuntimeException(e);
                }
                if (dialect.isSequencesBaseIdentity()) {
                    if (model.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.AUTO) {
                        ensureSequence(dialect, dialect.getNamingConvention().sqlTableSequenceName(entityMeta.getPersistenceName()));
                    }
                    for (MemberOperationsMeta member : model.operationsMeta().getCollectionMembers()) {
                        ensureSequence(dialect, member.getSqlSequenceName());
                    }
                }
                tables.put(entityMeta.getEntityClass(), model);
                usedTableNames.add(model.getTableName().toLowerCase(Locale.ENGLISH));
                for (MemberOperationsMeta member : model.operationsMeta().getCollectionMembers()) {
                    usedTableNames.add(member.sqlName().toLowerCase(Locale.ENGLISH));
                }
            } else if (traceInit) {
                log.trace(Trace.id() + "ensureTable {} TableModel alredy created", entityMeta.getPersistenceName());
            }
        }

        if (traceInit) {
            log.trace(Trace.returns() + "ensureTable {}", entityMeta.getPersistenceName());
        }

        return model;
    }

    private void ensureSequence(Dialect dialect, String sequenceName) {
        // Verify Sequence already exists
        if (!sequences.contains(sequenceName.toLowerCase(Locale.ENGLISH))) {
            Connection connection = connectionProvider.getConnection(ConnectionTarget.forUpdate);
            try {
                SQLUtils.execute(connection, dialect.getCreateSequenceSql(sequenceName));
            } catch (SQLException e) {
                log.error("sequence creation error", e);
                throw new RuntimeException(e);
            } finally {
                SQLUtils.closeQuietly(connection);
            }
            sequences.add(sequenceName.toLowerCase(Locale.ENGLISH));
        }
    }

    public void droppedTable(Dialect dialect, EntityMeta entityMeta) {
        TableModel model = tables.get(entityMeta.getEntityClass());
        if (model == null) {
            model = new TableModel(dialect, this, entityMeta);
        }
        usedTableNames.remove(model.getTableName().toLowerCase(Locale.ENGLISH));
        tables.remove(entityMeta.getEntityClass());

        if (dialect.isSequencesBaseIdentity()) {
            if (model.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.AUTO) {
                droppedSequence(dialect, dialect.getNamingConvention().sqlTableSequenceName(entityMeta.getPersistenceName()));
            }
            for (MemberOperationsMeta member : model.operationsMeta().getCollectionMembers()) {
                droppedSequence(dialect, member.getSqlSequenceName());
            }
        }
    }

    private void droppedSequence(Dialect dialect, String sequenceName) {
        if (sequences.contains(sequenceName.toLowerCase(Locale.ENGLISH))) {
            Connection connection = connectionProvider.getConnection(ConnectionTarget.forUpdate);
            try {
                SQLUtils.execute(connection, dialect.getDropSequenceSql(sequenceName));
            } catch (SQLException e) {
                log.error("sequence deletion error", e);
                throw new RuntimeException(e);
            } finally {
                SQLUtils.closeQuietly(connection);
            }
            sequences.remove(sequenceName.toLowerCase(Locale.ENGLISH));
        }
    }

    private void initSequences() {
        Connection connection = connectionProvider.getConnection(ConnectionTarget.forRead);
        Statement stmt = null;
        ResultSet rs = null;
        try {
            connectionProvider.getDialect().sqlSequenceMetaData();
            stmt = connection.createStatement();
            rs = stmt.executeQuery(connectionProvider.getDialect().sqlSequenceMetaData());
            while (rs.next()) {
                sequences.add(rs.getString(1).trim().toLowerCase(Locale.ENGLISH));
            }
        } catch (SQLException e) {
            log.error("query sequences metadata error", e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(connection);
        }
    }

}
