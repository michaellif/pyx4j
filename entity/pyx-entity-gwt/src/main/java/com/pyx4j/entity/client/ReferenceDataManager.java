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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RecoverableAsyncCallback;

/**
 * Cache Reference Data
 */
public class ReferenceDataManager {

    private static final Logger log = LoggerFactory.getLogger(ReferenceDataManager.class);

    private static final Map<EntityCriteria<?>, List<IEntity<?>>> cache = new HashMap<EntityCriteria<?>, List<IEntity<?>>>();

    private static final Map<EntityCriteria<?>, List<AsyncCallback<List<?>>>> concurrentLoad = new HashMap<EntityCriteria<?>, List<AsyncCallback<List<?>>>>();

    @SuppressWarnings("unchecked")
    public static <T extends IEntity<T>> void obtain(EntityCriteria<T> criteria, AsyncCallback<List<T>> handlingCallback, boolean background) {
        obtainImpl((EntityCriteria<?>) criteria, (AsyncCallback) handlingCallback, background);
    }

    /**
     * The second function to avoid Generics problem
     */
    private static void obtainImpl(final EntityCriteria<?> criteria, AsyncCallback<List<?>> handlingCallback, boolean background) {
        final boolean inCache = cache.containsKey(criteria);
        if (!inCache) {
            // Handle concurrent load
            List<AsyncCallback<List<?>>> loading = concurrentLoad.get(criteria);
            if (loading == null) {
                loading = new Vector<AsyncCallback<List<?>>>();
                loading.add(handlingCallback);
                concurrentLoad.put(criteria, loading);
            } else {
                loading.add(handlingCallback);
                return;
            }

            AsyncCallback callback = new RecoverableAsyncCallback<List<IEntity<?>>>() {

                public void onSuccess(List<IEntity<?>> result) {
                    try {
                        cache.put(criteria, result);
                        List<AsyncCallback<List<?>>> callbacks = concurrentLoad.remove(criteria);
                        for (AsyncCallback<List<?>> cb : callbacks) {
                            try {
                                cb.onSuccess(result);
                            } catch (Throwable e) {
                                log.error("Internal error [UIR]", e);
                            }
                        }
                    } catch (Throwable e) {
                        UncaughtHandler.onUnrecoverableError(e, "UIRonS");
                    }
                }

                public void onFailure(Throwable caught) {
                    List<AsyncCallback<List<?>>> callbacks = concurrentLoad.remove(criteria);
                    for (AsyncCallback<List<?>> cb : callbacks) {
                        try {
                            cb.onFailure(caught);
                        } catch (Throwable e) {
                            log.error("Internal error [UIRF]", e);
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

    public static boolean isCached(EntityCriteria<?> criteria) {
        return cache.containsKey(criteria);
    }

    /**
     * Update the reference data when Entity is modified by user.
     */
    public static void update(IEntity<?> ent) {
        for (Map.Entry<EntityCriteria<?>, List<IEntity<?>>> me : cache.entrySet()) {
            if (me.getKey().getDomainName().equals(ent.getObjectClass().getName())) {
                for (IEntity<?> item : me.getValue()) {
                    if (ent.equals(item) && (item != ent)) {
                        // Replace item in List
                        item.setValue(ent.getValue());
                        break;
                    }
                }
            }
        }
    }

    public static void remove(IEntity<?> ent) {
        for (Map.Entry<EntityCriteria<?>, List<IEntity<?>>> me : cache.entrySet()) {
            if (me.getKey().getDomainName().equals(ent.getObjectClass().getName())) {
                me.getValue().remove(ent);
            }
        }
    }

    public static void invalidate(String domain) {
        Iterator<EntityCriteria<?>> it = cache.keySet().iterator();
        while (it.hasNext()) {
            EntityCriteria<?> terms = it.next();
            if (terms.getDomainName().equals(domain)) {
                it.remove();
            }
        }
    }

    public static void invalidate() {
        cache.clear();
    }
}
