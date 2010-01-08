/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 29, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.validator.Validator;

@SuppressWarnings("serial")
public abstract class SharedEntityHandler<OBJECT_TYPE extends IEntity<?>> extends ObjectHandler<OBJECT_TYPE, Map<String, Object>> implements
        IEntity<OBJECT_TYPE> {

    private Map<String, Object> data;

    private transient boolean membersListCreated;

    protected transient final HashMap<String, IObject<?, ?>> members = new HashMap<String, IObject<?, ?>>();

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
    public SharedEntityHandler(Class<? extends IObject<?, ?>> clazz, IEntity<?> parent, String fieldName) {
        super(clazz, parent, fieldName);
    }

    //TODO Use IEntityMeta
    protected abstract void lazyCreateMembersNamesList();

    protected void createMemeber(String name) {
        if (!members.containsKey(name)) {
            members.put(name, null);
        }
    }

    protected abstract IObject<?, ?> lazyCreateMember(String name);

    public <T> IPrimitive<T> lazyCreateMemberIPrimitive(String name, Class<T> primitiveValueClass) {
        return new PrimitiveHandler<T>(this, name, primitiveValueClass);
    }

    @SuppressWarnings("unchecked")
    public ISet<?> lazyCreateMemberISet(String name) {
        return new SetHandler(this, name);
    }

    private void ensureData() {
        if (data == null) {
            setValue(new HashMap<String, Object>());
        }
    }

    public String getPrimaryKey() {
        if (data == null) {
            return null;
        }
        return (String) data.get(PRIMARY_KEY);
    }

    public void setPrimaryKey(String pk) {
        ensureData();
        data.put(PRIMARY_KEY, pk);
    }

    @Override
    public Map<String, Object> getValue() {
        return data;
    }

    @Override
    public void setValue(Map<String, Object> value) {
        this.data = value;
        if (getParent() != null) {
            getParent().getValue().put(getFieldName(), value);
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
    public void set(OBJECT_TYPE entity) {
        setValue(entity.getValue());
    }

    @Override
    public synchronized Set<String> getMemberNames() {
        if (!membersListCreated) {
            lazyCreateMembersNamesList();
            membersListCreated = true;
        }
        return members.keySet();
    }

    @Override
    public IObject<?, ?> getMember(String memberName) {
        IObject<?, ?> member = members.get(memberName);
        if (member == null) {
            member = lazyCreateMember(memberName);
            members.put(memberName, member);
        }
        return member;
    }

    /**
     * Use data map directly. No need to create Member
     */
    @Override
    public Object getMemberValue(String memberName) {
        // Like Elvis operator
        if (data == null) {
            return null;
        }
        return data.get(memberName);
    }

    /**
     * Use data map directly. No need to create Member
     */
    @Override
    public void setMemberValue(String memberName, Object value) {
        ensureData();
        data.put(memberName, value);
    }

    //TODO
    @Override
    public List<Validator> getValidators(Path memberPath) {
        return null;
    }

    @Override
    public String toString() {
        return getObjectClass().getName() + getValue();
    }
}
