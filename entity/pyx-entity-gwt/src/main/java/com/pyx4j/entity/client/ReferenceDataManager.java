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
 * @version $Id$
 */
package com.pyx4j.entity.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RecoverableAsyncCallback;

/**
 * Cache Reference Data
 */
public class ReferenceDataManager {

    private static final Logger log = LoggerFactory.getLogger(ReferenceDataManager.class);

    private static final Map<EntityQueryCriteria<?>, List<? extends IEntity>> cache = new HashMap<EntityQueryCriteria<?>, List<? extends IEntity>>();

    private static final Map<EntityQueryCriteria<?>, List<AsyncCallback<List<?>>>> concurrentLoad = new HashMap<EntityQueryCriteria<?>, List<AsyncCallback<List<?>>>>();

    private static EventBus eventBus;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T extends IEntity> void obtain(EntityQueryCriteria<T> criteria, AsyncCallback<List<T>> handlingCallback, boolean background) {
        obtainImpl((EntityQueryCriteria<?>) criteria, (AsyncCallback) handlingCallback, background);
    }

    /**
     * The second function to avoid Generics problem
     */
    private static void obtainImpl(EntityQueryCriteria<?> criteria, AsyncCallback<List<?>> handlingCallback, boolean background) {
        final boolean inCache = cache.containsKey(criteria);
        if (!inCache) {
            final EntityQueryCriteria<?> originalCriteria = criteria.iclone();
            // Handle concurrent load
            List<AsyncCallback<List<?>>> loading = concurrentLoad.get(originalCriteria);
            if (loading == null) {
                loading = new Vector<AsyncCallback<List<?>>>();
                loading.add(handlingCallback);
                concurrentLoad.put(originalCriteria, loading);
            } else {
                loading.add(handlingCallback);
                return;
            }

            AsyncCallback<Vector<? extends IEntity>> callback = new RecoverableAsyncCallback<Vector<? extends IEntity>>() {

                @Override
                public void onSuccess(Vector<? extends IEntity> result) {
                    try {
                        cache.put(originalCriteria, result);
                        List<AsyncCallback<List<?>>> callbacks = concurrentLoad.remove(originalCriteria);
                        for (AsyncCallback<List<?>> cb : callbacks) {
                            try {
                                cb.onSuccess(result);
                            } catch (Throwable e) {
                                log.error("Internal error [UIR]", e);
                                UncaughtHandler.onUnrecoverableError(e, "UIRonS");
                            }
                        }
                    } catch (Throwable e) {
                        UncaughtHandler.onUnrecoverableError(e, "UIRonS");
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    List<AsyncCallback<List<?>>> callbacks = concurrentLoad.remove(originalCriteria);
                    for (AsyncCallback<List<?>> cb : callbacks) {
                        try {
                            cb.onFailure(caught);
                        } catch (Throwable e) {
                            log.error("Internal error [UIRF]", e);
                            UncaughtHandler.onUnrecoverableError(e, "UIRonF");
                        }
                    }
                }
            };

            if (background) {
                RPCManager.executeBackground(EntityServices.Query.class, criteria, callback);
            } else {
                RPCManager.execute(EntityServices.Query.class, criteria, callback);
            }
        } else {
            handlingCallback.onSuccess(cache.get(criteria));
        }
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

    public static <T extends IEntity> void invalidate(Class<T> domain) {
        Iterator<EntityQueryCriteria<?>> it = cache.keySet().iterator();
        while (it.hasNext()) {
            EntityQueryCriteria<?> terms = it.next();
            if (terms.getEntityClass().equals(domain)) {
                it.remove();
            }
        }

        if (eventBus != null) {
            eventBus.fireEvent(new ValueChangeEvent<Class<T>>(domain) {
            });
        }
    }

    public static void invalidate() {
        cache.clear();
    }

    public static <T extends IEntity> HandlerRegistration addValueChangeHandler(ValueChangeHandler<Class<T>> handler) {
        if (eventBus == null) {
            eventBus = new SimpleEventBus();
        }
        return eventBus.addHandler(ValueChangeEvent.getType(), handler);

    }
}
