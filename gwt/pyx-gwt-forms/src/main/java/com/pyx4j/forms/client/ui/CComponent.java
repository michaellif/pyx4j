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
import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.forms.client.events.HasPropertyChangeHandlers;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.MandatoryValidationFailure;
import com.pyx4j.forms.client.validators.MandatoryValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;

public abstract class CComponent<DATA_TYPE, WIDGET_TYPE extends Widget & INativeEditableComponent<DATA_TYPE>> implements HasHandlers,
        HasPropertyChangeHandlers, IsWidget, HasValueChangeHandlers<DATA_TYPE> {

    private static I18n i18n = I18n.get(CComponent.class);

    private String title;

    private String tooltip;

    private CContainer<?, ?> parent;

    private CLayoutConstraints constraints;

    private boolean inheritContainerAccessRules = true;

    private final Collection<IAccessAdapter> accessAdapters = new ArrayList<IAccessAdapter>();

    ComponentAccessAdapter defaultAccessAdapter;

    private WIDGET_TYPE widget;

    private EventBus eventBus;

    private String width = "";

    private String height = "";

    private IDebugId debugId;

    private String mandatoryValidationMessage = i18n.tr("This field is Mandatory");

    private DATA_TYPE value = null;

    private List<EditableValueValidator<? super DATA_TYPE>> validators;

    // Have been changed after population
    private boolean visited = false;

    private boolean editing = false;

    private ValidationFailure validationFailure;

    public CComponent() {
        this(null);
    }

    public CComponent(String title) {
        this.title = title;
        defaultAccessAdapter = new ComponentAccessAdapter();
        addAccessAdapter(defaultAccessAdapter);
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
        setContainerAccessRules(inheritContainerAccessRules);
    }

    public void onAbandon() {
        parent = null;
        setContainerAccessRules(false);
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

    public void inheritContainerAccessRules(boolean inherit) {
        inheritContainerAccessRules = inherit;
        setContainerAccessRules(inherit);
    }

    private void setContainerAccessRules(boolean inherit) {
        if (parent != null) {
            if (inherit) {
                if (!containsAccessAdapter(parent.getContainerAccessAdapter())) {
                    addAccessAdapter(parent.getContainerAccessAdapter());
                }
            } else {
                removeAccessAdapter(parent.getContainerAccessAdapter());
            }
        }
    }

    public boolean isEnabled() {
        for (IAccessAdapter adapter : accessAdapters) {
            if (!adapter.isEnabled(this)) {
                return false;
            }
        }
        return true;
    }

    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            defaultAccessAdapter.setEnabled(enabled);
            applyEnablingRules();
            revalidate();
            if (!enabled) {
                setVisited(false);
            }
        }
    }

    public boolean isVisible() {
        for (IAccessAdapter adapter : accessAdapters) {
            if (!adapter.isVisible(this)) {
                return false;
            }
        }
        return true;
    }

    public void setVisible(boolean visible) {
        if (visible != isVisible()) {
            defaultAccessAdapter.setVisible(visible);
            applyVisibilityRules();
            revalidate();
            if (!visible) {
                setVisited(false);
            }
        }
    }

    public boolean isEditable() {
        for (IAccessAdapter adapter : getAccessAdapters()) {
            if (!adapter.isEditable(this)) {
                return false;
            }
        }
        return true;
    }

    public void setEditable(boolean editable) {
        if (editable != isEditable()) {
            defaultAccessAdapter.setEditable(editable);
            applyEditabilityRules();
            revalidate();
            if (!editable) {
                setVisited(false);
            }
        }
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
            addValueValidator(new MandatoryValidator<DATA_TYPE>(mandatoryValidationMessage));
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.mandatory);
            revalidate();
        }
    }

    public boolean isMandatoryConditionMet() {
        return !(validationFailure instanceof MandatoryValidationFailure);
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
        asWidget().setWidth(getWidth());
        asWidget().setHeight(getHeight());
        setNativeValue(getValue());
        this.addPropertyChangeHandler(asWidget());
    }

    public boolean isWidgetCreated() {
        return widget != null;
    }

    @Override
    public WIDGET_TYPE asWidget() {
        if (widget == null) {
            try {
                widget = createWidget();
            } catch (Throwable e) {
                throw new Error("Widget could not be initialized", e);
            }
            if (getDebugId() != null) {
                setDebugId(getDebugId());
            }
            onWidgetCreated();
        }
        return widget;
    }

    public IDebugId getDebugId() {
        if ((parent != null) && (debugId != null)) {
            return new CompositeDebugId(parent.getDebugId(), debugId);
        } else {
            return debugId;
        }
    }

    public void setDebugId(IDebugId debugId) {
        this.debugId = debugId;
        if ((widget != null) && (debugId != null)) {
            widget.ensureDebugId(getDebugId().debugId());
        }
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
        if (isWidgetCreated() && asWidget().isEnabled() != enabled) {
            asWidget().setEnabled(enabled);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.enabled);
        }
    }

    public void applyEditabilityRules() {
        boolean editable = isEditable();
        if (isWidgetCreated() && asWidget().isEditable() != editable) {
            asWidget().setEditable(editable);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.editable);
        }
    }

    public void applyAccessibilityRules() {
        applyVisibilityRules();
        applyEnablingRules();
        applyEditabilityRules();
    }

    protected Collection<IAccessAdapter> getAccessAdapters() {
        return accessAdapters;
    }

    public CLayoutConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(CLayoutConstraints constraints) {
        this.constraints = constraints;
    }

    public boolean isValid() {
        return validationFailure == null;
    }

    public void revalidate() {

        ValidationFailure newValidationFailure = null;

        if (isVisible() && isEditable() && isEnabled()) {
            newValidationFailure = getValidationResult();
        }

        if (newValidationFailure != validationFailure) {
            validationFailure = newValidationFailure;
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.valid);
        }
    }

    protected boolean update(DATA_TYPE value) {
        if (!isValuesEquals(getValue(), value)) {
            this.value = value;
            revalidate();
            ValueChangeEvent.fire(this, value);
            return true;
        }
        return false;
    }

    public void setValue(DATA_TYPE value, boolean fireEvent) {
        if (getValue() != value) {
            this.value = value;
            setNativeValue(value);
            revalidate();
            if (fireEvent) {
                ValueChangeEvent.fire(this, value);
            }
        }
    }

    public void setValue(DATA_TYPE value) {
        setValue(value, true);
    }

    public DATA_TYPE getValue() {
        return value;
    }

    /*
     * Call populate on init of component
     */
    public void populate(DATA_TYPE value) {

        setValue(value, false);

        setVisited(false);
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.repopulated);
    }

    public boolean isValueEmpty() {
        return getValue() == null;
    }

    public boolean isValuesEquals(DATA_TYPE value1, DATA_TYPE value2) {
        return EqualsHelper.equals(value1, value2);
    }

    public String getValidationMessage() {
        return validationFailure == null ? null : validationFailure.getMessage();

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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private ValidationFailure getValidationResult() {
        if (validators != null) {
            for (EditableValueValidator<? super DATA_TYPE> validator : validators) {
                ValidationFailure failure = validator.isValid((CComponent) this, getValue());
                if (failure != null) {
                    return failure;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder adaptersReport = new StringBuilder();
        for (IAccessAdapter adapter : getAccessAdapters()) {
            adaptersReport.append(adapter.getClass().getName()).append(" ");
            adaptersReport.append("isEnabled ").append(adapter.isEnabled(this)).append(" ");
            adaptersReport.append("isEditable ").append(adapter.isEditable(this)).append(" ");
        }

        return "Type:" + this.getClass() + ";\n Title: " + getTitle() + ";\n value:" + getValue() + "; isMandatory=" + isMandatory() + ";\n isEnabled="
                + isEnabled() + "; isEditable=" + isEditable() + "; isVisible=" + isVisible() + "; isVisited=" + isVisited() + "; isValid=" + isValid()
                + "; toolTip=" + getTooltip() + "; size=" + getWidth() + ":" + getHeight() + "; adapters=[" + adaptersReport.toString() + "]";
    }

    public String shortDebugInfo() {
        return GWTJava5Helper.getSimpleName(this.getClass()) + ((getDebugId() != null) ? " " + getDebugId() : "");
    }

    protected void setNativeValue(DATA_TYPE value) {
        if (isWidgetCreated()) {
            asWidget().setNativeValue(value);
        }
    }

    public boolean isEditing() {
        return editing;
    }

    public void onEditingStart() {
        if (isEnabled() && isVisible() && isEditable()) {
            editing = true;
        }
    }

    public void onEditingStop() {
        if (isEnabled() && isVisible() && isEditable()) {
            boolean isOrigEmpty = isValueEmpty();
            editing = false;
            try {
                update(asWidget().getNativeValue());
            } catch (ParseException e) {
                update(null);
            }

            if (!isOrigEmpty || (isOrigEmpty && !isValueEmpty())) {
                setVisited(true);
            }
        }
    }

}
