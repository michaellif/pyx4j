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
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.pyx4j.entity.client.ui.flex;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.client.ui.DelegatingEntityEditableComponent;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CTextComponent;

public class EntityBinder<E extends IEntity> {

    private static final Logger log = LoggerFactory.getLogger(EntityBinder.class);

    private final E entityPrototype;

    private E editableEntity;

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
                    value = ((IEntity) value).getValue();
                } else if ((value instanceof Date)) {
                    Class<?> cls = editableEntity.getEntityMeta().getMemberMeta(memberPath).getValueClass();
                    if (cls.equals(java.sql.Date.class)) {
                        value = new java.sql.Date(((Date) value).getTime());
                    }
                }
                editableEntity.setValue(memberPath, value);
            }
        }
    }

    public EntityBinder(Class<E> clazz) {
        entityPrototype = EntityFactory.getEntityPrototype(clazz);
        valuePropagation = new ValuePropagation();
    }

    public E proto() {
        return entityPrototype;
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
        throw new IndexOutOfBoundsException("Memeber " + member.getFieldName() + " is not bound");
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
    public void bind(CEditableComponent<?, ?> component, IObject<?> member) {
        component.addValueChangeHandler(valuePropagation);
        applyAttributes(component, member);
        binding.put(component, member.getPath());
    }

    protected void applyAttributes(CEditableComponent<?, ?> component, IObject<?> member) {
        MemberMeta mm = member.getMeta();
        if (mm.isValidatorAnnotationPresent(NotNull.class)) {
            component.setMandatory(true);
        }
        if (String.class == mm.getValueClass()) {
            ((CTextComponent<?, ?>) component).setMaxLength(mm.getStringLength());
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
    public void populate(E entity) {
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
            if (component instanceof DelegatingEntityEditableComponent) {
                ((DelegatingEntityEditableComponent) component).populateModel(null, m);
            } else if ((m instanceof IEntity) || (m instanceof ICollection)) {
                component.populate(m);
            } else {
                try {
                    component.populate(m.getValue());
                } catch (ClassCastException e) {
                    // TODO Auto-generated catch block
                    log.error("Error", e);
                    throw new ClassCastException("property " + memberPath + " ValueClass:" + m.getMeta().getValueClass() + " Error:" + e.getMessage());
                }
            }
        }
    }

    public E getValue() {
        return editableEntity;
    }

}
