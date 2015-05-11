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
 * Created on May 19, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.dialect;

import java.math.BigDecimal;
import java.sql.SQLException;

import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.rdb.cfg.Configuration.MultitenancyType;

public class H2Dialect extends Dialect {

    private final boolean sequencesBaseIdentity;

    public H2Dialect(NamingConvention namingConvention, MultitenancyType multitenancyType, boolean sequencesBaseIdentity) {
        super(DatabaseType.H2, namingConvention, multitenancyType);
        addTypeMeta(Short.class, "smallint");
        addTypeMeta(Byte.class, "tinyint");
        addTypeMeta(Long.class, "bigint");
        addTypeMeta(Double.class, "double");
        addTypeMeta(Boolean.class, "boolean");
        addTypeMeta(byte[].class, "longvarbinary", "varbinary");
        addTypeMeta(java.util.Date.class, "timestamp");

        // TODO use annotation for scale
        addTypeMeta(BigDecimal.class, "decimal", 18, 2);
        this.sequencesBaseIdentity = sequencesBaseIdentity;
    }

    @Override
    public int identifierMaximumLength() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String getGeneratedIdColumnString() {
        if (sequencesBaseIdentity) {
            return "NOT NULL";
        } else {
            return "NOT NULL AUTO_INCREMENT";
        }
    }

    @Override
    public boolean isSequencesBaseIdentity() {
        return sequencesBaseIdentity;
    }

    @Override
    public String sqlSequenceNextVal(String sequenceName) {
        if (sequencesBaseIdentity) {
            return "NEXT VALUE FOR " + sequenceName;
        } else {
            throw new Error("Configuration does not support sequences");
        }
    }

    @Override
    public String sqlSequenceCurentValue(String sequenceName) {
        if (sequencesBaseIdentity) {
            return "CURRENT VALUE FOR " + sequenceName;
        } else {
            throw new Error("Configuration does not support sequences");
        }
    }

    @Override
    public String sqlSequenceMetaData() {
        return "SELECT sequence_name FROM INFORMATION_SCHEMA.SEQUENCES";
    }

    @Override
    public String sqlCreateSequence(String sequenceName, int identityOffset) {
        if (sequencesBaseIdentity) {
            return "CREATE SEQUENCE " + sequenceName + ((identityOffset != 0) ? (" START WITH " + identityOffset) : "");
        } else {
            return super.sqlCreateSequence(sequenceName, identityOffset);
        }
    }

    @Override
    public String sqlDropSequence(String sequenceName) {
        if (sequencesBaseIdentity) {
            return "DROP SEQUENCE " + sequenceName;
        } else {
            throw new Error("Configuration does not support sequences");
        }
    }

    @Override
    public String sqlAlterIdentityColumn(String tableName, int identityOffset) {
        if (sequencesBaseIdentity) {
            return null;
        } else {
            return "ALTER TABLE " + tableName + " ALTER COLUMN " + this.getNamingConvention().sqlIdColumnName() + " BIGINT AUTO_INCREMENT (" + identityOffset
                    + ")";
        }
    }

    @Override
    public String sqlChangeNullable(String columnSqlName, String columnTypeSQLDefinition, boolean nullable) {
        return "ALTER COLUMN " + columnSqlName + " SET " + (nullable ? "" : "NOT ") + "NULL";
    }

    @Override
    public String sqlDropForeignKey(String tableName, String constraintName) {
        return "ALTER TABLE " + tableName + " DROP CONSTRAINT " + constraintName;
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
    public boolean isUniqueConstraintException(SQLException e) {
        return false;
    }
}
