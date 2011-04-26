/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-04-19
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.dialect;

import java.sql.Types;

import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;

public class OracleDialect extends Dialect {

    public OracleDialect(NamingConvention namingConvention) {
        super(DatabaseType.Oracle, namingConvention);
        addTypeMeta(Integer.class, "number", 10, 0);
        addTypeMeta(Short.class, "number", 5, 0);
        addTypeMeta(Long.class, "number", 19, 0);
        addTypeMeta(Double.class, "double precision", "float");
        addTypeMeta(Float.class, "float", "double precision");
        addTypeMeta(Boolean.class, "number", 1, 0);
        addTypeMeta(String.class, "varchar2");

        addTypeMeta(byte[].class, "blob");

        TypeMeta dateTypeMeta = new TypeMeta(java.util.Date.class, "timestamp", 0, -1);
        dateTypeMeta.setCompatibleTypeNames("timestamp(0)");
        addTypeMeta(dateTypeMeta);

        addTypeMeta(java.sql.Date.class, "date");

        TypeMeta timeTypeMeta = new TypeMeta(java.sql.Time.class, "timestamp", 0, -1);
        timeTypeMeta.setCompatibleTypeNames("timestamp(0)");
        addTypeMeta(timeTypeMeta);
    }

    @Override
    public int getTargetSqlType(Class<?> valueClass) {
        if (valueClass.equals(Boolean.class)) {
            return Types.BIT;
        } else {
            return super.getTargetSqlType(valueClass);
        }
    }

    @Override
    public int identifierMaximumLength() {
        return 32;
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
    public String sqlSequenceMetaData() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT sequence_name ");
        sql.append("  FROM user_sequences ");
        sql.append("UNION ");
        sql.append("SELECT synonym_name ");
        sql.append("  FROM user_synonyms us ");
        sql.append(" WHERE EXISTS ( ");
        sql.append("           SELECT 1 ");
        sql.append("             FROM all_sequences asq ");
        sql.append("            WHERE asq.sequence_name  = us.table_name");
        sql.append("              AND asq.sequence_owner = us.table_owner)");
        return sql.toString();
    }

    @Override
    public String getSequenceNextValSql(String sequenceName) {
        return sequenceName + ".nextval";
    }

    @Override
    public String getCreateSequenceSql(String sequenceName) {
        return "create sequence " + sequenceName;
    }

    @Override
    public String getDropSequenceSql(String sequenceName) {
        return "drop sequence " + sequenceName;
    }
}
