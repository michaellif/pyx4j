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
import com.pyx4j.entity.adapters.IndexAdapter;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.mapping.TableMetadata.ColumnMetadata;
import com.pyx4j.entity.rdb.mapping.TableModel.ModelType;
import com.pyx4j.entity.server.AdapterFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.server.contexts.NamespaceManager;

class TableDDL {

    private static class IndexColumnDef {

        String position;

        boolean ignoreCase;

        MemberOperationsMeta member;

        public IndexColumnDef(String position, boolean ignoreCase, MemberOperationsMeta member) {
            this.position = position;
            this.ignoreCase = ignoreCase;
            this.member = member;
        }
    }

    private static class IndexDef {

        String name;

        String group;

        boolean uniqueConstraint;

        Map<String, IndexColumnDef> columns = new HashMap<String, IndexColumnDef>();

        public String debugInfo() {
            StringBuilder b = new StringBuilder();
            for (IndexColumnDef m : columns.values()) {
                if (b.length() > 0) {
                    b.append(", ");
                }
                b.append('(').append(m.member.toString()).append(')');
            }
            return b.toString();
        }

    }

    protected static int itentityOffset = 0;

    private static final boolean NS_PART_OF_PK = true;

    static String getFullTableName(Dialect dialect, String tableName) {
        if (dialect.isMultitenantSeparateSchemas()) {
            return NamespaceManager.getNamespace() + "." + tableName;
        } else {
            return tableName;
        }
    }

    static List<String> sqlCreate(Dialect dialect, TableModel tableModel) {
        List<String> sqls = new Vector<String>();
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ");
        sql.append(getFullTableName(dialect, tableModel.tableName));
        sql.append(" (");
        sql.append(dialect.getNamingConvention().sqlIdColumnName()).append(' ').append(dialect.getSqlType(Long.class));
        if (tableModel.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.AUTO) {
            sql.append(" ").append(dialect.getGeneratedIdColumnString());
        }
        if (dialect.isMultitenantSharedSchema()) {
            sql.append(", ");
            sql.append(dialect.getNamingConvention().sqlNameSpaceColumnName()).append(' ').append(dialect.getSqlType(String.class));
            sql.append('(').append(64).append(')');
            sql.append(" NOT NULL ");
        }
        if (tableModel.classModel != ModelType.regular) {
            sql.append(", ");
            sql.append(dialect.sqlDiscriminatorColumnName()).append(' ').append(dialect.getSqlType(String.class));
            sql.append('(').append(64).append(')');
            sql.append(" NOT NULL ");
        }

        List<IndexDef> indexes = new Vector<IndexDef>();

        for (MemberOperationsMeta member : tableModel.operationsMeta().getColumnMembers()) {
            for (String sqlName : member.getValueAdapter().getColumnNames(member.sqlName())) {
                sql.append(", ");
                sql.append(sqlName).append(' ');
                member.getValueAdapter().appendColumnDefinition(sql, dialect, member, sqlName);
                if (member.hasNotNullConstraint() && (member.getSubclassDiscriminators() == null)) {
                    sql.append(" NOT NULL ");
                }
                if (member.getMemberMeta().isIndexed()) {
                    addIndexDef(indexes, member, sqlName, member.getMemberMeta().getAnnotation(Indexed.class));
                }
            }
        }

        for (MemberOperationsMeta member : tableModel.operationsMeta().getIndexMembers()) {
            sql.append(", ").append(member.sqlName()).append(' ');
            sql.append(indexSqlType(dialect, member));
        }

        sql.append(", CONSTRAINT ").append(dialect.getNamingConvention().sqlTablePKName(tableModel.tableName));
        sql.append(" PRIMARY KEY (").append(dialect.getNamingConvention().sqlIdColumnName());
        if (dialect.isMultitenantSharedSchema() && NS_PART_OF_PK) {
            sql.append(", ");
            sql.append(dialect.getNamingConvention().sqlNameSpaceColumnName());
        }
        sql.append(")");

        sql.append(')');
        sqls.add(sql.toString());

        Collections.reverse(sqls);

        // Index validations
        Map<String, IndexDef> indexValidations = new HashMap<String, IndexDef>();

        for (IndexDef indexDef : indexes) {
            sqls.add(createIndexSql(dialect, tableModel.tableName, indexDef));

            if (indexValidations.containsKey(indexDef.name)) {
                throw new AssertionError("Duplicate indexes " + indexDef.debugInfo() + " and " + indexValidations.get(indexDef.name).debugInfo());
            } else {
                indexValidations.put(indexDef.name, indexDef);
            }
        }

        // create Polymorphic NotNull constraint
        for (MemberOperationsMeta member : tableModel.operationsMeta().getColumnMembers()) {
            for (String sqlName : member.getValueAdapter().getColumnNames(member.sqlName())) {
                if (member.hasNotNullConstraint() && (member.getSubclassDiscriminators() != null)) {
                    sqls.add(createPolymorphicNotNullConstraint(dialect, tableModel.tableName, sqlName, member));
                }
            }
        }

        if (tableModel.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.AUTO) {
            sqls.add(sqlAlterIdentity(dialect, tableModel.tableName));
        }
        return sqls;
    }

    private static String createPolymorphicNotNullConstraint(Dialect dialect, String tableName, String sqlName, MemberOperationsMeta member) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(getFullTableName(dialect, tableName));
        sql.append(" ADD CONSTRAINT ");
        sql.append(dialect.getNamingConvention().sqlConstraintName(tableName, sqlName));
        sql.append(" CHECK ");
        sql.append(" (");
        for (String discriminator : member.getSubclassDiscriminators()) {
            sql.append(" (");
            sql.append(dialect.sqlDiscriminatorColumnName()).append(" = '").append(discriminator).append("'");
            sql.append(" AND ");
            sql.append(sqlName).append(" IS NOT NULL");
            sql.append(")");

            sql.append(" OR (");
            sql.append(dialect.sqlDiscriminatorColumnName()).append(" != '").append(discriminator).append("'");
            sql.append(" AND ");
            sql.append(sqlName).append(" IS NULL");
            sql.append(")");
        }
        sql.append(")");

        return sql.toString();
    }

    static String sqlCreateForeignKey(Dialect dialect, String tableFrom, String indexColName, String tableTo) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(getFullTableName(dialect, tableFrom));
        sql.append(" ADD CONSTRAINT ");
        sql.append(dialect.getNamingConvention().sqlForeignKeyName(tableFrom, indexColName, tableTo));
        sql.append(" FOREIGN KEY ");
        sql.append(" (");
        sql.append(indexColName);
        if (dialect.isMultitenantSharedSchema() && NS_PART_OF_PK) {
            sql.append(", ");
            sql.append(dialect.getNamingConvention().sqlNameSpaceColumnName());
        }
        sql.append(") REFERENCES ");
        sql.append(getFullTableName(dialect, tableTo));
        sql.append("(").append(dialect.getNamingConvention().sqlIdColumnName());
        if (dialect.isMultitenantSharedSchema() && NS_PART_OF_PK) {
            sql.append(", ");
            sql.append(dialect.getNamingConvention().sqlNameSpaceColumnName());
        }
        sql.append(")");
        return sql.toString();
    }

    private static void addIndexDef(List<IndexDef> indexes, MemberOperationsMeta member, String sqlName, Indexed indexedAnnotation) {
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
                            other.columns.put(sqlName, new IndexColumnDef(position, indexedAnnotation.ignoreCase(), member));
                            if (indexedAnnotation.uniqueConstraint()) {
                                other.uniqueConstraint = true;
                            }
                            continue nextGroup;
                        }
                    }
                }
                IndexDef def = new IndexDef();
                def.name = indexedAnnotation.name();
                def.uniqueConstraint = indexedAnnotation.uniqueConstraint();
                def.group = group;
                def.columns.put(sqlName, new IndexColumnDef(position, indexedAnnotation.ignoreCase(), member));
                indexes.add(def);
            }
        } else {
            IndexDef def = new IndexDef();
            def.name = indexedAnnotation.name();
            def.uniqueConstraint = indexedAnnotation.uniqueConstraint();
            def.columns.put(sqlName, new IndexColumnDef("", indexedAnnotation.ignoreCase(), member));
            indexes.add(def);
        }

    }

    private static String createIndexSql(Dialect dialect, String tableName, final IndexDef indexDef) {
        List<String> columnsSorted = new Vector<String>();
        columnsSorted.addAll(indexDef.columns.keySet());
        Collections.sort(columnsSorted, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return indexDef.columns.get(o1).position.compareTo(indexDef.columns.get(o2).position);
            }
        });

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE ");
        if (indexDef.uniqueConstraint) {
            sql.append("UNIQUE ");
        }
        sql.append("INDEX ");
        if (!CommonsStringUtils.isStringSet(indexDef.name)) {
            indexDef.name = dialect.getNamingConvention().sqlTableIndexName(tableName, columnsSorted);
        }
        sql.append(indexDef.name);

        sql.append(" ON ").append(getFullTableName(dialect, tableName)).append(" (");
        boolean first = true;
        if (dialect.isMultitenantSharedSchema()) {
            sql.append(dialect.getNamingConvention().sqlNameSpaceColumnName());
            first = false;
        }
        for (String column : columnsSorted) {
            if (first) {
                first = false;
            } else {
                sql.append(", ");
            }
            if (dialect.isFunctionIndexesSupported() && indexDef.columns.get(column).ignoreCase) {
                sql.append("LOWER(").append(column).append(')');
            } else {
                sql.append(column);
            }
        }
        if ((indexDef.group != null) && indexDef.group.startsWith("discriminator")) {
            sql.append(", ").append(dialect.sqlDiscriminatorColumnName());
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
                    sql.append(getFullTableName(dialect, tableModel.tableName));
                    sql.append(" ADD "); // [ column ]
                    sql.append(sqlName).append(' ');
                    member.getValueAdapter().appendColumnDefinition(sql, dialect, member, sqlName);
                    alterSqls.add(sql.toString());
                } else {
                    if (!member.getValueAdapter().isCompatibleType(dialect, columnMeta.getTypeName(), member, sqlName)) {
                        throw new RuntimeException(tableModel.tableName + "." + member.sqlName() + " incompatible SQL type '" + columnMeta.getTypeName()
                                + "' for Java type " + memberMeta.getValueClass());
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
                sql.append(getFullTableName(dialect, tableModel.tableName));
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

            IndexAdapter<?> adapter = AdapterFactory.getIndexAdapter(member.getIndexAdapterClass());
            int len = adapter.getIndexValueLength(member.getMemberMeta());
            if (len == 0) {
                len = TableModel.ORDINARY_STRING_LENGHT_MAX;
            }

            sql.append('(').append(len).append(')');
        }
        return sql.toString();
    }

    public static List<String> sqlCreateCollectionMember(Dialect dialect, MemberOperationsMeta member) {
        List<String> sqls = new Vector<String>();
        StringBuilder sql = new StringBuilder();

        String tableName = member.sqlName();

        sql.append("CREATE TABLE ");

        sql.append(getFullTableName(dialect, tableName));

        sql.append(" (");
        if (dialect.isMultitenantSharedSchema()) {
            sql.append(dialect.getNamingConvention().sqlNameSpaceColumnName());
            sql.append(' ').append(dialect.getSqlType(String.class)).append('(').append(200).append("), ");
        }
        sql.append(dialect.getNamingConvention().sqlIdColumnName()).append(' ');
        sql.append(dialect.getSqlType(Long.class)).append(" ").append(dialect.getGeneratedIdColumnString()).append(", ");
        sql.append(dialect.getNamingConvention().sqlAutoGeneratedJoinOwnerColumnName()).append(" ").append(dialect.getSqlType(Long.class));

        for (String sqlName : member.getValueAdapter().getColumnNames(dialect.getNamingConvention().sqlAutoGeneratedJoinValueColumnName())) {
            sql.append(", ");
            sql.append(sqlName).append(' ');
            member.getValueAdapter().appendColumnDefinition(sql, dialect, member, sqlName);
        }

        if (member.getMemberMeta().getObjectClassType() == ObjectClassType.EntityList) {
            sql.append(", ");
            sql.append(dialect.getNamingConvention().sqlAutoGeneratedJoinOrderColumnName()).append(" ").append(dialect.getSqlType(Integer.class));
        }

        sql.append(", CONSTRAINT ").append(dialect.getNamingConvention().sqlTablePKName(tableName));
        sql.append(" PRIMARY KEY (").append(dialect.getNamingConvention().sqlIdColumnName());
        if (dialect.isMultitenantSharedSchema() && NS_PART_OF_PK) {
            sql.append(", ");
            sql.append(dialect.getNamingConvention().sqlNameSpaceColumnName());
        }
        sql.append(")");

        sql.append(')');

        sqls.add(sql.toString());

        StringBuilder sqlIdx = new StringBuilder();
        sqlIdx.append("CREATE INDEX ");
        sqlIdx.append(dialect.getNamingConvention().sqlTableIndexName(tableName,
                Arrays.asList(dialect.getNamingConvention().sqlAutoGeneratedJoinOwnerColumnName())));
        sqlIdx.append(" ON ").append(getFullTableName(dialect, tableName)).append(" (")
                .append(dialect.getNamingConvention().sqlAutoGeneratedJoinOwnerColumnName()).append(")");

        sqls.add(sqlIdx.toString());

        sqls.add(sqlAlterIdentity(dialect, tableName));

        return sqls;
    }

    public static List<String> validateAndAlterCollectionMember(Dialect dialect, TableMetadata memberTableMetadata, MemberOperationsMeta member) {
        List<String> alterSqls = new Vector<String>();

        alterSqls.add(alterColumn(dialect, memberTableMetadata, dialect.getNamingConvention().sqlAutoGeneratedJoinOwnerColumnName(), Long.class,
                dialect.getSqlType(Long.class)));

        for (String sqlName : member.getValueAdapter().getColumnNames(dialect.getNamingConvention().sqlAutoGeneratedJoinValueColumnName())) {
            ColumnMetadata columnMeta = memberTableMetadata.getColumn(sqlName);
            if (columnMeta == null) {
                StringBuilder sql = new StringBuilder("ALTER TABLE ");
                sql.append(getFullTableName(dialect, memberTableMetadata.getTableName()));
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
            alterSqls.add(alterColumn(dialect, memberTableMetadata, dialect.getNamingConvention().sqlAutoGeneratedJoinOrderColumnName(), Integer.class,
                    dialect.getSqlType(Integer.class)));
        }

        return alterSqls;
    }

    private static String alterColumn(Dialect dialect, TableMetadata tableMetadata, String sqlName, Class<?> klass, String sqlType) {
        ColumnMetadata columnMeta = tableMetadata.getColumn(sqlName);
        if (columnMeta == null) {
            StringBuilder sql = new StringBuilder("alter table ");
            sql.append(getFullTableName(dialect, tableMetadata.getTableName()));
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
