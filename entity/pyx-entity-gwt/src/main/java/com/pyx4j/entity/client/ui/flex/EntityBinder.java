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
 * Created on Feb 8, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.client.ui.DelegatingEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CTextComponent;
import com.pyx4j.gwt.commons.UnrecoverableClientError;

public class EntityBinder<E extends IEntity> {

    private static final Logger log = LoggerFactory.getLogger(EntityBinder.class);

    private final CEntityEditor<E> editor;

    private final E entityPrototype;

    private E editableEntity;

    private E origEntity;

    private final HashMap<CEditableComponent<?, ?>, Path> binding = new HashMap<CEditableComponent<?, ?>, Path>();

    @SuppressWarnings("rawtypes")
    private final ValueChangeHandler valuePropagation;

    @SuppressWarnings("rawtypes")
    private class ValuePropagation implements ValueChangeHandler {

        @Override
        public void onValueChange(ValueChangeEvent event) {
            Path memberPath = binding.get(event.getSource());
            if ((memberPath != null) && (editableEntity != null)) {
                Object value = event.getValue();
                if (value instanceof IEntity) {
                    ((IEntity) editableEntity.getMember(memberPath)).set(((IEntity) value).cloneEntity());
                    return;
                }

                if (value instanceof ICollection) {
                    value = ((ICollection) value).getValue();
                } else if ((value instanceof Date)) {
                    Class<?> cls = editableEntity.getEntityMeta().getMemberMeta(memberPath).getValueClass();
                    if (cls.equals(LogicalDate.class)) {
                        value = new LogicalDate((Date) value);
                    } else if (cls.equals(java.sql.Date.class)) {
                        value = new java.sql.Date(((Date) value).getTime());
                    }
                }
                editableEntity.setValue(memberPath, value);
            }
        }
    }

    public EntityBinder(Class<E> clazz, CEntityEditor<E> editor) {
        this.entityPrototype = EntityFactory.getEntityPrototype(clazz);
        this.editor = editor;
        this.valuePropagation = new ValuePropagation();
    }

    public E proto() {
        return entityPrototype;
    }

    public CEntityEditor<E> getEntityEditor() {
        return editor;
    }

    @SuppressWarnings("unchecked")
    public <T extends IEntity> CEditableComponent<T, ?> get(T member) {
        return (CEditableComponent<T, ?>) get((IObject<?>) member);
    }

    @SuppressWarnings("unchecked")
    public <T> CEditableComponent<T, ?> get(IObject<T> member) {
        return getRaw(member);
    }

    @SuppressWarnings("rawtypes")
    public <T> CEditableComponent getRaw(IObject<T> member) {
        Path memberPath = member.getPath();
        for (Map.Entry<CEditableComponent<?, ?>, Path> me : binding.entrySet()) {
            if (me.getValue().equals(memberPath)) {
                return me.getKey();
            }
        }
        throw new IndexOutOfBoundsException("Member " + member.getFieldName() + " is not bound");
    }

    public boolean contains(IObject<?> member) {
        Path memberPath = member.getPath();
        for (Map.Entry<CEditableComponent<?, ?>, Path> me : binding.entrySet()) {
            if (me.getValue().equals(memberPath)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T> void bind(CEditableComponent<T, ?> component, IObject<?> member) {
        // verify that member actually exists in entity.
        assert (proto().getMember(member.getPath()) != null);
        component.addValueChangeHandler(valuePropagation);
        applyAttributes(component, member);
        binding.put(component, member.getPath());

        component.addPropertyChangeHandler(new PropertyChangeHandler() {
            boolean sheduled = false;

            @Override
            public void onPropertyChange(final PropertyChangeEvent event) {
                if (!sheduled) {
                    sheduled = true;
                    Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            if (PropertyChangeEvent.PropertyName.VALIDITY.equals(event.getPropertyName())) {
                                log.trace("CEntityEditor.onPropertyChange fired from {}. Changed property is {}.", editor.getTitle(), event.getPropertyName());
                                editor.revalidate();
                                PropertyChangeEvent.fire(editor, PropertyChangeEvent.PropertyName.VALIDITY);

                            }
                            sheduled = false;
                        }
                    });
                }
            }
        });

        component.addValueChangeHandler(new ValueChangeHandler<T>() {
            boolean sheduled = false;

            @Override
            public void onValueChange(final ValueChangeEvent<T> event) {
                if (!sheduled) {
                    sheduled = true;
                    Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            editor.revalidate();
                            log.trace("CEntityEditor.onValueChange fired from {}. New value is {}.", editor.getTitle(), event.getValue());
                            ValueChangeEvent.fire(editor, getValue());
                            sheduled = false;
                        }
                    });
                }

            }
        });

        component.addAccessAdapter(editor);
        if (component instanceof CEntityComponent) {
            ((CEntityComponent<?, ?>) component).onBound(editor);
        }

    }

    protected void applyAttributes(CEditableComponent<?, ?> component, IObject<?> member) {
        MemberMeta mm = member.getMeta();
        if (mm.isValidatorAnnotationPresent(NotNull.class)) {
            component.setMandatory(true);
        }
        if ((String.class == mm.getValueClass()) && (component instanceof CTextComponent)) {
            ((CTextComponent<?, ?>) component).setMaxLength(mm.getLength());
            if (mm.getDescription() != null) {
                ((CTextComponent<?, ?>) component).setWatermark(mm.getWatermark());
            }
        }
        if (mm.getDescription() != null) {
            component.setToolTip(mm.getDescription());
        }
        component.setTitle(mm.getCaption());
        component.setDebugId(member.getPath());
    }

    @SuppressWarnings("unchecked")
    public void setValue(E entity) {
        if (entity != null) {
            this.editableEntity = entity;
        } else {
            this.editableEntity = EntityFactory.create((Class<E>) proto().getObjectClass());
        }
        populateComponents();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void populateComponents() {
        for (CEditableComponent component : binding.keySet()) {
            Path memberPath = binding.get(component);
            IObject<?> m = editableEntity.getMember(memberPath);
            try {
                if (component instanceof DelegatingEntityEditableComponent) {
                    ((DelegatingEntityEditableComponent) component).populateModel(null, m);
                } else if ((m instanceof IEntity) || (m instanceof ICollection)) {
                    component.populate(m);
                } else {
                    component.populate(m.getValue());
                }
            } catch (ClassCastException e) {
                log.error("Invalid property access {} valueClass: {}", memberPath, m.getMeta().getValueClass());
                log.error("Error", e);
                throw new UnrecoverableClientError("Invalid property access " + memberPath + "; valueClass:" + m.getMeta().getValueClass() + " error:"
                        + e.getMessage());
            }

        }
    }

    public void setComponentsDebugId(IDebugId debugId) {
        for (Map.Entry<CEditableComponent<?, ?>, Path> me : binding.entrySet()) {
            me.getKey().setDebugId(new CompositeDebugId(debugId, me.getValue()));
        }
    }

    public E getValue() {
        return editableEntity;
    }

    public Set<CEditableComponent<?, ?>> getComponents() {
        return binding.keySet();
    }

    public E getOrigValue() {
        if (getEntityEditor().isBound()) {
            throw new Error("Editor is bound. Only isChanged() method of root editor can be called.");
        }
        return origEntity;
    }

    @SuppressWarnings("unchecked")
    public void populate(E entity) {
        if (getEntityEditor().isBound()) {
            setValue(entity);
        } else {
            this.origEntity = (E) entity.cloneEntity();
            if (entity != null) {
                setValue(entity);
            } else {
                setValue((E) EntityFactory.create(proto().getValueClass()));
            }
        }
    }

    public boolean isChanged() {
        if (getEntityEditor().isBound()) {
            throw new Error("Editor is bound. Only isChanged() method of root editor can be called.");
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
                if (memberMeta.isEmbedded()) {
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
}
