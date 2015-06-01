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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Safe to use as simple Singleton sequence instance.
 *
 * Example:
 *
 * public final static UniqueInteger singleton = new UniqueInteger("accountNumbers");
 * or
 * singleton = UniqueInteger.getInstance("accountNumbers");
 *
 */
public class UniqueInteger {

    private static final Map<String, UniqueInteger> registry = new HashMap<>();

    private String reservedName;

    private AtomicInteger atomicInteger;

    public UniqueInteger(String reservedName) {
        this.reservedName = reservedName;
        synchronized (registry) {
            UniqueInteger sameName = registry.get(reservedName);
            if (sameName != null) {
                this.atomicInteger = sameName.atomicInteger;
            } else {
                this.atomicInteger = new AtomicInteger();
            }
            registry.put(reservedName, this);
        }
    }

    public static UniqueInteger getInstance(String reservedName) {
        synchronized (registry) {
            UniqueInteger singleton = registry.get(reservedName);
            if (singleton != null) {
                return singleton;
            } else {
                return new UniqueInteger(reservedName);
            }
        }
    }

    public int next() {
        return atomicInteger.getAndIncrement();
    }

    public String nextAsString() {
        return String.valueOf(next());
    }

    public String nextIdAsString() {
        return reservedName + String.valueOf(next());
    }

    @Override
    public String toString() {
        return nextAsString();
    }

}
