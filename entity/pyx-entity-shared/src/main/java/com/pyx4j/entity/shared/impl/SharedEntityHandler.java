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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.IFullDebug;
import com.pyx4j.commons.IHaveServiceCallMarker;
import com.pyx4j.commons.IdentityHashSet;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LoopCounter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.impl.SetHandler.ElementsComparator;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.entity.shared.validator.Validator;

public abstract class SharedEntityHandler extends ObjectHandler<Map<String, Object>> implements IEntity, IFullDebug, IHaveServiceCallMarker {

    private static final long serialVersionUID = -7590484996971406115L;

    private static final boolean trace = false;

    private Map<String, Object> data;

    protected transient HashMap<String, IObject<?>> members;

    /**
     * N.B. Default initialization during serialization to 'false'.
     */
    private transient boolean delegateValue;

    private transient final boolean isTemplateEntity;

    /**
     * Creation of stand alone or member Entity
     * 
     * @param clazz
     * @param parent
     * @param fieldName
     */
    public SharedEntityHandler(Class<? extends IObject<?>> clazz, IObject<?> parent, String fieldName) {
        super(clazz, parent, fieldName);
        delegateValue = (parent != null) && (getOwner() == parent);
        isTemplateEntity = ".".equals(fieldName);
    }

    @Override
    void attachToOwner(IObject<?> parent, String fieldName) {
        super.attachToOwner(parent, fieldName);
        delegateValue = (parent != null) && (getOwner() == parent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends IEntity> getObjectClass() {
        return (Class<? extends IEntity>) super.getObjectClass();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends IEntity> getValueClass() {
        return (Class<? extends IEntity>) super.getObjectClass();
    }

    @Override
    public Class<? extends IEntity> getInstanceValueClass() {
        Map<String, Object> entityValue = getValue();
        if ((entityValue == null) || (!entityValue.containsKey(SharedEntityHandler.CONCRETE_TYPE_DATA_ATTR))) {
            return getObjectClass();
        } else {
            return ((IEntity) entityValue.get(SharedEntityHandler.CONCRETE_TYPE_DATA_ATTR)).getObjectClass();
        }
    }

    @Override
    public boolean isAssignableFrom(Class<? extends IEntity> targetType) {
        return getEntityMeta().isEntityClassAssignableFrom(EntityFactory.getEntityPrototype(targetType));
    }

    @Override
    public boolean isInstanceOf(Class<? extends IEntity> targetType) {
        return EntityFactory.getEntityMeta(targetType).isEntityClassAssignableFrom(this);
    }

    protected abstract IObject<?> lazyCreateMember(String name);

    public <T> IPrimitive<T> lazyCreateMemberIPrimitive(String memberName, Class<T> primitiveValueClass) {
        return new PrimitiveHandler<T>(this, memberName, primitiveValueClass);
    }

    public <T> IPrimitiveSet<T> lazyCreateMemberIPrimitiveSet(String memberName, Class<T> primitiveValueClass) {
        return new PrimitiveSetHandler<T>(this, memberName, primitiveValueClass);
    }

    public <T extends IEntity> T lazyCreateMemberIEntity(String memberName, Class<T> valueClass) {
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
            if (trace) {
                System.out.println("Value created for " + getObjectClass().getName());
            }
            setValue(v = new EntityValueMap(super.hashCode()));
        }
        return v;
    }

    @Override
    public Key getPrimaryKey() {
        Map<String, Object> v = getValue();
        if (v == null) {
            return null;
        } else {
            return (Key) v.get(PRIMARY_KEY);
        }
    }

    @Override
    public void setPrimaryKey(Key pk) {
        ensureValue().put(PRIMARY_KEY, pk);
    }

    @Override
    @SuppressWarnings("unchecked")
    public IPrimitive<Key> id() {
        return (IPrimitive<Key>) getMember(PRIMARY_KEY);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getValue() {
        assert !isTemplateEntity : "Template Entity data manipulations disabled";
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
        assert !isTemplateEntity : "Template Entity data manipulations disabled";
        if ((value != null) && !(value instanceof EntityValueMap)) {
            throw new ClassCastException("Entity expects EntityValueMap as value");
        }
        if (delegateValue) {
            ((SharedEntityHandler) getOwner()).ensureValue().put(getFieldName(), value);
            // ensure @Owner value is set properly.
            String ownerMemberName = getEntityMeta().getOwnerMemberName();
            if ((ownerMemberName != null) && (value != null) && (getMeta().isOwnedRelationships())) {
                value.put(ownerMemberName, getOwner().getValue());
            }
        } else {
            this.data = value;
        }
    }

    void removeValueFromGraph(Map<String, Object> v) {
        IEntity ent = this;
        while (ent.getOwner() != null) {
            ent = ent.getOwner();
        }
        Map<String, Object> root = ent.getValue();
        removeValueFromGraph(root, v, new HashSet<Map<String, Object>>());
    }

    @SuppressWarnings("unchecked")
    public static void removeValueFromGraph(Map<String, Object> map, Map<String, Object> v, Set<Map<String, Object>> processed) {
        if (processed.contains(map)) {
            return;
        }
        processed.add(map);
        Iterator<Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Object> me = it.next();
            if (me.getValue() instanceof Map<?, ?>) {
                if (me.getValue().equals(v)) {
                    it.remove();
                } else {
                    removeValueFromGraph((Map<String, Object>) me.getValue(), v, processed);
                }
            } else if (me.getValue() instanceof Collection<?>) {
                @SuppressWarnings("rawtypes")
                Iterator<Object> lit = ((Collection) me.getValue()).iterator();
                while (lit.hasNext()) {
                    Object listMameber = lit.next();
                    if (listMameber instanceof Map<?, ?>) {
                        if (listMameber.equals(v)) {
                            lit.remove();
                        } else {
                            removeValueFromGraph((Map<String, Object>) listMameber, v, processed);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void set(IEntity entity) {
        if (entity == null) {
            if ((getOwner() != null) && getMeta().isOwnedRelationships()) {
                Map<String, Object> v = getValue();
                if (v != null) {
                    removeValueFromGraph(v);
                }
            }
            setValue(null);
        } else {
            Map<String, Object> value = ((SharedEntityHandler) entity).ensureValue();
            //TODO Test type safety at runtime.
            if (!this.getObjectClass().equals(entity.getObjectClass())) {
                // allow AbstractMember
                value.put(CONCRETE_TYPE_DATA_ATTR, EntityFactory.getEntityPrototype(entity.getObjectClass()));
            }
            if ((getOwner() != null) && getMeta().isOwnedRelationships() && (((SharedEntityHandler) entity).getOwner() != this.getOwner())) {
                // attach incoming entity to new owner
                ((SharedEntityHandler) entity).attachToOwner(this.getOwner(), this.getFieldName());
            }
            setValue(value);

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
        if ((other == null) || (thisValue == null) || (!(other instanceof IEntity))
                || (!this.getInstanceValueClass().equals(((IEntity) other).getInstanceValueClass()))) {
            return false;
        }
        return thisValue.equals(((IEntity) other).getValue());
    }

    @Override
    public boolean businessEquals(IEntity other) {
        if (other == this) {
            return true;
        } else if (isNull()) {
            return ((other == null) || other.isNull());
        }
        List<String> m = getEntityMeta().getBusinessEqualMemberNames();
        if (m.size() == 0) {
            return equals(other);
        }
        for (String memberName : m) {
            if (!EqualsHelper.equals(this.getMemberValue(memberName), other.getMemberValue(memberName))) {
                return false;
            }
        }
        return true;
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
    public boolean isEmpty() {
        Map<String, Object> thisValue = this.getValue();
        if ((thisValue == null) || (thisValue.isEmpty())) {
            return true;
        }
        if (thisValue.size() == 1) {
            // Some other field is present other then PK
            if (thisValue.get(PRIMARY_KEY) == null) {
                return ((EntityValueMap) thisValue).isNull();
            } else {
                return true;
            }
        }
        return ((EntityValueMap) thisValue).isNull();
    }

    @Override
    public EntityMeta getEntityMeta() {
        // Cache EntityMeta is done in Entity implementations using static member.
        return EntityFactory.getEntityMeta(getObjectClass());
    }

    /**
     * Generator will create the list ordered by member declaration in source.
     */
    public abstract String[] getMembers();

    @Override
    public IObject<?> getMember(String memberName) {
        if (members == null) {
            members = new HashMap<String, IObject<?>>();
        }
        IObject<?> member = members.get(memberName);
        if (member == null) {
            if (PRIMARY_KEY.equals(memberName)) {
                return lazyCreateMemberIPrimitive(PRIMARY_KEY, Long.class);
            } else {
                member = lazyCreateMember(memberName);
                if (member == null) {
                    throw new RuntimeException("Unknown member " + memberName + " in " + getObjectClass().getName());
                }
            }
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

    @Override
    public boolean containsMemberValue(String memberName) {
        Map<String, Object> v = getValue();
        if (v != null) {
            return v.containsKey(memberName);
        } else {
            return false;
        }
    }

    //    private final void assertPath(Path path) {
    //        if (!GWTJava5Helper.getSimpleName(this.getObjectClass()).equals(path.getRootObjectClassName())) {
    //            throw new IllegalArgumentException("Path of " + path.getRootObjectClassName() + " expected");
    //        }
    //    }

    @Override
    public IObject<?> getMember(Path path) {
        //assertPath(path);
        IObject<?> obj = this;
        for (String memberName : path.getPathMembers()) {
            //TODO ICollection support
            if (!(obj instanceof IEntity)) {
                throw new RuntimeException("Invalid member in path " + memberName + " in " + getObjectClass().getName());
            }
            obj = ((IEntity) obj).getMember(memberName);
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getValue(Path path) {
        //assertPath(path);
        Object value = this.getValue();
        for (String memberName : path.getPathMembers()) {
            if (value == null) {
                return null;
            }
            //TODO ICollection support
            if (!(value instanceof Map<?, ?>)) {
                throw new RuntimeException("Invalid member in path " + memberName + " in " + getObjectClass().getName());
            }
            value = ((Map<String, Object>) value).get(memberName);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(Path path, Object value) {
        //assertPath(path);
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

    private Object getMemberStringView(String memberName, boolean forMessageFormatFormat) {
        MemberMeta mm = getEntityMeta().getMemberMeta(memberName);
        if (mm.isEntity()) {
            return ((IEntity) getMember(memberName)).getStringView();
        } else if (IPrimitive.class.equals(mm.getObjectClass())) {
            IPrimitive<?> member = ((IPrimitive<?>) getMember(memberName));
            if (forMessageFormatFormat && (mm.isNumberValueClass())) {
                return member.getValue();
            } else if (forMessageFormatFormat && mm.getValueClass().equals(Boolean.class)) {
                if (member.isBooleanTrue()) {
                    return Integer.valueOf(1);
                } else {
                    return Integer.valueOf(0);
                }
            } else {
                return member.getStringView();
            }
        } else {
            return getMemberValue(memberName);
        }
    }

    @Override
    public String getStringView() {
        if (isNull()) {
            return getEntityMeta().getNullString();
        }
        List<String> sm = getEntityMeta().getToStringMemberNames();
        String format = getEntityMeta().getToStringFormat();
        if (format != null) {
            List<Object> values = new Vector<Object>();
            for (String memberName : sm) {
                values.add(getMemberStringView(memberName, true));
            }
            return SimpleMessageFormat.format(format, values.toArray());
        } else {
            switch (sm.size()) {
            case 0:
                return getEntityMeta().getNullString();
            case 1:
                return CommonsStringUtils.nvl(getMemberStringView(sm.get(0), false));
            case 2:
                return CommonsStringUtils.nvl_concat(getMemberStringView(sm.get(0), false), getMemberStringView(sm.get(1), false), " ");
            default:
                Map<String, Object> entityValue = getValue();
                if (entityValue == null) {
                    return getEntityMeta().getNullString();
                }
                StringBuilder sb = new StringBuilder();
                for (String memberName : sm) {
                    Object v = entityValue.get(memberName);
                    if (v != null) {
                        if (sb.length() > 0) {
                            sb.append(" ");
                        }
                        sb.append(getMemberStringView(memberName, false));
                    }
                }
                return sb.toString();
            }
        }
    }

    //TODO
    @Override
    public List<Validator> getValidators(Path memberPath) {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IEntity cloneEntity() {
        IEntity entity = EntityFactory.create((Class<IEntity>) getObjectClass());
        Map<String, Object> v = getValue();
        if (v != null) {
            Map<String, Object> data2 = new EntityValueMap();
            cloneMap(v, data2, new IdentityHashMap<Object, Object>());
            entity.setValue(data2);
        }
        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IEntity> T detach() {
        IEntity entity = EntityFactory.create((Class<IEntity>) getObjectClass());
        entity.setValue(getValue());
        return (T) entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IEntity> T cast() {
        Map<String, Object> entityValue = getValue();
        if ((entityValue == null) || (!entityValue.containsKey(SharedEntityHandler.CONCRETE_TYPE_DATA_ATTR))) {
            return (T) this;
        } else {
            T typeAttr = (T) entityValue.get(SharedEntityHandler.CONCRETE_TYPE_DATA_ATTR);
            Class<T> clazz = (Class<T>) typeAttr.getValueClass();
            T entity = EntityFactory.create(clazz, getParent(), getFieldName());
            entity.setValue(ensureValue());
            return entity;
        }
    }

    @Override
    public <T extends IEntity> T clone(Class<T> entityClass) {
        Map<String, Object> entityValue = getValue();
        T entity = EntityFactory.create(entityClass);
        if (!entity.getEntityMeta().isEntityClassAssignableFrom(this)) {
            throw new ClassCastException(entity.getEntityMeta().getCaption() + " is not assignable from " + this.getEntityMeta().getCaption());
        }
        entity.setPrimaryKey(this.getPrimaryKey());
        Map<Object, Object> processed = new IdentityHashMap<Object, Object>();
        for (String memberName : entity.getEntityMeta().getMemberNames()) {
            if (entityValue.containsKey(memberName)) {
                entity.setMemberValue(memberName, cloneValue(entityValue.get(memberName), processed));
            }
        }
        return entity;
    }

    @Override
    public boolean isObjectClassSameAsDef() {
        Map<String, Object> entityValue = getValue();
        if (entityValue == null) {
            return true;
        } else {
            return !entityValue.containsKey(SharedEntityHandler.CONCRETE_TYPE_DATA_ATTR);
        }
    }

    private void cloneMap(Map<String, Object> src, Map<String, Object> dst, Map<Object, Object> processed) {
        for (Map.Entry<String, Object> me : src.entrySet()) {
            dst.put(me.getKey(), cloneValue(me.getValue(), processed));
        }
    }

    @SuppressWarnings("unchecked")
    private Object cloneValue(Object value, Map<Object, Object> processed) {
        if (value instanceof Map<?, ?>) {
            Object existingClone = processed.get(value);
            if (existingClone != null) {
                return existingClone;
            }
            Map<String, Object> m = new EntityValueMap();
            processed.put(value, m);
            cloneMap((Map<String, Object>) value, m, processed);
            return m;
        } else if (value instanceof List<?>) {
            Object existingClone = processed.get(value);
            if (existingClone != null) {
                return existingClone;
            }
            @SuppressWarnings("rawtypes")
            List l = new Vector();
            processed.put(value, l);
            for (Object lm : (List<?>) value) {
                l.add(cloneValue(lm, processed));
            }
            return l;
        } else if (value instanceof HashSet<?>) {
            //IPrimitiveSet
            @SuppressWarnings("rawtypes")
            Set s = new HashSet<Object>();
            for (Object lm : (Set<?>) value) {
                s.add(cloneValue(lm, processed));
            }
            return s;
        } else if (value instanceof TreeSet<?>) {
            Object existingClone = processed.get(value);
            if (existingClone != null) {
                return existingClone;
            }
            @SuppressWarnings("rawtypes")
            Set s = new TreeSet<Map<String, Object>>(new ElementsComparator());
            processed.put(value, s);
            for (Object lm : (Set<?>) value) {
                s.add(cloneValue(lm, processed));
            }
            return s;
        } else {
            // Primitive values, non-mutable anyway
            return value;
        }
    }

    @Override
    public String debugString() {
        StringBuilder b = new StringBuilder();
        b.append(getObjectClass().getName()).append(" ");
        if (isTemplateEntity) {
            b.append("{meta}");
            return b.toString();
        } else {
            b.append('@').append(Integer.toHexString(this.hashCode()));
            b.append('(').append(Integer.toHexString(System.identityHashCode(this))).append(')');
            Map<String, Object> v = getValue();
            if ((v != null) && (v.size() != 0)) {
                b.append('{');
                if (ToStringStyle.fieldMultiLine) {
                    b.append('\n');
                }
                EntityValueMap.dumpMap(b, v, new IdentityHashSet<Map<String, Object>>(), ToStringStyle.PADDING);
                b.append("}");
            } else {
                b.append("{null}");
            }
            return b.toString();
        }
    }

    @Override
    public String toString() {
        return debugString();
    }

    @Override
    public String getServiceCallMarker() {
        String domainName = getObjectClass().getName();
        return domainName.substring(domainName.lastIndexOf(".") + 1);
    }

    /**
     * TODO add this check to GWT compiler
     * 
     * @deprecated Forbidden name, Eclipse Search for references bug
     */
    @Deprecated
    protected final void length() {
        throw new IllegalStateException();
    }

    /**
     * @deprecated Forbidden name, Eclipse Search for references bug
     */
    @Deprecated
    protected final void size() {
        throw new IllegalStateException();
    }
}
