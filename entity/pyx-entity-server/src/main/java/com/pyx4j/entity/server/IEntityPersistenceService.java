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

import java.util.List;

import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.IEntity;

public interface IEntityPersistenceService {

    public void persist(IEntity<?> entity);

    public void merge(IEntity<?> entity);

    public <T extends IEntity<?>> T retrieve(Class<T> entityClass, String primaryKey);

    public <T extends IEntity<?>> T retrieve(EntityCriteria<T> criteria);

    public <T extends IEntity<?>> List<T> query(EntityCriteria<T> criteria);

    public <T extends IEntity<?>> List<String> queryKeys(EntityCriteria<T> criteria);

    public <T extends IEntity<?>> int count(EntityCriteria<T> criteria);

    public void delete(IEntity<?> entity);

    public void delete(Class<?> entityClass, String primaryKey);

    public <T extends IEntity<?>> void delete(EntityCriteria<T> criteria);

}
