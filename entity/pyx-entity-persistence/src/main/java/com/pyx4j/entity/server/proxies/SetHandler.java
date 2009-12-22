/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Oct 20, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.server.proxies;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IOwnedMember;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.Path;

public class SetHandler<T extends IEntity<?, ?>> extends EntityHandler<T> implements ISet<IEntity<T, ?>, IObject<?>> {

    SetHandler(Class<T> clazz, ObjectHandler<?> parentHandler, IObject<?> parent, String fieldName) {
        super(clazz, parentHandler, parent, fieldName);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Object.class) || method.getDeclaringClass().isAssignableFrom(ISet.class)) {
            return method.invoke(this, args);
        } else if (method.getDeclaringClass().equals(IOwnedMember.class)) {
            if ("getParent".equals(method.getName())) {
                return getParent();
            } else {
                return null;
            }

        } else {
            return null;
        }
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
    public void set(ISet<IEntity<T, ?>, IObject<?>> entity) {
        setValue(entity.getValue());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<?> getValue() {
        Map<String, ?> data = getParentHandler().getValue();
        if (data == null) {
            return null;
        } else {
            return (Set<IEntity<?, ?>>) data.get(getFieldName());
        }
    }

    @Override
    public void setValue(Set<?> value) {
        Map<String, Object> data = getParentHandler().getValue();
        if (data == null) {
            data = new HashMap<String, Object>();
            getParentHandler().setValue(data);
        }
        data.put(getFieldName(), value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean add(IEntity<T, ?> entity) {
        Map<String, Object> data = getParentHandler().getValue();
        if (data == null) {
            data = new HashMap<String, Object>();
            getParentHandler().setValue(data);
        }
        if (!data.containsKey(getFieldName())) {
            data.put(getFieldName(), new HashSet<Object>());
        }
        return ((HashSet<Object>) data.get(getFieldName())).add(entity.getValue());
    }

    @Override
    public boolean addAll(Collection<? extends IEntity<T, ?>> c) {
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
        if (o instanceof IEntity<?, ?>) {
            Set<?> set = getValue();
            if (set != null) {
                return set.contains(((IEntity<?, ?>) o).getValue());
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
    public Iterator<IEntity<T, ?>> iterator() {
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
    public <T> T[] toArray(T[] a) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return getEntityClass().getSimpleName() + getValue();
    }

}