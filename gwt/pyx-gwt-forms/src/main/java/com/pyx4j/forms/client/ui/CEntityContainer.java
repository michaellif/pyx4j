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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.widgets.client.Button;

public abstract class CEntityContainer<E extends IObject<?>> extends CComponent<E> implements IEditableComponentFactory {

    private static final Logger log = LoggerFactory.getLogger(CEntityContainer.class);

    private final HashMap<CComponent<?>, HandlerRegistration> propertyChangeHandlerRegistrations = new HashMap<CComponent<?>, HandlerRegistration>();

    private final HashMap<CComponent<?>, HandlerRegistration> valueChangeHandlerRegistrations = new HashMap<CComponent<?>, HandlerRegistration>();

    private ImageResource icon;

    private boolean initiated = false;

    private final SimplePanel contentHolder;

    private final ContainerPanel containerPanel;

    public CEntityContainer() {
        applyAccessibilityRules();
        containerPanel = new ContainerPanel();
        if (false) {
            Button debugButton = new Button("Debug", new Command() {

                @Override
                public void execute() {
                    log.info(CEntityContainer.this.getValue().toString());
                    new EntityViewerDialog(CEntityContainer.this.getValue()).show();
                }
            });
            debugButton.getElement().getStyle().setProperty("display", "inline-block");
            containerPanel.add(debugButton);
            containerPanel.getElement().getStyle().setProperty("border", "red solid 1px");
        }

        contentHolder = new SimplePanel();
        contentHolder.setStyleName(CEntityContainerTheme.StyleName.ContentHolder.name());

        containerPanel.add(contentHolder);
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
                                CEntityContainer.super.revalidate();
                                PropertyChangeEvent.fire(CEntityContainer.this, PropertyName.valid);
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
                            CEntityContainer.super.revalidate();
                            log.trace("CContainer.onValueChange fired from {}", shortDebugInfo());
                            ValueChangeEvent.fire(CEntityContainer.this, getValue());
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

    @Override
    public Widget asWidget() {
        return containerPanel;
    }

    public abstract IsWidget createContent();

    protected IDecorator<?> createDecorator() {
        return null;
    }

    @Override
    public void setDecorator(IDecorator decorator) {
        throw new Error("Use createDecorator() instead");
    }

    public final void initContent() {
        assert initiated == false;
        if (!initiated) {
            asWidget();
            IDecorator<?> decorator = createDecorator();
            if (decorator == null) {
                contentHolder.setWidget(createContent());
            } else {
                super.setDecorator(decorator);
                contentHolder.setWidget(getDecorator());
            }
            addValidations();
            if (ApplicationMode.isDevelopment()) {
                DevelopmentShortcutUtil.attachDevelopmentShortcuts(asWidget(), this);
            }
            initiated = true;
        }
    }

    public void setIcon(ImageResource icon) {
        this.icon = icon;
    }

    public ImageResource getIcon() {
        return icon;
    }

    @Override
    public boolean isVisited() {
        return true;
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
            initContent();
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

    class ContainerPanel extends FlowPanel implements RequiresResize, ProvidesResize {

        public ContainerPanel() {
        }

        @Override
        public void onResize() {
            if (contentHolder.getWidget() instanceof RequiresResize) {
                ((RequiresResize) contentHolder.getWidget()).onResize();
            }
        }
    }

}
