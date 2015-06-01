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
 * Created on May 31, 2011
 * @author michaellif
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.validators.EntityContainerValidator;
import com.pyx4j.widgets.client.Button;

public abstract class CContainer<SELF_TYPE extends CComponent<SELF_TYPE, DATA_TYPE, NContainer<DATA_TYPE>, DECORATOR_TYPE>, DATA_TYPE extends IObject<?>, DECORATOR_TYPE extends IDecorator<? super SELF_TYPE>>
        extends CComponent<SELF_TYPE, DATA_TYPE, NContainer<DATA_TYPE>, DECORATOR_TYPE> implements IEditableComponentFactory {

    private static final Logger log = LoggerFactory.getLogger(CContainer.class);

    private final HashMap<CComponent<?, ?, ?, ?>, HandlerRegistration> propertyChangeHandlerRegistrations = new HashMap<CComponent<?, ?, ?, ?>, HandlerRegistration>();

    private final HashMap<CComponent<?, ?, ?, ?>, HandlerRegistration> valueChangeHandlerRegistrations = new HashMap<CComponent<?, ?, ?, ?>, HandlerRegistration>();

    private ImageResource icon;

    private boolean initiated = false;

    @SuppressWarnings("unchecked")
    public CContainer() {

        setNativeComponent(new NContainer<DATA_TYPE>(this));

        if (false) {
            Button debugButton = new Button("Debug", new Command() {

                @Override
                public void execute() {
                    new EntityViewerDialog(CContainer.this.getValue()).show();
                }
            });
            debugButton.getElement().getStyle().setProperty("display", "inline-block");
            getNativeComponent().add(debugButton);
            getNativeComponent().getElement().getStyle().setProperty("border", "red solid 1px");
        }

        applyAccessibilityRules();

        addComponentValidator(new EntityContainerValidator());
    }

    public abstract Collection<? extends CComponent<?, ?, ?, ?>> getComponents();

    protected abstract void setComponentsValue(DATA_TYPE value, boolean fireEvent, boolean populate);

    /**
     * Implementations should call super to avoid setting value to null.
     */
    @Override
    protected DATA_TYPE preprocessValue(DATA_TYPE value, boolean fireEvent, boolean populate) {
        if (!populate && value == null) {
            value = getValue();
            if (value != null) {
                value.clear();
            }
        }
        return super.preprocessValue(value, fireEvent, populate);
    }

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

    protected <T> void updateContainer(CComponent<?, T, ?, ?> component) {

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void adopt(final CComponent<?, ?, ?, ?> component) {

        propertyChangeHandlerRegistrations.put(component, component.addPropertyChangeHandler(new PropertyChangeHandler() {

            @Override
            public void onPropertyChange(final PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.valid)) {
                    revalidate();
                    PropertyChangeEvent.fire(CContainer.this, PropertyName.valid);
                }
            }
        }));

        valueChangeHandlerRegistrations.put(component, component.addValueChangeHandler(new ValueChangeHandler() {

            @Override
            public void onValueChange(final ValueChangeEvent event) {
                CContainer.super.revalidate();
                log.trace("CContainer.onValueChange fired from {}", shortDebugInfo());
                ValueChangeEvent.fire(CContainer.this, getValue());
            }
        }));

        component.onAdopt(this);
    }

    public void abandon(CComponent<?, ?, ?, ?> component) {
        propertyChangeHandlerRegistrations.remove(component).removeHandler();
        valueChangeHandlerRegistrations.remove(component).removeHandler();
        component.onAbandon();
    }

    public void setVisitedRecursive() {
        if (getComponents() != null) {
            for (CComponent<?, ?, ?, ?> ccomponent : getComponents()) {
                if (ccomponent instanceof CField) {
                    ((CField<?, ?>) ccomponent).setVisited(true);
                } else if (ccomponent instanceof CContainer) {
                    ((CContainer<?, ?, ?>) ccomponent).setVisitedRecursive();
                }
            }
        }
        setVisited(true);
    }

    @Override
    public boolean isValidatable() {
        return isVisible() && !isViewable() && isPopulated();
    }

    @Override
    protected void onReset() {
        if (getComponents() != null) {
            for (CComponent<?, ?, ?, ?> ccomponent : getComponents()) {
                ccomponent.reset();
            }
        }
        super.onReset();
    }

    @Override
    public void applyVisibilityRules() {
        super.applyVisibilityRules();
        asWidget().setVisible(isVisible());
        if (getComponents() != null) {
            for (CComponent<?, ?, ?, ?> component : getComponents()) {
                component.applyVisibilityRules();
            }
        }

        //TODO Workaround to fire event for container - that should be reviewed - event should be fired
        //on accessibility adapters change
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.visible);
    }

    @Override
    public void applyViewabilityRules() {
        super.applyViewabilityRules();
        if (getComponents() != null) {
            for (CComponent<?, ?, ?, ?> component : getComponents()) {
                component.applyViewabilityRules();
            }
        }
        //TODO Workaround to fire event for container - that should be reviewed - event should be fired
        //on accessibility adapters change
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.viewable);

    }

    @Override
    public void applyEnablingRules() {
        super.applyEnablingRules();
        if (getComponents() != null) {
            for (CComponent<?, ?, ?, ?> component : getComponents()) {
                component.applyEnablingRules();
            }
        }
        //TODO Workaround to fire event for container - that should be reviewed - event should be fired
        //on accessibility adapters change
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.enabled);

    }

    @Override
    public void applyEditabilityRules() {
        super.applyEditabilityRules();
        if (getComponents() != null) {
            for (CComponent<?, ?, ?, ?> component : getComponents()) {
                component.applyEditabilityRules();
            }
        }
        //TODO Workaround to fire editable event for container - that should be reviewed - event should be fired
        //on accessibility adapters change
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.editable);
    }

    @Override
    protected void setDebugId(IDebugId debugId) {
        asWidget().ensureDebugId(debugId == null ? null : debugId.debugId());

    }

    protected abstract IsWidget createContent();

    @Deprecated
    /**
     * @deprecated use {@link setDecorator(IDecorator decorator)}
     */
    protected DECORATOR_TYPE createDecorator() {
        return null;
    }

    public final void init() {
        assert initiated == false;
        if (!initiated) {
            asWidget();

            getNativeComponent().setContent(createContent());

            DECORATOR_TYPE decorator = createDecorator();
            if (decorator != null) {
                setDecorator(decorator);
            }

            addValidations();

            if (ApplicationMode.isDemo() || ApplicationMode.isDevelopment()) {
                DevelopmentShortcutUtil.attachDevelopmentShortcuts(asWidget(), this);
            }

            initiated = true;
        }
    }

    @Override
    public CField<?, ?> create(IObject<?> member) {
        assert (getParent() != null) : "Flex Component " + this.getClass().getName() + " is not bound";
        return getParent().create(member);
    }

    @Override
    public void onAdopt(CContainer<?, ?, ?> parent) {
        super.onAdopt(parent);
        if (!initiated) {
            init();
        }
    }

    public void addValidations() {

    }

    public final HandlerRegistration addDevShortcutHandler(DevShortcutHandler handler) {
        return addHandler(handler, DevShortcutEvent.getType());
    }

    public void setIcon(ImageResource icon) {
        this.icon = icon;
    }

    public ImageResource getIcon() {
        return icon;
    }

}
