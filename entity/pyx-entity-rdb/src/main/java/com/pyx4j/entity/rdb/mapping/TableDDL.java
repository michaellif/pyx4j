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
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.mapping.TableMetadata.ColumnMetadata;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.MemberMeta;

class TableDDL {

    static List<String> sqlCreate(Dialect dialect, TableModel tableModel) {
        List<String> sqls = new Vector<String>();
        StringBuilder sql = new StringBuilder();
        sql.append("create table ");
        sql.append(tableModel.tableName);
        sql.append(" (");
        sql.append(" id ").append(dialect.getSqlType(Long.class));
        if (tableModel.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.AUTO) {
            sql.append(" ").append(dialect.getGeneratedIdColumnString());
        }

        for (MemberOperationsMeta member : tableModel.operationsMeta().getColumnMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            sql.append(", ").append(member.sqlName()).append(' ');
            if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                // TODO create FK
            }
            sql.append(sqlType(dialect, memberMeta));
        }

        for (MemberOperationsMeta member : tableModel.operationsMeta().getIndexMembers()) {
            sql.append(", ").append(member.sqlName()).append(' ');
            sql.append(indexSqlType(dialect, member));
        }

        // TODO other dialects
        sql.append(", PRIMARY KEY (id)");

        sql.append(')');
        sqls.add(sql.toString());

        Collections.reverse(sqls);
        return sqls;
    }

    static List<String> validateAndAlter(Dialect dialect, TableMetadata tableMetadata, TableModel tableModel) throws SQLException {
        List<String> alterSqls = new Vector<String>();
        for (MemberOperationsMeta member : tableModel.operationsMeta().getColumnMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            ColumnMetadata columnMeta = tableMetadata.getColumn(member.sqlName());
            if (columnMeta == null) {
                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    continue;
                }
                StringBuilder sql = new StringBuilder("alter table ");
                sql.append(tableModel.tableName);
                sql.append(" add column ");
                sql.append(member.sqlName()).append(' ');
                sql.append(sqlType(dialect, memberMeta));
                alterSqls.add(sql.toString());
            } else {
                if (!dialect.isCompatibleType(memberMeta.getValueClass(), columnMeta.getTypeName())) {
                    throw new RuntimeException(tableModel.tableName + "." + member.sqlName() + " incompatible SQL type " + columnMeta.getTypeName() + " != "
                            + dialect.getSqlType(memberMeta.getValueClass()));
                }
            }
        }

        for (MemberOperationsMeta member : tableModel.operationsMeta().getIndexMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            ColumnMetadata columnMeta = tableMetadata.getColumn(member.sqlName());
            if (columnMeta == null) {
                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    continue;
                }
                StringBuilder sql = new StringBuilder("alter table ");
                sql.append(tableModel.tableName);
                sql.append(" add column ");
                sql.append(member.sqlName()).append(' ');
                sql.append(indexSqlType(dialect, member));
                alterSqls.add(sql.toString());
            } else {
                if (!dialect.isCompatibleType(member.getIndexValueClass(), columnMeta.getTypeName())) {
                    throw new RuntimeException(tableModel.tableName + "." + member.sqlName() + " incompatible SQL type " + columnMeta.getTypeName() + " != "
                            + dialect.getSqlType(member.getIndexValueClass()));
                }
            }
        }

        return alterSqls;
    }

    private static String sqlType(Dialect dialect, MemberMeta memberMeta) {
        StringBuilder sql = new StringBuilder();
        sql.append(dialect.getSqlType(memberMeta.getValueClass()));
        if (Enum.class.isAssignableFrom(memberMeta.getValueClass())) {
            sql.append("(" + TableModel.ENUM_STRING_LENGHT_MAX + ")");
        } else if (String.class == memberMeta.getValueClass()) {
            sql.append('(').append((memberMeta.getLength() == 0) ? TableModel.ORDINARY_STRING_LENGHT_MAX : memberMeta.getLength()).append(')');
        }
        return sql.toString();
    }

    private static String indexSqlType(Dialect dialect, MemberOperationsMeta member) {
        StringBuilder sql = new StringBuilder();
        sql.append(dialect.getSqlType(member.getIndexValueClass()));
        if (Enum.class.isAssignableFrom(member.getIndexValueClass())) {
            sql.append("(" + TableModel.ENUM_STRING_LENGHT_MAX + ")");
        } else if (String.class == member.getIndexValueClass()) {
            sql.append('(')
                    .append((member.getMemberMeta().getLength() == 0) ? TableModel.ORDINARY_STRING_LENGHT_MAX : member.getMemberMeta().getLength())
                    .append(')');
        }
        return sql.toString();
    }

    public static List<String> sqlCreateCollectionMember(Dialect dialect, TableModel tableModel, MemberOperationsMeta member) {
        List<String> sqls = new Vector<String>();
        StringBuilder sql = new StringBuilder();
        sql.append("create table ");

        sql.append(member.sqlName());

        sql.append(" (");

        sql.append(" id ").append(dialect.getSqlType(Long.class)).append(" ").append(dialect.getGeneratedIdColumnString()).append(", ");
        sql.append(" owner ").append(dialect.getSqlType(Long.class)).append(", ");
        sql.append(" value ").append(sqlType(dialect, member.getMemberMeta()));

        // TODO store value class for AbstractEntity

        if (member.getMemberMeta().getObjectClassType() == ObjectClassType.EntityList) {
            sql.append(", ");
            sql.append(" seq ").append(dialect.getSqlType(Integer.class));
        }

        // TODO other dialects
        sql.append(", PRIMARY KEY (id)");

        sql.append(')');

        sqls.add(sql.toString());

        return sqls;
    }

    public static List<String> validateAndAlterCollectionMember(Connection connection, Dialect dialect, TableMetadata memberTableMetadata,
            TableModel tableModel, MemberOperationsMeta member) {
        List<String> alterSqls = new Vector<String>();
        // TODO Auto-generated method stub
        return alterSqls;
    }

}
