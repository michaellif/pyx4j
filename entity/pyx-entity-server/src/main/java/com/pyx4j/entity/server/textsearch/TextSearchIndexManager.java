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

import com.pyx4j.entity.core.AttachLevel;
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

    private static class UpdateChainData<T extends IEntity> {

        //TODO This is not used.  Will need to add optimization in future
        Class<? extends ITextSearchIndex<T>> indexClass;

        UpdateChain<? extends IEntity, T> updateChain;

        public UpdateChainData(Class<? extends ITextSearchIndex<T>> indexClass, UpdateChain<? extends IEntity, T> updateChain) {
            this.indexClass = indexClass;
            this.updateChain = updateChain;
        }

    }

    private Map<Class<? extends IEntity>, List<UpdateChainData<? extends IEntity>>> chains = new HashMap<>();

    private Map<Class<? extends IEntity>, List<Class<? extends ITextSearchIndex<?>>>> indexes = new HashMap<>();

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

    void reset() {
        chains.clear();
        updateRules.clear();
    }

    private void addUpdateChainData(Class<? extends IEntity> entityClass, UpdateChainData<? extends IEntity> updateChainData) {
        List<UpdateChainData<? extends IEntity>> classChains = chains.get(entityClass);
        if (classChains == null) {
            classChains = new ArrayList<>();
            chains.put(entityClass, classChains);
        }
        classChains.add(updateChainData);
    }

    <T extends IEntity, E extends IEntity> void registerUpdateChain(Class<T> entityClass, Class<? extends ITextSearchIndex<E>> indexClass,
            UpdateChain<T, E> updateChain) {
        addUpdateChainData(entityClass, new UpdateChainData<E>(indexClass, updateChain));
    }

    private void addIndex(Class<? extends IEntity> entityClass, Class<? extends ITextSearchIndex<?>> indexClass) {
        List<Class<? extends ITextSearchIndex<?>>> indexClassesList = indexes.get(entityClass);
        if (indexClassesList == null) {
            indexClassesList = new ArrayList<>();
            indexes.put(entityClass, indexClassesList);
        }
        indexClassesList.add(indexClass);
    }

    <E extends IEntity> void registerUpdateRule(Class<? extends ITextSearchIndex<E>> indexClass, Class<? extends KeywordUpdateRule<E>> ruleClass) {
        addIndex(EntityFactory.getEntityPrototype(indexClass).owner().getValueClass(), indexClass);

        if (updateRules.containsKey(indexClass)) {
            throw new Error("Duplicate rule definition for index " + indexClass.getName());
        }
        updateRules.put(indexClass, ruleClass);
    }

    Collection<Class<? extends IEntity>> getIndexedEntityClasses() {
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

    <E extends IEntity> void queueIndexUpdate(final E entity) {
        List<UpdateChainData<? extends IEntity>> classChains = chains.get(entity.getValueClass());
        if (classChains != null) {
            Persistence.service().addTransactionCompletionHandler(new Executable<Void, RuntimeException>() {
                @Override
                public Void execute() {
                    getQueue().queue(NamespaceManager.getNamespace(), entity.createIdentityStub(), true);
                    return null;
                }
            });
        }

        List<Class<? extends ITextSearchIndex<?>>> indexClassesList = indexes.get(entity.getValueClass());
        if (indexClassesList != null) {
            Persistence.service().addTransactionCompletionHandler(new Executable<Void, RuntimeException>() {
                @Override
                public Void execute() {
                    getQueue().queue(NamespaceManager.getNamespace(), entity.createIdentityStub(), false);
                    return null;
                }
            });
        }

        if ((classChains == null) && (indexClassesList == null)) {
            throw new Error("No registered chains or update Rules for class " + entity.getValueClass().getName());
        }
    }

    <E extends IEntity> void queueIndexUpdateChains(E entity) {
        List<UpdateChainData<? extends IEntity>> classChains = chains.get(entity.getValueClass());
        for (UpdateChainData<? extends IEntity> updateChainData : classChains) {
            @SuppressWarnings("unchecked")
            EntityQueryCriteria<IEntity> criteria = ((UpdateChain<E, IEntity>) updateChainData.updateChain).criteria(entity);
            for (IEntity indexed : Persistence.service().query(criteria, AttachLevel.IdOnly)) {
                getQueue().queue(NamespaceManager.getNamespace(), indexed, false);
            }
        }
    }

    TextSearchIndexUpdateQueue getQueue() {
        return queue;
    }

    <E extends IEntity> void updateIndex(E entity) {
        List<Class<? extends ITextSearchIndex<?>>> indexClassesList = indexes.get(entity.getValueClass());
        for (Class<? extends ITextSearchIndex<? extends IEntity>> indexClass : indexClassesList) {
            update(entity, indexClass);
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
