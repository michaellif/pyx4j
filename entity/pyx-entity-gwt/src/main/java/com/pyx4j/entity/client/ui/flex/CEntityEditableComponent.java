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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.ValidationResults;

public class CEntityEditableComponent<E extends IEntity> extends CEditableComponent<E, NativeEntityEditor<E>> implements IFlexConextComponent {

    private final EntityBinder<E> binder;

    public CEntityEditableComponent(EntityBinder<E> binder) {
        this.binder = binder;
    }

    public CEntityEditableComponent(Class<E> clazz) {
        binder = new EntityBinder<E>(clazz);
    }

    @Override
    public void createContent() {
    }

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
    public boolean validate() {
        if (!isEditable() || !isEnabled()) {
            return true;
        }
        for (CComponent<?> ccomponent : binder.getComponents()) {
            if (ccomponent instanceof CEditableComponent<?, ?> && !((CEditableComponent<?, ?>) ccomponent).validate()) {
                return false;
            }
        }
        return true;
    }

    public ValidationResults getValidationResults() {
        ValidationResults validationResults = new ValidationResults();
        for (CComponent<?> ccomponent : binder.getComponents()) {
            if (ccomponent instanceof CEntityEditableComponent<?> && !((CEntityEditableComponent<?>) ccomponent).validate()) {
                validationResults.appendValidationErrors(((CEntityEditableComponent<?>) ccomponent).getValidationResults());
            } else if (ccomponent instanceof CEditableComponent<?, ?> && !((CEditableComponent<?, ?>) ccomponent).validate()) {
                validationResults.appendValidationError("Field '" + ccomponent.getTitle() + "'  is not valid. "
                        + ((CEditableComponent<?, ?>) ccomponent).getValidationMessage());
            } else if (ccomponent instanceof CEntityFolder<?> && !((CEntityFolder<?>) ccomponent).validate()) {
                validationResults.appendValidationErrors(((CEntityFolder<?>) ccomponent).getValidationResults());
            }
        }
        return validationResults;
    }

    public CEditableComponent<?, ?> bind(CEditableComponent<?, ?> component, IObject<?> member) {
        binder.bind(component, member);
        if (component instanceof IFlexConextComponent) {
            ((IFlexConextComponent) component).createContent();
        }
        return component;
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
