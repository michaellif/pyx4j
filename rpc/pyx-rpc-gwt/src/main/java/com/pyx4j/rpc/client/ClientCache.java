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

import com.pyx4j.security.shared.Context;

/**
 * Client cache gets cleaned upon login/logout
 */
public class ClientCache {

    private static <V> Map<Object, V> getCache() {
        @SuppressWarnings("unchecked")
        Map<Object, V> map = (Map<Object, V>) Context.instance().getVisitTransientAttributes().get("cleintCache");
        if (map == null) {
            map = new HashMap<>();
            Context.instance().getVisitTransientAttributes().put("cleintCache", map);
        }
        return map;
    }

    public static <V> void put(Object key, V value) {
        getCache().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <V> V get(Object key) {
        return (V) getCache().get(key);
    }
}
