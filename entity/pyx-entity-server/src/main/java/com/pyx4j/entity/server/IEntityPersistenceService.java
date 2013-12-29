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

import java.io.Closeable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.ICollection;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.meta.EntityMeta;

public interface IEntityPersistenceService {

    /**
     * Start short lived Online Persistence Context with Transaction without nested transactions.
     * 
     * Usually called when on startRequest from Lifecycle.beginRequest
     * 
     * ! TODO rename to startPersistenceContext
     */
    public void startTransaction();

    public void startBackgroundProcessTransaction();

    /**
     * 
     * @param transactionScopeOption
     * @param connectionType
     *            Web (Online Transaction), BackgroundProcess all the rest,
     *            Web transactions are replaced with TransactionProcessing when started in context of BackgroundProcess.
     */
    public void startTransaction(TransactionScopeOption transactionScopeOption, ConnectionTarget connectionTarget);

    //TODO VladS& MishaL proper enum and proper name
    // return  not null when there are transaction.
    // return false when transation is autoCommit
    public Boolean getTransactionScopeOption();

    public void setAssertTransactionManangementCallOrigin();

    /**
     * Validate that Transaction is explicitly committed or rolled back and then close it.
     */
    public void endTransaction();

    public void removeThreadLocale();

    public void commit();

    public void rollback();

    /**
     * CompensationHandler is fired after rollback is executed in separate unit of work.
     */
    public void addTransactionCompensationHandler(CompensationHandler handler);

    public void setTransactionUserKey(Key currentUserKey);

    public interface ICursorIterator<T> extends Iterator<T>, Closeable {

        /**
         * @see com.google.appengine.api.datastore.Cursor#toWebSafeString()
         * @return Encoded current cursor as a web safe string that can later be decoded
         */
        public String encodedCursorReference();

        @Override
        public void close();

    }

    public void persist(IEntity entity);

    public <T extends IEntity> void persist(Iterable<T> entityIterable);

    public void merge(IEntity entity);

    public <T extends IEntity> void merge(Iterable<T> entityIterable);

    public <T extends IEntity> T retrieve(Class<T> entityClass, Key primaryKey);

    public <T extends IEntity> T retrieve(Class<T> entityClass, Key primaryKey, AttachLevel attachLevel, boolean forUpdate);

    /**
     * Fill all the information to already existing entity object that has only PK value
     * set. e.g. @link Detached entity member.
     * 
     * @return false If entity not found.
     */
    public <T extends IEntity> boolean retrieve(T entity);

    public <T extends IEntity> boolean retrieve(T entity, AttachLevel attachLevel, boolean forUpdate);

    //TODO make @Deprecated and use retrieve
    public <T extends IEntity> void retrieveMember(T entityMember);

    public <T extends IEntity> void retrieveMember(T entityMember, AttachLevel attachLevel);

    public <T extends IEntity> void retrieve(Iterable<T> entityIterable);

    public <T extends IEntity> void retrieve(Iterable<T> entityIterable, AttachLevel attachLevel);

    //TODO make @Deprecated and use retrieve
    public <T extends IEntity> void retrieveMember(ICollection<T, ?> collectionMember);

    public <T extends IEntity> void retrieveMember(ICollection<T, ?> collectionMember, AttachLevel attachLevel);

    /**
     * @param criteria
     * @return null if entity not found
     */
    public <T extends IEntity> T retrieve(EntityQueryCriteria<T> criteria);

    public <T extends IEntity> T retrieve(EntityQueryCriteria<T> criteria, AttachLevel attachLevel);

    public <T extends IEntity> Map<Key, T> retrieve(Class<T> entityClass, Iterable<Key> primaryKeys);

    /**
     * This may be a join with secondary table in RDBMS
     */
    public String getIndexedPropertyName(EntityMeta meta, Path path);

    public String getPropertyName(EntityMeta meta, Path path);

    /**
     * @param criteria
     * @return never returns null, if not found returns empty list
     */
    public <T extends IEntity> List<T> query(EntityQueryCriteria<T> criteria);

    /**
     * @param criteria
     * @return never returns null, if not found returns empty list
     */
    public <T extends IEntity> List<T> query(EntityQueryCriteria<T> criteria, AttachLevel attachLevel);

    public <T extends IEntity> ICursorIterator<T> query(String encodedCursorReference, EntityQueryCriteria<T> criteria, AttachLevel attachLevel);

    public <T extends IEntity> List<Key> queryKeys(EntityQueryCriteria<T> criteria);

    public <T extends IEntity> ICursorIterator<Key> queryKeys(String encodedCursorReference, EntityQueryCriteria<T> criteria);

    public <T extends IEntity> int count(EntityQueryCriteria<T> criteria);

    public <T extends IEntity> boolean exists(Class<T> entityClass, Key primaryKey);

    public <T extends IEntity> boolean exists(EntityQueryCriteria<T> criteria);

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
     * Only delete specified entities row, does not affect Owned entities e.g. no Cascade
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
