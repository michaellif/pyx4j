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
package com.pyx4j.entity.client.ui.crud;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.entity.shared.criterion.PathSearch;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CNumberField;
import com.pyx4j.forms.client.ui.CTextBox;
import com.pyx4j.forms.client.ui.CTextField;

public class EntitySearchCriteriaFormModel<E extends IEntity> {

    private final E metaEntity;

    private EntitySearchCriteria<E> editableCriteria;

    private final HashMap<CEditableComponent<?>, PathSearch> binding = new HashMap<CEditableComponent<?>, PathSearch>();

    @SuppressWarnings("unchecked")
    private final ValueChangeHandler valuePropagation;

    private final PropertyChangeHandler visibilityPropagation;

    @SuppressWarnings("unchecked")
    private class ValuePropagationHandler implements ValueChangeHandler {

        @Override
        public void onValueChange(ValueChangeEvent event) {
            PathSearch path = binding.get(event.getSource());
            if ((path != null) && (editableCriteria != null)) {
                setPropertyValue(path, event.getValue());
            }
        }
    }

    private class VisibilityPropagationHandler implements PropertyChangeHandler {

        @Override
        public void onPropertyChange(PropertyChangeEvent event) {
            if ((event.getPropertyName() == PropertyChangeEvent.PropertyName.VISIBILITY_PROPERTY)
                    || (event.getPropertyName() == PropertyChangeEvent.PropertyName.ENABLED_PROPERTY)) {
                PathSearch path = binding.get(event.getSource());
                if ((path != null) && (editableCriteria != null)) {
                    CEditableComponent<?> component = (CEditableComponent<?>) event.getSource();
                    if (component.isVisible() && component.isEnabled()) {
                        setPropertyValue(path, component.getValue());
                    } else {
                        editableCriteria.removeValue(path);
                    }
                }
            }
        }

    }

    public EntitySearchCriteriaFormModel(Class<E> clazz) {
        metaEntity = EntityFactory.create(clazz);
        valuePropagation = new ValuePropagationHandler();
        visibilityPropagation = new VisibilityPropagationHandler();
    }

    public E meta() {
        return metaEntity;
    }

    public static <T extends IEntity> EntitySearchCriteriaFormModel<T> create(Class<T> clazz) {
        return new EntitySearchCriteriaFormModel<T>(clazz);
    }

    public CEditableComponent<?> create(IObject<?> member) {
        return create(null, member, null);
    }

    @SuppressWarnings("unchecked")
    public CEditableComponent<?> create(String name, IObject<?> member, String pathProperty) {
        MemberMeta mm = member.getMeta();
        CEditableComponent<?> comp;
        if (mm.isEntity()) {
            comp = new CEntityComboBox(mm.getCaption(), mm.getObjectClass());
        } else if (mm.getValueClass().isEnum()) {
            comp = new CComboBox();
        } else if (mm.getValueClass().equals(Date.class)) {
            comp = new CDatePicker();
        } else {
            comp = new CTextField();
        }
        comp.setTitle((name == null) ? mm.getCaption() : name);
        bind(comp, new PathSearch(member, pathProperty));
        return comp;
    }

    @SuppressWarnings("unchecked")
    public void bind(CEditableComponent<?> component, PathSearch path) {
        binding.put(component, path);
        component.addValueChangeHandler(valuePropagation);
        component.addPropertyChangeHandler(visibilityPropagation);
    }

    @SuppressWarnings("unchecked")
    public <T> CEditableComponent<T> get(IObject<T> member, String pathProperty) {
        PathSearch memberPath = new PathSearch(member, pathProperty);
        for (Map.Entry<CEditableComponent<?>, PathSearch> me : binding.entrySet()) {
            if (me.getValue().equals(memberPath)) {
                return (CEditableComponent<T>) me.getKey();
            }
        }
        throw new IndexOutOfBoundsException("Memeber " + member.getFieldName() + " is not bound");
    }

    public <T> CEditableComponent<T> get(IObject<T> member) {
        return get(member, null);
    }

    @SuppressWarnings("unchecked")
    public void populate(EntitySearchCriteria<E> entity) {
        if (entity != null) {
            // TODO use clone
            this.editableCriteria = new EntitySearchCriteria<E>((Class<E>) metaEntity.getValueClass());
        } else {
            this.editableCriteria = new EntitySearchCriteria<E>((Class<E>) metaEntity.getValueClass());
        }

        for (Map.Entry<CEditableComponent<?>, PathSearch> me : binding.entrySet()) {
            ((CEditableComponent) me.getKey()).setValue(editableCriteria.getValue(me.getValue()));
        }
    }

    public void setPropertyValue(PathSearch path, Object value) {
        editableCriteria.setValue(path, value);
    }

    public void removePropertyValue(PathSearch path) {
        editableCriteria.removeValue(path);
    }

    public EntitySearchCriteria<E> getValue() {
        return editableCriteria;
    }

    public void populateHistory(Map<String, String> history) {
        if (history == null) {
            return;
        }
        for (Map.Entry<CEditableComponent<?>, PathSearch> me : binding.entrySet()) {
            CEditableComponent comp = me.getKey();
            if (!comp.isVisible()) {
                continue;
            }
            Object value = history.get(me.getValue().getHistoryKey());
            if (value == null) {
                comp.setValue(null);
            } else if (comp instanceof CTextField) {
                comp.setValue(value);
            } else if (comp instanceof CNumberField) {
                comp.setValue(((CNumberField) comp).valueOf((String) value));
            } else if (comp instanceof CEntityComboBox) {
                ((CEntityComboBox) comp).setValueByItemName((String) value);
            } else if (comp instanceof CComboBox) {
                ((CComboBox) comp).setValueByItemName((String) value);
            } else if (comp instanceof CDatePicker) {
                //TODO
            } else {

            }
        }
    }

    public Map<String, String> getHistory() {
        Map<String, String> map = new HashMap<String, String>();
        for (Map.Entry<CEditableComponent<?>, PathSearch> me : binding.entrySet()) {
            CEditableComponent<?> comp = me.getKey();
            if ((!comp.isVisible()) || (comp.isValueEmpty())) {
                continue;
            }
            Object value = comp.getValue();

            String historyValue = null;
            //TODO support all types
            if (comp instanceof CTextField) {
                historyValue = (String) value;
            } else if (comp instanceof CEntityComboBox) {
                historyValue = ((CEntityComboBox) comp).getItemName((IEntity) value);
            } else if (comp instanceof CComboBox) {
                if (value instanceof Enum) {
                    historyValue = ((Enum<?>) value).toString();
                } else {
                    historyValue = (String) value;
                }
            } else if (comp instanceof CDatePicker) {
                //TODO
            } else if (comp instanceof CTextBox) {
                historyValue = value.toString();
            } else {

            }

            if (historyValue != null) {
                map.put(me.getValue().getHistoryKey(), historyValue);
            }

        }
        return map;
    }
}
