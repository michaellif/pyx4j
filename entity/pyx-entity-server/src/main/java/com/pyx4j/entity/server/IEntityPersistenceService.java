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

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.meta.MemberMeta;

public interface IEntityPersistenceService {

    public interface ICursorIterator<T extends IEntity> extends Iterator<T> {

        /**
         * @see com.google.appengine.api.datastore.Cursor.toWebSafeString()
         * @return
         */
        public String encodedCursorRefference();

    }

    public void persist(IEntity entity);

    public void merge(IEntity entity);

    public <T extends IEntity> T retrieve(Class<T> entityClass, long primaryKey);

    public <T extends IEntity> T retrieve(EntityQueryCriteria<T> criteria);

    /**
     * This may be a join with secondary table in RDBMS
     */
    public String getIndexedPropertyName(MemberMeta memberMeta);

    public <T extends IEntity> List<T> query(EntityQueryCriteria<T> criteria);

    public <T extends IEntity> ICursorIterator<T> query(String encodedCursorRefference, EntityQueryCriteria<T> criteria);

    public <T extends IEntity> List<Long> queryKeys(EntityQueryCriteria<T> criteria);

    public <T extends IEntity> int count(EntityQueryCriteria<T> criteria);

    public void delete(IEntity entity);

    public void delete(Class<IEntity> entityClass, long primaryKey);

    public <T extends IEntity> int delete(EntityQueryCriteria<T> criteria);

    public int getDatastoreCallCount();

}
