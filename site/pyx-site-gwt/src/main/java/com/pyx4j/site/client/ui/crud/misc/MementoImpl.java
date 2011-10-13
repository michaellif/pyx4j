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
package com.pyx4j.site.client.ui.crud.misc;

import java.util.HashMap;

import com.google.gwt.place.shared.Place;

public class MementoImpl implements IMemento {

    private final HashMap<String, Object> values = new HashMap<String, Object>();

    private Place currentPlace;

    private Place previousPlace;

    public MementoImpl() {
    }

    @Override
    public void setCurrentPlace(Place place) {
        previousPlace = currentPlace;
        currentPlace = place;
    }

    @Override
    public Place getCurrentPlace() {
        return currentPlace;
    }

    @Override
    public boolean mayRestore() {
        if (currentPlace != null && previousPlace != null) {
            return (!isEmpty() && currentPlace.equals(previousPlace));
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public String[] getAttributeKeys() {
        return (String[]) values.keySet().toArray();
    }

    @Override
    public void putString(String key, String value) {
        values.put(key, value);
    }

    @Override
    public Boolean getBoolean(String key) {
        if (values.containsKey(key)) {
            return (Boolean) values.get(key);
        }
        return null;
    }

    @Override
    public void putBoolean(String key, boolean value) {
        values.put(key, value);
    }

    @Override
    public Integer getInteger(String key) {
        if (values.containsKey(key)) {
            return (Integer) values.get(key);
        }
        return null;
    }

    @Override
    public void putInteger(String key, int value) {
        values.put(key, value);
    }

    @Override
    public Float getFloat(String key) {
        if (values.containsKey(key)) {
            return (Float) values.get(key);
        }
        return null;
    }

    @Override
    public void putFloat(String key, float value) {
        values.put(key, value);
    }

    @Override
    public String getString(String key) {
        if (values.containsKey(key)) {
            return (String) values.get(key);
        }
        return null;
    }

    @Override
    public Object getObject(String key) {
        if (values.containsKey(key)) {
            return values.get(key);
        }
        return null;
    }

    @Override
    public void putObject(String key, Object value) {
        values.put(key, value);
    }

}
