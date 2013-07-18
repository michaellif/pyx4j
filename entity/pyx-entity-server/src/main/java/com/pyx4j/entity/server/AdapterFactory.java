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
 * Created on 2010-11-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.adapters.EntityModificationAdapter;
import com.pyx4j.entity.adapters.IndexAdapter;
import com.pyx4j.entity.adapters.MemberModificationAdapter;
import com.pyx4j.entity.adapters.PersistenceAdapter;

public class AdapterFactory {

    private static Map<Class<? extends IndexAdapter<?>>, IndexAdapter<?>> indexAdapters;

    private static Map<Class<? extends MemberModificationAdapter<?>>, MemberModificationAdapter<?>> memberModificationAdapters;

    private static Map<Class<? extends EntityModificationAdapter<?>>, EntityModificationAdapter<?>> entityModificationAdapters;

    private static Map<Class<? extends PersistenceAdapter<?, ?>>, PersistenceAdapter<?, ?>> persistenceAdapters;

    public static IndexAdapter<?> getIndexAdapter(Class<? extends IndexAdapter<?>> adapterClass) {
        IndexAdapter<?> adapter = null;
        if (indexAdapters == null) {
            indexAdapters = new HashMap<Class<? extends IndexAdapter<?>>, IndexAdapter<?>>();
        } else {
            adapter = indexAdapters.get(adapterClass);
        }
        if (adapter == null) {
            if (adapterClass.isInterface()) {
                adapter = ServerSideFactory.create(adapterClass);
            } else {
                try {
                    adapter = adapterClass.newInstance();
                } catch (InstantiationException e) {
                    throw new Error(e);
                } catch (IllegalAccessException e) {
                    throw new Error(e);
                }
            }
            indexAdapters.put(adapterClass, adapter);
        }
        return adapter;
    }

    public static MemberModificationAdapter<?> getMemberModificationAdapter(Class<? extends MemberModificationAdapter<?>> adapterClass) {
        MemberModificationAdapter<?> adapter = null;
        if (memberModificationAdapters == null) {
            memberModificationAdapters = new HashMap<Class<? extends MemberModificationAdapter<?>>, MemberModificationAdapter<?>>();
        } else {
            adapter = memberModificationAdapters.get(adapterClass);
        }
        if (adapter == null) {
            if (adapterClass.isInterface()) {
                adapter = ServerSideFactory.create(adapterClass);
            } else {
                try {
                    adapter = adapterClass.newInstance();
                } catch (InstantiationException e) {
                    throw new Error(e);
                } catch (IllegalAccessException e) {
                    throw new Error(e);
                }
            }
            memberModificationAdapters.put(adapterClass, adapter);
        }
        return adapter;
    }

    public static EntityModificationAdapter<?> getEntityModificationAdapters(Class<? extends EntityModificationAdapter<?>> adapterClass) {
        EntityModificationAdapter<?> adapter = null;
        if (entityModificationAdapters == null) {
            entityModificationAdapters = new HashMap<Class<? extends EntityModificationAdapter<?>>, EntityModificationAdapter<?>>();
        } else {
            adapter = entityModificationAdapters.get(adapterClass);
        }
        if (adapter == null) {
            if (adapterClass.isInterface()) {
                adapter = ServerSideFactory.create(adapterClass);
            } else {
                try {
                    adapter = adapterClass.newInstance();
                } catch (InstantiationException e) {
                    throw new Error(e);
                } catch (IllegalAccessException e) {
                    throw new Error(e);
                }
            }
            entityModificationAdapters.put(adapterClass, adapter);
        }
        return adapter;
    }

    public static PersistenceAdapter<?, ?> getPersistenceAdapter(Class<? extends PersistenceAdapter<?, ?>> adapterClass) {
        PersistenceAdapter<?, ?> adapter = null;
        if (persistenceAdapters == null) {
            persistenceAdapters = new HashMap<Class<? extends PersistenceAdapter<?, ?>>, PersistenceAdapter<?, ?>>();
        } else {
            adapter = persistenceAdapters.get(adapterClass);
        }
        if (adapter == null) {
            if (adapterClass.isInterface()) {
                adapter = ServerSideFactory.create(adapterClass);
            } else {
                try {
                    adapter = adapterClass.newInstance();
                } catch (InstantiationException e) {
                    throw new Error(e);
                } catch (IllegalAccessException e) {
                    throw new Error(e);
                }
            }
            persistenceAdapters.put(adapterClass, adapter);
        }
        return adapter;
    }
}
