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
 * Created on Feb 18, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.validator.Email;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.annotations.validator.Password;
import com.pyx4j.entity.annotations.validator.Phone;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CDoubleField;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CEmailField;
import com.pyx4j.forms.client.ui.CIntegerField;
import com.pyx4j.forms.client.ui.CLongField;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CPhoneField;
import com.pyx4j.forms.client.ui.CSuggestBox;
import com.pyx4j.forms.client.ui.CTextArea;
import com.pyx4j.forms.client.ui.CTextField;

public class EntityFormModel<E extends IEntity> {

    private final E metaEntity;

    private E origEntity;

    private E editableEntity;

    private final HashMap<CEditableComponent<?>, Path> binding = new HashMap<CEditableComponent<?>, Path>();

    @SuppressWarnings("unchecked")
    private final ValueChangeHandler valuePropagation;

    @SuppressWarnings("unchecked")
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

    public EntityFormModel(Class<E> clazz) {
        metaEntity = EntityFactory.create(clazz);
        valuePropagation = new ValuePropagation();
    }

    public E meta() {
        return metaEntity;
    }

    public void setComponets(IObject<?>[][] components2) {
        // TODO Auto-generated method stub

    }

    public CEditableComponent<?> create(IObject<?> member) {
        MemberMeta mm = member.getMeta();
        CEditableComponent<?> comp;
        EditorType editorType = mm.getEditorType();

        if (editorType != null) {
            switch (editorType) {
            case text:
                comp = new CTextField(mm.getCaption());
                break;
            case password:
                comp = new CPasswordTextField(mm.getCaption());
                break;
            case textarea:
                comp = new CTextArea(mm.getCaption());
                break;
            case combo:
                if (mm.isEntity()) {
                    comp = new CEntityComboBox(mm.getCaption(), mm.getObjectClass());
                } else {
                    comp = new CComboBox(mm.getCaption());
                }
                break;
            case suggest:
                comp = new CSuggestBox(mm.getCaption());
                break;
            case captcha:
                comp = new CCaptcha();
                break;
            default:
                throw new Error("Unknown ");
            }
        } else if (mm.getValueClass().equals(String.class)) {
            if (mm.isValidatorAnnotationPresent(Password.class)) {
                comp = new CPasswordTextField(mm.getCaption());
            } else if (mm.isValidatorAnnotationPresent(Email.class)) {
                comp = new CEmailField(mm.getCaption());
            } else if (mm.isValidatorAnnotationPresent(Phone.class)) {
                comp = new CPhoneField(mm.getCaption());
            } else {
                comp = new CTextField(mm.getCaption());
            }
        } else if (mm.isEntity()) {
            comp = new CEntityComboBox(mm.getCaption(), mm.getObjectClass());
        } else if (mm.getValueClass().isEnum()) {
            comp = new CComboBox(mm.getCaption());
        } else if (mm.getValueClass().equals(Date.class) || (mm.getValueClass().equals(java.sql.Date.class))) {
            comp = new CDatePicker(mm.getCaption());
        } else if (mm.getValueClass().equals(Boolean.class)) {
            comp = new CCheckBox(mm.getCaption());
        } else if (mm.getValueClass().equals(Integer.class)) {
            comp = new CIntegerField(mm.getCaption());
        } else if (mm.getValueClass().equals(Long.class)) {
            comp = new CLongField(mm.getCaption());
        } else if (mm.getValueClass().equals(Double.class)) {
            comp = new CDoubleField(mm.getCaption());
            if (mm.getFormat() != null) {
                ((CDoubleField) comp).setNumberFormat(mm.getFormat());
            }
        } else if (mm.getObjectClass().equals(IList.class)) {
            comp = new CEntityFormFolder(((IList) member).$().getObjectClass());
        } else {
            comp = new CTextField(mm.getCaption());
        }
        if (mm.isValidatorAnnotationPresent(NotNull.class)) {
            comp.setMandatory(true);
        }

        if (mm.getDescription() != null) {
            comp.setToolTip(mm.getDescription());
        }
        comp.setTitle(mm.getCaption());
        bind(comp, member.getPath());
        return comp;
    }

    @SuppressWarnings("unchecked")
    public <T extends IEntity> CEditableComponent<T> get(T member) {
        return (CEditableComponent<T>) get((IObject<?>) member);
    }

    @SuppressWarnings("unchecked")
    public <T> CEditableComponent<T> get(IObject<T> member) {
        Path memberPath = member.getPath();
        for (Map.Entry<CEditableComponent<?>, Path> me : binding.entrySet()) {
            if (me.getValue().equals(memberPath)) {
                return (CEditableComponent<T>) me.getKey();
            }
        }
        throw new IndexOutOfBoundsException("Memeber " + member.getFieldName() + " is not bound");
    }

    public boolean contains(IObject<?> member) {
        Path memberPath = member.getPath();
        for (Map.Entry<CEditableComponent<?>, Path> me : binding.entrySet()) {
            if (me.getValue().equals(memberPath)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void bind(CEditableComponent<?> component, Path path) {
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
            Path memberPath = binding.get(component);
            IObject<?> m = editableEntity.getMember(memberPath);
            if (m instanceof IEntity) {
                component.setValue(m);
            } else {
                component.setValue(m.getValue());
            }
        }
    }

    public E getValue() {
        return editableEntity;
    }

    public E getOrigValue() {
        return origEntity;
    }

}
