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
 * Created on Sep 7, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.sessionstorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ForwardingMap;

// Very simplified ObservableMap, find something better in frameworks.
class ObservableMapImpl<K, V> extends ForwardingMap<K, V> {

    private final Map<K, V> delegate;

    private final List<ObservableMapChangeListener> listeners = new ArrayList<>();

    ObservableMapImpl(Map<K, V> map) {
        delegate = map;
    }

    @Override
    protected Map<K, V> delegate() {
        return delegate;
    }

    public void addListener(ObservableMapChangeListener listener) {
        listeners.add(listener);
    }

    private void onChanged() {
        for (ObservableMapChangeListener listener : listeners) {
            listener.onChanged();
        }
    }

    @Override
    public V put(K key, V value) {
        try {
            return super.put(key, value);
        } finally {
            onChanged();
        }
    }

    @Override
    public V remove(Object object) {
        try {
            return super.remove(object);
        } finally {
            onChanged();
        }
    }

}
