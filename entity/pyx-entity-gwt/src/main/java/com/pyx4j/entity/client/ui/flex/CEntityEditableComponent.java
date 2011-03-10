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

import java.util.Collection;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.ValidationResults;

public abstract class CEntityEditableComponent<E extends IEntity> extends CEditableComponent<E, NativeEntityEditor<E>> implements IFlexContentComponent,
        IComponentContainer, PropertyChangeHandler {

    private final EntityBinder<E> binder;

    private IFlexContentComponent bindParent;

    private EditableComponentsContainerHelper containerHelper;

    public CEntityEditableComponent(Class<E> clazz) {
        this(new EntityBinder<E>(clazz));
    }

    public CEntityEditableComponent(EntityBinder<E> binder) {
        this.binder = binder;
        containerHelper = new EditableComponentsContainerHelper(this);
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
    public Collection<? extends CEditableComponent<?, ?>> getComponents() {
        return binder.getComponents();
    }

    @Override
    public boolean isValid() {
        if (!isEditable() || !isEnabled()) {
            return true;
        }
        if (!super.isValid()) {
            return false;
        } else {
            return containerHelper.isValid();
        }
    }

    @Override
    public ValidationResults getValidationResults() {
        return containerHelper.getValidationResults();
    }

    @Override
    public void setVisited(boolean visited) {
        super.setVisited(visited);
        containerHelper.setVisited(visited);
    }

    @Override
    public void onPropertyChange(PropertyChangeEvent event) {
        if (PropertyChangeEvent.PropertyName.VALIDITY.equals(event.getPropertyName())) {
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.VALIDITY);
        }
    }

    public final void bind(CEditableComponent<?, ?> component, IObject<?> member) {
        binder.bind(component, member);
        component.addPropertyChangeHandler(this);
        component.addAccessAdapter(containerHelper);
        if (component instanceof IFlexContentComponent) {
            ((IFlexContentComponent) component).onBound(this);
        }
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        super.setDebugId(debugId);
        binder.setComponentsDebugId(this.getDebugId());
    }

    @Override
    public void onBound(IFlexContentComponent parent) {
        assert (bindParent == null) : "Flex Component " + this.getClass().getName() + " is already bound to " + bindParent;
        bindParent = parent;
        initContent();
    }

    private final void initContent() {
        attachContent();
        addValidations();
    }

    @Override
    public void addValidations() {

    }

    public void attachContent() {
        setWidget(createContent());
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        assert (bindParent != null) : "Flex Component " + this.getClass().getName() + "is not bound";
        return bindParent.create(member);
    }

    public final CEditableComponent<?, ?> inject(IObject<?> member) {
        CEditableComponent<?, ?> comp = create(member);
        bind(comp, member);
        return comp;
    }

    public final CEditableComponent<?, ?> inject(IObject<?> member, CEditableComponent<?, ?> comp) {
        bind(comp, member);
        return comp;
    }

    public <T> CEditableComponent<T, ?> get(IObject<T> member) {
        return binder.get(member);
    }

    public CEditableComponent<?, ?> getRaw(IObject<?> member) {
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
