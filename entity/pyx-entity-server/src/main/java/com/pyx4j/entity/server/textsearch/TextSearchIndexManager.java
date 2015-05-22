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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.textsearch.TextSearchFacade.KeywordUpdateRule;
import com.pyx4j.entity.server.textsearch.TextSearchFacade.UpdateChain;
import com.pyx4j.entity.shared.ITextSearchIndex;
import com.pyx4j.entity.shared.TextSearchDocument;
import com.pyx4j.server.contexts.NamespaceManager;

class TextSearchIndexManager {

    private static Logger log = LoggerFactory.getLogger(TextSearchIndexManager.class);

    private static class UpdateChainData<T extends IEntity> {

        Class<? extends ITextSearchIndex<T>> indexClass;

        UpdateChain<? extends IEntity, T> updateChain;

        public UpdateChainData(Class<? extends ITextSearchIndex<T>> indexClass, UpdateChain<? extends IEntity, T> updateChain) {
            this.indexClass = indexClass;
            this.updateChain = updateChain;
        }

    }

    private Map<Class<? extends IEntity>, List<UpdateChainData<? extends IEntity>>> chains = new HashMap<>();

    private Map<Class<? extends ITextSearchIndex<?>>, Class<? extends KeywordUpdateRule<?>>> updateRules = new HashMap<>();

    private TextSearchIndexUpdateQueue queue = new TextSearchIndexUpdateQueue();

    private TextSearchIndexManager() {
    }

    private static class SingletonHolder {
        public static final TextSearchIndexManager INSTANCE = new TextSearchIndexManager();
    }

    static TextSearchIndexManager instance() {
        return SingletonHolder.INSTANCE;
    }

    private void addUpdateChainData(Class<? extends IEntity> entityClass, UpdateChainData<? extends IEntity> updateChainData) {
        List<UpdateChainData<? extends IEntity>> classChains = chains.get(entityClass);
        if (classChains == null) {
            classChains = new ArrayList<>();
            chains.put(entityClass, classChains);
        }
        classChains.add(updateChainData);
    }

    public <T extends IEntity, E extends IEntity> void registerUpdateChain(Class<T> entityClass, Class<? extends ITextSearchIndex<E>> indexClass,
            UpdateChain<T, E> updateChain) {
        addUpdateChainData(entityClass, new UpdateChainData<E>(indexClass, updateChain));
    }

    public <E extends IEntity> void registerUpdateRule(Class<? extends ITextSearchIndex<E>> indexClass, Class<? extends KeywordUpdateRule<E>> ruleClass) {
        addUpdateChainData(EntityFactory.getEntityPrototype(indexClass).owner().getValueClass(), new UpdateChainData<E>(indexClass, null));

        if (updateRules.containsKey(indexClass)) {
            throw new Error("Duplicate rule definition for index " + indexClass.getName());
        }
        updateRules.put(indexClass, ruleClass);
    }

    public Collection<Class<? extends IEntity>> getIndexedEntityClasses() {
        List<Class<? extends IEntity>> classes = new ArrayList<>();
        for (Entry<Class<? extends IEntity>, List<UpdateChainData<? extends IEntity>>> chainEntry : chains.entrySet()) {
            for (UpdateChainData<? extends IEntity> updateChainData : chainEntry.getValue()) {
                if (updateChainData.updateChain == null) {
                    classes.add(chainEntry.getKey());
                    break;
                }
            }
        }
        return Collections.unmodifiableCollection(classes);
    }

    public <E extends IEntity> void queueIndexUpdate(final E entity) {
        List<UpdateChainData<? extends IEntity>> classChains = chains.get(entity.getValueClass());
        if (classChains == null) {
            throw new Error("No registered chains for class " + entity.getValueClass().getName());
        }

        Persistence.service().addTransactionCompletionHandler(new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() {
                getQueue().queue(entity.createIdentityStub(), NamespaceManager.getNamespace());
                return null;
            }
        });

    }

    TextSearchIndexUpdateQueue getQueue() {
        return queue;
    }

    public <E extends IEntity> void updateIndex(E entity) {
        List<UpdateChainData<? extends IEntity>> classChains = chains.get(entity.getValueClass());
        for (UpdateChainData<? extends IEntity> updateChainData : classChains) {
            update(entity, updateChainData.indexClass);
        }
    }

    public <E extends IEntity> void updateAllIndexes(E entity) {
        List<UpdateChainData<? extends IEntity>> classChains = chains.get(entity.getValueClass());
        for (UpdateChainData<? extends IEntity> updateChainData : classChains) {
            if (updateChainData.updateChain != null) {
                @SuppressWarnings("unchecked")
                EntityQueryCriteria<IEntity> criteria = ((UpdateChain<E, IEntity>) updateChainData.updateChain).criteria(entity);
                for (IEntity indexed : Persistence.service().query(criteria)) {
                    update(indexed, updateChainData.indexClass);
                }
            } else {
                Persistence.service().retrieve(entity);
                update(entity, updateChainData.indexClass);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void update(IEntity indexed, Class<? extends ITextSearchIndex<? extends IEntity>> indexClass) {
        Class<? extends KeywordUpdateRule<?>> updaterClass = updateRules.get(indexClass);
        KeywordUpdateRule<IEntity> updater;
        try {
            updater = (KeywordUpdateRule<IEntity>) updaterClass.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new Error(e);
        }

        EntityQueryCriteria<ITextSearchIndex<IEntity>> criteria = new EntityQueryCriteria<>((Class<ITextSearchIndex<IEntity>>) indexClass);
        criteria.eq(criteria.proto().owner(), indexed);

        ITextSearchIndex<IEntity> index = Persistence.service().retrieve(criteria);
        if (index == null) {
            index = EntityFactory.create((Class<ITextSearchIndex<IEntity>>) indexClass);
            index.owner().set(indexed);
        }

        if (index.keywords().isNull()) {
            index.keywords().setValue(new TextSearchDocument());
        }
        index.keywords().getValue().setText(updater.buildIndex(indexed));

        Persistence.service().persist(index);
    }
}
