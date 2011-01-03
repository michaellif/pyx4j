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
package com.pyx4j.entity.rdb.mapping;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.rdb.ConnectionProvider;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;

public class Mappings {

    private static final Logger log = LoggerFactory.getLogger(Mappings.class);

    private final ConnectionProvider connectionProvider;

    private final Map<Class<? extends IEntity>, TableModel> tables = new Hashtable<Class<? extends IEntity>, TableModel>();

    private final Set<String> usedTableNames = new HashSet<String>();

    public Mappings(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public TableModel ensureTable(EntityMeta entityMeta) {
        if (entityMeta.isTransient()) {
            throw new Error("Can't operate on Transient Entity");
        }
        synchronized (entityMeta.getEntityClass()) {
            TableModel model = tables.get(entityMeta.getEntityClass());
            if (model == null) {
                model = new TableModel(entityMeta);
                String tableName = entityMeta.getPersistenceName().toUpperCase(Locale.ENGLISH);
                if (usedTableNames.contains(tableName)) {
                    log.warn("redefining/extending table {} for class {}", tableName, entityMeta.getEntityClass());
                }
                try {
                    model.ensureExists(connectionProvider);
                } catch (SQLException e) {
                    log.error("SQL Error", e);
                    throw new RuntimeException(e);
                }
                tables.put(entityMeta.getEntityClass(), model);
                usedTableNames.add(tableName);
            }
            return model;
        }
    }
}
