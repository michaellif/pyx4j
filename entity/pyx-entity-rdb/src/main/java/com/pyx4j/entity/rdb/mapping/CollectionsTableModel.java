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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
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

    public static void insert(Connection connection, Dialect dialect, IEntity entity, MemberOperationsMeta member) {
        @SuppressWarnings("unchecked")
        Collection<Object> dataSet = (Collection<Object>) member.getMemberValue(entity);
        if ((dataSet == null) || dataSet.isEmpty()) {
            return;
        } else {
            insert(connection, dialect, entity.getPrimaryKey(), member, dataSet, true);
        }
    }

    /**
     * Convert collection to its IDs
     */
    @SuppressWarnings("rawtypes")
    private static List convertICollectionKeys(MemberOperationsMeta member, Collection<Object> dataSet, boolean insertNull) {
        List<Long> idDataSet = new ArrayList<Long>();
        for (Object value : dataSet) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) value;
            if (map == null) {
                if (!insertNull) {
                    idDataSet.add(null);
                } else {
                    throw new Error("Saving null item from collection " + member.getMemberName());
                }
            } else {
                Key childKey = (Key) map.get(IEntity.PRIMARY_KEY);
                if (childKey == null) {
                    throw new Error("Saving non persisted item from collection " + member.getMemberName());
                }
                idDataSet.add(childKey.asLong());
            }
        }
        return idDataSet;
    }

    @SuppressWarnings("unchecked")
    private static void insert(Connection connection, Dialect dialect, Key primaryKey, MemberOperationsMeta member, Collection<Object> dataSet,
            boolean insertNull) {
        PreparedStatement stmt = null;
        Class<?> valueClass = member.getMemberMeta().getValueClass();
        ObjectClassType type = member.getMemberMeta().getObjectClassType();
        boolean isList = (type == ObjectClassType.EntityList);
        if (type != ObjectClassType.PrimitiveSet) {
            dataSet = convertICollectionKeys(member, dataSet, insertNull);
            valueClass = Long.class;
        }

        int targetSqlType = dialect.getTargetSqlType(valueClass);
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("INSERT INTO ").append(member.sqlName()).append(" ( ");
            sql.append("owner, value");

            if (isList) {
                sql.append(", seq");
            }
            if (dialect.isMultitenant()) {
                sql.append(", ns");
            }
            if (dialect.isSequencesBaseIdentity()) {
                sql.append(", id");
            }

            sql.append(" ) VALUES (?, ?");

            if (isList) {
                sql.append(", ?");
            }
            if (dialect.isMultitenant()) {
                sql.append(", ?");
            }
            if (dialect.isSequencesBaseIdentity()) {
                sql.append(", ").append(dialect.getSequenceNextValSql(member.getSqlSequenceName()));
            }

            sql.append(")");

            stmt = connection.prepareStatement(sql.toString());
            int seq = 0;
            for (Object value : dataSet) {
                if ((value != null) || insertNull) {
                    int parameterIndex = 1;
                    stmt.setLong(parameterIndex, primaryKey.asLong());
                    parameterIndex++;
                    stmt.setObject(parameterIndex, TableModel.encodeValue(valueClass, value), targetSqlType);
                    parameterIndex++;
                    if (isList) {
                        stmt.setInt(parameterIndex, seq);
                        parameterIndex++;
                    }
                    if (dialect.isMultitenant()) {
                        stmt.setString(parameterIndex, NamespaceManager.getNamespace());
                        parameterIndex++;
                    }
                    if (EntityPersistenceServiceRDB.trace) {
                        log.info(Trace.id() + "insert {} (" + primaryKey + ", " + value + ", " + seq + ")", member.sqlName());
                    }
                    stmt.executeUpdate();
                }
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
    public static void update(Connection connection, Dialect dialect, IEntity entity, MemberOperationsMeta member) {
        ObjectClassType type = member.getMemberMeta().getObjectClassType();
        boolean isList = (type == ObjectClassType.EntityList);
        boolean isPrimitive = (type == ObjectClassType.PrimitiveSet);

        Collection<Object> dataSet = (Collection<Object>) member.getMemberValue(entity);

        List<Object> insertData = new Vector<Object>();

        List<Object> collectionValues = null;
        if (dataSet != null) {
            if (isPrimitive) {
                insertData.addAll(dataSet);
            } else {
                insertData = convertICollectionKeys(member, dataSet, false);
                collectionValues = new ArrayList<Object>();
                collectionValues.addAll(dataSet);
            }
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("SELECT id, value ");
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
                    throw new RuntimeException("namespace acess error");
                }
                Object value;
                if (isPrimitive) {
                    value = TableModel.decodeValue(rs.getObject("value"), member.getMemberMeta());
                } else {
                    value = TableModel.getLongValue(rs, "value");
                }
                int valueIdx = insertData.indexOf(value);
                if (valueIdx != -1) {
                    if (isPrimitive) {
                        insertData.remove(value);
                    } else {
                        if (isList) {
                            if (valueIdx != rs.getInt("seq")) {
                                if (EntityPersistenceServiceRDB.trace) {
                                    log.info(Trace.id() + "update {} (" + entity.getPrimaryKey() + ", " + value + ", " + rs.getInt("seq") + "->" + valueIdx
                                            + ")", member.sqlName());
                                }
                                rs.updateInt("seq", valueIdx);
                                rs.updateRow();
                            }
                        }
                        collectionValues.set(valueIdx, null);
                    }
                } else {
                    rs.deleteRow();
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
            if (isPrimitive) {
                insert(connection, dialect, entity.getPrimaryKey(), member, insertData, true);
            } else {
                insert(connection, dialect, entity.getPrimaryKey(), member, collectionValues, false);
            }
        }
    }

    static void retrieve(Connection connection, Dialect dialect, IEntity entity, MemberOperationsMeta member) {
        ObjectClassType type = member.getMemberMeta().getObjectClassType();
        Collection<Object> dataSet = retrieveData(connection, dialect, entity.getPrimaryKey(), member, type);
        if (type == ObjectClassType.PrimitiveSet) {
            member.setMemberValue(entity, dataSet);
        } else {
            @SuppressWarnings("unchecked")
            ICollection<IEntity, ?> iCollectionMember = (ICollection<IEntity, ?>) member.getMember(entity);
            for (Object value : dataSet) {
                iCollectionMember.add((IEntity) value);
            }
        }
    }

    private static Collection<Object> retrieveData(Connection connection, Dialect dialect, Key primaryKey, MemberOperationsMeta member, ObjectClassType type) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean isList = (type == ObjectClassType.EntityList);
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("SELECT value FROM ").append(member.sqlName()).append(" WHERE owner = ?");
            if (dialect.isMultitenant()) {
                sql.append(" AND ns = ?");
            }
            if (isList) {
                sql.append(" ORDER BY seq");
            }
            stmt = connection.prepareStatement(sql.toString());
            stmt.setLong(1, primaryKey.asLong());
            if (dialect.isMultitenant()) {
                stmt.setString(2, NamespaceManager.getNamespace());
            }
            rs = stmt.executeQuery();
            Collection<Object> dataSet = isList ? new Vector<Object>() : new HashSet<Object>();
            while (rs.next()) {
                if (type == ObjectClassType.PrimitiveSet) {
                    dataSet.add(TableModel.decodeValue(rs.getObject("value"), member.getMemberMeta()));
                } else {
                    // TODO get type
                    @SuppressWarnings("unchecked")
                    IEntity childIEntity = EntityFactory.create((Class<IEntity>) member.getMemberMeta().getValueClass());
                    long ck = rs.getLong("value");
                    if (!rs.wasNull()) {
                        childIEntity.setPrimaryKey(new Key(ck));
                    }
                    dataSet.add(childIEntity);
                }
            }
            return dataSet;
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

    static <T extends IEntity> void retrieve(Connection connection, Map<Long, T> entities, MemberOperationsMeta member) {
        ObjectClassType type = member.getMemberMeta().getObjectClassType();

        List<Long> pkList = new Vector<Long>();
        for (Long pk : entities.keySet()) {
            pkList.add(pk);
        }
        Map<Long, Collection<Object>> dataSet = retrieveData(connection, pkList, member, type);

        for (T entity : entities.values()) {
            Collection<Object> entSet = dataSet.get(entity.getPrimaryKey());
            if (type == ObjectClassType.PrimitiveSet) {
                member.setMemberValue(entity, entSet);
            } else {
                @SuppressWarnings("unchecked")
                ICollection<IEntity, ?> iCollectionMember = (ICollection<IEntity, ?>) member.getMember(entity);
                for (Object value : entSet) {
                    iCollectionMember.add((IEntity) value);
                }
            }
        }
    }

    private static Map<Long, Collection<Object>> retrieveData(Connection connection, List<Long> primaryKeys, MemberOperationsMeta member, ObjectClassType type) {
        boolean isList = (type == ObjectClassType.EntityList);
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT owner, value FROM ").append(member.sqlName()).append(" WHERE owner IN (");
        int count = 0;
        for (long primaryKey : primaryKeys) {
            if (count != 0) {
                queryStr.append(',');
            }
            queryStr.append(primaryKey);
            count++;
        }
        queryStr.append(") ORDER BY owner ");
        if (isList) {
            queryStr.append(", seq");
        }
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement(queryStr.toString());
            rs = stmt.executeQuery();

            Map<Long, Collection<Object>> retMap = new HashMap<Long, Collection<Object>>();
            Collection<Object> dataSet = isList ? new Vector<Object>() : new HashSet<Object>();
            long currKey = -1, prevKey = -1;
            while (rs.next()) {
                currKey = rs.getLong("owner");
                if (prevKey == -1) {
                    prevKey = currKey;
                }
                if (currKey != prevKey) { // if we roll to new owner, then add dataSet to the retMap
                    retMap.put(prevKey, dataSet);
                    dataSet = isList ? new Vector<Object>() : new HashSet<Object>(); // create object for next owner
                    prevKey = currKey;
                }
                if (type == ObjectClassType.PrimitiveSet) {
                    dataSet.add(TableModel.decodeValue(rs.getObject("value"), member.getMemberMeta()));
                } else {
                    // TODO get type
                    @SuppressWarnings("unchecked")
                    IEntity childIEntity = EntityFactory.create((Class<IEntity>) member.getMemberMeta().getValueClass());
                    long ck = rs.getLong("value");
                    if (!rs.wasNull()) {
                        childIEntity.setPrimaryKey(new Key(ck));
                    }
                    dataSet.add(childIEntity);
                }
            }
            return retMap;
        } catch (SQLException e) {
            log.error("{} SQL select error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }
    }

}
