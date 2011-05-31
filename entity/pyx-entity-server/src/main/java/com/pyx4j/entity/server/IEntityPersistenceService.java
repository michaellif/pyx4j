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
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.meta.EntityMeta;

public interface IEntityPersistenceService {

    public interface ICursorIterator<T> extends Iterator<T> {

        /**
         * @see com.google.appengine.api.datastore.Cursor#toWebSafeString()
         * @return Encoded current cursor as a web safe string that can later be decoded
         */
        public String encodedCursorReference();

        public void completeRetrieval();

    }

    public void persist(IEntity entity);

    public <T extends IEntity> void persist(Iterable<T> entityIterable);

    public void merge(IEntity entity);

    public <T extends IEntity> T retrieve(Class<T> entityClass, Key primaryKey);

    /**
     * Fill all the information to already existing entity object that has only PK value
     * set. e.g. @link Detached entity member.
     * 
     * @return false If entity not found.
     */
    public <T extends IEntity> boolean retrieve(T entity);

    public <T extends IEntity> T retrieve(EntityQueryCriteria<T> criteria);

    public <T extends IEntity> Map<Key, T> retrieve(Class<T> entityClass, Iterable<Key> primaryKeys);

    /**
     * This may be a join with secondary table in RDBMS
     */
    public String getIndexedPropertyName(EntityMeta meta, Path path);

    public String getPropertyName(EntityMeta meta, Path path);

    public <T extends IEntity> List<T> query(EntityQueryCriteria<T> criteria);

    public <T extends IEntity> ICursorIterator<T> query(String encodedCursorRefference, EntityQueryCriteria<T> criteria);

    public <T extends IEntity> List<Key> queryKeys(EntityQueryCriteria<T> criteria);

    public <T extends IEntity> ICursorIterator<String> queryKeys(String encodedCursorRefference, EntityQueryCriteria<T> criteria);

    public <T extends IEntity> int count(EntityQueryCriteria<T> criteria);

    /**
     * Cascade delete entity and Owned entities
     */
    public void delete(IEntity entity);

    /**
     * TODO: Cascade delete entity and Owned entities
     */
    public <T extends IEntity> int delete(EntityQueryCriteria<T> criteria);

    /**
     * Only delete one row, does not affect Owned entities e.g. no Cascade delete
     */
    public <T extends IEntity> void delete(Class<T> entityClass, Key primaryKey);

    /**
     * Only delete specified enties row, does not affect Owned entities e.g. no Cascade
     * delete
     */
    public <T extends IEntity> void delete(Class<T> entityClass, Iterable<Key> primaryKeys);

    public <T extends IEntity> void truncate(Class<T> entityClass);

    /**
     * Only affects retrieve by PK functions.
     */
    public void requestsAggregationStart();

    public void requestsAggregationComplete();

    public int getDatastoreCallCount();

    public int getDatastoreWriteCallCount();
}
