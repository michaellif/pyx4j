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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.mapping.TableMetadata.ColumnMetadata;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.MemberMeta;

class TableDDL {

    private static class IndexDef {

        String name;

        String group;

        boolean uniqueConstraint;

        Map<String, String> columns = new HashMap<String, String>();

    }

    protected static int itentityOffset = 0;

    static List<String> sqlCreate(Dialect dialect, TableModel tableModel) {
        List<String> sqls = new Vector<String>();
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ");
        sql.append(tableModel.tableName);
        sql.append(" (");
        sql.append(" id ").append(dialect.getSqlType(Long.class));
        if (tableModel.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.AUTO) {
            sql.append(" ").append(dialect.getGeneratedIdColumnString());
        }
        if (dialect.isMultitenant()) {
            sql.append(", ");
            sql.append(" ns ").append(dialect.getSqlType(String.class)).append('(').append(200).append(')');
        }

        List<IndexDef> indexes = new Vector<IndexDef>();

        for (MemberOperationsMeta member : tableModel.operationsMeta().getColumnMembers()) {
            for (String sqlName : member.getValueAdapter().getColumnNames(member.sqlName())) {
                sql.append(", ");
                sql.append(sqlName).append(' ');
                member.getValueAdapter().appendColumnDefinition(sql, dialect, member, sqlName);

                if (member.getMemberMeta().isIndexed()) {
                    addIndexDef(indexes, sqlName, member.getMemberMeta().getAnnotation(Indexed.class));
                }
            }

            // TODO create FK
//            MemberMeta memberMeta = member.getMemberMeta();
//            if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
//            }
        }

        for (MemberOperationsMeta member : tableModel.operationsMeta().getIndexMembers()) {
            sql.append(", ").append(member.sqlName()).append(' ');
            sql.append(indexSqlType(dialect, member));
        }

        sql.append(", CONSTRAINT ").append(dialect.getNamingConvention().sqlTablePKName(tableModel.tableName)).append(" PRIMARY KEY (id)");

        sql.append(')');
        sqls.add(sql.toString());

        Collections.reverse(sqls);

        for (IndexDef indexDef : indexes) {
            sqls.add(createIndexSql(dialect, tableModel.tableName, indexDef));
        }

        if (tableModel.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.AUTO) {
            sqls.add(sqlAlterIdentity(dialect, tableModel.tableName));
        }
        return sqls;
    }

    private static void addIndexDef(List<IndexDef> indexes, String sqlName, Indexed indexedAnnotation) {
        if ((indexedAnnotation.group() != null) && (indexedAnnotation.group().length > 0)) {
            nextGroup: for (String group : indexedAnnotation.group()) {
                // find index of the same group
                String position = "";
                if (group != null) {
                    int p = group.indexOf(',');
                    if (p != -1) {
                        position = group.substring(p + 1).trim();
                        group = group.substring(0, p).trim();
                    }

                    for (IndexDef other : indexes) {
                        if (group.equals(other.group)) {
                            other.columns.put(sqlName, position);
                            continue nextGroup;
                        }
                    }
                }
                IndexDef def = new IndexDef();
                def.name = indexedAnnotation.name();
                def.uniqueConstraint = indexedAnnotation.uniqueConstraint();
                def.group = group;
                def.columns.put(sqlName, position);
                indexes.add(def);
            }
        } else {
            IndexDef def = new IndexDef();
            def.name = indexedAnnotation.name();
            def.uniqueConstraint = indexedAnnotation.uniqueConstraint();
            def.columns.put(sqlName, "");
            indexes.add(def);
        }

    }

    private static String createIndexSql(Dialect dialect, String tableName, final IndexDef indexDef) {
        List<String> columnsSorted = new Vector<String>();
        columnsSorted.addAll(indexDef.columns.keySet());
        Collections.sort(columnsSorted, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return indexDef.columns.get(o1).compareTo(indexDef.columns.get(o2));
            }
        });

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE ");
        if (indexDef.uniqueConstraint) {
            sql.append("UNIQUE ");
        }
        sql.append("INDEX ");
        if (CommonsStringUtils.isStringSet(indexDef.name)) {
            sql.append(indexDef.name);
        } else {
            sql.append(dialect.getNamingConvention().sqlTableIndexName(tableName, columnsSorted));
        }
        sql.append(" ON ").append(tableName).append(" (");
        boolean first = true;
        if (dialect.isMultitenant()) {
            sql.append(" ns ");
            first = false;
        }
        for (String column : columnsSorted) {
            if (first) {
                first = false;
            } else {
                sql.append(", ");
            }
            sql.append(column);
        }
        sql.append(")");
        return sql.toString();
    }

    static List<String> validateAndAlter(Dialect dialect, TableMetadata tableMetadata, TableModel tableModel) throws SQLException {
        List<String> alterSqls = new Vector<String>();
        for (MemberOperationsMeta member : tableModel.operationsMeta().getColumnMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                continue;
            }
            for (String sqlName : member.getValueAdapter().getColumnNames(member.sqlName())) {
                ColumnMetadata columnMeta = tableMetadata.getColumn(sqlName);
                if (columnMeta == null) {
                    StringBuilder sql = new StringBuilder("ALTER TABLE ");
                    sql.append(tableModel.tableName);
                    sql.append(" ADD "); // [ column ]
                    sql.append(sqlName).append(' ');
                    member.getValueAdapter().appendColumnDefinition(sql, dialect, member, sqlName);
                    alterSqls.add(sql.toString());
                } else {
                    if (!member.getValueAdapter().isCompatibleType(dialect, columnMeta.getTypeName(), member, sqlName)) {
                        throw new RuntimeException(tableModel.tableName + "." + member.sqlName() + " incompatible SQL type " + columnMeta.getTypeName()
                                + " for Java type " + memberMeta.getValueClass());
                    }
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
                StringBuilder sql = new StringBuilder("ALTER TABLE ");
                sql.append(tableModel.tableName);
                sql.append(" ADD "); // [ column ]
                sql.append(member.sqlName()).append(' ');
                sql.append(indexSqlType(dialect, member));
                alterSqls.add(sql.toString());
            } else {
                if (!dialect.isCompatibleType(member.getIndexValueClass(), memberMeta.getLength(), columnMeta.getTypeName())) {
                    throw new RuntimeException(tableModel.tableName + "." + member.sqlName() + " incompatible SQL type " + columnMeta.getTypeName() + " != "
                            + dialect.getSqlType(member.getIndexValueClass()));
                }
            }
        }

        return alterSqls;
    }

    private static String indexSqlType(Dialect dialect, MemberOperationsMeta member) {
        StringBuilder sql = new StringBuilder();
        sql.append(dialect.getSqlType(member.getIndexValueClass()));
        if (Enum.class.isAssignableFrom(member.getIndexValueClass())) {
            sql.append("(" + TableModel.ENUM_STRING_LENGHT_MAX + ")");
        } else if (String.class == member.getIndexValueClass()) {
            sql.append('(').append((member.getMemberMeta().getLength() == 0) ? TableModel.ORDINARY_STRING_LENGHT_MAX : member.getMemberMeta().getLength())
                    .append(')');
        }
        return sql.toString();
    }

    public static List<String> sqlCreateCollectionMember(Dialect dialect, MemberOperationsMeta member) {
        List<String> sqls = new Vector<String>();
        StringBuilder sql = new StringBuilder();

        String tableName = member.sqlName();

        sql.append("CREATE TABLE ");

        sql.append(tableName);

        sql.append(" (");
        if (dialect.isMultitenant()) {
            sql.append(" ns ").append(dialect.getSqlType(String.class)).append('(').append(200).append("), ");
        }
        sql.append(" id ").append(dialect.getSqlType(Long.class)).append(" ").append(dialect.getGeneratedIdColumnString()).append(", ");
        sql.append(" owner ").append(dialect.getSqlType(Long.class));

        for (String sqlName : member.getValueAdapter().getColumnNames("value")) {
            sql.append(", ");
            sql.append(sqlName).append(' ');
            member.getValueAdapter().appendColumnDefinition(sql, dialect, member, sqlName);
        }

        if (member.getMemberMeta().getObjectClassType() == ObjectClassType.EntityList) {
            sql.append(", ");
            sql.append(" seq ").append(dialect.getSqlType(Integer.class));
        }

        sql.append(", CONSTRAINT ").append(dialect.getNamingConvention().sqlTablePKName(tableName)).append(" PRIMARY KEY (id)");

        sql.append(')');

        sqls.add(sql.toString());

        StringBuilder sqlIdx = new StringBuilder();
        sqlIdx.append("CREATE INDEX ");
        sqlIdx.append(dialect.getNamingConvention().sqlTableIndexName(tableName, Arrays.asList("owner")));
        sqlIdx.append(" ON ").append(tableName).append(" (").append("owner)");

        sqls.add(sqlIdx.toString());

        sqls.add(sqlAlterIdentity(dialect, tableName));

        return sqls;
    }

    public static List<String> validateAndAlterCollectionMember(Dialect dialect, TableMetadata memberTableMetadata, MemberOperationsMeta member) {
        List<String> alterSqls = new Vector<String>();

        alterSqls.add(alterColumn(dialect, memberTableMetadata, "owner", Long.class, dialect.getSqlType(Long.class)));

        for (String sqlName : member.getValueAdapter().getColumnNames("value")) {
            ColumnMetadata columnMeta = memberTableMetadata.getColumn(sqlName);
            if (columnMeta == null) {
                StringBuilder sql = new StringBuilder("ALTER TABLE ");
                sql.append(memberTableMetadata.getTableName());
                sql.append(" ADD "); // [ column ]
                sql.append(sqlName).append(' ');
                member.getValueAdapter().appendColumnDefinition(sql, dialect, member, sqlName);
                alterSqls.add(sql.toString());
            } else {
                if (!member.getValueAdapter().isCompatibleType(dialect, columnMeta.getTypeName(), member, sqlName)) {
                    throw new RuntimeException(memberTableMetadata.getTableName() + "." + sqlName + " incompatible SQL type " + columnMeta.getTypeName());
                }
            }
        }

        if (member.getMemberMeta().getObjectClassType() == ObjectClassType.EntityList) {
            alterSqls.add(alterColumn(dialect, memberTableMetadata, "seq", Integer.class, dialect.getSqlType(Integer.class)));
        }

        return alterSqls;
    }

    private static String alterColumn(Dialect dialect, TableMetadata tableMetadata, String sqlName, Class<?> klass, String sqlType) {
        ColumnMetadata columnMeta = tableMetadata.getColumn(sqlName);
        if (columnMeta == null) {
            StringBuilder sql = new StringBuilder("alter table ");
            sql.append(tableMetadata.getTableName());
            sql.append(" add "); // [ column ]
            sql.append(sqlName).append(' ');
            sql.append(sqlType);
            return sql.toString();
        } else {
            if (!dialect.isCompatibleType(klass, -1, columnMeta.getTypeName())) {
                throw new RuntimeException(tableMetadata.getTableName() + "." + sqlName + " incompatible SQL type " + columnMeta.getTypeName() + " != "
                        + dialect.getSqlType(klass));
            }
            return null;
        }
    }

    private static String sqlAlterIdentity(Dialect dialect, String tableName) {
        if (dialect.getTablesItentityOffset() == 0) {
            return null;
        } else {
            itentityOffset += dialect.getTablesItentityOffset();
            return dialect.sqlAlterIdentityColumn(tableName, itentityOffset);
        }
    }

}
