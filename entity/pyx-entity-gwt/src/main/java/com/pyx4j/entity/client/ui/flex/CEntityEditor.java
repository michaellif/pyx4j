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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.ValidationResults;

public abstract class CEntityEditor<E extends IEntity> extends CEditableComponent<E, NativeEntityEditor<E>> implements IFlexContentComponent,
        IComponentContainer {

    private static final Logger log = LoggerFactory.getLogger(CEntityEditor.class);

    private final EntityBinder<E> binder;

    private IFlexContentComponent bindParent;

    private EditableComponentsContainerHelper containerHelper;

    public CEntityEditor(Class<E> clazz) {
        this(new EntityBinder<E>(clazz));
    }

    public CEntityEditor(EntityBinder<E> binder) {
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
        return containerHelper.getAllValidationResults();
    }

    @Override
    public void setVisited(boolean visited) {
        super.setVisited(visited);
        containerHelper.setVisited(visited);
    }

    public final <T> void bind(CEditableComponent<T, ?> component, IObject<?> member) {
        binder.bind(component, member);

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
                                log.debug("CEntityEditableComponent.onPropertyChange fired from {}. Changed property is {}.",
                                        CEntityEditor.this.getTitle(), event.getPropertyName());
                                revalidate();
                                PropertyChangeEvent.fire(CEntityEditor.this, PropertyChangeEvent.PropertyName.VALIDITY);

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
                            revalidate();
                            log.debug("CEntityEditableComponent.onValueChange fired from {}. New value is {}.", CEntityEditor.this.getTitle(),
                                    event.getValue());
                            ValueChangeEvent.fire(CEntityEditor.this, getValue());
                            sheduled = false;
                        }
                    });
                }

            }
        });

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

    final void initContent() {
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

    @SuppressWarnings("unchecked")
    public <T extends IEntity> CEditableComponent<T, ?> get(T member) {
        return (CEditableComponent<T, ?>) binder.get((IObject<?>) member);
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

    @Override
    public boolean isVisited() {
        return true;
    }
}
