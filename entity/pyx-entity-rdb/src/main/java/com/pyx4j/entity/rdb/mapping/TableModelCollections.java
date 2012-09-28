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
 * Created on Jan 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.Trace;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.SQLUtils;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.dialect.HSQLDialect;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.server.contexts.NamespaceManager;

public class TableModelCollections {

    private static final Logger log = LoggerFactory.getLogger(TableModelCollections.class);

    public static void validate(IEntity entity, MemberOperationsMeta member) {
        int maxLength = member.getMemberMeta().getLength();
        if (maxLength <= 0) {
            return;
        }
        @SuppressWarnings("rawtypes")
        Collection c = (Collection) member.getMemberValue(entity);
        if ((c != null) && (c.size() > maxLength)) {
            throw new RuntimeException("Member size violation in entity '" + entity.getEntityMeta().getCaption() + "' member '" + member.getMemberName()
                    + "' size " + c.size() + " is greater than max allowed " + maxLength);
        }
    }

    @SuppressWarnings("unchecked")
    public static void insert(PersistenceContext persistenceContext, IEntity entity, MemberCollectionOperationsMeta member) {
        if (!member.isAutogenerated() && !member.getMemberMeta().isCascadePersist()) {
            // Never update
            return;
        }
        Collection<Object> collectionMember = (Collection<Object>) member.getMember(entity);
        if (((IObject<?>) collectionMember).getAttachLevel() == AttachLevel.Detached) {
            return;
        }
        if ((collectionMember == null) || collectionMember.isEmpty()) {
            return;
        } else {
            insert(persistenceContext, entity, member, (Collection<Object>) member.getMember(entity), null);
        }
    }

    private static void insert(PersistenceContext persistenceContext, IEntity entity, MemberCollectionOperationsMeta member, Collection<Object> dataSet,
            List<Object> dataPositions) {
        PreparedStatement stmt = null;

        ObjectClassType type = member.getMemberMeta().getObjectClassType();
        boolean isList = (type == ObjectClassType.EntityList);

        StringBuilder sql = new StringBuilder();
        Dialect dialect = persistenceContext.getDialect();
        try {
            int numberOfParams = 0;
            sql.append("INSERT INTO ");
            if (dialect.isMultitenantSeparateSchemas()) {
                sql.append(NamespaceManager.getNamespace()).append('.');
            }
            sql.append(member.sqlName()).append(" ( ");

            for (String name : member.getOwnerValueAdapter().getColumnNames(member.sqlOwnerName())) {
                if (numberOfParams > 0) {
                    sql.append(", ");
                }
                sql.append(name);
                numberOfParams++;
            }

            for (String name : member.getValueAdapter().getColumnNames(member.sqlValueName())) {
                sql.append(", ");
                sql.append(name);
                numberOfParams++;
            }

            if (isList) {
                sql.append(", ").append(member.sqlOrderColumnName());
                numberOfParams++;
            }
            if (dialect.isMultitenantSharedSchema()) {
                sql.append(", ").append(dialect.getNamingConvention().sqlNameSpaceColumnName());
                numberOfParams++;
            }
            if (dialect.isSequencesBaseIdentity()) {
                sql.append(", ").append(dialect.getNamingConvention().sqlIdColumnName());
            }

            sql.append(" ) VALUES (");
            for (int i = 0; i < numberOfParams; i++) {
                if (i != 0) {
                    sql.append(", ");
                }
                sql.append("?");
            }

            if (dialect.isSequencesBaseIdentity()) {
                sql.append(", ").append(dialect.getSequenceNextValSql(member.getSqlSequenceName()));
            }

            sql.append(")");
            if (EntityPersistenceServiceRDB.traceSql) {
                log.debug(Trace.id() + " {} ", sql);
            }
            stmt = persistenceContext.getConnection().prepareStatement(sql.toString());
            int seq = 0;
            for (Object value : dataSet) {
                if ((type == ObjectClassType.EntityList) || (type == ObjectClassType.EntitySet)) {
                    IEntity childEntity = (IEntity) value;
                    if ((childEntity.getPrimaryKey() == null) && !childEntity.isNull()) {
                        log.error("Saving non persisted reference {}\n{}\n", childEntity, Trace.getCallOrigin(EntityPersistenceServiceRDB.class));
                        throw new Error("Saving non persisted reference " + childEntity.getDebugExceptionInfoString());
                    }
                }
                int parameterIndex = 1;
                parameterIndex += member.getOwnerValueAdapter().bindValue(persistenceContext, stmt, parameterIndex, entity);
                parameterIndex += member.getValueAdapter().bindValue(persistenceContext, stmt, parameterIndex, value);

                if (isList) {
                    if (dataPositions != null) {
                        seq = dataPositions.indexOf(value);
                    }
                    stmt.setInt(parameterIndex, seq);
                    parameterIndex++;
                }
                if (dialect.isMultitenantSharedSchema()) {
                    stmt.setString(parameterIndex, NamespaceManager.getNamespace());
                    parameterIndex++;
                }
                if (EntityPersistenceServiceRDB.trace) {
                    log.info(Trace.id() + "insert {} (" + entity.getPrimaryKey() + ", " + value + ", " + seq + ")", member.sqlName());
                }
                persistenceContext.setUncommittedChanges();
                stmt.executeUpdate();
                seq++;
            }
        } catch (SQLException e) {
            log.error("{} SQL {}", member.sqlName(), sql);
            log.error("{} SQL insert error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

    @SuppressWarnings("unchecked")
    public static void update(PersistenceContext persistenceContext, IEntity entity, MemberCollectionOperationsMeta member, List<IEntity> cascadeRemove) {
        if (!member.isAutogenerated() && !member.getMemberMeta().isCascadePersist()) {
            // Never update
            return;
        }
        Collection<Object> collectionMember = (Collection<Object>) member.getMember(entity);
        if (((IObject<?>) collectionMember).getAttachLevel() == AttachLevel.Detached) {
            return;
        }
        ObjectClassType type = member.getMemberMeta().getObjectClassType();
        boolean isList = (type == ObjectClassType.EntityList);

        List<Object> allData = new Vector<Object>();
        if (type == ObjectClassType.PrimitiveSet) {
            allData.addAll(collectionMember);
        } else {
            for (Object ent : collectionMember) {
                allData.add(((IEntity) ent).cast());
            }
        }

        List<Object> insertData = new Vector<Object>();
        insertData.addAll(allData);

        Dialect dialect = persistenceContext.getDialect();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("SELECT ");
            boolean first = true;
            if (!member.sqlValueName().equals(dialect.getNamingConvention().sqlIdColumnName())) {
                sql.append(dialect.getNamingConvention().sqlIdColumnName());
                first = false;
            }
            for (String name : member.getValueAdapter().getColumnNames(member.sqlValueName())) {
                if (first) {
                    first = false;
                } else {
                    sql.append(", ");
                }
                sql.append(name);
            }
            if (dialect.isMultitenantSharedSchema()) {
                sql.append(", ").append(dialect.getNamingConvention().sqlNameSpaceColumnName());
            }
            if (isList) {
                sql.append(", ").append(member.sqlOrderColumnName());
            }
            sql.append(" FROM ");
            if (dialect.isMultitenantSeparateSchemas()) {
                sql.append(NamespaceManager.getNamespace()).append('.');
            }
            sql.append(member.sqlName()).append(" WHERE ");

            boolean firstWhereColumn = true;
            for (String name : member.getOwnerValueAdapter().getColumnNames(member.sqlOwnerName())) {
                if (firstWhereColumn) {
                    firstWhereColumn = false;
                } else {
                    sql.append(" AND ");
                }
                sql.append(name).append(" = ?");
            }
            if (member.hasChildJoinContition()) {
                sql.append(" AND ").append(member.getSqlChildJoinContition());
            }

            if (dialect.isMultitenantSharedSchema()) {
                sql.append(" AND ").append(dialect.getNamingConvention().sqlNameSpaceColumnName()).append(" = ?");
            }
            if (EntityPersistenceServiceRDB.traceSql) {
                log.debug(Trace.id() + " {}", sql);
            }
            stmt = persistenceContext.getConnection().prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

            int parameterIndex = 1;
            parameterIndex += member.getOwnerValueAdapter().bindValue(persistenceContext, stmt, parameterIndex, entity);

            if (dialect.isMultitenantSharedSchema()) {
                stmt.setString(parameterIndex, NamespaceManager.getNamespace());
            }
            rs = stmt.executeQuery();
            while (rs.next()) {
                if ((dialect.isMultitenantSharedSchema())
                        && !rs.getString(dialect.getNamingConvention().sqlNameSpaceColumnName()).equals(NamespaceManager.getNamespace())) {
                    throw new RuntimeException("namespace access error");
                }
                Object value = member.getValueAdapter().retrieveValue(rs, member.sqlValueName());
                int valueIdx = allData.indexOf(value);
                if (valueIdx != -1) {
                    insertData.remove(value);
                    if (isList) {
                        if (valueIdx != rs.getInt(member.sqlOrderColumnName())) {
                            if (EntityPersistenceServiceRDB.trace) {
                                log.info(Trace.id() + "update {} (" + entity.getPrimaryKey() + ", " + value + ", " + rs.getInt(member.sqlOrderColumnName())
                                        + "->" + valueIdx + ")", member.sqlName());
                            }
                            persistenceContext.setUncommittedChanges();
                            rs.updateInt(member.sqlOrderColumnName(), valueIdx);
                            rs.updateRow();
                        }
                    }
                } else {
                    persistenceContext.setUncommittedChanges();
                    rs.deleteRow();
                    if ((value instanceof IEntity) && (member.getMemberMeta().isOwnedRelationships()) && (!member.isJoinTableSameAsTarget())) {
                        if (EntityPersistenceServiceRDB.trace) {
                            log.info(Trace.id() + "add cascadeRemove " + ((IEntity) value).getDebugExceptionInfoString());
                        }
                        cascadeRemove.add((IEntity) value);
                    }
                }
            }
            if (EntityPersistenceServiceRDB.traceWarnings) {
                SQLUtils.logAndClearWarnings(persistenceContext.getConnection());
            } else if (dialect instanceof HSQLDialect) {
                // https://sourceforge.net/tracker/?func=detail&aid=3490661&group_id=23316&atid=378131
                persistenceContext.getConnection().clearWarnings();
            }
        } catch (SQLException e) {
            log.error("{} Cursor SQL: {}", member.sqlName(), sql);
            log.error("{} SQL update error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }

        if (!insertData.isEmpty()) {
            insert(persistenceContext, entity, member, insertData, allData);
        }
    }

    static void retrieve(PersistenceContext persistenceContext, IEntity entity, MemberCollectionOperationsMeta member) {
        Dialect dialect = persistenceContext.getDialect();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ObjectClassType type = member.getMemberMeta().getObjectClassType();
        boolean isList = (type == ObjectClassType.EntityList);
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("SELECT ");
            boolean firstColumn = true;
            for (String name : member.getValueAdapter().getColumnNames(member.sqlValueName())) {
                if (firstColumn) {
                    firstColumn = false;
                } else {
                    sql.append(", ");
                }
                sql.append(name);
            }
            sql.append(" FROM ");
            if (dialect.isMultitenantSeparateSchemas()) {
                sql.append(NamespaceManager.getNamespace()).append('.');
            }
            sql.append(member.sqlName()).append(" WHERE ");

            boolean firstWhereColumn = true;
            for (String name : member.getOwnerValueAdapter().getColumnNames(member.sqlOwnerName())) {
                if (firstWhereColumn) {
                    firstWhereColumn = false;
                } else {
                    sql.append(" AND ");
                }
                sql.append(name).append(" = ?");
            }
            if (member.hasChildJoinContition()) {
                sql.append(" AND ").append(member.getSqlChildJoinContition());
            }

            if (dialect.isMultitenantSharedSchema()) {
                sql.append(" AND ").append(dialect.getNamingConvention().sqlNameSpaceColumnName()).append(" = ?");
            }
            if (isList) {
                sql.append(" ORDER BY ").append(member.sqlOrderColumnName());
            }
            if (EntityPersistenceServiceRDB.traceSql) {
                log.debug(Trace.id() + " {} ", sql);
            }
            stmt = persistenceContext.getConnection().prepareStatement(sql.toString());
            // zero means there is no limit, Need for pooled connections 
            stmt.setMaxRows(0);
            int parameterIndex = 1;
            parameterIndex += member.getOwnerValueAdapter().bindValue(persistenceContext, stmt, parameterIndex, entity);

            if (dialect.isMultitenantSharedSchema()) {
                stmt.setString(parameterIndex, NamespaceManager.getNamespace());
                parameterIndex++;
            }
            rs = stmt.executeQuery();

            @SuppressWarnings("unchecked")
            Collection<Object> collectionMember = (Collection<Object>) member.getMember(entity);
            if (!collectionMember.isEmpty()) {
                log.warn("retrieving to not empty collection {}\n called from {}", member.getMemberPath(),
                        Trace.getCallOrigin(EntityPersistenceServiceRDB.class));
            }
            while (rs.next()) {
                Object value = member.getValueAdapter().retrieveValue(rs, member.sqlValueName());
                if ((value == null) && (type != ObjectClassType.PrimitiveSet)) {
                    log.error("Entity {} is null", member.getMember(entity).getPath());
                    throw new Error("Collection data retrival error " + member.getMember(entity).getPath());
                }
                collectionMember.add(value);
            }

        } catch (SQLException e) {
            log.error("{} SQL {}", member.sqlName(), sql);
            log.error("{} SQL select error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }
    }

    static <T extends IEntity> void retrieve(PersistenceContext persistenceContext, Map<Long, T> entities, MemberCollectionOperationsMeta member) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ObjectClassType type = member.getMemberMeta().getObjectClassType();
        boolean isList = (type == ObjectClassType.EntityList);
        StringBuilder sql = new StringBuilder();
        Dialect dialect = persistenceContext.getDialect();
        try {
            sql.append("SELECT owner");
            for (String name : member.getValueAdapter().getColumnNames(member.sqlValueName())) {
                sql.append(", ");
                sql.append(name);
            }
            sql.append(" FROM ");
            if (dialect.isMultitenantSeparateSchemas()) {
                sql.append(NamespaceManager.getNamespace()).append('.');
            }
            sql.append(member.sqlName()).append(" WHERE ");

            if (dialect.isMultitenantSharedSchema()) {
                sql.append(" ns = ? AND ");
            }
            //TODO use  member.getOwnerValueAdapter().
            sql.append(' ').append(member.sqlOwnerName()).append(" = IN (");
            for (int i = 0; i < entities.keySet().size(); i++) {
                if (i != 0) {
                    sql.append(',');
                }
                sql.append(" ? ");
            }
            sql.append(')');
            sql.append(" ORDER BY ").append(member.sqlOwnerName());
            if (isList) {
                sql.append(", ").append(member.sqlOrderColumnName());
            }
            if (EntityPersistenceServiceRDB.traceSql) {
                log.debug(Trace.id() + " {} ", sql);
            }
            stmt = persistenceContext.getConnection().prepareStatement(sql.toString());
            // zero means there is no limit, Need for pooled connections 
            stmt.setMaxRows(0);
            int parameterIndex = 1;
            if (dialect.isMultitenantSharedSchema()) {
                stmt.setString(parameterIndex, NamespaceManager.getNamespace());
                parameterIndex++;
            }
            for (Long pk : entities.keySet()) {
                stmt.setLong(parameterIndex, pk);
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                long currKey = rs.getLong(member.sqlOwnerName());
                T entity = entities.get(currKey);
                @SuppressWarnings("unchecked")
                Collection<Object> collectionMember = (Collection<Object>) member.getMember(entity);
                Object value = member.getValueAdapter().retrieveValue(rs, member.sqlValueName());
                collectionMember.add(value);
            }

        } catch (SQLException e) {
            log.error("{} SQL {}", member.sqlName(), sql);
            log.error("{} SQL select error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }
    }

    public static void delete(PersistenceContext persistenceContext, Key primaryKey, MemberCollectionOperationsMeta member) {
        PreparedStatement stmt = null;
        StringBuilder sql = new StringBuilder();
        Dialect dialect = persistenceContext.getDialect();
        try {
            if (member.getOwnerValueAdapter() instanceof ValueAdapterEntityPolymorphic) {
                throw new Error("TODO delete by Polymorphic Owner");
            }
            sql.append("DELETE FROM ");
            if (dialect.isMultitenantSeparateSchemas()) {
                sql.append(NamespaceManager.getNamespace()).append('.');
            }
            sql.append(member.sqlName()).append(" WHERE ").append(member.sqlOwnerName()).append(" = ?");

            if (dialect.isMultitenantSharedSchema()) {
                sql.append(" AND ").append(dialect.getNamingConvention().sqlNameSpaceColumnName()).append(" = ?");
            }
            if (EntityPersistenceServiceRDB.traceSql) {
                log.debug(Trace.id() + " {} ", sql);
            }
            stmt = persistenceContext.getConnection().prepareStatement(sql.toString());
            stmt.setLong(1, primaryKey.asLong());
            if (dialect.isMultitenantSharedSchema()) {
                stmt.setString(2, NamespaceManager.getNamespace());
            }
            persistenceContext.setUncommittedChanges();
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("{} SQL: {}", member.sqlName(), sql);
            log.error("{} SQL delete error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

    public static void delete(PersistenceContext persistenceContext, Iterable<Key> primaryKeys, MemberCollectionOperationsMeta member) {
        PreparedStatement stmt = null;
        StringBuilder sql = new StringBuilder();
        Dialect dialect = persistenceContext.getDialect();
        try {
            //TODO delete by Polymorphic Owner
            sql.append("DELETE FROM ");
            if (dialect.isMultitenantSeparateSchemas()) {
                sql.append(NamespaceManager.getNamespace()).append('.');
            }
            sql.append(member.sqlName()).append(" WHERE ").append(member.sqlOwnerName()).append(" = ?");

            if (dialect.isMultitenantSharedSchema()) {
                sql.append(" AND ").append(dialect.getNamingConvention().sqlNameSpaceColumnName()).append(" = ?");
            }
            if (EntityPersistenceServiceRDB.traceSql) {
                log.debug(Trace.id() + " {} ", sql);
            }
            stmt = persistenceContext.getConnection().prepareStatement(sql.toString());
            int pkSize = 0;
            for (Key primaryKey : primaryKeys) {
                stmt.setLong(1, primaryKey.asLong());
                if (dialect.isMultitenantSharedSchema()) {
                    stmt.setString(2, NamespaceManager.getNamespace());
                }
                stmt.addBatch();
                pkSize++;
            }
            if (pkSize == 0) { //nothing to delete
                return;
            }
            persistenceContext.setUncommittedChanges();
            stmt.executeBatch();

            if (EntityPersistenceServiceRDB.traceWarnings) {
                SQLUtils.logAndClearWarnings(persistenceContext.getConnection());
            } else if (dialect instanceof HSQLDialect) {
                persistenceContext.getConnection().clearWarnings();
            }
        } catch (SQLException e) {
            log.error("{} SQL: {}", member.sqlName(), sql);
            log.error("{} SQL delete error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

    public static void truncate(PersistenceContext persistenceContext, MemberOperationsMeta member) {
        PreparedStatement stmt = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("TRUNCATE TABLE ");
            if (persistenceContext.getDialect().isMultitenantSeparateSchemas()) {
                sql.append(NamespaceManager.getNamespace()).append('.');
            }
            sql.append(member.sqlName());

            stmt = persistenceContext.getConnection().prepareStatement(sql.toString());
            stmt.execute();
        } catch (SQLException e) {
            log.error("{} SQL truncate error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

}
