/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 29, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.Path;

public abstract class SharedEntityHandler<OBJECT_TYPE extends IEntity<?>> extends ObjectHandler<OBJECT_TYPE, Map<String, Object>> implements
        IEntity<OBJECT_TYPE> {

    private Map<String, Object> data;

    private transient boolean membersListCreated;

    protected transient final HashMap<String, IObject<?, ?>> meta = new HashMap<String, IObject<?, ?>>();

    public SharedEntityHandler(Class<OBJECT_TYPE> clazz) {
        super(clazz);
        data = new HashMap<String, Object>();
    }

    /**
     * Creation of Member object
     * 
     * @param clazz
     * @param parent
     * @param fieldName
     */
    public SharedEntityHandler(Class<? extends IObject> clazz, IEntity<?> parent, String fieldName) {
        super(clazz, parent, fieldName);
    }

    protected abstract void lazyCreateMembersNamesList();

    protected void createMemeber(String name) {
        if (!meta.containsKey(name)) {
            meta.put(name, null);
        }
    }

    protected abstract IObject<?, ?> lazyCreateMember(String name);

    protected <T> IPrimitive<T> lazyCreateMemberIPrimitive(String name, Class<T> primitiveValueClass) {
        return new PrimitiveHandler<T>(this, name, primitiveValueClass);
    }

    protected ISet<?> lazyCreateMemberISet(String name) {
        return new SetHandler(this, name);
    }

    @Override
    public Map<String, Object> getValue() {
        return data;
    }

    @Override
    public void setValue(Map<String, Object> value) {
        this.data = value;
        getParent().getValue().put(getFieldName(), value);
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
    public void set(OBJECT_TYPE entity) {
        setValue(entity.getValue());
    }

    @Override
    public synchronized Set<String> getMemberNames() {
        if (!membersListCreated) {
            lazyCreateMembersNamesList();
            membersListCreated = true;
        }
        return meta.keySet();
    }

    @Override
    public IObject<?, ?> getMember(String name) {
        IObject<?, ?> member = meta.get(name);
        if (member == null) {
            member = lazyCreateMember(name);
            meta.put(name, member);
        }
        return member;
    }

    /**
     * TODO use data map directly. No need to create Member
     */
    @Override
    public Object getMemberValue(String name) {
        IObject<?, ?> i = getMember(name);
        return i.getValue();
    }

    /**
     * TODO use data map directly. No need to create Member
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setMemberValue(String name, Object value) {
        IObject<?, Object> i = (IObject<?, Object>) getMember(name);
        i.setValue(value);
    }

    @Override
    public String toString() {
        return getObjectClass().getName() + getValue();
    }
}
