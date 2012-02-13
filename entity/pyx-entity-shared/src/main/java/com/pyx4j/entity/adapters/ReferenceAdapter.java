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
 * Created on 2010-10-01
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.adapters;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

/**
 * Use together with @Reference
 * 
 * @see com.pyx4j.entity.annotations.Reference
 * 
 * @param <E>
 */
public interface ReferenceAdapter<E extends IEntity> {

    /**
     * Server would lock-up new referenced entity by this criteria and use it if entity found.
     * 
     * @param newEntity
     * @return Criteria to retrieve one item, null if creation of new Entities is disabled
     */
    public EntityQueryCriteria<E> getMergeCriteria(E newEntity);

    /**
     * Sets additional members when new Entity is created
     * 
     * @param newEntity
     *            new Entity to be persisted
     * @return the modified same newEntity
     */
    public E onEntityCreation(E newEntity);
}
