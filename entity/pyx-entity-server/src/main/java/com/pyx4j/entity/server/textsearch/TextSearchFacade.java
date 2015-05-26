/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on May 14, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.textsearch;

import java.util.Collection;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.ITextSearchIndex;

/**
 * Facade to manage automatic index updates.
 */
public interface TextSearchFacade {

    /**
     * Rule to find Entities to be updated. Index to update is defined in registerUpdateChain call.
     *
     * @param <T>
     *            trigger entity
     * @param <E>
     *            entity with index to be updated
     */
    public interface UpdateChain<T extends IEntity, E extends IEntity> {

        public EntityQueryCriteria<E> criteria(T triggerEntity);
    }

    /**
     * Register the rule to find Entities to be updated once the trigger entity is updated.
     * N.B. You still need to use @UpdateTextSearchIndexModificationAdapter or create your own create EntityModificationAdapter to call queueIndexUpdate.
     */
    <T extends IEntity, E extends IEntity> void registerUpdateChain(Class<T> entityClass, Class<? extends ITextSearchIndex<E>> indexClass,
            UpdateChain<T, E> updateChain);

    /**
     * Function to create text document for indexing entity <E>.
     * The space separated keywords.
     */
    public interface KeywordUpdateRule<E extends IEntity> {

        public String buildIndex(E entity);
    }

    <E extends IEntity> void registerUpdateRule(Class<? extends ITextSearchIndex<E>> indexClass, Class<? extends KeywordUpdateRule<E>> ruleClass);

    /**
     * This will trigger UpdateChain asynchronously.
     *
     * Need to have UpdateChain registered for this entityClass or this entity will update its own indexes. Or you will get Error
     */
    <E extends IEntity> void queueIndexUpdate(E entity);

    void reset();

    void flushQueue();

    void shutdown();

    Collection<Class<? extends IEntity>> getIndexedEntityClasses();

    <E extends IEntity> void updateIndex(E entity);

}
