/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Apr 2, 2014
 * @author vlads
 */
package com.pyx4j.unit.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Safe to use as simple Singleton sequence instance.
 * 
 * Example:
 * 
 * public final static UniqueLong singleton = new UniqueLong("accountNumbers");
 * or
 * singleton = UniqueLong.getInstance("accountNumbers");
 * 
 */
public class UniqueLong {

    private static final Map<String, UniqueLong> registry = new HashMap<>();

    private AtomicLong atomicInteger;

    public UniqueLong(String reservedName) {
        synchronized (registry) {
            UniqueLong sameName = registry.get(reservedName);
            if (sameName != null) {
                this.atomicInteger = sameName.atomicInteger;
            } else {
                this.atomicInteger = new AtomicLong();
            }
            registry.put(reservedName, this);
        }
    }

    public static UniqueLong getInstance(String reservedName) {
        synchronized (registry) {
            UniqueLong singleton = registry.get(reservedName);
            if (singleton != null) {
                return singleton;
            } else {
                return new UniqueLong(reservedName);
            }
        }
    }

    public long next() {
        return atomicInteger.getAndIncrement();
    }

    public String nextAsString() {
        return String.valueOf(next());
    }

    @Override
    public String toString() {
        return nextAsString();
    }

}
