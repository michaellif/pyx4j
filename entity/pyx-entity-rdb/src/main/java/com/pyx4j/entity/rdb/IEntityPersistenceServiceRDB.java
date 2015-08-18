/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Aug 27, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.rdb.cfg.Configuration.MultitenancyType;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.IEntityPersistenceServiceExt;

public interface IEntityPersistenceServiceRDB extends IEntityPersistenceService, IEntityPersistenceServiceExt {

    public DatabaseType getDatabaseType();

    public String getDatabaseName();

    public MultitenancyType getMultitenancyType();

    public void ensureSchemaModel(Iterable<Class<? extends IEntity>> classes);

    public void resetMapping();

    public boolean isTableExists(Class<? extends IEntity> entityClass);

    public void dropTable(Class<? extends IEntity> entityClass);

    public PersistenceRuntimeInfo getPersistenceRuntime();

}
