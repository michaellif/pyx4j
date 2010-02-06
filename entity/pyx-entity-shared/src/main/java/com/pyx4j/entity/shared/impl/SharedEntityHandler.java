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
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFullDebug;
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

public abstract class SharedEntityHandler<OBJECT_TYPE extends IEntity<?>> extends ObjectHandler<OBJECT_TYPE, Map<String, Object>> implements
        IEntity<OBJECT_TYPE>, IFullDebug {

    private static final long serialVersionUID = -7590484996971406115L;

    private Map<String, Object> data;

    private transient EntityMeta entityMeta;

    protected transient HashMap<String, IObject<?, ?>> members;

    public SharedEntityHandler(Class<OBJECT_TYPE> clazz) {
        super(clazz);
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

    protected abstract IObject<?, ?> lazyCreateMember(String name);

    public <T> IPrimitive<T> lazyCreateMemberIPrimitive(String name, Class<T> primitiveValueClass) {
        return new PrimitiveHandler<T>(this, name, primitiveValueClass);
    }

    public <T> IPrimitiveSet<T> lazyCreateMemberIPrimitiveSet(String name, Class<T> primitiveValueClass) {
        return new PrimitiveSetHandler<T>(this, name, primitiveValueClass);
    }

    public <T extends IEntity<?>> ISet<T> lazyCreateMemberISet(String name, Class<T> setValueClass) {
        return new SetHandler<T>(this, name, setValueClass);
    }

    public <T extends IEntity<?>> IList<T> lazyCreateMemberIList(String name, Class<T> setValueClass) {
        return new ListHandler<T>(this, name, setValueClass);
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

    public String getPrimaryKey() {
        Map<String, Object> v = getValue();
        if (v == null) {
            return null;
        } else {
            return (String) v.get(PRIMARY_KEY);
        }
    }

    public void setPrimaryKey(String pk) {
        ensureValue().put(PRIMARY_KEY, pk);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getValue() {
        if (getParent() != null) {
            Map<String, Object> v = getParent().getValue();
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
        if (getParent() != null) {
            ((SharedEntityHandler) getParent()).ensureValue().put(getFieldName(), value);
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
        if ((other == null) || (thisValue == null) || (!(other instanceof IEntity<?>)) || (!this.getClass().equals(other.getClass()))) {
            return false;
        }
        return thisValue.equals(((IEntity<?>) other).getValue());
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
    public Path getPath() {
        return new Path(this);
    }

    @Override
    public boolean isNull() {
        Map<String, Object> thisValue = this.getValue();
        return (thisValue == null) || (thisValue.size() == 0);
    }

    @Override
    public void set(OBJECT_TYPE entity) {
        setValue(entity.getValue());
    }

    @SuppressWarnings("unchecked")
    @Override
    public EntityMeta getEntityMeta() {
        if (entityMeta == null) {
            // Cache Meta for this class.
            entityMeta = EntityFactory.getEntityMeta((Class<IEntity<?>>) getObjectClass());
        }
        return entityMeta;
    }

    @Override
    public IObject<?, ?> getMember(String memberName) {
        if (members == null) {
            members = new HashMap<String, IObject<?, ?>>();
        }
        IObject<?, ?> member = members.get(memberName);
        if (member == null) {
            member = lazyCreateMember(memberName);
            members.put(memberName, member);
        }
        return member;
    }

    @Override
    public IObject<?, ?> getMember(Path path) {
        //TODO implement
        return null;
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
    public Object getMemberValue(Path path) {
        //TODO implement
        return null;
    }

    /**
     * Use data map directly. No need to create Member
     */
    @Override
    public void setMemberValue(String memberName, Object value) {
        ensureValue().put(memberName, value);
    }

    private Object getMemberStringView(String memberName) {
        if (entityMeta.getMemberMeta(memberName).isEntity()) {
            return ((IEntity<?>) getMember(memberName)).getStringView();
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
    public OBJECT_TYPE cloneEntity() {
        OBJECT_TYPE entity = EntityFactory.create((Class<OBJECT_TYPE>) getObjectClass());
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
            EntityValueMap.dumpMap(b, v);
        } else {
            b.append("{null}");
        }
        return b.toString();
    }

    @Override
    public String toString() {
        return getObjectClass().getName() + getValue();
    }
}
