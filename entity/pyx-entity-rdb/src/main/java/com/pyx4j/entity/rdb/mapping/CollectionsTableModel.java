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
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.rdb.SQLUtils;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ObjectClassType;

public class CollectionsTableModel {

    private static final Logger log = LoggerFactory.getLogger(CollectionsTableModel.class);

    public static void insert(Connection connection, Dialect dialect, IEntity entity, MemberOperationsMeta member) {
        Collection<Object> dataSet = (Collection<Object>) member.getMemberValue(entity);
        if ((dataSet == null) || dataSet.isEmpty()) {
            return;
        } else {
            insert(connection, dialect, entity.getPrimaryKey(), member, dataSet);
        }
    }

    /**
     * Convert collection to its IDs
     */
    private static Collection convertICollectionValue(MemberOperationsMeta member, Collection<Object> dataSet) {
        Collection<Long> idDataSet = new Vector<Long>();
        for (Object value : dataSet) {
            Map<String, Object> map = (Map<String, Object>) value;
            if (map == null) {
                throw new Error("Saving null reference " + member.getMemberMeta().getCaption());
            }
            Long childKey = (Long) map.get(IEntity.PRIMARY_KEY);
            if (childKey == null) {
                throw new Error("Saving non persisted reference " + member.getMemberMeta().getCaption());
            }
            idDataSet.add(childKey);
        }
        return idDataSet;
    }

    private static void insert(Connection connection, Dialect dialect, long primaryKey, MemberOperationsMeta member, Collection<Object> dataSet) {
        PreparedStatement stmt = null;
        Class<?> valueClass = member.getMemberMeta().getValueClass();
        ObjectClassType type = member.getMemberMeta().getObjectClassType();
        boolean isList = (type == ObjectClassType.EntityList);
        if (type != ObjectClassType.PrimitiveSet) {
            dataSet = convertICollectionValue(member, dataSet);
            valueClass = Long.class;
        }

        int targetSqlType = dialect.getTargetSqlType(valueClass);
        try {
            if (isList) {
                stmt = connection.prepareStatement("INSERT INTO " + member.sqlName() + " ( owner, value, seq ) VALUES (?, ?, ?)");
            } else {
                stmt = connection.prepareStatement("INSERT INTO " + member.sqlName() + " ( owner,  value ) VALUES (?, ?)");
            }
            int seq = 0;
            for (Object value : dataSet) {
                stmt.setLong(1, primaryKey);
                stmt.setObject(2, TableModel.encodeValue(valueClass, value), targetSqlType);
                if (isList) {
                    stmt.setInt(3, seq);
                }
                seq++;
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("{} SQL insert error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

    public static void update(Connection connection, Dialect dialect, IEntity entity, MemberOperationsMeta member) {
        ObjectClassType type = member.getMemberMeta().getObjectClassType();
        boolean isList = (type == ObjectClassType.EntityList);

        Collection<Object> dataSet = (Collection<Object>) member.getMemberValue(entity);

        Collection<Object> insertData = isList ? new Vector<Object>() : new HashSet<Object>();
        if (dataSet != null) {
            insertData.addAll(dataSet);
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement("SELECT id, value FROM " + member.sqlName() + " WHERE owner = ?" + (isList ? " ORDER BY seq" : ""),
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.setLong(1, entity.getPrimaryKey());
            rs = stmt.executeQuery();
            while (rs.next()) {
                Object value = TableModel.decodeValue(rs.getObject("value"), member.getMemberMeta());
                if (insertData.contains(value)) {
                    insertData.remove(value);
                } else {
                    rs.deleteRow();
                }
            }
        } catch (SQLException e) {
            log.error("{} SQL update error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }

        if (!insertData.isEmpty()) {
            insert(connection, dialect, entity.getPrimaryKey(), member, insertData);
        }
    }

    static void retrieve(Connection connection, IEntity entity, MemberOperationsMeta member) {
        ObjectClassType type = member.getMemberMeta().getObjectClassType();
        Collection<Object> dataSet = retrieveData(connection, entity.getPrimaryKey(), member, type);
        if (type == ObjectClassType.PrimitiveSet) {
            member.setMemberValue(entity, dataSet);
        } else {
            ICollection<IEntity, ?> iCollectionMember = (ICollection<IEntity, ?>) member.getMember(entity);
            for (Object value : dataSet) {
                iCollectionMember.add((IEntity) value);
            }
        }
    }

    private static Collection<Object> retrieveData(Connection connection, long primaryKey, MemberOperationsMeta member, ObjectClassType type) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean isList = (type == ObjectClassType.EntityList);
        try {
            stmt = connection.prepareStatement("SELECT value FROM " + member.sqlName() + " WHERE owner = ?" + (isList ? " ORDER BY seq" : ""));
            stmt.setLong(1, primaryKey);
            rs = stmt.executeQuery();
            Collection<Object> dataSet = isList ? new Vector<Object>() : new HashSet<Object>();
            while (rs.next()) {
                Object value = TableModel.decodeValue(rs.getObject("value"), member.getMemberMeta());

                if (type == ObjectClassType.PrimitiveSet) {
                    dataSet.add(value);
                } else {
                    // TODO get type
                    IEntity childIEntity = EntityFactory.create((Class<IEntity>) member.getMemberMeta().getValueClass());
                    childIEntity.setPrimaryKey((Long) value);
                    dataSet.add(childIEntity);
                }
            }
            return dataSet;
        } catch (SQLException e) {
            log.error("{} SQL select error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }
    }

    static void delete(Connection connection, long primaryKey, MemberOperationsMeta member) {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("DELETE FROM " + member.sqlName() + " WHERE owner = ?");
            stmt.setLong(1, primaryKey);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("{} SQL delete error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }

    }
}
