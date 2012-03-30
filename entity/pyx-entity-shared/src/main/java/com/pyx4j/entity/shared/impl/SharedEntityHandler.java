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
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.IFullDebug;
import com.pyx4j.commons.IHaveServiceCallMarker;
import com.pyx4j.commons.IdentityHashSet;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LoopCounter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
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
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18n.I18nStrategy;

@I18n(strategy = I18nStrategy.IgnoreAll)
public abstract class SharedEntityHandler extends ObjectHandler<Map<String, Object>> implements IEntity, IFullDebug, IHaveServiceCallMarker {

    protected static final Logger log = LoggerFactory.getLogger(SharedEntityHandler.class);

    private static final long serialVersionUID = -7590484996971406115L;

    private static final boolean trace = false;

    private Map<String, Object> data;

    private transient HashMap<String, IObject<?>> members;

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

    /**
     * We Override this method in generator with proper value to help java objects deserialization
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends IEntity> getObjectClass() {
        return (Class<? extends IEntity>) super.getObjectClass();
    }

    @Override
    public Class<? extends IEntity> getValueClass() {
        return getObjectClass();
    }

    @Override
    public Class<? extends IEntity> getInstanceValueClass() {
        if (isTemplateEntity) {
            return getObjectClass();
        } else {
            Map<String, Object> entityValue = getValue();
            if ((entityValue == null) || (!entityValue.containsKey(SharedEntityHandler.CONCRETE_TYPE_DATA_ATTR))) {
                return getObjectClass();
            } else {
                return ((IEntity) entityValue.get(SharedEntityHandler.CONCRETE_TYPE_DATA_ATTR)).getObjectClass();
            }
        }
    }

    @Override
    public boolean isAssignableFrom(Class<? extends IEntity> targetType) {
        return getEntityMeta().isEntityClassAssignableFrom(EntityFactory.getEntityPrototype(targetType));
    }

    @Override
    public boolean isInstanceOf(Class<? extends IEntity> targetType) {
        return EntityFactory.getEntityMeta(targetType).isEntityClassAssignableFrom(EntityFactory.getEntityPrototype(getInstanceValueClass()));
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

    private String exceptionInfo(Map<String, Object> v) {
        StringBuilder b = new StringBuilder();
        b.append(GWTJava5Helper.getSimpleName(getObjectClass()));
        if (v != null) {
            Object pk = v.get(IEntity.PRIMARY_KEY);
            if (pk != null) {
                b.append(" id=").append(pk);
            }
        }
        if (getOwner() != null) {
            b.append(" '").append(this.getFieldName()).append("' of '").append(GWTJava5Helper.getSimpleName(getOwner().getObjectClass())).append("' ")
                    .append(getOwner().getPath());
        }
        return b.toString();
    }

    @Override
    public String getDebugExceptionInfoString() {
        return exceptionInfo(getValue(false));
    }

    @Override
    public Key getPrimaryKey() {
        Map<String, Object> v = getValue(false);
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

    @Override
    public Map<String, Object> getValue() {
        return getValue(false);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getValue(boolean assertDetached) {
        assert !isTemplateEntity : "Template Entity '" + getObjectClass().getName() + "' data manipulations disabled";
        if (delegateValue) {
            Map<String, Object> ownerValue = ((SharedEntityHandler) getOwner()).getValue(assertDetached);
            if (ownerValue == null) {
                return null;
            } else {
                Map<String, Object> value = (Map<String, Object>) ownerValue.get(getFieldName());
                if (assertDetached && (value != null) && value.containsKey(DETACHED_ATTR)) {
                    //log.error("Access to detached entity {}", exceptionInfo(v), new Throwable());
                    throw new RuntimeException("Access to detached " + value.get(DETACHED_ATTR) + " entity " + exceptionInfo(value));
                }
                return value;
            }
        } else {
            if (assertDetached && (data != null) && data.containsKey(DETACHED_ATTR)) {
                //log.error("Access to detached entity {}", exceptionInfo(data), new Throwable());
                throw new RuntimeException("Access to detached entity " + exceptionInfo(data));
            }
            return data;
        }
    }

    /**
     * Guarantee that data is created before setting the value of member
     */
    protected Map<String, Object> ensureValue() {
        return ensureValue(false);
    }

    private Map<String, Object> ensureValue(boolean assertDetached) {
        Map<String, Object> v = getValue(assertDetached);
        if (v == null) {
            if (trace) {
                System.out.println("Value created for " + getObjectClass().getName());
            }
            setValue(v = new EntityValueMap(super.hashCode()));
        }
        return v;
    }

    private boolean isActualOwner(String ownerMemberName) {
        if ((getOwner() != null) && getMeta().isOwnedRelationships()) {
            @SuppressWarnings("unchecked")
            Class<IEntity> ownerType = (Class<IEntity>) this.getEntityMeta().getMemberMeta(ownerMemberName).getValueClass();
            return getOwner().isInstanceOf(ownerType);
        } else {
            return false;
        }
    }

    @Override
    public void setValue(Map<String, Object> value) {
        assert !isTemplateEntity : "Template Entity '" + getObjectClass().getName() + "' data manipulations disabled";
        if ((value != null) && !(value instanceof EntityValueMap)) {
            throw new ClassCastException("Entity expects EntityValueMap as value");
        }
        if (delegateValue) {
            Map<String, Object> ownerValue = ((SharedEntityHandler) getOwner()).ensureValue();
            ownerValue.put(getFieldName(), value);
            // ensure @Owner value is set properly.
            String ownerMemberName = getEntityMeta().getOwnerMemberName();
            if ((ownerMemberName != null) && (value != null) && (isActualOwner(ownerMemberName))) {
                value.put(ownerMemberName, ownerValue);
                if (!this.getMember(ownerMemberName).getObjectClass().equals(getOwner().getInstanceValueClass())) {
                    ownerValue.put(CONCRETE_TYPE_DATA_ATTR, EntityFactory.getEntityPrototype(getOwner().getInstanceValueClass()));
                }
            }
        } else {
            this.data = value;
        }
    }

    @Override
    public void clearValues() {
        Map<String, Object> entityValue = ensureValue();

        Object ownerValue = null;
        String ownerMemberName = null;
        if (getOwner() != null) {
            ownerMemberName = getEntityMeta().getOwnerMemberName();
            if ((ownerMemberName != null) && (isActualOwner(ownerMemberName))) {
                ownerValue = entityValue.get(ownerMemberName);
            }
        }

        IEntity typeAttr = (IEntity) entityValue.get(SharedEntityHandler.CONCRETE_TYPE_DATA_ATTR);
        entityValue.clear();
        if (typeAttr != null) {
            entityValue.put(SharedEntityHandler.CONCRETE_TYPE_DATA_ATTR, typeAttr);
        }
        // ensure @Owner value is set properly.
        if ((ownerMemberName != null) && (ownerValue != null)) {
            entityValue.put(ownerMemberName, ownerValue);
        }
    }

    void removeValueFromGraph(IEntity entity) {
        IEntity ent = this;
        while (ent.getOwner() != null) {
            ent = ent.getOwner();
        }
        removeValueFromGraph(ent, entity, new IdentityHashSet<Object>());
    }

    @SuppressWarnings("unchecked")
    public static void removeValueFromGraph(IEntity root, IEntity entity, Set<Object> processed) {
        Map<String, Object> map = root.getValue();
        if (processed.contains(root) || processed.contains(map)) {
            return;
        }
        processed.add(root);
        if (map == null) {
            return;
        }
        processed.add(map);
        root = root.cast();
        Iterator<Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Object> me = it.next();
            String memberName = me.getKey();
            if (memberName.startsWith(IEntity.ATTR_PREFIX)) {
                continue;
            }
            MemberMeta memberMeta = root.getEntityMeta().getMemberMeta(memberName);
            switch (memberMeta.getObjectClassType()) {
            case Entity: {
                IEntity member = (IEntity) root.getMember(memberName);
                if (member.equals(entity)) {
                    it.remove();
                } else {
                    removeValueFromGraph(member, entity, processed);
                }
            }
                break;
            case EntityList:
            case EntitySet: {
                IObject<?> member = root.getMember(memberName);
                if (member.getAttachLevel() != AttachLevel.Detached) {
                    Iterator<IEntity> lit = ((ICollection<IEntity, ?>) member).iterator();
                    while (lit.hasNext()) {
                        IEntity listMember = lit.next();
                        if (listMember.equals(entity)) {
                            lit.remove();
                        } else {
                            removeValueFromGraph(listMember, entity, processed);
                        }
                    }
                }
            }
                break;
            }
        }
    }

    @Override
    public void set(IEntity entity) {
        if (entity == null) {
            if ((getOwner() != null) && getMeta().isOwnedRelationships()) {
                Map<String, Object> v = getValue();
                if (v != null) {
                    removeValueFromGraph(this.detach());
                }
            }
            setValue(null);
        } else {
            assert !((SharedEntityHandler) entity).isTemplateEntity : "Template Entity '" + getObjectClass().getName() + "' data manipulations disabled";
            assert this.getEntityMeta().isEntityClassAssignableFrom(entity) : this.getEntityMeta().getCaption() + " is not assignable from "
                    + entity.getEntityMeta().getCaption();
            Map<String, Object> value = ((SharedEntityHandler) entity).ensureValue();
            //TODO Test type safety at runtime.
            if (!this.getObjectClass().equals(entity.getInstanceValueClass())) {
                // allow polymorphic Member
                value.put(CONCRETE_TYPE_DATA_ATTR, EntityFactory.getEntityPrototype(entity.getInstanceValueClass()));
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
        if (isTemplateEntity) {
            return (other != null) && this.getClass().equals(other.getClass());
        } else {
            if ((other == null) || (!(other instanceof SharedEntityHandler)) || (((SharedEntityHandler) other).isTemplateEntity)) {
                return false;
            }
            Map<String, Object> thisValue = this.getValue(false);
            assert getAttachLevel() != AttachLevel.Detached : "Access to detached entity " + getDebugExceptionInfoString();
            if (thisValue == null) {
                return false;
            }
            Map<String, Object> otherValue = ((SharedEntityHandler) other).getValue(false);
            if (otherValue == null) {
                return false;
            }
            if (otherValue == thisValue) {
                return true;
            }
            Object pk = thisValue.get(IEntity.PRIMARY_KEY);
            if (pk == null) {
                return false;
            }
            return EqualsHelper.equals(pk, otherValue.get(IEntity.PRIMARY_KEY))
                    && (this.getInstanceValueClass().equals(((IEntity) other).getInstanceValueClass()));
        }
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
        if (isTemplateEntity) {
            return super.hashCode();
        } else {
            Map<String, Object> thisValue = this.getValue(false);
            if (thisValue == null) {
                return super.hashCode();
            } else {
                return thisValue.hashCode();
            }
        }
    }

    @Override
    public boolean isNull() {
        Map<String, Object> thisValue = this.getValue(false);
        if ((thisValue == null) || (thisValue.isEmpty())) {
            return true;
        }
        return ((EntityValueMap) thisValue).isNull();
    }

    @Override
    public boolean isEmpty() {
        Map<String, Object> thisValue = this.getValue(false);
        if ((thisValue == null) || (thisValue.isEmpty())) {
            return true;
        }
        // Just one field is present and is PK
        if ((1 == thisValue.size()) && (thisValue.containsKey(PRIMARY_KEY))) {
            return true;
        } else {
            return ((EntityValueMap) thisValue).isNull(new HashSet<Map<String, Object>>(), true);
        }
    }

    @Override
    public boolean isValueDetached() {
        return getAttachLevel() != AttachLevel.Attached;
    }

    @Override
    public void setValuePopulated() {
        setAttachLevel(AttachLevel.Attached);
    }

    @Override
    public void setValueDetached() {
        setAttachLevel(AttachLevel.IdOnly);
    }

    @Override
    public AttachLevel getAttachLevel() {
        Map<String, Object> thisValue = this.getValue(false);
        if ((thisValue == null) || (thisValue.isEmpty())) {
            if ((delegateValue) && getOwner().isValueDetached()) {
                return AttachLevel.Detached;
            } else {
                return AttachLevel.Attached;
            }
        }
        AttachLevel level = (AttachLevel) thisValue.get(DETACHED_ATTR);
        if (level == null) {
            return AttachLevel.Attached;
        } else {
            return level;
        }
    }

    @Override
    public void setAttachLevel(AttachLevel level) {
        if (level == AttachLevel.Attached) {
            ensureValue(false).remove(DETACHED_ATTR);
        } else if (level == AttachLevel.ToStringMembers) {
            String stringView = this.getStringView();
            Key key = this.getPrimaryKey();
            Map<String, Object> thisValue = ensureValue(false);
            thisValue.clear();
            if (key != null) {
                thisValue.put(IEntity.PRIMARY_KEY, key);
            }
            thisValue.put(TO_STRING_ATTR, stringView);
            thisValue.put(DETACHED_ATTR, level);
        } else {
            ensureValue(false).put(DETACHED_ATTR, level);
        }
    }

    @Override
    public void copyStringView(IEntity target) {
        Map<String, Object> targetValue = ((SharedEntityHandler) target).ensureValue(false);
        targetValue.clear();
        if (this.getPrimaryKey() != null) {
            targetValue.put(IEntity.PRIMARY_KEY, this.getPrimaryKey());
        }
        targetValue.put(TO_STRING_ATTR, this.getStringView());
        targetValue.put(DETACHED_ATTR, AttachLevel.ToStringMembers);
    }

    @Override
    public EntityMeta getEntityMeta() {
        // Cache EntityMeta is done in J2SE Entity implementations using static member.
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
                return lazyCreateMemberIPrimitive(PRIMARY_KEY, Key.class);
            } else {
                member = lazyCreateMember(memberName);
                if (member == null) {
                    throw new RuntimeException("Unknown member '" + memberName + "' in " + getObjectClass().getName());
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
        Map<String, Object> v = getValue(true);
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
        Map<String, Object> ownerValueMap = ensureValue(true);
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
        ensureValue(true).put(memberName, value);
    }

    @Override
    public <T extends IObject<?>> void set(T member, T value) {
        ensureValue(true).put(member.getFieldName(), value.getValue());
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
        } else if (!isObjectClassSameAsDef()) {
            return this.cast().getStringView();
        }
        Map<String, Object> thisValue = getValue(false);
        if ((thisValue != null) && thisValue.containsKey(TO_STRING_ATTR)) {
            return (String) thisValue.get(TO_STRING_ATTR);
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
    public IEntity duplicate() {
        IEntity entity = EntityFactory.create((Class<IEntity>) getObjectClass());
        Map<String, Object> v = getValue();
        if (v != null) {
            Map<String, Object> data2 = new EntityValueMap();
            Map<Object, Object> processed = new IdentityHashMap<Object, Object>();
            processed.put(v, data2);
            cloneMap(v, data2, processed);
            entity.setValue(data2);
        }
        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IEntity> T detach() {
        IEntity entity = EntityFactory.create((Class<IEntity>) getInstanceValueClass());
        entity.setValue(getValue());
        return (T) entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IEntity> T createIdentityStub() {
        IEntity entity = EntityFactory.create((Class<IEntity>) getInstanceValueClass());
        entity.setPrimaryKey(this.getPrimaryKey());
        entity.setValueDetached();
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
            if (this.getValueClass().equals(clazz)) {
                return (T) this;
            } else {
                T entity = EntityFactory.create(clazz, getParent(), getFieldName());
                entity.setValue(ensureValue());
                return entity;
            }
        }
    }

    @Override
    public <T extends IEntity> T duplicate(Class<T> entityClass) {
        Map<String, Object> entityValue = getValue();
        T entity = EntityFactory.create(entityClass);
        // Down cast
        if (entity.getEntityMeta().isEntityClassAssignableFrom(this)) {
            entity.setPrimaryKey(this.getPrimaryKey());
            if (this.isValueDetached()) {
                entity.setValueDetached();
            } else {
                Map<Object, Object> processed = new IdentityHashMap<Object, Object>();
                processed.put(this.getValue(), entity.getValue());
                for (String memberName : entity.getEntityMeta().getMemberNames()) {
                    if (entityValue.containsKey(memberName)) {
                        entity.setMemberValue(memberName, cloneValue(entityValue.get(memberName), processed));
                    }
                }
            }
            // Up cast
        } else if (this.getEntityMeta().isEntityClassAssignableFrom(entity)) {
            entity.setPrimaryKey(this.getPrimaryKey());
            if (this.isValueDetached()) {
                entity.setValueDetached();
            } else {
                Map<Object, Object> processed = new IdentityHashMap<Object, Object>();
                processed.put(this.getValue(), entity.getValue());
                for (String memberName : this.getEntityMeta().getMemberNames()) {
                    if (entityValue.containsKey(memberName)) {
                        entity.setMemberValue(memberName, cloneValue(entityValue.get(memberName), processed));
                    }
                }
            }
        } else {
            throw new ClassCastException(entity.getEntityMeta().getCaption() + " is not assignable from " + this.getEntityMeta().getCaption());
        }
        return entity;
    }

    @Override
    public boolean isObjectClassSameAsDef() {
        Map<String, Object> entityValue = getValue();
        if (entityValue == null) {
            return true;
        } else {
            IEntity typeAttr = (IEntity) entityValue.get(SharedEntityHandler.CONCRETE_TYPE_DATA_ATTR);
            if (typeAttr == null) {
                return true;
            } else {
                @SuppressWarnings("unchecked")
                Class<IEntity> clazz = (Class<IEntity>) typeAttr.getValueClass();
                return (this.getValueClass().equals(clazz));
            }
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
