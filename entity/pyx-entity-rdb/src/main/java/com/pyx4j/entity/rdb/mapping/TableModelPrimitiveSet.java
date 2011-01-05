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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.rdb.SQLUtils;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.shared.IEntity;

class TableModelPrimitiveSet {

    private static final Logger log = LoggerFactory.getLogger(TableModelPrimitiveSet.class);

    static void insert(Connection connection, Dialect dialect, IEntity entity, MemberOperationsMeta member) {
        Collection<Object> dataSet = (Collection<Object>) member.getMemberValue(entity);
        if ((dataSet == null) || dataSet.isEmpty()) {
            return;
        } else {
            insert(connection, dialect, entity.getPrimaryKey(), member, dataSet);
        }
    }

    static void insert(Connection connection, Dialect dialect, long primaryKey, MemberOperationsMeta member, Collection<Object> dataSet) {
        PreparedStatement stmt = null;
        Class<?> valueClass = member.getMemberMeta().getValueClass();
        int targetSqlType = dialect.getTargetSqlType(valueClass);
        try {
            stmt = connection.prepareStatement("INSERT INTO " + member.sqlName() + " ( owner,  value ) VALUES (?, ?)");

            for (Object value : dataSet) {
                stmt.setLong(1, primaryKey);
                stmt.setObject(2, TableModel.encodeValue(valueClass, value), targetSqlType);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            log.error("{} SQL insert error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

    static void update(Connection connection, Dialect dialect, IEntity entity, MemberOperationsMeta member) {
        Collection<Object> dataSet = (Collection<Object>) member.getMemberValue(entity);

        Set<Object> insertDataSet = new HashSet<Object>();
        if (dataSet != null) {
            insertDataSet.addAll(dataSet);
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement("SELECT id, value FROM " + member.sqlName() + " WHERE owner = ?", ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt.setLong(1, entity.getPrimaryKey());
            rs = stmt.executeQuery();
            while (rs.next()) {
                Object value = TableModel.decodeValue(rs.getObject("value"), member.getMemberMeta());
                if (insertDataSet.contains(value)) {
                    insertDataSet.remove(value);
                } else {
                    rs.deleteRow();
                }
            }
        } catch (SQLException e) {
            log.error("{} SQL select error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }

        if (!insertDataSet.isEmpty()) {
            insert(connection, dialect, entity.getPrimaryKey(), member, insertDataSet);
        }
    }

    static void retrieve(Connection connection, IEntity entity, MemberOperationsMeta member) {
        member.setMemberValue(entity, retrieveData(connection, entity.getPrimaryKey(), member));
    }

    static Set<Object> retrieveData(Connection connection, long primaryKey, MemberOperationsMeta member) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement("SELECT value FROM " + member.sqlName() + " WHERE owner = ?");
            stmt.setLong(1, primaryKey);
            rs = stmt.executeQuery();
            Set<Object> dataSet = new HashSet<Object>();
            while (rs.next()) {
                Object value = TableModel.decodeValue(rs.getObject("value"), member.getMemberMeta());
                dataSet.add(value);
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
