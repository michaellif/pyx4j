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
 * Created on Jan 18, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.annotations.validator.Password;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CTextField;

public class EntityForm<E extends IEntity<?>> {

    private final E metaEntity;

    private E origEntity;

    private E editableEntity;

    private final HashMap<CEditableComponent<?>, String> binding = new HashMap<CEditableComponent<?>, String>();

    @SuppressWarnings("unchecked")
    private final ValueChangeHandler valuePropagation;

    public static <T extends IEntity<?>> EntityForm<T> create(Class<T> clazz) {
        return new EntityForm<T>(clazz);
    }

    @SuppressWarnings("unchecked")
    private class ValuePropagation implements ValueChangeHandler {

        @Override
        public void onValueChange(ValueChangeEvent event) {
            String memberName = binding.get(event.getSource());
            if ((memberName != null) && (editableEntity != null)) {
                editableEntity.setMemberValue(memberName, event.getValue());
            }
        }
    }

    public EntityForm(Class<E> clazz) {
        metaEntity = EntityFactory.create(clazz);
        valuePropagation = new ValuePropagation();
    }

    public E meta() {
        return metaEntity;
    }

    public CEditableComponent<?> create(IObject<?, ?> member) {
        MemberMeta mm = member.getMeta();
        CEditableComponent<?> comp;
        if (mm.getValueClass().equals(String.class)) {
            if (mm.isValidatorAnnotationPresent(Password.class)) {
                comp = new CPasswordTextField(mm.getCaption());
            } else {
                comp = new CTextField(mm.getCaption());
            }
        } else if (mm.getValueClass().isEnum()) {
            comp = new CComboBox(mm.getCaption());
        } else {
            comp = new CTextField(mm.getCaption());
        }
        if (mm.isValidatorAnnotationPresent(NotNull.class)) {
            comp.setMandatory(true);
        }
        bind(comp, mm.getFieldName());
        return comp;
    }

    @SuppressWarnings("unchecked")
    public <T> CEditableComponent<T> get(IObject<?, T> member) {
        for (Map.Entry<CEditableComponent<?>, String> me : binding.entrySet()) {
            if (me.getValue().equals(member.getFieldName())) {
                return (CEditableComponent<T>) me.getKey();
            }
        }
        throw new IndexOutOfBoundsException("Memeber " + member.getFieldName() + " is not bound");
    }

    @SuppressWarnings("unchecked")
    public void bind(CEditableComponent<?> component, String path) {
        binding.put(component, path);
        component.addValueChangeHandler(valuePropagation);
    }

    @SuppressWarnings("unchecked")
    public void populate(E entity) {
        this.origEntity = entity;
        if (entity != null) {
            this.editableEntity = (E) entity.cloneEntity();
        } else {
            this.editableEntity = EntityFactory.create((Class<E>) meta().getObjectClass());
        }

        for (CEditableComponent component : binding.keySet()) {
            String memberName = binding.get(component);
            component.setValue(editableEntity.getMemberValue(memberName));
        }
    }

    public E getValue() {
        return editableEntity;
    }

}
