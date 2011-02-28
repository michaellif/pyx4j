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
package com.pyx4j.entity.client.ui.flex;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.ValidationResults;

public abstract class CEntityEditableComponent<E extends IEntity> extends CEditableComponent<E, NativeEntityEditor<E>> implements IFlexConextComponent,
        PropertyChangeHandler {

    private final EntityBinder<E> binder;

    public CEntityEditableComponent(EntityBinder<E> binder) {
        this.binder = binder;
    }

    public CEntityEditableComponent(Class<E> clazz) {
        binder = new EntityBinder<E>(clazz);
    }

    @Override
    public abstract void createContent();

    public EntityBinder<E> binder() {
        return binder;
    }

    public E proto() {
        return binder.proto();
    }

    @Override
    public void populate(E value) {
        binder.populate(value);
        super.populate(binder.getValue());
    }

    @Override
    public void setValue(E value) {
        binder.setValue(value);
        super.setValue(binder.getValue());
    }

    @Override
    public E getValue() {
        return binder.getValue();
    }

    @Override
    public boolean isValid() {
        if (!isEditable() || !isEnabled()) {
            return true;
        }
        for (CComponent<?> ccomponent : binder.getComponents()) {
            if (ccomponent instanceof CEditableComponent<?, ?> && !((CEditableComponent<?, ?>) ccomponent).isValid()) {
                return false;
            }
        }
        return true;
    }

    public ValidationResults getValidationResults() {
        ValidationResults validationResults = new ValidationResults();
        for (CComponent<?> ccomponent : binder.getComponents()) {
            if (ccomponent instanceof CEntityEditableComponent<?> && !((CEntityEditableComponent<?>) ccomponent).isValid()) {
                validationResults.appendValidationErrors(((CEntityEditableComponent<?>) ccomponent).getValidationResults());
            } else if (ccomponent instanceof CEditableComponent<?, ?>) {
                CEditableComponent<?, ?> editableComponent = (CEditableComponent<?, ?>) ccomponent;
                if (!editableComponent.isValid() && editableComponent.isVisited()) {
                    validationResults.appendValidationError(editableComponent.isVisited() + " Field '" + ccomponent.getTitle() + "'  is not valid. "
                            + editableComponent.getValidationMessage());
                }
            } else if (ccomponent instanceof CEntityFolder<?> && !((CEntityFolder<?>) ccomponent).isValid()) {
                validationResults.appendValidationErrors(((CEntityFolder<?>) ccomponent).getValidationResults());
            }
        }
        return validationResults;
    }

    @Override
    public void setVisited(boolean visited) {
        for (CComponent<?> ccomponent : binder.getComponents()) {
            if (ccomponent instanceof CEditableComponent<?, ?>) {
                ((CEditableComponent<?, ?>) ccomponent).setVisited(visited);
            }
        }
    }

    @Override
    public void onPropertyChange(PropertyChangeEvent event) {
        if (PropertyChangeEvent.PropertyName.VALIDITY.equals(event.getPropertyName())) {
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.VALIDITY);
        }
    }

    public void bind(CEditableComponent<?, ?> component, IObject<?> member) {
        binder.bind(component, member);
        component.addPropertyChangeHandler(this);

        if (component instanceof IFlexConextComponent) {
            ((IFlexConextComponent) component).createContent();
        }
    }

    public <T> CEditableComponent<T, ?> get(IObject<T> member) {
        return binder.get(member);
    }

    @Override
    protected NativeEntityEditor<E> createWidget() {
        return new NativeEntityEditor<E>();
    }

    public void setWidget(IsWidget widget) {
        asWidget().setWidget(widget);
    }

}
