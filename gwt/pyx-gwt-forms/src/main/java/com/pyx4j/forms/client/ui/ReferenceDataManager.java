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
 * Created on Jan 31, 2010
 * @author vlads
 */
package com.pyx4j.forms.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityCriteriaFilter;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.rpc.ReferenceDataService;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.rpc.client.RecoverableAsyncCallback;
import com.pyx4j.security.client.BehaviorChangeEvent;
import com.pyx4j.security.client.BehaviorChangeHandler;
import com.pyx4j.security.client.ClientSecurityController;

/**
 * Cache Reference Data
 */
public class ReferenceDataManager {

    private static final Logger log = LoggerFactory.getLogger(ReferenceDataManager.class);

    private static final Map<EntityQueryCriteria<?>, List<? extends IEntity>> cache = new HashMap<>();

    private static final Map<EntityQueryCriteria<?>, List<AsyncLoadingHandler>> concurrentLoad = new HashMap<>();

    private static EventBus eventBus;

    static {
        ClientSecurityController.addSecurityControllerHandler(new BehaviorChangeHandler() {
            @Override
            public void onBehaviorChange(BehaviorChangeEvent event) {
                ReferenceDataManager.invalidate();
            }
        });
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T extends IEntity> AsyncLoadingHandler obtain(EntityQueryCriteria<T> criteria, AsyncCallback<List<T>> handlingCallback) {
        AsyncLoadingHandler handler = new AsyncLoadingHandler((AsyncCallback) handlingCallback);
        final boolean inCache = cache.containsKey(criteria);
        if (!inCache) {
            final EntityQueryCriteria<?> originalCriteria = criteria.iclone();
            // Handle concurrent load
            List<AsyncLoadingHandler> loading = concurrentLoad.get(originalCriteria);
            if (loading == null) {
                loading = new Vector<AsyncLoadingHandler>();
                concurrentLoad.put(originalCriteria, loading);
            }
            loading.add(handler);

            AsyncCallback<EntitySearchResult<? extends IEntity>> callback = new RecoverableAsyncCallback<EntitySearchResult<? extends IEntity>>() {

                @Override
                public void onSuccess(final EntitySearchResult<? extends IEntity> result) {
                    try {
                        cache.put(originalCriteria, result.getData());
                        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {
                                List<AsyncLoadingHandler> handlers = concurrentLoad.remove(originalCriteria);
                                if (handlers == null) {
                                    return;
                                }
                                for (AsyncLoadingHandler handler : handlers) {
                                    try {
                                        handler.onSuccess(result.getData());
                                    } catch (Throwable e) {
                                        log.error("Internal error [UIR]", e);
                                        UncaughtHandler.onUnrecoverableError(e, "UIRonS");
                                    }
                                }
                            }
                        });
                    } catch (Throwable e) {
                        UncaughtHandler.onUnrecoverableError(e, "UIRonS");
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    List<AsyncLoadingHandler> callbacks = concurrentLoad.remove(originalCriteria);
                    if (callbacks != null) {
                        for (AsyncLoadingHandler handler : callbacks) {
                            try {
                                handler.onFailure(caught);
                            } catch (Throwable e) {
                                log.error("Internal error [UIRF]", e);
                                UncaughtHandler.onUnrecoverableError(e, "UIRonF");
                            }
                        }
                    }
                }
            };

            ReferenceDataService service = GWT.create(ReferenceDataService.class);
            service.query(callback, criteria);
        } else {
            handler.onSuccess(cache.get(criteria));
        }
        return handler;
    }

    public static boolean isCached(EntityQueryCriteria<?> criteria) {
        return cache.containsKey(criteria);
    }

    /**
     * Update the reference data when Entity is modified by user.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T extends IEntity> void update(T ent) {
        for (Map.Entry<EntityQueryCriteria<?>, List<? extends IEntity>> me : cache.entrySet()) {
            if (me.getKey().getEntityClass().equals(ent.getObjectClass())) {
                boolean found = false;
                for (IEntity item : me.getValue()) {
                    if (ent.equals(item) && (item != ent)) {
                        // Replace item in List
                        item.setValue(ent.getValue());
                        log.debug("ref entity updated {}", ent);
                        found = true;
                        break;
                    }
                }
                if ((!found) && !me.getKey().hasCriteria()) {
                    ((List<T>) me.getValue()).add(ent);
                }
            }
        }
        if (eventBus != null) {
            eventBus.fireEvent(new ValueChangeEvent(ent.getObjectClass()) {
            });
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T extends IEntity> void created(T ent) {
        for (Map.Entry<EntityQueryCriteria<?>, List<? extends IEntity>> me : cache.entrySet()) {
            if (me.getKey().getEntityClass().equals(ent.getObjectClass())) {
                if (!me.getKey().hasCriteria()) {
                    ((List<T>) me.getValue()).add(ent);
                } else {
                    EntityCriteriaFilter f = new EntityCriteriaFilter(me.getKey());
                    if (f.accept(ent)) {
                        ((List<T>) me.getValue()).add(ent);
                    }
                }
            }
        }
        if (eventBus != null) {
            eventBus.fireEvent(new ValueChangeEvent(ent.getObjectClass()) {
            });
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void remove(IEntity ent) {
        for (Map.Entry<EntityQueryCriteria<?>, List<? extends IEntity>> me : cache.entrySet()) {
            if (me.getKey().getEntityClass().equals(ent.getObjectClass())) {
                me.getValue().remove(ent);
            }
        }
        if (eventBus != null) {
            eventBus.fireEvent(new ValueChangeEvent(ent.getObjectClass()) {
            });
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends IEntity> void invalidate(Class<T> domainClass) {
        EntityMeta meta = EntityFactory.getEntityMeta(domainClass);
        if (meta.getBOClass() != null) {
            domainClass = (Class<T>) meta.getBOClass();
        }
        Iterator<EntityQueryCriteria<?>> it = cache.keySet().iterator();
        while (it.hasNext()) {
            EntityQueryCriteria<?> terms = it.next();
            if (terms.getEntityClass().equals(domainClass)) {
                it.remove();
            }
        }

        if (eventBus != null) {
            eventBus.fireEvent(new ValueChangeEvent<Class<T>>(domainClass) {
            });
        }
    }

    public static void invalidate() {
        cache.clear();
        if (eventBus != null) {
            eventBus.fireEvent(new ValueChangeEvent<Class<IEntity>>(IEntity.class) {
            });
        }
    }

    public static <T extends IEntity> HandlerRegistration addValueChangeHandler(ValueChangeHandler<Class<T>> handler) {
        if (eventBus == null) {
            eventBus = new SimpleEventBus();
        }
        return eventBus.addHandler(ValueChangeEvent.getType(), handler);

    }

    public static <E extends IEntity> EntityDataSource<E> getDataSource() {
        return new EntityDataSource<E>() {

            @Override
            public AsyncLoadingHandler obtain(EntityQueryCriteria<E> criteria, final AsyncCallback<EntitySearchResult<E>> handlingCallback) {
                return ReferenceDataManager.obtain(criteria, new AsyncCallback<List<E>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        handlingCallback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(List<E> data) {
                        EntitySearchResult<E> result = new EntitySearchResult<E>();
                        result.setData(new Vector<E>(data));
                        handlingCallback.onSuccess(result);
                    }
                });
            }

            @Override
            public HandlerRegistration addDataChangeHandler(ValueChangeHandler<Class<E>> handler) {
                return ReferenceDataManager.addValueChangeHandler(handler);
            }

        };
    }
}
