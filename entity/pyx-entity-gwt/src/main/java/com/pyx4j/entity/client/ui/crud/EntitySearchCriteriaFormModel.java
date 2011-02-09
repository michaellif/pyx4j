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
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.CEntitySuggestBox;
import com.pyx4j.entity.client.ui.EditableComponentFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.entity.shared.criterion.PathSearch;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.events.HasAsyncValue;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.IAcceptText;

public class EntitySearchCriteriaFormModel<E extends IEntity> {

    private final E entityPrototype;

    private final EditableComponentFactory editableComponentFactory;

    private EntitySearchCriteria<E> editableCriteria;

    private final HashMap<CEditableComponent<?, ?>, PathSearch> binding = new HashMap<CEditableComponent<?, ?>, PathSearch>();

    @SuppressWarnings("rawtypes")
    private final ValueChangeHandler valuePropagation;

    private final PropertyChangeHandler visibilityPropagation;

    @SuppressWarnings("rawtypes")
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
                    CEditableComponent<?, ?> component = (CEditableComponent<?, ?>) event.getSource();
                    if (component.isVisible() && component.isEnabled()) {
                        setPropertyValue(path, component.getValue());
                    } else {
                        editableCriteria.removeValue(path);
                    }
                }
            }
        }

    }

    public EntitySearchCriteriaFormModel(Class<E> clazz, EditableComponentFactory editableComponentFactory) {
        entityPrototype = EntityFactory.getEntityPrototype(clazz);
        valuePropagation = new ValuePropagationHandler();
        visibilityPropagation = new VisibilityPropagationHandler();
        if (editableComponentFactory == null) {
            this.editableComponentFactory = new CriteriaEditableComponentFactory();
        } else {
            this.editableComponentFactory = editableComponentFactory;
        }
    }

    public E proto() {
        return entityPrototype;
    }

    public static <T extends IEntity> EntitySearchCriteriaFormModel<T> create(Class<T> clazz, EditableComponentFactory editableComponentFactory) {
        return new EntitySearchCriteriaFormModel<T>(clazz, editableComponentFactory);
    }

    public CEditableComponent<?, ?> create(IObject<?> member) {
        return create(null, member, null);
    }

    public CEditableComponent<?, ?> create(String name, IObject<?> member, String pathProperty) {
        MemberMeta mm = member.getMeta();
        CEditableComponent<?, ?> comp = editableComponentFactory.create(member);
        comp.setTitle((name == null) ? mm.getCaption() : name);
        bind(comp, new PathSearch(member, pathProperty));
        return comp;
    }

    @SuppressWarnings("unchecked")
    public void bind(CEditableComponent<?, ?> component, PathSearch path) {
        binding.put(component, path);
        component.addValueChangeHandler(valuePropagation);
        component.addPropertyChangeHandler(visibilityPropagation);
    }

    @SuppressWarnings("unchecked")
    public <T> CEditableComponent<T, ?> get(IObject<T> member, String pathProperty) {
        return getRaw(member, pathProperty);
    }

    @SuppressWarnings("rawtypes")
    public <T> CEditableComponent getRaw(IObject<T> member, String pathProperty) {
        PathSearch memberPath = new PathSearch(member, pathProperty);
        for (Map.Entry<CEditableComponent<?, ?>, PathSearch> me : binding.entrySet()) {
            if (me.getValue().equals(memberPath)) {
                return me.getKey();
            }
        }
        throw new IndexOutOfBoundsException("Memeber " + member.getFieldName() + " is not bound");
    }

    @SuppressWarnings("unchecked")
    public <T extends IEntity> CEditableComponent<T, ?> get(T member) {
        return (CEditableComponent<T, ?>) get((IObject<?>) member);
    }

    public <T> CEditableComponent<T, ?> get(IObject<T> member) {
        return get(member, null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void populate(EntitySearchCriteria<E> entity) {
        if (entity != null) {
            // TODO use clone
            this.editableCriteria = new EntitySearchCriteria<E>((Class<E>) entityPrototype.getValueClass());
        } else {
            this.editableCriteria = new EntitySearchCriteria<E>((Class<E>) entityPrototype.getValueClass());
        }

        for (Map.Entry<CEditableComponent<?, ?>, PathSearch> me : binding.entrySet()) {
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

    public void obtainEntitySearchCriteria(final AsyncCallback<EntitySearchCriteria<E>> callback) {
        obtainEntitySearchCriteria(binding.keySet().iterator(), callback);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void obtainEntitySearchCriteria(final Iterator<CEditableComponent<?, ?>> iterator, final AsyncCallback<EntitySearchCriteria<E>> callback) {
        while (iterator.hasNext()) {
            CEditableComponent<?, ?> comp = iterator.next();
            if (comp instanceof HasAsyncValue) {
                if (((HasAsyncValue) comp).isAsyncValue()) {
                    ((HasAsyncValue) comp).obtainValue(new AsyncCallback() {
                        @Override
                        public void onFailure(Throwable caught) {
                            callback.onFailure(caught);
                        }

                        @Override
                        public void onSuccess(Object result) {
                            obtainEntitySearchCriteria(iterator, callback);
                        }
                    });
                    return;
                }
            }
        }
        callback.onSuccess(editableCriteria);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void populateHistory(Map<String, String> history) {
        for (Map.Entry<CEditableComponent<?, ?>, PathSearch> me : binding.entrySet()) {
            CEditableComponent comp = me.getKey();
            if (!comp.isVisible()) {
                continue;
            }
            String value = history == null ? null : history.get(me.getValue().getHistoryKey());
            if (value == null) {
                comp.setValue(null);
            } else if (comp instanceof IAcceptText) {
                ((IAcceptText) comp).setValueByString(value);
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map<String, String> getHistory() {
        Map<String, String> map = new HashMap<String, String>();
        for (Map.Entry<CEditableComponent<?, ?>, PathSearch> me : binding.entrySet()) {
            CEditableComponent<?, ?> comp = me.getKey();
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
            } else if (value == null) {
                // By pass other cases
            } else if (comp instanceof CEntitySuggestBox) {
                historyValue = ((CEntitySuggestBox) comp).getOptionName((IEntity) value);
            } else if (comp instanceof CComboBox) {
                if (value instanceof Enum) {
                    historyValue = ((Enum<?>) value).toString();
                } else {
                    historyValue = (String) value;
                }
            } else if (comp instanceof CDatePicker) {
                historyValue = ((CDatePicker) comp).getFormat().format((Date) value).replace('/', '-');
            } else if (value instanceof IEntity) {
                historyValue = ((IEntity) value).getStringView();
            } else if (comp instanceof CTextFieldBase) {
                historyValue = value.toString();
            }

            if (historyValue != null) {
                map.put(me.getValue().getHistoryKey(), historyValue);
            }

        }
        return map;
    }
}
