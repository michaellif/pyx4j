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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.events.HasPropertyChangeHandlers;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.MandatoryValidationFailure;
import com.pyx4j.forms.client.validators.MandatoryValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;

public abstract class CComponent<DATA_TYPE, WIDGET_TYPE extends INativeComponent<DATA_TYPE>> implements HasHandlers, HasPropertyChangeHandlers, IsWidget,
        HasValueChangeHandlers<DATA_TYPE> {

    private static final Logger log = LoggerFactory.getLogger(CComponent.class);

    private static final I18n i18n = I18n.get(CComponent.class);

    private String title;

    private String locationHint;

    private String tooltip;

    private CContainer<?, ?> parent;

    private CLayoutConstraints constraints;

    private final Collection<IAccessAdapter> accessAdapters = new ArrayList<IAccessAdapter>();

    private ComponentAccessAdapter componentAccessAdapter;

    private ContainerAccessAdapter containerAccessAdapter;

    private WIDGET_TYPE widget;

    private EventBus eventBus;

    private String width = "";

    private String height = "";

    private IDebugId debugId;

    private IDebugId debugIdSuffix;

    private String mandatoryValidationMessage = i18n.tr("this field can't be empty");

    private DATA_TYPE value = null;

    private List<EditableValueValidator<? super DATA_TYPE>> validators;

    // Have been changed after population
    private boolean visited = false;

    private boolean editing = false;

    private ValidationError validationError;

    public CComponent() {
        this(null);
    }

    public CComponent(String title) {
        this.title = title;
        componentAccessAdapter = new ComponentAccessAdapter();
        containerAccessAdapter = new ContainerAccessAdapter();
        addAccessAdapter(componentAccessAdapter);
        addAccessAdapter(containerAccessAdapter);
    }

    /**
     * Basic information would be available in server log
     */
    public static String runtimeCrashInfo(CComponent<?, ?> component) {
        if (component == null) {
            return "n/a";
        }
        return component.getClass() + " " + component.getTitle();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.title);
    }

    public String getLocationHint() {
        return locationHint;
    }

    public void setLocationHint(String locationHint) {
        this.locationHint = locationHint;
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.locationHint);
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
        if (isWidgetCreated()) {
            asWidget().setWidth(width);
        }
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
        if (isWidgetCreated()) {
            asWidget().setHeight(height);
        }
    }

    public CContainer<?, ?> getParent() {
        return parent;
    }

    public boolean isAttached() {
        return parent != null;
    }

    public void onAdopt(CContainer<?, ?> parent) {
        assert (this.parent == null) : "Component " + this.getClass().getName() + " is already bound to " + this.parent;
        this.parent = parent;
        setContainerAccessRules(true);

        setDebugIdSuffix(debugIdSuffix);
    }

    public void onAbandon() {
        parent = null;
        setContainerAccessRules(false);
        setDebugIdSuffix(null);
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.debugId);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (eventBus != null) {
            eventBus.fireEventFromSource(event, this);
        }
    }

    protected EventBus ensureHandlers() {
        return eventBus == null ? eventBus = new SimpleEventBus() : eventBus;
    }

    protected final <H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type) {
        return ensureHandlers().addHandler(type, handler);
    }

    private void setContainerAccessRules(boolean inherit) {
        if (parent != null) {
            if (inherit) {
                containerAccessAdapter.setContainer(parent);
            } else {
                containerAccessAdapter.setContainer(null);
            }
            applyAccessibilityRules();
        }
    }

    public final boolean isEnabled() {
        for (IAccessAdapter adapter : accessAdapters) {
            Boolean enabled = adapter.isEnabled(this);
            if (enabled != null && !enabled) {
                return false;
            }
        }
        return true;
    }

    public final void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            componentAccessAdapter.setEnabled(enabled);
            applyEnablingRules();
            revalidate();
            if (!enabled) {
                setVisited(false);
            }
        }
    }

    public final void inheritEnabled(boolean flag) {
        containerAccessAdapter.inheritEnabled(flag);
        applyEnablingRules();
    }

    public final boolean isVisible() {
        for (IAccessAdapter adapter : accessAdapters) {
            Boolean visible = adapter.isVisible(this);
            if (visible != null && !visible) {
                return false;
            }
        }
        return true;
    }

    public final void setVisible(boolean visible) {
        if (visible != isVisible()) {
            componentAccessAdapter.setVisible(visible);
            applyVisibilityRules();
            revalidate();
            if (!visible) {
                setVisited(false);
            }
        }
    }

    public final void inheritVisible(boolean flag) {
        containerAccessAdapter.inheritVisible(flag);
        applyVisibilityRules();
    }

    public final boolean isEditable() {
        for (IAccessAdapter adapter : accessAdapters) {
            Boolean editable = adapter.isEditable(this);
            if (editable != null && !editable) {
                return false;
            }
        }
        return true;
    }

    public final void setEditable(boolean editable) {
        if (editable != isEditable()) {
            componentAccessAdapter.setEditable(editable);
            applyEditabilityRules();
            revalidate();
            if (!editable) {
                setVisited(false);
            }
        }
    }

    public final void inheritEditable(boolean flag) {
        containerAccessAdapter.inheritEditable(flag);
        applyEditabilityRules();
    }

    public final boolean isViewable() {
        for (IAccessAdapter adapter : accessAdapters) {
            Boolean viewable = adapter.isViewable(this);
            if (viewable != null && adapter.isViewable(this)) {
                return true;
            }
        }
        return false;
    }

    public final void setViewable(boolean viewable) {
        if (viewable != isViewable()) {
            componentAccessAdapter.setViewable(viewable);
            applyViewabilityRules();
            revalidate();
            if (!viewable) {
                setVisited(false);
            }
        }
    }

    public final void inheritViewable(boolean flag) {
        containerAccessAdapter.inheritViewable(flag);
        applyViewabilityRules();
    }

    public boolean isMandatory() {
        if (validators == null) {
            return false;
        }
        for (EditableValueValidator<?> validator : validators) {
            if (validator instanceof MandatoryValidator) {
                return true;
            }
        }
        return false;
    }

    public void setMandatory(boolean mandatory) {
        if (isMandatory() != mandatory) {
            if (mandatory) {
                addValueValidator(new MandatoryValidator<DATA_TYPE>(mandatoryValidationMessage));
            } else {
                EditableValueValidator<? super DATA_TYPE> mandatoryValidator = null;
                for (EditableValueValidator<? super DATA_TYPE> validator : validators) {
                    if (validator instanceof MandatoryValidator) {
                        mandatoryValidator = validator;
                    }
                }
                removeValueValidator(mandatoryValidator);
            }
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.mandatory);
            revalidate();
        }
    }

    public boolean isMandatoryConditionMet() {
        return !(validationError instanceof MandatoryValidationFailure);
    }

    @Override
    public HandlerRegistration addPropertyChangeHandler(PropertyChangeHandler handler) {
        return addHandler(handler, PropertyChangeEvent.getType());
    }

    public void addAccessAdapter(IAccessAdapter adapter) {
        accessAdapters.add(adapter);
        applyAccessibilityRules();
    }

    public void removeAccessAdapter(IAccessAdapter adapter) {
        accessAdapters.remove(adapter);
        applyAccessibilityRules();
    }

    public boolean containsAccessAdapter(IAccessAdapter adapter) {
        return accessAdapters.contains(adapter);
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.tooltip);
    }

    protected abstract WIDGET_TYPE createWidget();

    protected void onWidgetCreated() {
        applyAccessibilityRules();
        widget.setWidth(getWidth());
        widget.setHeight(getHeight());

        setNativeValue(getValue());

        if (debugId != null) {
            widget.setDebugId(debugId);
        }

        this.addPropertyChangeHandler(widget);
    }

    public boolean isWidgetCreated() {
        return widget != null;
    }

    @Override
    public Widget asWidget() {
        if (widget == null) {
            try {
                widget = createWidget();
            } catch (Throwable e) {
                throw new UnrecoverableClientError("Widget could not be initialized", e);
            }
            onWidgetCreated();
        }
        return widget.asWidget();
    }

    public final WIDGET_TYPE getWidget() {
        return widget;
    }

    public IDebugId getDebugId() {
        return debugId;
    }

    public void setDebugIdSuffix(IDebugId debugIdSuffix) {
        this.debugIdSuffix = debugIdSuffix;
        if (debugIdSuffix != null) {
            if (parent != null) {
                debugId = new CompositeDebugId(parent.getDebugId(), debugIdSuffix);
            } else {
                debugId = debugIdSuffix;
            }
        } else {
            debugId = null;
        }
        if (isWidgetCreated()) {
            widget.setDebugId(debugId);
        }
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.debugId);

    }

    public void applyVisibilityRules() {
        boolean visible = isVisible();
        if (isWidgetCreated() && asWidget().isVisible() != visible) {
            asWidget().setVisible(visible);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.visible);
        }
    }

    public void applyEnablingRules() {
        boolean enabled = isEnabled();
        if (isWidgetCreated() && widget.isEnabled() != enabled) {
            widget.setEnabled(enabled);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.enabled);
        }
    }

    public void applyEditabilityRules() {
        boolean editable = isEditable();
        if (isWidgetCreated() && widget.isEditable() != editable) {
            widget.setEditable(editable);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.editable);
        }

    }

    public void applyViewabilityRules() {
        boolean viewable = isViewable();
        if (isWidgetCreated() && widget.isViewable() != viewable) {
            widget.setViewable(viewable);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.viewable);
        }
    }

    public void applyAccessibilityRules() {
        applyVisibilityRules();
        applyEnablingRules();
        applyViewabilityRules();
        applyEditabilityRules();
    }

    public CLayoutConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(CLayoutConstraints constraints) {
        this.constraints = constraints;
    }

    public boolean isValid() {
        return validationError == null;
    }

    public void revalidate() {

        ValidationError newValidationError = null;

        if (isVisible() && isEditable() && isEnabled() && !isViewable()) {

            if (validators != null) {
                for (EditableValueValidator<? super DATA_TYPE> validator : validators) {
                    ValidationError ve = validator.isValid((CComponent) this, getValue());
                    if (ve != null) {
                        ve.setTitle(getTitle());
                        ve.setLocationHint(getLocationHint());
                        newValidationError = ve;
                    }
                }
            }

        }

        if (newValidationError != validationError) {
            validationError = newValidationError;
            log.trace("CComponent.PropertyChangeEvent.valid fired from {}", shortDebugInfo());
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.valid);
        }
    }

    public void reset() {
        populate(null);
    }

    protected final void update(DATA_TYPE value) {
        if (!isValuesEquals(getValue(), value)) {
            this.value = value;
            revalidate();
            if (isValid()) {
                setNativeValue(value);
            }
            if (getParent() != null) {
                getParent().updateContainer(this, value);
            }
            ValueChangeEvent.fire(this, value);
        }
    }

    public final void setValue(DATA_TYPE value, boolean fireEvent, boolean populate) {
        if (!isValuesEquals(getValue(), value)) {
            this.value = preprocessValue(value, fireEvent, populate);
            setNativeValue(this.value);
            revalidate();
            if (getParent() != null) {
                getParent().updateContainer(this, value);
            }
            if (fireEvent) {
                ValueChangeEvent.fire(this, this.value);
            }
        }
        propagateValue(this.value, fireEvent, populate);
        if (populate) {
            setVisited(false);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.repopulated);
            onPopulate();
        }
    }

    public final void setValue(DATA_TYPE value, boolean fireEvent) {
        setValue(value, fireEvent, false);
    }

    /**
     * Attention! Fires onValueChange event
     * 
     * @param value
     */
    public final void setValue(DATA_TYPE value) {
        setValue(value, true, false);
    }

    /*
     * Call populate on init of component
     */
    public final void populate(DATA_TYPE value) {
        setValue(value, false, true);
    }

    protected void onPopulate() {
    }

    protected void propagateValue(DATA_TYPE value, boolean fireEvent, boolean populate) {
    }

    protected DATA_TYPE preprocessValue(DATA_TYPE value, boolean fireEvent, boolean populate) {
        return value;
    }

    public final DATA_TYPE getValue() {
        return value;
    }

    public boolean isValueEmpty() {
        return getValue() == null;
    }

    public boolean isValuesEquals(DATA_TYPE value1, DATA_TYPE value2) {
        return value1 == value2;
    }

    public ValidationResults getValidationResults() {
        ValidationResults results = new ValidationResults();
        if (validationError != null) {
            results.appendValidationError(getTitle(), validationError.getMessage(), getLocationHint());
        }
        return results;
    }

    public void setMandatoryValidationMessage(String message) {
        mandatoryValidationMessage = message;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean newVisited) {
        if (newVisited != visited) {
            visited = newVisited;
            if (this.visited) {
                revalidate();
            }
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.visited);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DATA_TYPE> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void addValueValidator(EditableValueValidator<? super DATA_TYPE> validator) {
        if (validators == null) {
            validators = new Vector<EditableValueValidator<? super DATA_TYPE>>();
        }
        validators.add(validator);
    }

    public boolean removeValueValidator(EditableValueValidator<? super DATA_TYPE> validator) {
        if (validators != null) {
            return validators.remove(validator);
        } else {
            return false;
        }
    }

    public void removeAllValueValidators() {
        if (validators != null) {
            validators.clear();
        }
    }

    @Override
    public String toString() {
        StringBuilder adaptersReport = new StringBuilder();
        for (IAccessAdapter adapter : accessAdapters) {
            adaptersReport.append(adapter.getClass().getName()).append(" ");
            adaptersReport.append("isEnabled ").append(adapter.isEnabled(this)).append(" ");
            adaptersReport.append("isEditable ").append(adapter.isEditable(this)).append(" ");
        }

        return "Type:" + this.getClass() + ";\n Title: " + getTitle() + ";\n value:" + getValue() + "; isMandatory=" + isMandatory() + ";\n isEnabled="
                + isEnabled() + "; isEditable=" + isEditable() + "; isVisible=" + isVisible() + "; isVisited=" + isVisited() + "; isValid=" + isValid()
                + "; toolTip=" + getTooltip() + "; size=" + getWidth() + ":" + getHeight() + "; adapters=[" + adaptersReport.toString() + "]";
    }

    public String shortDebugInfo() {
        return GWTJava5Helper.getSimpleName(this.getClass()) + ((debugId != null) ? " " + debugId : "");
    }

    protected void setNativeValue(DATA_TYPE value) {
        if (isWidgetCreated()) {
            widget.setNativeValue(value);
        }
    }

    public boolean isEditing() {
        return editing;
    }

    public void onEditingStart() {
        if (isEnabled() && isVisible() && isEditable() && !isViewable()) {
            editing = true;
        }
    }

    public void onEditingStop() {
        if (isEnabled() && isVisible() && isEditable() && !isViewable()) {
            boolean isOrigEmpty = isValueEmpty();
            editing = false;
            try {
                update(widget.getNativeValue());
            } catch (ParseException e) {
                update(null);
            }

            if (!isOrigEmpty || (isOrigEmpty && !isValueEmpty())) {
                setVisited(true);
            }
        }
    }

}
