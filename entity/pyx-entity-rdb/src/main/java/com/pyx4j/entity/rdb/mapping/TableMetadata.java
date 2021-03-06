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
 * Created on Jul 12, 2010
 * @author vlads
 */
package com.pyx4j.entity.rdb.mapping;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.SQLUtils;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.server.contexts.NamespaceManager;

public class TableMetadata {

    private static final Logger log = LoggerFactory.getLogger(TableMetadata.class);

    private final String catalog;

    private final String schema;

    private final String name;

    private final Map<String, ColumnMetadata> columnsMetadata = new HashMap<String, ColumnMetadata>();

    private List<String> foreignKeys;

    private Map<String, String> foreignKeysReference;

    public static TableMetadata getTableMetadata(PersistenceContext persistenceContext, Configuration configuration, String name) throws SQLException {
        ResultSet rs = null;
        try {
            DatabaseMetaData dbMeta = persistenceContext.getConnection().getMetaData();
            String storedName = toDbCase(name, dbMeta);
            String schema = null;
            if (persistenceContext.getDialect().isMultitenantSeparateSchemas()) {
                schema = toDbCase(NamespaceManager.getNamespace(), dbMeta);
            } else if (configuration.forceQualifiedNames()) {
                schema = toDbCase(configuration.tablesSchema(), dbMeta);
            }
            rs = dbMeta.getTables(null, schema, storedName, null);
            if (rs.next()) {
                return new TableMetadata(rs, dbMeta);
            } else {
                return null;
            }
        } finally {
            SQLUtils.closeQuietly(rs);
        }
    }

    private static String toDbCase(String name, DatabaseMetaData dbMeta) throws SQLException {
        if (dbMeta.storesLowerCaseIdentifiers()) {
            return name.toLowerCase(Locale.ENGLISH);
        } else if (dbMeta.storesUpperCaseIdentifiers()) {
            return name.toUpperCase(Locale.ENGLISH);
        } else {
            return name;
        }
    }

    public static boolean isTableExists(PersistenceContext persistenceContext, String name) throws SQLException {
        return isTableExists(persistenceContext.getDialect(), persistenceContext.getConnection(), NamespaceManager.getNamespace(), name);
    }

    public static boolean isTableExists(Dialect dialect, Connection connection, String namespace, String name) throws SQLException {
        ResultSet rs = null;
        try {
            DatabaseMetaData dbMeta = connection.getMetaData();
            String storedName = toDbCase(name, dbMeta);
            String schema = null;
            if (dialect.isMultitenantSeparateSchemas() && (namespace != null)) {
                schema = toDbCase(namespace, dbMeta);
            }
            rs = dbMeta.getTables(null, schema, storedName, null);
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } finally {
            SQLUtils.closeQuietly(rs);
        }
    }

    TableMetadata(ResultSet rs, DatabaseMetaData dbMeta) throws SQLException {
        catalog = rs.getString("TABLE_CAT");
        schema = rs.getString("TABLE_SCHEM");
        name = rs.getString("TABLE_NAME");
        // read Columns
        ResultSet crs = null;
        try {
            crs = dbMeta.getColumns(catalog, schema, name, "%");
            while (crs.next()) {
                ColumnMetadata cm = new ColumnMetadata(crs);
                columnsMetadata.put(cm.getName(), cm);
            }
        } finally {
            SQLUtils.closeQuietly(crs);
        }

        log.trace("table {} @ {} ", name, schema);
        log.trace("columns {}", columnsMetadata);
    }

    void readForeignKeys(PersistenceContext persistenceContext) throws SQLException {
        foreignKeys = new ArrayList<String>();
        // read Foreign Keys on this table
        ResultSet krs = null;
        DatabaseMetaData dbMeta = persistenceContext.getConnection().getMetaData();
        try {
            krs = dbMeta.getImportedKeys(catalog, schema, name);
            while (krs.next()) {
                String constraintName = krs.getString("FK_NAME");
                //TODO investigate: I have no idea why they may appear twice
                if (!this.foreignKeys.contains(constraintName)) {
                    this.foreignKeys.add(constraintName);
                }
            }
        } finally {
            SQLUtils.closeQuietly(krs);
        }
    }

    void readReferenceForeignKeys(PersistenceContext persistenceContext) throws SQLException {
        if (foreignKeys == null) {
            readForeignKeys(persistenceContext);
        }
        foreignKeysReference = new HashMap<String, String>();
        // read Foreign Keys to this table
        ResultSet krs = null;
        DatabaseMetaData dbMeta = persistenceContext.getConnection().getMetaData();
        try {
            krs = dbMeta.getExportedKeys(catalog, schema, name);
            while (krs.next()) {
                String constraintName = krs.getString("FK_NAME");
                // Ignore self References
                if (!this.foreignKeys.contains(constraintName)) {
                    foreignKeysReference.put(constraintName, krs.getString("FKTABLE_NAME"));
                }
            }
        } finally {
            SQLUtils.closeQuietly(krs);
        }
    }

    String getTableName() {
        return name;
    }

    public ColumnMetadata getColumn(String name) {
        return columnsMetadata.get(name.toUpperCase(Locale.ENGLISH));
    }

    public Collection<String> getForeignKeyNames() {
        return foreignKeys;
    }

    public boolean hasForeignKey(String constraintName) {
        if (foreignKeys.contains(constraintName)) {
            return true;
        } else {
            for (String fk : foreignKeys) {
                if (fk.equalsIgnoreCase(constraintName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public Map<String, String> getForeignKeysReference() {
        return foreignKeysReference;
    }

    public static class ColumnMetadata {

        private final String name;

        private final int sqlDataType;

        private final String typeName;

        private final int columnSize;

        private final Boolean isNullable;

        ColumnMetadata(ResultSet rs) throws SQLException {
            name = rs.getString("COLUMN_NAME").toUpperCase(Locale.ENGLISH);
            sqlDataType = rs.getInt("DATA_TYPE");
            typeName = rs.getString("TYPE_NAME").toLowerCase(Locale.ENGLISH);
            columnSize = rs.getInt("COLUMN_SIZE");
            String nullable = rs.getString("IS_NULLABLE");
            if (nullable == null) {
                isNullable = null;
            } else if (nullable.equals("YES")) {
                isNullable = true;
            } else {
                isNullable = false;
            }
        }

        public String getName() {
            return name;
        }

        public int getJavaSQLType() {
            return sqlDataType;
        }

        public String getTypeName() {
            return typeName;
        }

        public int getColumnSize() {
            return columnSize;
        }

        public Boolean getNullable() {
            return isNullable;
        }

        @Override
        public String toString() {
            return getName() + ' ' + getTypeName() + ' ' + getColumnSize() + ", Nullable " + isNullable;
        }
    }

}
