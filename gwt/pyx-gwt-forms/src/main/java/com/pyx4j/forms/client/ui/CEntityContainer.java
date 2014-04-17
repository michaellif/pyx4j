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
 * @version $Id$
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
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

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

public abstract class CEntityContainer<E extends IObject<?>> extends CComponent<E> implements IEditableComponentFactory {

    private static final Logger log = LoggerFactory.getLogger(CEntityContainer.class);

    private final HashMap<CComponent<?>, HandlerRegistration> propertyChangeHandlerRegistrations = new HashMap<CComponent<?>, HandlerRegistration>();

    private final HashMap<CComponent<?>, HandlerRegistration> valueChangeHandlerRegistrations = new HashMap<CComponent<?>, HandlerRegistration>();

    private ImageResource icon;

    private boolean initiated = false;

    private final NEntityContainer nativeComponent;

    @SuppressWarnings("unchecked")
    public CEntityContainer() {
        nativeComponent = new NEntityContainer();

        if (false) {
            Button debugButton = new Button("Debug", new Command() {

                @Override
                public void execute() {
                    new EntityViewerDialog(CEntityContainer.this.getValue()).show();
                }
            });
            debugButton.getElement().getStyle().setProperty("display", "inline-block");
            nativeComponent.add(debugButton);
            nativeComponent.getElement().getStyle().setProperty("border", "red solid 1px");
        }

        applyAccessibilityRules();

        addComponentValidator(new EntityContainerValidator());
    }

    public abstract Collection<? extends CComponent<?>> getComponents();

    protected abstract void setComponentsValue(E value, boolean fireEvent, boolean populate);

    @Override
    protected void onValuePropagation(E value, boolean fireEvent, boolean populate) {
        super.onValuePropagation(value, fireEvent, populate);
        setComponentsValue(value, fireEvent, populate);
    }

    @Override
    protected void setEditorValue(E value) {

    }

    @Override
    protected E getEditorValue() throws ParseException {
        return null;
    }

    protected <T> void updateContainer(CComponent<T> component) {

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void adopt(final CComponent<?> component) {

        propertyChangeHandlerRegistrations.put(component, component.addPropertyChangeHandler(new PropertyChangeHandler() {

            @Override
            public void onPropertyChange(final PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.valid)) {
                    boolean wasValid = isValid();
                    CEntityContainer.super.revalidate();
                    if (wasValid != isValid()) {
                        PropertyChangeEvent.fire(CEntityContainer.this, PropertyName.valid);
                    }

                }
            }
        }));

        valueChangeHandlerRegistrations.put(component, component.addValueChangeHandler(new ValueChangeHandler() {

            @Override
            public void onValueChange(final ValueChangeEvent event) {
                CEntityContainer.super.revalidate();
                log.trace("CContainer.onValueChange fired from {}", shortDebugInfo());
                ValueChangeEvent.fire(CEntityContainer.this, getValue());
            }
        }));

        component.onAdopt(this);
    }

    public void abandon(CComponent<?> component) {
        propertyChangeHandlerRegistrations.remove(component).removeHandler();
        valueChangeHandlerRegistrations.remove(component).removeHandler();
        component.onAbandon();
    }

    public void setVisitedRecursive() {
        if (getComponents() != null) {
            for (CComponent<?> ccomponent : getComponents()) {
                if (ccomponent instanceof CField) {
                    ((CField<?, ?>) ccomponent).setVisited(true);
                } else if (ccomponent instanceof CEntityContainer) {
                    ((CEntityContainer<?>) ccomponent).setVisitedRecursive();
                }
            }
        }
        setVisited(true);
    }

    @Override
    public boolean isValidatable() {
        return isVisible() && isEditable() && isEnabled() && !isViewable() && isVisited();
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
    public void applyVisibilityRules() {
        super.applyVisibilityRules();
        asWidget().setVisible(isVisible());
        if (getComponents() != null) {
            for (CComponent<?> component : getComponents()) {
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
            for (CComponent<?> component : getComponents()) {
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
            for (CComponent<?> component : getComponents()) {
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
            for (CComponent<?> component : getComponents()) {
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

    @Override
    public Widget asWidget() {
        return nativeComponent;
    }

    protected abstract IsWidget createContent();

    @Deprecated
    /**
     * @deprecated use {@link setDecorator(IDecorator decorator)}
     */
    protected IDecorator<?> createDecorator() {
        return null;
    }

    public final void init() {
        assert initiated == false;
        if (!initiated) {
            asWidget();

            nativeComponent.setContent(createContent());

            IDecorator<?> decorator = createDecorator();
            if (decorator != null) {
                setDecorator(decorator);
            }

            addValidations();

            if (ApplicationMode.isDevelopment()) {
                DevelopmentShortcutUtil.attachDevelopmentShortcuts(asWidget(), this);
            }

            initiated = true;
        }
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        assert (getParent() != null) : "Flex Component " + this.getClass().getName() + "is not bound";
        return ((CEntityContainer<?>) getParent()).create(member);
    }

    @Override
    public void onAdopt(CEntityContainer<?> parent) {
        super.onAdopt(parent);
        if (!initiated) {
            init();
        }
    }

    @Override
    public void onAbandon() {
        super.onAbandon();
    }

    public void addValidations() {

    }

    public final HandlerRegistration addDevShortcutHandler(DevShortcutHandler handler) {
        return addHandler(handler, DevShortcutEvent.getType());
    }

    @Override
    public final INativeComponent<E> getNativeComponent() {
        return nativeComponent;
    }

    public void setIcon(ImageResource icon) {
        this.icon = icon;
    }

    public ImageResource getIcon() {
        return icon;
    }

    class NEntityContainer extends SimplePanel implements RequiresResize, ProvidesResize, INativeComponent<E> {

        private IsWidget content;

        public NEntityContainer() {
        }

        @Override
        public IsWidget getContent() {
            return content;
        }

        @SuppressWarnings({ "unchecked" })
        public void setContent(IsWidget content) {
            this.content = content;
            content.asWidget().setStyleName(CComponentTheme.StyleName.CEntityContainerContentHolder.name());
            if (getWidget() instanceof IDecorator) {
                ((IDecorator<CEntityContainer<E>>) getWidget()).setContent(content);
                ((IDecorator<CEntityContainer<E>>) getWidget()).init(CEntityContainer.this);
            } else {
                setWidget(content);
            }
        }

        @Override
        public SimplePanel getContentHolder() {
            return this;
        }

        @Override
        public void onResize() {
            if (content instanceof RequiresResize) {
                ((RequiresResize) content).onResize();
            }
        }

        @Override
        public CComponent<E> getCComponent() {
            return CEntityContainer.this;
        }

    }

}
