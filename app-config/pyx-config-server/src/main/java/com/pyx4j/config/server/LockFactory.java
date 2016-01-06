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
 * Created on Sep 22, 2015
 * @author vlads
 */
package com.pyx4j.config.server;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.MapMaker;

public class LockFactory<K> {

    private final ReentrantLock mapLock = new ReentrantLock();

    private final Map<K, ReentrantLock> entityMap = new MapMaker().concurrencyLevel(1).weakValues().<K, ReentrantLock> makeMap();

    public Lock get(K key) {
        ReentrantLock result = entityMap.get(key);
        if (result == null) {
            mapLock.lock();
            try {
                result = entityMap.get(key);
                if (result == null) {
                    result = new ReentrantLock();
                    entityMap.put(key, result);
                }
            } finally {
                if (mapLock.isHeldByCurrentThread()) {
                    mapLock.unlock();
                }
            }
        }
        return result;
    }

}
