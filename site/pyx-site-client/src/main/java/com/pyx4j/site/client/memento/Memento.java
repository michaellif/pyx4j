/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Sep 12, 2011
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.memento;

import java.util.HashMap;

public class Memento {

    private final HashMap<String, Object> values = new HashMap<>();

    public Memento() {
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public void clear() {
        values.clear();
    }

    public String[] getAttributeKeys() {
        return (String[]) values.keySet().toArray();
    }

    public void putString(String key, String value) {
        values.put(key, value);
    }

    public Boolean getBoolean(String key) {
        if (values.containsKey(key)) {
            return (Boolean) values.get(key);
        }
        return null;
    }

    public void putBoolean(String key, boolean value) {
        values.put(key, value);
    }

    public Integer getInteger(String key) {
        if (values.containsKey(key)) {
            return (Integer) values.get(key);
        }
        return null;
    }

    public void putInteger(String key, int value) {
        values.put(key, value);
    }

    public Float getFloat(String key) {
        if (values.containsKey(key)) {
            return (Float) values.get(key);
        }
        return null;
    }

    public void putFloat(String key, float value) {
        values.put(key, value);
    }

    public String getString(String key) {
        if (values.containsKey(key)) {
            return (String) values.get(key);
        }
        return null;
    }

    public Object getObject(String key) {
        if (values.containsKey(key)) {
            return values.get(key);
        }
        return null;
    }

    public void putObject(String key, Object value) {
        values.put(key, value);
    }

}
