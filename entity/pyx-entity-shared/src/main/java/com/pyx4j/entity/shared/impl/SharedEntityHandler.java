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
 * Created on Dec 29, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.IFullDebug;
import com.pyx4j.commons.LoopCounter;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.validator.Validator;

public abstract class SharedEntityHandler extends ObjectHandler<Map<String, Object>> implements IEntity, IFullDebug {

    private static final long serialVersionUID = -7590484996971406115L;

    private Map<String, Object> data;

    private transient EntityMeta entityMeta;

    protected transient HashMap<String, IObject<?>> members;

    /**
     * N.B. Default initialization during serialization to 'false'.
     */
    private transient final boolean delegateValue;

    /**
     * Creation of stand alone or member Entity
     * 
     * @param clazz
     * @param parent
     * @param fieldName
     */
    @SuppressWarnings("unchecked")
    public SharedEntityHandler(Class<? extends IObject> clazz, IObject<?> parent, String fieldName) {
        super(clazz, parent, fieldName);
        delegateValue = (parent != null) && (getOwner() == parent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends IEntity> getValueClass() {
        return (Class<? extends IEntity>) getObjectClass();
    }

    protected abstract IObject<?> lazyCreateMember(String name);

    public <T> IPrimitive<T> lazyCreateMemberIPrimitive(String memberName, Class<T> primitiveValueClass) {
        return new PrimitiveHandler<T>(this, memberName, primitiveValueClass);
    }

    public <T> IPrimitiveSet<T> lazyCreateMemberIPrimitiveSet(String memberName, Class<T> primitiveValueClass) {
        return new PrimitiveSetHandler<T>(this, memberName, primitiveValueClass);
    }

    @SuppressWarnings("unchecked")
    public <T extends IEntity> T lazyCreateMemberIEntity(String memberName, Class<T> valueClass) {
        // Is Bidirectional Relationship ?
        if ((getOwner() != null) && (getEntityMeta().getMemberMeta(memberName).isOwner())) {
            return (T) getOwner();
        }
        return EntityFactory.create(valueClass, this, memberName);
    }

    public <T extends IEntity> ISet<T> lazyCreateMemberISet(String memberName, Class<T> setValueClass) {
        return new SetHandler<T>(this, memberName, setValueClass);
    }

    public <T extends IEntity> IList<T> lazyCreateMemberIList(String memberName, Class<T> setValueClass) {
        return new ListHandler<T>(this, memberName, setValueClass);
    }

    /**
     * Guarantee that data is created before setting the value of member
     */
    protected Map<String, Object> ensureValue() {
        Map<String, Object> v = getValue();
        if (v == null) {
            setValue(v = new EntityValueMap());
        }
        return v;
    }

    @Override
    public Long getPrimaryKey() {
        Map<String, Object> v = getValue();
        if (v == null) {
            return null;
        } else {
            return (Long) v.get(PRIMARY_KEY);
        }
    }

    @Override
    public void setPrimaryKey(Long pk) {
        ensureValue().put(PRIMARY_KEY, pk);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getValue() {
        if (delegateValue) {
            Map<String, Object> v = getOwner().getValue();
            if (v == null) {
                return null;
            } else {
                return (Map<String, Object>) v.get(getFieldName());
            }
        } else {
            return data;
        }
    }

    @Override
    public void setValue(Map<String, Object> value) {
        if ((value != null) && !(value instanceof EntityValueMap)) {
            throw new ClassCastException("Entity expects EntityValueMap as value");
        }
        if (delegateValue) {
            ((SharedEntityHandler) getOwner()).ensureValue().put(getFieldName(), value);
        } else {
            this.data = value;
        }
    }

    /**
     * IEntity equals by value or Map object (e.g. the same map) or value of PK.equals().
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        Map<String, Object> thisValue = this.getValue();
        if ((other == null) || (thisValue == null) || (!(other instanceof IEntity)) || (!this.getClass().equals(other.getClass()))) {
            return false;
        }
        return thisValue.equals(((IEntity) other).getValue());
    }

    @Override
    public int hashCode() {
        Map<String, Object> thisValue = this.getValue();
        if (thisValue == null) {
            return super.hashCode();
        } else {
            return thisValue.hashCode();
        }
    }

    @Override
    public boolean isNull() {
        Map<String, Object> thisValue = this.getValue();
        if ((thisValue == null) || (thisValue.isEmpty())) {
            return true;
        }
        return ((EntityValueMap) thisValue).isNull();
    }

    @Override
    public void set(IEntity entity) {
        //TODO at type safety at runtime.
        setValue(entity.getValue());
    }

    @SuppressWarnings("unchecked")
    @Override
    public EntityMeta getEntityMeta() {
        if (entityMeta == null) {
            // Cache Meta for this class.
            entityMeta = EntityFactory.getEntityMeta((Class<IEntity>) getObjectClass());
        }
        return entityMeta;
    }

    @Override
    public IObject<?> getMember(String memberName) {
        if (members == null) {
            members = new HashMap<String, IObject<?>>();
        }
        IObject<?> member = members.get(memberName);
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
        Map<String, Object> v = getValue();
        if (v == null) {
            return null;
        } else {
            return v.get(memberName);
        }
    }

    @Override
    public Object removeMemberValue(String memberName) {
        Map<String, Object> v = getValue();
        if (v != null) {
            return v.remove(memberName);
        } else {
            return null;
        }
    }

    private final void assertPath(Path path) {
        if (!GWTJava5Helper.getSimpleName(this.getObjectClass()).equals(path.getRootObjectClassName())) {
            throw new IllegalArgumentException("Path of " + path.getRootObjectClassName() + " expected");
        }
    }

    @Override
    public IObject<?> getMember(Path path) {
        assertPath(path);
        IObject<?> obj = this;
        for (String memberName : path.getPathMembers()) {
            //TODO ICollection support
            if (!(obj instanceof IEntity)) {
                throw new RuntimeException("Invalid member in path " + memberName);
            }
            obj = ((IEntity) obj).getMember(memberName);
        }
        return obj;
    }

    @Override
    public Object getValue(Path path) {
        assertPath(path);
        Object value = this.getValue();
        for (String memberName : path.getPathMembers()) {
            if (value == null) {
                return null;
            }
            //TODO ICollection support
            if (!(value instanceof Map<?, ?>)) {
                throw new RuntimeException("Invalid member in path " + memberName);
            }
            value = ((Map<String, Object>) value).get(memberName);
        }
        return value;
    }

    @Override
    public void setValue(Path path, Object value) {
        assertPath(path);
        Map<String, Object> ownerValueMap = ensureValue();
        LoopCounter c = new LoopCounter(path.getPathMembers());
        for (String memberName : path.getPathMembers()) {
            switch (c.next()) {
            case SINGLE:
            case LAST:
                ownerValueMap.put(memberName, value);
                break;
            default:
                Object ownerValue = ownerValueMap.get(memberName);
                if (ownerValue instanceof Map<?, ?>) {
                    ownerValueMap = (Map<String, Object>) ownerValue;
                } else {
                    // ensureValue
                    // TODO ICollection support
                    ownerValueMap.put(memberName, ownerValue = new EntityValueMap());
                    ownerValueMap = (Map<String, Object>) ownerValue;
                }
            }
        }
    }

    /**
     * Use data map directly. No need to create Member
     */
    @Override
    public void setMemberValue(String memberName, Object value) {
        ensureValue().put(memberName, value);
    }

    @Override
    public <T extends IObject<?>> void set(T member, T value) {
        ensureValue().put(member.getFieldName(), value.getValue());
    }

    private Object getMemberStringView(String memberName) {
        if (entityMeta.getMemberMeta(memberName).isEntity()) {
            return ((IEntity) getMember(memberName)).getStringView();
        } else {
            return getMemberValue(memberName);
        }
    }

    @Override
    public String getStringView() {
        List<String> sm = getEntityMeta().getToStringMemberNames();
        switch (sm.size()) {
        case 0:
            return null;
        case 1:
            return CommonsStringUtils.nvl(getMemberStringView(sm.get(0)));
        case 2:
            return CommonsStringUtils.nvl_concat(getMemberStringView(sm.get(0)), getMemberStringView(sm.get(1)), " ");
        default:
            Map<String, Object> entityValue = getValue();
            if (entityValue == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for (String memeberName : sm) {
                Object v = entityValue.get(memeberName);
                if (v != null) {
                    if (sb.length() > 0) {
                        sb.append(" ");
                    }
                    sb.append(getMemberStringView(memeberName));
                }
            }
            return sb.toString();
        }
    }

    //TODO
    @Override
    public List<Validator> getValidators(Path memberPath) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public IEntity cloneEntity() {
        IEntity entity = EntityFactory.create((Class<IEntity>) getObjectClass());
        Map<String, Object> v = getValue();
        if (v != null) {
            Map<String, Object> data2 = new EntityValueMap();
            cloneMap(v, data2);
            entity.setValue(data2);
        }
        return entity;
    }

    @SuppressWarnings("unchecked")
    private void cloneMap(Map<String, Object> src, Map<String, Object> dst) {
        for (Map.Entry<String, Object> me : src.entrySet()) {
            if (me.getValue() instanceof Map<?, ?>) {
                Map<String, Object> data2 = new EntityValueMap();
                cloneMap((Map<String, Object>) me.getValue(), data2);
                dst.put(me.getKey(), data2);
            } else {
                dst.put(me.getKey(), me.getValue());
            }
        }
    }

    @Override
    public String debugString() {
        StringBuilder b = new StringBuilder();
        b.append(getObjectClass().getName()).append(" ");
        Map<String, Object> v = getValue();
        if (v != null) {
            EntityValueMap.dumpMap(b, v, new HashSet<Map>());
        } else {
            b.append("{null}");
        }
        return b.toString();
    }

    @Override
    public String toString() {
        return getObjectClass().getName() + " " + debugString();
    }
}
