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
 * Created on 2011-02-09
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.StringDebugId;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.ICollection;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.gwt.commons.UnrecoverableClientError;

public abstract class CEntityForm<E extends IEntity> extends CEntityContainer<E> {

    private static final Logger log = LoggerFactory.getLogger(CEntityForm.class);

    protected IEditableComponentFactory factory;

    private final E entityPrototype;

    private E origEntity;

    // Bidirectional map CComponent to Path
    private final Map<Path, CComponent<?>> components = new HashMap<Path, CComponent<?>>();

    private final Map<CComponent<?>, Path> binding = new HashMap<CComponent<?>, Path>();

    public CEntityForm(Class<E> clazz) {
        this(clazz, null);
    }

    public CEntityForm(Class<E> clazz, IEditableComponentFactory factory) {
        if (factory == null) {
            factory = new BaseEditableComponentFactory();
        }
        this.entityPrototype = EntityFactory.getEntityPrototype(clazz);
        this.factory = factory;
        setDebugIdSuffix(new StringDebugId(GWTJava5Helper.getSimpleName(clazz)));
    }

    public E proto() {
        return entityPrototype;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (isAttached()) {
            return super.create(member);
        } else {
            return factory.create(member);
        }
    }

    public final CComponent<?> inject(IObject<?> member) {
        CComponent<?> comp = create(member);
        bind(comp, member);
        return comp;
    }

    public final <T extends CComponent<?>> T inject(IObject<?> member, T comp) {
        bind(comp, member);
        return comp;
    }

    @SuppressWarnings("unchecked")
    public <T extends IEntity> CComponent<T> get(T member) {
        return getRaw((IObject<?>) member);
    }

    @SuppressWarnings("unchecked")
    public <T extends IEntity> CComponent<List<T>> get(IList<T> member) {
        return getRaw((IObject<?>) member);
    }

    @SuppressWarnings("unchecked")
    public <T> CComponent<T> get(IObject<T> member) {
        return getRaw(member);
    }

    @SuppressWarnings("rawtypes")
    private <T> CComponent getRaw(IObject<T> member) {
        CComponent component = components.get(member.getPath());
        if (component == null) {
            throw new IndexOutOfBoundsException("Member " + member.getFieldName() + " is not bound");
        } else {
            return component;
        }
    }

    public boolean contains(IObject<?> member) {
        return components.containsKey(member.getPath());
    }

    public void bind(CComponent<?> component, IObject<?> member) {
        // verify that member actually exists in entity.
        assert (proto().getMember(member.getPath()) != null);
        CComponent<?> alreadyBound = components.get(member.getPath());
        if (alreadyBound != null) {
            throw new Error("Path '" + member.getPath() + "' already bound");
        }
        binding.put(component, member.getPath());
        components.put(member.getPath(), component);
        adopt(component);
    }

    public void unbind(IObject<?> member) {
        CComponent<?> component = components.get(member.getPath());
        if (component != null) {
            binding.remove(component);
        }
        components.remove(member.getPath());
    }

    public boolean isBound(IObject<?> member) {
        return (components.get(member.getPath()) != null);
    }

    @Override
    public void adopt(CComponent<?> component) {
        IObject<?> member = proto().getMember(binding.get(component));
        MemberMeta mm = member.getMeta();
        if (mm.isValidatorAnnotationPresent(NotNull.class)) {
            component.setMandatory(true);
        }
        if (component instanceof CTextComponent) {
            ((CTextComponent<?, ?>) component).setMaxLength(mm.getLength());
            if (mm.getWatermark() != null) {
                ((CTextComponent<?, ?>) component).setWatermark(mm.getWatermark());
            } else if (mm.getDescription() != null) {
                ((CTextComponent<?, ?>) component).setWatermark(mm.getWatermark());
            }
        }
        if (mm.getDescription() != null) {
            component.setTooltip(mm.getDescription());
        }
        component.setTitle(mm.getCaption());
        component.setDebugIdSuffix(new StringDebugId(member.getFieldName()));
        super.adopt(component);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onValuePropagation(E value, boolean fireEvent, boolean populate) {
        assert (value == null) || proto().isAssignableFrom(value.getInstanceValueClass()) : "Trying to set value of a wrong type, expected "
                + proto().getValueClass() + ", got " + value.getInstanceValueClass() + " in form " + getTitle();
        if (populate) {
            assert value != null : "Entity Editor should not be populated with null. Use reset() instead";
            if (!isAttached()) {
                this.origEntity = (E) value.duplicate();
            }
        }
        super.onValuePropagation(value, fireEvent, populate);

    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void setComponentsValue(E entity, boolean fireEvent, boolean populate) {
        if (entity == null) {
            for (CComponent component : getComponents()) {
                if (component instanceof CEntityForm) {
                    ((CEntityForm) component).reset();
                } else {
                    component.setValue(null, fireEvent, populate);
                }
            }
        } else {
            for (CComponent component : getComponents()) {
                Path memberPath = binding.get(component);
                IObject<?> m = entity.getMember(memberPath);
                try {
                    if (m instanceof IEntity) {
                        component.setValue(((IEntity) m).cast(), fireEvent, populate);
                    } else if (m instanceof ICollection) {
                        component.setValue(m, fireEvent, populate);
                    } else {
                        component.setValue(m.getValue(), fireEvent, populate);
                    }
                } catch (ClassCastException e) {
                    log.error("Invalid property access {} valueClass: {}", memberPath, m.getMeta().getValueClass());
                    log.error("Error", e);
                    throw new UnrecoverableClientError("Invalid property access " + memberPath + "; valueClass:" + m.getMeta().getValueClass() + " error:"
                            + e.getMessage());
                }
            }
        }
    }

//    public void setComponentsDebugId(IDebugId debugId) {
//        for (Map.Entry<CComponent<?>, Path> me : binding.entrySet()) {
//            me.getKey().setDebugIdSuffix(new CompositeDebugId(debugId, me.getValue()));
//        }
//    }

    @Override
    public Collection<? extends CComponent<?>> getComponents() {
        if (binding != null) {
            return binding.keySet();
        }
        return null;
    }

    private E getOrigValue() {
        if (isAttached()) {
            throw new Error("Editor is bound. Only isChanged() method of root editor can be called.");
        }
        return origEntity;
    }

    /**
     * Initialize from with empty Entity
     */
    @SuppressWarnings("unchecked")
    public void populateNew() {
        populate((E) EntityFactory.create(proto().getObjectClass()));
    }

    @Override
    public void onReset() {
        super.onReset();
        this.origEntity = null;
        setUnconditionalValidationErrorRendering(false);
    }

    public boolean isDirty() {
        if (isAttached()) {
            throw new Error("Editor is bound. Only isDirty() method of root editor can be called.");
        }
        return !equalRecursive(getOrigValue(), getValue());
    }

    public static boolean equalRecursive(IEntity entity1, IEntity entity2) {
        return equalRecursive(entity1, entity2, new HashSet<IEntity>());
    }

    private static boolean equalRecursive(IEntity entity1, IEntity entity2, Set<IEntity> processed) {
        if (((entity2 == null) || entity2.isNull())) {
            return isEmptyEntity(entity1);
        } else if ((entity1 == null) || entity1.isNull()) {
            return isEmptyEntity(entity2);
        }
        if (processed != null) {
            if (processed.contains(entity1)) {
                return true;
            }
            processed.add(entity1);
        }
        EntityMeta em = entity1.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (memberMeta.isDetached() || memberMeta.isTransient() || memberMeta.isRpcTransient()) {
                continue;
            }
            if (memberMeta.isEntity()) {
                if (memberMeta.isOwnedRelationships()) {
                    if (!equalRecursive((IEntity) entity1.getMember(memberName), (IEntity) entity2.getMember(memberName), processed)) {
                        log.debug("changed {}", memberName);
                        return false;
                    }
                } else if (((IEntity) entity1.getMember(memberName)).isNull()) {
                    if (!((IEntity) entity2.getMember(memberName)).isNull()) {
                        log.debug("changed [null] -> [{}]", entity2.getMember(memberName));
                        return false;
                    }
                } else if (!EqualsHelper.equals(entity1.getMember(memberName), entity2.getMember(memberName))) {
                    log.debug("changed [{}] -> [{}]", entity1.getMember(memberName), entity2.getMember(memberName));
                    return false;
                }
            } else if (ISet.class.equals(memberMeta.getObjectClass())) {
                //TODO OwnedRelationships
                if (!EqualsHelper.equals((ISet<?>) entity1.getMember(memberName), (ISet<?>) entity2.getMember(memberName))) {
                    log.debug("changed {}", memberName);
                    return false;
                }
            } else if (IList.class.equals(memberMeta.getObjectClass())) {
                if (memberMeta.isOwnedRelationships()) {
                    if (!listValuesEquals((IList<?>) entity1.getMember(memberName), (IList<?>) entity2.getMember(memberName), processed)) {
                        log.debug("changed {}", memberName);
                        return false;
                    }
                } else if (!EqualsHelper.equals((IList<?>) entity1.getMember(memberName), (IList<?>) entity2.getMember(memberName))) {
                    log.debug("changed {}", memberName);
                    return false;
                }
            } else if (!EqualsHelper.equals(entity1.getMember(memberName), entity2.getMember(memberName))) {
                log.debug("changed {}", memberName);
                log.debug("[{}] -> [{}]", entity1.getMember(memberName), entity2.getMember(memberName));
                return false;
            }
        }
        return true;
    }

    private static boolean listValuesEquals(IList<?> value1, IList<?> value2, Set<IEntity> processed) {
        if (value1.size() != value2.size()) {
            return false;
        }
        Iterator<?> iter1 = value1.iterator();
        Iterator<?> iter2 = value2.iterator();
        for (; iter1.hasNext() && iter2.hasNext();) {
            if (!equalRecursive((IEntity) iter1.next(), (IEntity) iter2.next(), processed)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmptyEntity(IEntity entity) {
        if ((entity == null) || entity.isNull()) {
            return true;
        }
        EntityMeta em = entity.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (memberMeta.isDetached() || memberMeta.isTransient() || memberMeta.isRpcTransient()) {
                continue;
            }
            IObject<?> member = entity.getMember(memberName);
            if (member.isNull()) {
                continue;
            } else if (memberMeta.isEntity()) {
                if (!isEmptyEntity((IEntity) member)) {
                    log.debug("member {} not empty; {}", memberName, member);
                    return false;
                }
            } else if ((ISet.class.equals(memberMeta.getObjectClass())) || (IList.class.equals(memberMeta.getObjectClass()))) {
                if (!((ICollection<?, ?>) member).isEmpty()) {
                    log.debug("member {} not empty; {}", memberName, member);
                    return false;
                }
            } else if (Boolean.class.equals(memberMeta.getValueClass())) {
                // Special case for values presented by CheckBox
                if (member.getValue() == Boolean.TRUE) {
                    log.debug("member {} not empty; {}", memberName, member);
                    return false;
                }
            } else {
                log.debug("member {} not empty; {}", memberName, member);
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        if (getParent() == null) {
            return super.toString() + "; dirty=" + isDirty();
        }
        return super.toString();
    }

    @Override
    protected final <T> void updateContainer(CComponent<T> component) {
        T value = component.getValue();
        Path memberPath = binding.get(component);
        if ((memberPath != null) && (getValue() != null)) {
            if (value instanceof IEntity) {
                // Process on the object level to avoid Polymorphic problems
                ((IEntity) getValue().getMember(memberPath)).set((IEntity) value);
            } else if (value instanceof ICollection) {
                ((ICollection<?, ?>) getValue().getMember(memberPath)).set((ICollection) value);
            } else if (!(component instanceof CEntityContainer)) {
                if (value instanceof Date) {
                    Class<?> cls = getValue().getEntityMeta().getMemberMeta(memberPath).getValueClass();
                    // Synchronize the value in Editor with model
                    if (cls.equals(LogicalDate.class)) {
                        value = (T) new LogicalDate((Date) value);
                    } else if (cls.equals(java.sql.Date.class)) {
                        value = (T) new java.sql.Date(((Date) value).getTime());
                    }
                }
                if ((value instanceof Collection) && getValue().getEntityMeta().getMemberMeta(memberPath).getObjectClassType() == ObjectClassType.EntityList) {
                    ICollection<IEntity, ?> collectionMember = (ICollection<IEntity, ?>) getValue().getMember(memberPath);
                    collectionMember.clear();
                    collectionMember.addAll((Collection<IEntity>) value);
                } else {
                    getValue().setValue(memberPath, (Serializable) value);
                }
            }
            log.trace("CEntityEditor {} model updated {}", shortDebugInfo(), memberPath);
        }
    }
}
