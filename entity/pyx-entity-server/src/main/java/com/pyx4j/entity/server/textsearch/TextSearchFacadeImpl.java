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
import com.pyx4j.entity.shared.ITextSearchIndex;

public class TextSearchFacadeImpl implements TextSearchFacade {

    @Override
    public <T extends IEntity, E extends IEntity> void registerUpdateChain(Class<T> entityClass, Class<? extends ITextSearchIndex<E>> indexClass,
            UpdateChain<T, E> updateChain) {
        TextSearchIndexManager.instance().registerUpdateChain(entityClass, indexClass, updateChain);
    }

    @Override
    public <E extends IEntity> void registerUpdateRule(Class<? extends ITextSearchIndex<E>> indexClass, Class<? extends KeywordUpdateRule<E>> ruleClass) {
        TextSearchIndexManager.instance().registerUpdateRule(indexClass, ruleClass);
    }

    @Override
    public <E extends IEntity> void queueIndexUpdate(E entity) {
        TextSearchIndexManager.instance().queueIndexUpdate(entity);
    }

    @Override
    public Collection<Class<? extends IEntity>> getIndexedEntityClasses() {
        return TextSearchIndexManager.instance().getIndexedEntityClasses();
    }

    @Override
    public void reset() {
        TextSearchIndexManager.instance().reset();
    }

    @Override
    public void flushQueue() {
        TextSearchIndexManager.instance().getQueue().flushQueue();
    }

    @Override
    public void shutdown() {
        TextSearchIndexManager.instance().getQueue().shutdown();
        TextSearchIndexManager.instance().reset();
    }

    @Override
    public <E extends IEntity> void updateIndex(E entity) {
        TextSearchIndexManager.instance().updateIndex(entity);
    }

}
