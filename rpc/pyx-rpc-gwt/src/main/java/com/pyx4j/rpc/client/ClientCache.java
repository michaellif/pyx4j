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
 * Created on Mar 31, 2016
 * @author vlads
 */
package com.pyx4j.rpc.client;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.commons.Consts;
import com.pyx4j.security.shared.Context;

/**
 * Client cache gets cleaned upon login/logout
 */
public class ClientCache {

    private static class CacheValueHolder<E> {

        private E value;

        private long expirationTimestamp;

        public CacheValueHolder(E value, int timeoutMinutes) {
            this.value = value;
            if (timeoutMinutes < 0) {
                expirationTimestamp = 0;
            } else {
                expirationTimestamp = System.currentTimeMillis() + timeoutMinutes * Consts.MIN2MSEC;
            }
        }

        public boolean expired() {
            if (expirationTimestamp == 0) {
                return false;
            } else {
                return System.currentTimeMillis() > expirationTimestamp;
            }
        }

        public E getValue() {
            return value;
        }

    }

    private static <V> Map<Object, V> getCache() {
        @SuppressWarnings("unchecked")
        Map<Object, V> map = (Map<Object, V>) Context.instance().getVisitTransientAttributes().get("cleintCache");
        if (map == null) {
            map = new HashMap<>();
            Context.instance().getVisitTransientAttributes().put("cleintCache", map);
        }
        return map;
    }

    public static <V> void put(Object key, V value, int timeoutMinutes) {
        getCache().put(key, new CacheValueHolder<V>(value, timeoutMinutes));
    }

    @SuppressWarnings("unchecked")
    private static <V> CacheValueHolder<V> getValueHolder(Object key) {
        return (CacheValueHolder<V>) getCache().get(key);
    }

    public static boolean containsKey(Object key) {
        CacheValueHolder<Object> valueHolder = getValueHolder(key);
        return valueHolder != null && !valueHolder.expired();
    }

    public static <V> V get(Object key) {
        CacheValueHolder<V> valueHolder = getValueHolder(key);
        if (valueHolder != null && !valueHolder.expired()) {
            return valueHolder.getValue();
        } else {
            return null;
        }
    }

    public static void remove(Object key) {
        getCache().remove(key);
    }

    public static void clear() {
        getCache().clear();
    }

}
