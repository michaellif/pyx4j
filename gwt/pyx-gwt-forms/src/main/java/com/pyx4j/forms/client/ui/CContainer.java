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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.Collection;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;

public abstract class CContainer<DATA_TYPE, WIDGET_TYPE extends Widget & INativeEditableComponent<DATA_TYPE>> extends CComponent<DATA_TYPE, WIDGET_TYPE> {

    private static final Logger log = LoggerFactory.getLogger(CContainer.class);

    private final IAccessAdapter aggregatingAccessAdapter;

    private final HashMap<CComponent<?, ?>, HandlerRegistration> propertyChangeHandlerRegistrations = new HashMap<CComponent<?, ?>, HandlerRegistration>();

    private final HashMap<CComponent<?, ?>, HandlerRegistration> valueChangeHandlerRegistrations = new HashMap<CComponent<?, ?>, HandlerRegistration>();

    public CContainer() {
        this(null);
    }

    public CContainer(String title) {
        super(title);
        aggregatingAccessAdapter = new ContainerAccessAdapter(this);
    }

    public abstract Collection<? extends CComponent<?, ?>> getComponents();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void adopt(CComponent<?, ?> component) {

        propertyChangeHandlerRegistrations.put(component, component.addPropertyChangeHandler(new PropertyChangeHandler() {
            boolean sheduled = false;

            @Override
            public void onPropertyChange(final PropertyChangeEvent event) {
                if (!sheduled) {
                    sheduled = true;
                    Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            if (PropertyChangeEvent.PropertyName.valid.equals(event.getPropertyName())) {
                                log.trace("CEntityEditor.onPropertyChange fired from {}. Changed property is {}.", getTitle(), event.getPropertyName());
                                revalidate();
                                PropertyChangeEvent.fire(CContainer.this, PropertyChangeEvent.PropertyName.valid);

                            }
                            sheduled = false;
                        }
                    });
                }
            }
        }));

        valueChangeHandlerRegistrations.put(component, component.addValueChangeHandler(new ValueChangeHandler() {
            boolean sheduled = false;

            @Override
            public void onValueChange(final ValueChangeEvent event) {
                if (!sheduled) {
                    sheduled = true;
                    Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            revalidate();
                            log.trace("CEntityEditor.onValueChange fired from {}. New value is {}.", getTitle(), event.getValue());
                            ValueChangeEvent.fire(CContainer.this, getValue());
                            sheduled = false;
                        }
                    });
                }

            }
        }));

        component.onAdopt(this);
    }

    public void abandon(CComponent<?, ?> component) {
        propertyChangeHandlerRegistrations.remove(component).removeHandler();
        valueChangeHandlerRegistrations.remove(component).removeHandler();
        component.onAbandon();
    }

    @Override
    public boolean isValid() {
        if (!isEditable() || !isEnabled()) {
            return true;
        }
        if (getComponents() != null) {
            for (CComponent<?, ?> ccomponent : getComponents()) {
                if (!ccomponent.isValid()) {
                    return false;
                }
            }
        }
        return true;
    }

    public ValidationResults getValidationResults() {
        ValidationResults validationResults = new ValidationResults();
        if (getComponents() != null) {
            for (CComponent<?, ?> ccomponent : getComponents()) {
                if (ccomponent instanceof CContainer && !((CContainer<?, ?>) ccomponent).isValid()) {
                    validationResults.appendValidationErrors(((CContainer<?, ?>) ccomponent).getValidationResults());
                } else if (!((CComponent<?, ?>) ccomponent).isValid()) {
                    validationResults.appendValidationError("Field '" + ccomponent.getTitle() + "'  is not valid. "
                            + ((CComponent<?, ?>) ccomponent).getValidationMessage());
                }
            }
        }
        return validationResults;
    }

    public IAccessAdapter getContainerAccessAdapter() {
        return aggregatingAccessAdapter;
    }

    @Override
    public boolean isEditable() {
        for (IAccessAdapter adapter : getAccessAdapters()) {
            if (!adapter.isEditable(this)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setEditable(boolean editable) {
        defaultAccessAdapter.setEditable(editable);
        applyEditabilityRules();
    }

    @Override
    public void applyVisibilityRules() {
        super.applyVisibilityRules();
        if (getComponents() != null) {
            for (CComponent<?, ?> component : getComponents()) {
                component.applyVisibilityRules();
            }
        }
    }

    @Override
    public void applyEnablingRules() {
        super.applyEnablingRules();
        if (getComponents() != null) {
            for (CComponent<?, ?> component : getComponents()) {
                component.applyEnablingRules();
            }
        }
    }

    @Override
    public void applyEditabilityRules() {
        if (getComponents() != null) {
            for (CComponent<?, ?> component : getComponents()) {
                component.applyEditabilityRules();
            }
        }
    }

    @Override
    public void applyAccessibilityRules() {
        super.applyAccessibilityRules();
        applyEditabilityRules();
    }
}
