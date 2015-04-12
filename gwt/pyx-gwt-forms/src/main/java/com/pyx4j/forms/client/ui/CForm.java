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
 */
package com.pyx4j.forms.client.ui;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.StringDebugId;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.ICollection;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.EntityGraph.EntityGraphEqualOptions;
import com.pyx4j.forms.client.ui.decorators.IFieldDecorator;
import com.pyx4j.forms.client.ui.decorators.IFormDecorator;
import com.pyx4j.gwt.commons.UnrecoverableClientError;

public abstract class CForm<E extends IEntity> extends CContainer<CForm<E>, E, IFormDecorator<E>> {

    private static final Logger log = LoggerFactory.getLogger(CForm.class);

    protected IEditableComponentFactory factory;

    private E origEntity;

    private Class<E> clazz;

    // Bidirectional map CComponent to Path
    private final Map<Path, CComponent<?, ?, ?, ?>> components = new LinkedHashMap<Path, CComponent<?, ?, ?, ?>>();

    private final Map<CComponent<?, ?, ?, ?>, Path> binding = new LinkedHashMap<CComponent<?, ?, ?, ?>, Path>();

    public CForm(Class<E> clazz) {
        this(clazz, null);
    }

    public CForm(Class<E> clazz, IEditableComponentFactory factory) {
        this.clazz = clazz;
        if (factory == null) {
            factory = new BaseEditableComponentFactory();
        }
        this.factory = factory;
        setDebugIdSuffix(new StringDebugId(GWTJava5Helper.getSimpleName(clazz)));
    }

    public E proto() {
        return EntityFactory.getEntityPrototype(clazz);
    }

    public <T extends E> T proto(Class<T> subclass) {
        return EntityFactory.getEntityPrototype(subclass);
    }

    @Override
    public CField<?, ?> create(IObject<?> member) {
        if (isAttached()) {
            return super.create(member);
        } else {
            return factory.create(member);
        }
    }

    public final <T extends CComponent<?, ?, ?, ?>> T inject(IObject<?> member, T comp) {
        bind(comp, member);
        return comp;
    }

    public final CField<?, ?> inject(IObject<?> member) {
        CField<?, ?> comp = create(member);
        bind(comp, member);
        return comp;
    }

    public final CField<?, ?> inject(IObject<?> member, IFieldDecorator decorator) {
        CField<?, ?> comp = inject(member);
        comp.setDecorator(decorator);
        return comp;
    }

    public final <T extends CField<?, ?>> T inject(IObject<?> member, T comp, IFieldDecorator decorator) {
        comp.setDecorator(decorator);
        return inject(member, comp);
    }

    @SuppressWarnings("unchecked")
    public <T extends IEntity> CComponent<?, T, ?, ?> get(T member) {
        return getRaw((IObject<?>) member);
    }

    @SuppressWarnings("unchecked")
    public <T extends IEntity> CComponent<?, List<T>, ?, ?> get(IList<T> member) {
        return getRaw((IObject<?>) member);
    }

    @SuppressWarnings("unchecked")
    public <T> CComponent<?, T, ?, ?> get(IObject<T> member) {
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

    public void bind(CComponent<?, ?, ?, ?> component, final IObject<?> member) {
        // verify that member actually exists in entity.
        assert EntityFactory.getEntityPrototype(clazz).isAssignableFrom(member.getPath().getRootEntityClass());

        CComponent<?, ?, ?, ?> alreadyBound = components.get(member.getPath());
        if (alreadyBound != null) {
            throw new Error("Path '" + member.getPath() + "' already bound");
        }
        binding.put(component, member.getPath());
        components.put(member.getPath(), component);

        component.addAccessAdapter(new IAccessAdapter() {

            @Override
            public Boolean isVisible() {
                if (getValue() == null) {
                    return null;
                } else {
                    return getValue().isInstanceOf(member.getPath().getRootEntityClass());
                }
            }

            @Override
            public Boolean isViewable() {
                return null;
            }

            @Override
            public Boolean isEnabled() {
                return null;
            }

            @Override
            public Boolean isEditable() {
                return null;
            }
        });

        adopt(component);
    }

    public void unbind(IObject<?> member) {
        CComponent<?, ?, ?, ?> component = components.get(member.getPath());
        if (component != null) {
            binding.remove(component);
            abandon(component);
        }
        components.remove(member.getPath());
    }

    public boolean isBound(IObject<?> member) {
        return (components.get(member.getPath()) != null);
    }

    @Override
    public void adopt(CComponent<?, ?, ?, ?> component) {
        Path path = binding.get(component);
        if (path != null) {
            @SuppressWarnings("unchecked")
            IObject<?> member = proto((Class<E>) path.getRootEntityClass()).getMember(path);
            MemberMeta mm = member.getMeta();
            if (mm.isAnnotationPresent(NotNull.class)) {
                component.setMandatory(true);
            }
            if (component instanceof CTextComponent) {
                ((CTextComponent<?, ?>) component).setMaxLength(mm.getLength());
            }
            if (component instanceof CValueBoxBase) {
                if (mm.getWatermark() != null && !mm.getWatermark().trim().equals("")) {
                    ((CValueBoxBase<?, ?>) component).setWatermark(mm.getWatermark());
                }
            }
            if (mm.getDescription() != null) {
                component.setTooltip(mm.getDescription());
            }
            component.setTitle(mm.getCaption());
            component.setDebugIdSuffix(new StringDebugId(member.getFieldName()));
        }
        super.adopt(component);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onValuePropagation(E value, boolean fireEvent, boolean populate) {
//        assert (value == null) || proto().isAssignableFrom(value.getInstanceValueClass()) : "Trying to set value of a wrong type, expected "
//                + proto().getValueClass() + ", got " + value.getInstanceValueClass() + " in form " + getTitle();
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
                if (component instanceof CForm) {
                    ((CForm) component).reset();
                } else {
                    component.setValue(null, fireEvent, populate);
                }
            }
        } else {
            for (CComponent component : getComponents()) {
                Path memberPath = binding.get(component);

                if (entity.isInstanceOf(memberPath.getRootEntityClass())) {
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
                component.applyAccessibilityRules();
            }
        }
    }

//    public void setComponentsDebugId(IDebugId debugId) {
//        for (Map.Entry<CComponent<?, ?>, Path> me : binding.entrySet()) {
//            me.getKey().setDebugIdSuffix(new CompositeDebugId(debugId, me.getValue()));
//        }
//    }

    @Override
    public Collection<? extends CComponent<?, ?, ?, ?>> getComponents() {
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
        setVisited(false);
    }

    public boolean isDirty() {
        if (isAttached()) {
            throw new Error("Editor is bound. Only isDirty() method of root editor can be called.");
        }
        // The rest was called
        if (this.origEntity == null) {
            return false;
        }
        EntityGraphEqualOptions options = new EntityGraphEqualOptions(false);
        options.ignoreTransient = false;
        options.ignoreRpcTransient = true;
        options.trace = true;
        return !EntityGraph.fullyEqual(getOrigValue(), getValue(), options);
    }

    @Override
    public String toString() {
        if (getParent() == null) {
            return super.toString() + "; dirty=" + isDirty();
        }
        return super.toString();
    }

    @Override
    protected final <T> void updateContainer(CComponent<?, T, ?, ?> component) {
        if (component.isPopulated()) {
            T value = component.getValue();
            Path memberPath = binding.get(component);
            if ((memberPath != null) && (getValue() != null)) {
                if (value instanceof IEntity) {
                    // Process on the object level to avoid Polymorphic problems
                    ((IEntity) getValue().getMember(memberPath)).set((IEntity) value);
                } else if (value instanceof ICollection) {
                    ((ICollection<?, ?>) getValue().getMember(memberPath)).set((ICollection) value);
                } else if (!(component instanceof CContainer)) {
                    if (value instanceof Date) {
                        Class<?> cls = getValue().getEntityMeta().getMemberMeta(memberPath).getValueClass();
                        // Synchronize the value in Editor with model
                        if (cls.equals(LogicalDate.class)) {
                            value = (T) new LogicalDate((Date) value);
                        } else if (cls.equals(java.sql.Date.class)) {
                            value = (T) new java.sql.Date(((Date) value).getTime());
                        }
                    }
                    if ((value instanceof Collection)
                            && getValue().getEntityMeta().getMemberMeta(memberPath).getObjectClassType() == ObjectClassType.EntityList) {
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

}
