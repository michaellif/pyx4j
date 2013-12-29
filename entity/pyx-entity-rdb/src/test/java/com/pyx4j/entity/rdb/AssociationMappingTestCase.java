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
 * Created on Feb 9, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.sql.SQLException;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rdb.mapping.TableMetadata;
import com.pyx4j.entity.test.server.DatastoreTestBase;

public abstract class AssociationMappingTestCase extends DatastoreTestBase {

    protected void resetTables(Class<? extends IEntity>... types) {
        EntityPersistenceServiceRDB service = ((EntityPersistenceServiceRDB) srv);
        for (Class<? extends IEntity> type : types) {
            if (service.isTableExists(type)) {
                service.dropTable(type);
            }
        }
    }

    public boolean testColumnExists(Class<? extends IEntity> type, String columnName) {
        srv.count(EntityQueryCriteria.create(type));
        EntityPersistenceServiceRDB service = ((EntityPersistenceServiceRDB) srv);
        try {
            TableMetadata tableMetadata = service.getTableMetadata(EntityFactory.getEntityMeta(type));
            return tableMetadata.getColumn(columnName) != null;
        } catch (SQLException e) {
            throw new Error("Error in getTableMetadata", e);
        }
    }

}
