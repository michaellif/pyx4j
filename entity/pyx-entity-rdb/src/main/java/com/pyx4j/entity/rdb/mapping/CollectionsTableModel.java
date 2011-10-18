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

import java.sql.Connection;
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
import com.pyx4j.entity.rdb.SQLUtils;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.server.contexts.NamespaceManager;

public class CollectionsTableModel {

    private static final Logger log = LoggerFactory.getLogger(CollectionsTableModel.class);

    public static void validate(IEntity entity, MemberOperationsMeta member) {
        int maxLength = member.getMemberMeta().getLength();
        if (maxLength <= 0) {
            return;
        }
        @SuppressWarnings("rawtypes")
        Collection c = (Collection) member.getMemberValue(entity);
        if ((c != null) && (c.size() > maxLength)) {
            throw new RuntimeException("Member size vialoation in entity '" + entity.getEntityMeta().getCaption() + "' member '" + member.getMemberName()
                    + "' size " + c.size() + " is greater than max allowed " + maxLength);
        }
    }

    @SuppressWarnings("unchecked")
    public static void insert(Connection connection, Dialect dialect, IEntity entity, MemberOperationsMeta member) {
        Collection<Object> dataSet = (Collection<Object>) member.getMemberValue(entity);
        if ((dataSet == null) || dataSet.isEmpty()) {
            return;
        } else {
            insert(connection, dialect, entity, member, (Collection<Object>) member.getMember(entity), null);
        }
    }

    private static void insert(Connection connection, Dialect dialect, IEntity entity, MemberOperationsMeta member, Collection<Object> dataSet,
            List<Object> dataPositions) {
        PreparedStatement stmt = null;

        ObjectClassType type = member.getMemberMeta().getObjectClassType();
        boolean isList = (type == ObjectClassType.EntityList);

        StringBuilder sql = new StringBuilder();
        try {
            int numberOfParams = 0;
            sql.append("INSERT INTO ").append(member.sqlName()).append(" ( ");
            sql.append("owner");
            numberOfParams++;

            for (String name : member.getValueAdapter().getColumnNames("value")) {
                sql.append(", ");
                sql.append(name);
                numberOfParams++;
            }

            if (isList) {
                sql.append(", seq");
                numberOfParams++;
            }
            if (dialect.isMultitenant()) {
                sql.append(", ns");
                numberOfParams++;
            }
            if (dialect.isSequencesBaseIdentity()) {
                sql.append(", id");
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

            stmt = connection.prepareStatement(sql.toString());
            int seq = 0;
            for (Object value : dataSet) {
                if ((type == ObjectClassType.EntityList) || (type == ObjectClassType.EntitySet)) {
                    IEntity childEntity = (IEntity) value;
                    if ((childEntity.getPrimaryKey() == null) && !childEntity.isNull()) {
                        log.error("Saving non persisted reference {}", childEntity);
                        throw new Error("Saving non persisted reference " + member.getMemberMeta().getValueClass().getName() + " from collection "
                                + member.getMemberMeta().getCaption() + " of " + entity.getEntityMeta().getCaption());
                    }
                }
                int parameterIndex = 1;
                stmt.setLong(parameterIndex, entity.getPrimaryKey().asLong());
                parameterIndex++;

                parameterIndex += member.getValueAdapter().bindValue(stmt, parameterIndex, value);

                if (isList) {
                    if (dataPositions != null) {
                        seq = dataPositions.indexOf(value);
                    }
                    stmt.setInt(parameterIndex, seq);
                    parameterIndex++;
                }
                if (dialect.isMultitenant()) {
                    stmt.setString(parameterIndex, NamespaceManager.getNamespace());
                    parameterIndex++;
                }
                if (EntityPersistenceServiceRDB.trace) {
                    log.info(Trace.id() + "insert {} (" + entity.getPrimaryKey() + ", " + value + ", " + seq + ")", member.sqlName());
                }
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
    public static void update(Connection connection, Dialect dialect, IEntity entity, MemberOperationsMeta member, List<IEntity> cascadeRemove) {
        ObjectClassType type = member.getMemberMeta().getObjectClassType();
        boolean isList = (type == ObjectClassType.EntityList);

        Collection<Object> collectionMember = (Collection<Object>) member.getMember(entity);

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

        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("SELECT id");
            for (String name : member.getValueAdapter().getColumnNames("value")) {
                sql.append(", ");
                sql.append(name);
            }
            if (isList) {
                sql.append(", seq");
            }
            if (dialect.isMultitenant()) {
                sql.append(", ns");
            }
            sql.append(" FROM ").append(member.sqlName()).append(" WHERE owner = ?");
            if (dialect.isMultitenant()) {
                sql.append(" AND ns = ?");
            }

            stmt = connection.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.setLong(1, entity.getPrimaryKey().asLong());
            if (dialect.isMultitenant()) {
                stmt.setString(2, NamespaceManager.getNamespace());
            }
            rs = stmt.executeQuery();
            while (rs.next()) {
                if ((dialect.isMultitenant()) && !rs.getString("ns").equals(NamespaceManager.getNamespace())) {
                    throw new RuntimeException("namespace access error");
                }
                Object value = member.getValueAdapter().retrieveValue(rs, "value");
                int valueIdx = allData.indexOf(value);
                if (valueIdx != -1) {
                    insertData.remove(value);
                    if (isList) {
                        if (valueIdx != rs.getInt("seq")) {
                            if (EntityPersistenceServiceRDB.trace) {
                                log.info(Trace.id() + "update {} (" + entity.getPrimaryKey() + ", " + value + ", " + rs.getInt("seq") + "->" + valueIdx + ")",
                                        member.sqlName());
                            }
                            rs.updateInt("seq", valueIdx);
                            rs.updateRow();
                        }
                    }
                } else {
                    rs.deleteRow();
                    if ((value instanceof IEntity) && (member.getMemberMeta().isOwnedRelationships())) {
                        cascadeRemove.add((IEntity) value);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("{} SQL {}", member.sqlName(), sql);
            log.error("{} SQL update error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }

        if (!insertData.isEmpty()) {
            insert(connection, dialect, entity, member, insertData, allData);
        }
    }

    static void retrieve(Connection connection, Dialect dialect, IEntity entity, MemberOperationsMeta member) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ObjectClassType type = member.getMemberMeta().getObjectClassType();
        boolean isList = (type == ObjectClassType.EntityList);
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("SELECT ");
            boolean firstColumn = true;
            for (String name : member.getValueAdapter().getColumnNames("value")) {
                if (firstColumn) {
                    firstColumn = false;
                } else {
                    sql.append(", ");
                }
                sql.append(name);
            }
            sql.append(" FROM ").append(member.sqlName()).append(" WHERE owner = ?");
            if (dialect.isMultitenant()) {
                sql.append(" AND ns = ?");
            }
            if (isList) {
                sql.append(" ORDER BY seq");
            }
            stmt = connection.prepareStatement(sql.toString());
            stmt.setLong(1, entity.getPrimaryKey().asLong());
            if (dialect.isMultitenant()) {
                stmt.setString(2, NamespaceManager.getNamespace());
            }
            rs = stmt.executeQuery();

            @SuppressWarnings("unchecked")
            Collection<Object> collectionMember = (Collection<Object>) member.getMember(entity);
            if (!collectionMember.isEmpty()) {
                log.warn("retrieving to not empty collection {}", member.getMemberPath());
            }
            while (rs.next()) {
                Object value = member.getValueAdapter().retrieveValue(rs, "value");
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

    static <T extends IEntity> void retrieve(Connection connection, Dialect dialect, Map<Long, T> entities, MemberOperationsMeta member) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ObjectClassType type = member.getMemberMeta().getObjectClassType();
        boolean isList = (type == ObjectClassType.EntityList);
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("SELECT owner");
            for (String name : member.getValueAdapter().getColumnNames("value")) {
                sql.append(", ");
                sql.append(name);
            }
            sql.append(" FROM ").append(member.sqlName()).append(" WHERE ");
            if (dialect.isMultitenant()) {
                sql.append(" ns = ? AND ");
            }
            sql.append(" owner = IN (");
            for (int i = 0; i < entities.keySet().size(); i++) {
                if (i != 0) {
                    sql.append(',');
                }
                sql.append(" ? ");
            }
            sql.append(')');
            sql.append(" ORDER BY owner");
            if (isList) {
                sql.append(", seq");
            }
            stmt = connection.prepareStatement(sql.toString());
            int parameterIndex = 1;
            if (dialect.isMultitenant()) {
                stmt.setString(parameterIndex, NamespaceManager.getNamespace());
                parameterIndex++;
            }
            for (Long pk : entities.keySet()) {
                stmt.setLong(parameterIndex, pk);
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                long currKey = rs.getLong("owner");
                T entity = entities.get(currKey);
                @SuppressWarnings("unchecked")
                Collection<Object> collectionMember = (Collection<Object>) member.getMember(entity);
                Object value = member.getValueAdapter().retrieveValue(rs, "value");
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

    public static void delete(Connection connection, Dialect dialect, Key primaryKey, MemberOperationsMeta member) {
        PreparedStatement stmt = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("DELETE FROM ").append(member.sqlName()).append(" WHERE owner = ?");
            if (dialect.isMultitenant()) {
                sql.append(" AND ns = ?");
            }
            stmt = connection.prepareStatement(sql.toString());
            stmt.setLong(1, primaryKey.asLong());
            if (dialect.isMultitenant()) {
                stmt.setString(2, NamespaceManager.getNamespace());
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("{} SQL {}", member.sqlName(), sql);
            log.error("{} SQL delete error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

    public static void delete(Connection connection, Dialect dialect, Iterable<Key> primaryKeys, MemberOperationsMeta member) {
        PreparedStatement stmt = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("DELETE FROM ").append(member.sqlName()).append(" WHERE owner = ?");
            if (dialect.isMultitenant()) {
                sql.append(" AND ns = ?");
            }
            stmt = connection.prepareStatement(sql.toString());
            int pkSize = 0;
            for (Key primaryKey : primaryKeys) {
                stmt.setLong(1, primaryKey.asLong());
                if (dialect.isMultitenant()) {
                    stmt.setString(2, NamespaceManager.getNamespace());
                }
                stmt.addBatch();
                pkSize++;
            }
            if (pkSize == 0) { //nothing to delete
                return;
            }

            stmt.executeBatch();
        } catch (SQLException e) {
            log.error("{} SQL delete error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

    public static void truncate(Connection connection, MemberOperationsMeta member) {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("TRUNCATE TABLE " + member.sqlName());
            stmt.execute();
        } catch (SQLException e) {
            log.error("{} SQL truncate error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

}
