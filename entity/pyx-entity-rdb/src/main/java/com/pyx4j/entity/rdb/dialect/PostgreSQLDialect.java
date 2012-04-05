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
package com.pyx4j.entity.rdb.dialect;

import java.math.BigDecimal;
import java.sql.Types;

import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;

public class PostgreSQLDialect extends Dialect {

    public PostgreSQLDialect(NamingConvention namingConvention, boolean multitenant) {
        super(DatabaseType.PostgreSQL, namingConvention, multitenant);
        addTypeMeta(Byte.class, "smallint", "int2");
        addTypeMeta(Short.class, "smallint", "int2");
        addTypeMeta(Integer.class, "integer", "int4");
        addTypeMeta(Long.class, "bigint", "int8");
        addTypeMeta(Float.class, "real", "float4");
        addTypeMeta(Double.class, "double precision", "float8");
        addTypeMeta(Boolean.class, "bool");

        // TODO use annotation for scale
        addTypeMeta(BigDecimal.class, "numeric", 18, 2);

        addTypeMeta(byte[].class, "bytea");

        addTypeMeta(java.util.Date.class, "timestamp");
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
    public boolean isSequencesBaseIdentity() {
        return true;
    }

    @Override
    public String getSequenceNextValSql(String sequenceName) {
        return "nextval ('" + sequenceName + "')";
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
    public String getCreateSequenceSql(String sequenceName) {
        return "create sequence " + sequenceName;
    }

    @Override
    public String getDropSequenceSql(String sequenceName) {
        return "drop sequence " + sequenceName;
    }

//    @Override
//    public String sqlAlterIdentityColumn(String tableName, int itentityOffset) {
//        return "ALTER TABLE  " + tableName + "  AUTO_INCREMENT = " + itentityOffset;
//    }

    @Override
    public String sqlDropForeignKey(String tableName, String constraintName) {
        return "ALTER TABLE " + tableName + " DROP FOREIGN KEY " + constraintName;
    }

    @Override
    public String applyLimitCriteria(String sql) {
        return sql + " LIMIT ? OFFSET ?";
    }

    @Override
    public boolean limitCriteriaIsRelative() {
        return true;
    }

    @Override
    public String sqlSortNulls(boolean descending) {
        if (descending) {
            return " NULLS LAST";
        } else {
            return " NULLS FIRST";
        }
    }
}
