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
import java.sql.SQLException;

import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.rdb.cfg.Configuration.MultitenancyType;

public class DerbyDialect extends Dialect {

    private final boolean sequencesBaseIdentity;

    public DerbyDialect(NamingConvention namingConvention, MultitenancyType multitenancyType, boolean sequencesBaseIdentity) {
        super(DatabaseType.Derby, namingConvention, multitenancyType);
        addTypeMeta(Short.class, "smallint");
        addTypeMeta(Byte.class, "smallint");
        addTypeMeta(Long.class, "bigint");
        addTypeMeta(Double.class, "double");
        addTypeMeta(Boolean.class, "boolean");
        addTypeMeta(java.util.Date.class, "timestamp");

        //TODO add proper blob size  support  http://db.apache.org/derby/docs/10.1/ref/rrefblob.html
        addTypeMeta(byte[].class, "blob(1M)");

        // TODO use annotation for scale
        addTypeMeta(BigDecimal.class, "decimal", 18, 2);
        this.sequencesBaseIdentity = sequencesBaseIdentity;
    }

    @Override
    public int identifierMaximumLength() {
        return 128;
    }

    @Override
    public String getGeneratedIdColumnString() {
        if (sequencesBaseIdentity) {
            return "NOT NULL";
        } else {
            return "NOT NULL GENERATED ALWAYS AS IDENTITY";
        }
    }

    @Override
    public boolean isSequencesBaseIdentity() {
        return sequencesBaseIdentity;
    }

    @Override
    public String getSequenceNextValSql(String sequenceName) {
        if (sequencesBaseIdentity) {
            return "NEXT VALUE FOR " + sequenceName;
        } else {
            throw new Error("Configuration does not support sequences");
        }
    }

    @Override
    public String sqlSequenceMetaData() {
        return "SELECT SEQUENCENAME FROM SYS.SYSSEQUENCES";
    }

    @Override
    public String getCreateSequenceSql(String sequenceName, int identityOffset) {
        if (sequencesBaseIdentity) {
            return "CREATE SEQUENCE " + sequenceName + ((identityOffset != 0) ? (" START WITH " + identityOffset) : "");
        } else {
            return super.getCreateSequenceSql(sequenceName, identityOffset);
        }
    }

    @Override
    public String getDropSequenceSql(String sequenceName) {
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
            return "ALTER TABLE  " + tableName + "  ALTER COLUMN " + this.getNamingConvention().sqlIdColumnName() + " RESTART WITH " + identityOffset + "";
        }
    }

    @Override
    public String sqlDropForeignKey(String tableName, String constraintName) {
        return "ALTER TABLE " + tableName + " DROP CONSTRAINT " + constraintName;
    }

    @Override
    public String applyLimitCriteria(String sql) {
        return sql + "OFFSET ? ROWS";
    }

    @Override
    public LimitOffsetSyntax limitCriteriaType() {
        return LimitOffsetSyntax.OffsetOnly;
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
        return false;
    }
}
