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
 */
package com.pyx4j.entity.rdb.dialect;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.rdb.cfg.Configuration.MultitenancyType;
import com.pyx4j.entity.shared.TextSearchDocument;

public class PostgreSQLDialect extends Dialect {

    private final boolean enableTextSearchSupport = true;

    public PostgreSQLDialect(NamingConvention namingConvention, MultitenancyType multitenancyType) {
        super(DatabaseType.PostgreSQL, namingConvention, multitenancyType);
        addTypeMeta(Byte.class, "smallint", "int2");
        addTypeMeta(Short.class, "smallint", "int2");
        addTypeMeta(Integer.class, "integer", "int4", "int2", "int8").requireConversion("int2", "int8");
        addTypeMeta(Long.class, "bigint", "int8", "int2", "int4").requireConversion("int2", "int4");
        addTypeMeta(Float.class, "real", "float4", "float8").requireConversion("float8");
        addTypeMeta(Double.class, "double precision", "float8", "float4").requireConversion("float4");
        addTypeMeta(Boolean.class, "bool");

        // We use annotation @MemberColumn to override scale
        addTypeMeta(BigDecimal.class, "numeric", 18, 2);

        addTypeMeta(byte[].class, "bytea");

        addTypeMeta(java.util.Date.class, "timestamp");

        if (enableTextSearchSupport) {
            addTypeMeta(TextSearchDocument.class, "tsvector");
        }
    }

    @Override
    public int getTargetSqlType(Class<?> valueClass) {
        if (valueClass.equals(byte[].class)) {
            return Types.VARBINARY;
        } else {
            return super.getTargetSqlType(valueClass);
        }
    }

    @Override
    public int identifierMaximumLength() {
        return 63;
    }

    @Override
    public String getGeneratedIdColumnString() {
        return "NOT NULL";
    }

    @Override
    public String likeOperator() {
        return "ILIKE";
    }

    @Override
    public String textSearchToSqlValue(String argumentPlaceHolder) {
        if (enableTextSearchSupport) {
            return "to_tsvector(" + argumentPlaceHolder + ")";
        } else {
            return super.textSearchToSqlValue(argumentPlaceHolder);
        }
    }

    @Override
    public String textSearchToBindValue(String value) {
        if (value != null) {
            return value.replaceAll("[^A-Za-z0-9 ]", " ");
        } else {
            return value;
        }
    }

    @Override
    public String textSearchOperator() {
        if (enableTextSearchSupport) {
            return "@@";
        } else {
            return super.textSearchOperator();
        }
    }

    @Override
    public String textSearchQueryBindValue(Object searchValue) {
        if (enableTextSearchSupport) {
            StringBuilder query = new StringBuilder();
            String value = searchValue.toString().replaceAll("[^A-Za-z0-9 ]", " ");
            for (String str : value.split(" ")) {
                if (CommonsStringUtils.isEmpty(str)) {
                    continue;
                }
                if (query.length() > 0) {
                    query.append(" & ");
                }
                query.append(str.trim()).append(":*");
            }
            return query.toString();
        } else {
            return super.textSearchQueryBindValue(searchValue);
        }
    }

    @Override
    public String textSearchToSqlQueryValue(String argumentPlaceHolder) {
        if (enableTextSearchSupport) {
            return "to_tsquery(" + argumentPlaceHolder + ")";
        } else {
            return super.textSearchToSqlQueryValue(argumentPlaceHolder);
        }
    }

    @Override
    public boolean isFunctionIndexesSupported() {
        return true;
    }

    @Override
    public boolean isSequencesBaseIdentity() {
        return true;
    }

    @Override
    public String sqlDBSystemDate() {
        return "SELECT TO_CHAR(NOW(), 'YYYY-MM-DD HH24:MI:SS')";
    }

    @Override
    public String sqlSequenceNextVal(String sequenceName) {
        return "nextval ('" + sequenceName + "')";
    }

    @Override
    public String sqlSequenceCurentValue(String sequenceName) {
        return "SELECT last_value FROM " + sequenceName;
    }

    @Override
    public String sqlSequenceMetaData() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT relname ");
        sql.append("  FROM pg_class ");
        sql.append(" WHERE relkind = 'S' ");
        return sql.toString();
    }

    @Override
    public String sqlCreateSequence(String sequenceName, int identityOffset) {
        return "CREATE SEQUENCE " + sequenceName + ((identityOffset != 0) ? (" START WITH " + identityOffset) : "");
    }

    @Override
    public String sqlDropSequence(String sequenceName) {
        return "DROP SEQUENCE " + sequenceName;
    }

    @Override
    public String sqlChangeDateType(String columnSqlName) {
        return "ALTER COLUMN " + columnSqlName + " TYPE";
    }

    @Override
    public String sqlChangeNullable(String columnSqlName, String columnTypeSQLDefinition, boolean nullable) {
        return "ALTER COLUMN " + columnSqlName + (nullable ? " DROP" : " SET") + " NOT NULL";
    }

    @Override
    public String sqlDropForeignKey(String tableName, String constraintName) {
        return "ALTER TABLE " + tableName + " DROP CONSTRAINT " + constraintName;
    }

    @Override
    public boolean isForeignKeyDeferrableSupported() {
        return true;
    }

    @Override
    public String applyLimitCriteria(String sql) {
        return sql + " LIMIT ? OFFSET ?";
    }

    @Override
    public LimitOffsetSyntax limitCriteriaType() {
        return LimitOffsetSyntax.Standard;
    }

    @Override
    public String sqlSortNulls(boolean descending) {
        if (descending) {
            return " NULLS LAST";
        } else {
            return " NULLS FIRST";
        }
    }

    @Override
    public boolean isUniqueConstraintException(SQLException e) {
        String message = e.getMessage();
        if (message == null) {
            return false;
        } else {
            return message.contains("duplicate") || message.contains("unique");
        }
    }

    @Override
    public boolean isIntegrityConstraintException(SQLException e) {
        String message = e.getMessage();
        if (message == null) {
            return false;
        } else {
            return message.contains("violates foreign key constraint");
        }
    }
}
