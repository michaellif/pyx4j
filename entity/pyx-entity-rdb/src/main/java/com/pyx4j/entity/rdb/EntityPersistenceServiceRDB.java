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
 * Created on 2010-07-07
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.mapping.Mappings;
import com.pyx4j.entity.rdb.mapping.TableModel;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.IEntityPersistenceServiceExt;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.meta.EntityMeta;

public class EntityPersistenceServiceRDB implements IEntityPersistenceService, IEntityPersistenceServiceExt {

    private static final Logger log = LoggerFactory.getLogger(EntityPersistenceServiceRDB.class);

    private final ConnectionProvider connectionProvider;

    private final Mappings mappings;

    public EntityPersistenceServiceRDB() {
        try {
            IPersistenceConfiguration cfg = ServerSideConfiguration.instance().getPersistenceConfiguration();
            if (!(cfg instanceof Configuration)) {
                throw new RuntimeException("Invalid RDB configuration class " + cfg);
            }
            connectionProvider = new ConnectionProvider((Configuration) cfg);
        } catch (SQLException e) {
            log.error("RDB initialization error", e);
            throw new RuntimeException(e.getMessage());
        }
        mappings = new Mappings(connectionProvider);
    }

    @Override
    public void dispose() {
        connectionProvider.dispose();
    }

    @Override
    public void deregister() {
        connectionProvider.deregister();
    }

    public void dropTable(Class<? extends IEntity> entityClass) {
        TableModel tm = new TableModel(EntityFactory.getEntityMeta(entityClass));
        try {
            tm.dropTable(connectionProvider);
        } catch (SQLException e) {
            log.error("drop table error", e);
            throw new RuntimeExceptionSerializable(e);
        }
    }

    @Override
    public void persist(IEntity entity) {
        EntityMeta entityMeta = entity.getEntityMeta();
        if (entityMeta.isTransient()) {
            throw new Error("Can't persist Transient Entity");
        }
        mappings.ensureTable(entityMeta);
    }

    @Override
    public <T extends IEntity> void persist(Iterable<T> entityIterable) {
        // TODO Auto-generated method stub

    }

    @Override
    public void merge(IEntity entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public <T extends IEntity> T retrieve(Class<T> entityClass, long primaryKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends IEntity> T retrieve(EntityQueryCriteria<T> criteria) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends IEntity> Map<Long, T> retrieve(Class<T> entityClass, Iterable<Long> primaryKeys) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getIndexedPropertyName(EntityMeta meta, Path path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPropertyName(EntityMeta meta, Path path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends IEntity> List<T> query(EntityQueryCriteria<T> criteria) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends IEntity> ICursorIterator<T> query(String encodedCursorRefference, EntityQueryCriteria<T> criteria) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends IEntity> List<Long> queryKeys(EntityQueryCriteria<T> criteria) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends IEntity> ICursorIterator<Long> queryKeys(String encodedCursorRefference, EntityQueryCriteria<T> criteria) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends IEntity> int count(EntityQueryCriteria<T> criteria) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void delete(IEntity entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public <T extends IEntity> void delete(Class<T> entityClass, long primaryKey) {
        // TODO Auto-generated method stub

    }

    @Override
    public <T extends IEntity> int delete(EntityQueryCriteria<T> criteria) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <T extends IEntity> void delete(Class<T> entityClass, Iterable<Long> primaryKeys) {
        // TODO Auto-generated method stub

    }

    @Override
    public void requestsAggregationStart() {

    }

    @Override
    public void requestsAggregationComplete() {

    }

    @Override
    public int getDatastoreCallCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getDatastoreWriteCallCount() {
        return 0;
    }
}
