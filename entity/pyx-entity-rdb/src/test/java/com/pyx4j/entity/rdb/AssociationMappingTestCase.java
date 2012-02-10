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

import java.sql.Connection;
import java.sql.SQLException;

import com.pyx4j.entity.rdb.mapping.TableMetadata;
import com.pyx4j.entity.rdb.mapping.TableModel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.test.server.DatastoreTestBase;

public abstract class AssociationMappingTestCase extends DatastoreTestBase {

    public boolean testColumnExists(Class<? extends IEntity> type, String columnName) {
        EntityPersistenceServiceRDB service = ((EntityPersistenceServiceRDB) srv);
        Connection connection = service.getConnection();
        try {
            if (service.isTableExists(type)) {
                service.dropTable(type);
            }
            TableModel tableModel = service.tableModel(connection, EntityFactory.getEntityMeta(type));

            TableMetadata tableMetadata = TableMetadata.getTableMetadata(connection, tableModel.getTableName());
            return tableMetadata.getColumn(columnName) != null;
        } catch (SQLException e) {
            throw new Error("Error in getTableMetadata", e);
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

}
