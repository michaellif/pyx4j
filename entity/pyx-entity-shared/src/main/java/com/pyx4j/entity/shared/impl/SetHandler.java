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
 * Created on Oct 20, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.Path;

public class SetHandler<OBJECT_TYPE extends IObject<?, ?>> extends ObjectHandler<ISet<OBJECT_TYPE>, Set<Map<String, ?>>> implements ISet<OBJECT_TYPE> {

    public SetHandler(IEntity<?> parent, String fieldName) {
        super(ISet.class, parent, fieldName);
    }

    @Override
    public Path getPath() {
        return new Path(this);
    }

    @Override
    public boolean isNull() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void set(ISet<OBJECT_TYPE> entity) {
        setValue(entity.getValue());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Map<String, ?>> getValue() {
        Map<String, ?> data = getParent().getValue();
        if (data == null) {
            return null;
        } else {
            return (Set<Map<String, ?>>) data.get(getFieldName());
        }
    }

    @Override
    public void setValue(Set<Map<String, ?>> value) {
        Map<String, Object> data = getParent().getValue();
        if (data == null) {
            data = new HashMap<String, Object>();
            getParent().setValue(data);
        }
        data.put(getFieldName(), value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean add(OBJECT_TYPE entity) {
        Map<String, Object> data = getParent().getValue();
        if (data == null) {
            data = new HashMap<String, Object>();
            getParent().setValue(data);
        }
        if (!data.containsKey(getFieldName())) {
            data.put(getFieldName(), new HashSet<Object>());
        }
        return ((HashSet<Object>) data.get(getFieldName())).add(entity.getValue());
    }

    @Override
    public boolean addAll(Collection<? extends OBJECT_TYPE> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clear() {
        Set<?> set = getValue();
        if (set != null) {
            set.clear();
        }
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof IObject<?, ?>) {
            Set<?> set = getValue();
            if (set != null) {
                return set.contains(((IObject<?, ?>) o).getValue());
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Iterator<OBJECT_TYPE> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean remove(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Object[] toArray() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <TT> TT[] toArray(TT[] a) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return getObjectClass().getName() + getValue();
    }

}