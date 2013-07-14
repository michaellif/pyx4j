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

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.i18n.shared.I18n;

public abstract class CContainer<DATA_TYPE> extends CComponent<DATA_TYPE> {

    private static final Logger log = LoggerFactory.getLogger(CContainer.class);

    private static final I18n i18n = I18n.get(CContainer.class);

    private final HashMap<CComponent<?>, HandlerRegistration> propertyChangeHandlerRegistrations = new HashMap<CComponent<?>, HandlerRegistration>();

    private final HashMap<CComponent<?>, HandlerRegistration> valueChangeHandlerRegistrations = new HashMap<CComponent<?>, HandlerRegistration>();

    public CContainer() {
        this(null);
    }

    public CContainer(String title) {
        super(title);
        applyAccessibilityRules();
    }

    public abstract Collection<? extends CComponent<?>> getComponents();

    protected abstract void setComponentsValue(DATA_TYPE value, boolean fireEvent, boolean populate);

    @Override
    protected void onValuePropagation(DATA_TYPE value, boolean fireEvent, boolean populate) {
        super.onValuePropagation(value, fireEvent, populate);
        setComponentsValue(value, fireEvent, populate);
    }

    @Override
    protected void setEditorValue(DATA_TYPE value) {

    }

    @Override
    protected DATA_TYPE getEditorValue() throws ParseException {
        return null;
    }

    protected <T> void updateContainer(CComponent<T> component) {

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void adopt(final CComponent<?> component) {

        propertyChangeHandlerRegistrations.put(component, component.addPropertyChangeHandler(new PropertyChangeHandler() {
            boolean sheduled = false;

            @Override
            public void onPropertyChange(final PropertyChangeEvent event) {
                if (!sheduled) {
                    sheduled = true;
                    Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            if (event.isEventOfType(PropertyName.valid)) {
                                log.trace("CContainer.onPropertyChange fired from {}. Changed property is {}.", shortDebugInfo(), event.getPropertyName());
                                CContainer.super.revalidate();
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
                            CContainer.super.revalidate();
                            log.trace("CContainer.onValueChange fired from {}", shortDebugInfo());
                            ValueChangeEvent.fire(CContainer.this, getValue());
                            sheduled = false;
                        }
                    });
                }

            }
        }));

        component.onAdopt(this);
    }

    public void abandon(CComponent<?> component) {
        propertyChangeHandlerRegistrations.remove(component).removeHandler();
        valueChangeHandlerRegistrations.remove(component).removeHandler();
        component.onAbandon();
    }

    @Override
    public void setVisited(boolean visited) {
        super.setVisited(visited);
        if (getComponents() != null) {
            for (CComponent<?> ccomponent : getComponents()) {
                ((CComponent<?>) ccomponent).setVisited(visited);
            }
        }
    }

    @Override
    public void setUnconditionalValidationErrorRendering(boolean flag) {
        super.setUnconditionalValidationErrorRendering(flag);
        if (getComponents() != null) {
            for (CComponent<?> ccomponent : getComponents()) {
                ((CComponent<?>) ccomponent).setUnconditionalValidationErrorRendering(flag);
            }
        }
    }

    @Override
    public boolean isValid() {
        if (getComponents() != null) {
            for (CComponent<?> ccomponent : getComponents()) {
                if (!ccomponent.isValid()) {
                    return false;
                }
            }
        }
        return super.isValid();
    }

    @Override
    protected void onReset() {
        if (getComponents() != null) {
            for (CComponent<?> ccomponent : getComponents()) {
                ccomponent.reset();
            }
        }
        super.onReset();
    }

    @Override
    public ValidationResults getValidationResults() {
        ValidationResults validationResults = super.getValidationResults();
        if (getComponents() != null) {
            for (CComponent<?> component : this.getComponents()) {
                if (!component.isValid()) {
                    validationResults.appendValidationErrors(component.getValidationResults());
                }
            }
        }
        return validationResults;
    }

    @Override
    public void applyVisibilityRules() {
        super.applyVisibilityRules();
        if (getComponents() != null) {
            for (CComponent<?> component : getComponents()) {
                component.applyVisibilityRules();
            }
        }
    }

    @Override
    public void applyViewabilityRules() {
        super.applyViewabilityRules();
        if (getComponents() != null) {
            for (CComponent<?> component : getComponents()) {
                component.applyViewabilityRules();
            }
        }
    }

    @Override
    public void applyEnablingRules() {
        super.applyEnablingRules();
        if (getComponents() != null) {
            for (CComponent<?> component : getComponents()) {
                component.applyEnablingRules();
            }
        }
    }

    @Override
    public void applyEditabilityRules() {
        super.applyEditabilityRules();
        if (getComponents() != null) {
            for (CComponent<?> component : getComponents()) {
                component.applyEditabilityRules();
            }
        }
    }

    @Override
    public void revalidate() {
        if (getComponents() != null) {
            for (CComponent<?> component : getComponents()) {
                component.revalidate();
            }
        }
        super.revalidate();
    }

    @Override
    protected void setDebugId(IDebugId debugId) {
        asWidget().ensureDebugId(debugId == null ? null : debugId.debugId());

    }
}
