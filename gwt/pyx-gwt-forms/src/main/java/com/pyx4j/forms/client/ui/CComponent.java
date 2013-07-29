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
import java.util.LinkedList;
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

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.events.HasPropertyChangeHandlers;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.DefaultCComponentsTheme.StyleDependent;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.MandatoryValidationFailure;
import com.pyx4j.forms.client.validators.MandatoryValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.i18n.shared.I18n;

public abstract class CComponent<DATA_TYPE> implements HasHandlers, HasPropertyChangeHandlers, IsWidget, HasValueChangeHandlers<DATA_TYPE> {

    private static final Logger log = LoggerFactory.getLogger(CComponent.class);

    private static final I18n i18n = I18n.get(CComponent.class);

    public static enum NoteStyle {

        Info(DefaultCComponentsTheme.StyleDependent.info),

        Warn(DefaultCComponentsTheme.StyleDependent.warning);

        private NoteStyle(StyleDependent style) {
            this.style = style;
        }

        DefaultCComponentsTheme.StyleDependent style;

        public DefaultCComponentsTheme.StyleDependent getStyle() {
            return style;
        }
    }

    private String title;

    private String locationHint;

    private String tooltip;

    private String note;

    private NoteStyle noteStyle;

    private CContainer<?> parent;

    private CLayoutConstraints constraints;

    private final Collection<IAccessAdapter> accessAdapters = new ArrayList<IAccessAdapter>();

    private ComponentAccessAdapter componentAccessAdapter;

    private ContainerAccessAdapter containerAccessAdapter;

    private EventBus eventBus;

    private IDebugId debugId;

    private IDebugId debugIdSuffix;

    private String mandatoryValidationMessage = i18n.tr("this field can't be empty");

    private DATA_TYPE value = null;

    private List<EditableValueValidator<DATA_TYPE>> validators;

    // Have been changed after population
    private boolean visited = false;

    private boolean editingInProgress = false;

    private ValidationError validationError;

    private IDecorator<?> decorator;

    private boolean unconditionalValidationErrorRendering;

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
    public static String runtimeCrashInfo(CComponent<?> component) {
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

    public CContainer<?> getParent() {
        return parent;
    }

    public boolean isAttached() {
        return parent != null;
    }

    public void onAdopt(CContainer<?> parent) {
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
            Boolean enabled = adapter.isEnabled();
            if (enabled != null && !enabled) {
                return false;
            }
        }
        return true;
    }

    public final void setEnabled(boolean enabled) {
        boolean before = isEnabled();
        componentAccessAdapter.setEnabled(enabled);
        if (before != isEnabled()) {
            applyEnablingRules();
            revalidate();
        }
    }

    public final void inheritEnabled(boolean flag) {
        containerAccessAdapter.inheritEnabled(flag);
        applyEnablingRules();
    }

    public final boolean isVisible() {
        for (IAccessAdapter adapter : accessAdapters) {
            Boolean visible = adapter.isVisible();
            if (visible != null && !visible) {
                return false;
            }
        }
        return true;
    }

    public final void setVisible(boolean visible) {
        boolean before = isVisible();
        componentAccessAdapter.setVisible(visible);
        if (before != isVisible()) {
            applyVisibilityRules();
            revalidate();
        }
    }

    public final void inheritVisible(boolean flag) {
        containerAccessAdapter.inheritVisible(flag);
        applyVisibilityRules();
    }

    public final boolean isEditable() {
        for (IAccessAdapter adapter : accessAdapters) {
            Boolean editable = adapter.isEditable();
            if (editable != null && !editable) {
                return false;
            }
        }
        return true;
    }

    public final void setEditable(boolean editable) {
        boolean before = isEditable();
        componentAccessAdapter.setEditable(editable);
        if (before != isEditable()) {
            applyEditabilityRules();
            revalidate();
        }
    }

    public final void inheritEditable(boolean flag) {
        containerAccessAdapter.inheritEditable(flag);
        applyEditabilityRules();
    }

    public final boolean isViewable() {
        for (IAccessAdapter adapter : accessAdapters) {
            Boolean viewable = adapter.isViewable();
            if (viewable != null && adapter.isViewable()) {
                return true;
            }
        }
        return false;
    }

    public final void setViewable(boolean viewable) {
        boolean before = isViewable();
        componentAccessAdapter.setViewable(viewable);
        if (before != isViewable()) {
            applyViewabilityRules();
            revalidate();
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
        for (EditableValueValidator<DATA_TYPE> validator : validators) {
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
                Collection<MandatoryValidator<DATA_TYPE>> validatorsForRemoval = new LinkedList<MandatoryValidator<DATA_TYPE>>();
                for (EditableValueValidator<DATA_TYPE> validator : validators) {
                    if (validator instanceof MandatoryValidator) {
                        validatorsForRemoval.add((MandatoryValidator<DATA_TYPE>) validator);
                    }
                }
                for (MandatoryValidator<DATA_TYPE> validator : validatorsForRemoval) {
                    removeValueValidator(validator);
                }
            }
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.mandatory);
            revalidate();
        }
    }

    public boolean isMandatoryConditionMet() {
        return !(validationError instanceof MandatoryValidationFailure);
    }

    public void setMandatoryValidationMessage(String message) {
        mandatoryValidationMessage = message;
    }

    @Override
    public HandlerRegistration addPropertyChangeHandler(PropertyChangeHandler handler) {
        return addHandler(handler, PropertyChangeEvent.getType());
    }

    public void addAccessAdapter(IAccessAdapter adapter) {
        accessAdapters.add(adapter);
    }

    public void removeAccessAdapter(IAccessAdapter adapter) {
        accessAdapters.remove(adapter);
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

    public String getNote() {
        return note;
    }

    public NoteStyle getNoteStyle() {
        return noteStyle;
    }

    public void setNote(String note, NoteStyle style) {
        this.note = note;
        this.noteStyle = style;
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.note);
    }

    public void setNote(String note) {
        setNote(note, NoteStyle.Info);
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
        setDebugId(debugId);
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.debugId);

    }

    protected abstract void setDebugId(IDebugId debugId);

    public void applyVisibilityRules() {
        if (!isVisible()) {
            setVisited(false);
        }
    }

    public void applyEnablingRules() {
        if (!isEnabled()) {
            setVisited(false);
        }
    }

    public void applyEditabilityRules() {
        if (!isEditable()) {
            setVisited(false);
        }
    }

    public void applyViewabilityRules() {
        if (!isViewable()) {
            setVisited(false);
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
                for (EditableValueValidator<DATA_TYPE> validator : validators) {
                    ValidationError ve = validator.isValid(this, getValue());
                    if (ve != null) {
                        ve.setLocationHint(getLocationHint());
                        newValidationError = ve;
                        break;
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

    public final void reset() {
        this.value = null;
        setEditorValue(null);
        if (getParent() != null) {
            getParent().updateContainer(this);
        }
        onReset();
        setVisited(false);
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.reset);
    }

    protected final void update(DATA_TYPE value) {
        if (!isValuesEquals(getValue(), value)) {
            this.value = value;
            revalidate();
            //Overwrite native value with the value that has been formatted by getNativeValue()
            if (isValid()) {
                setEditorValue(value);
            }
            if (getParent() != null) {
                getParent().updateContainer(this);
            }
            ValueChangeEvent.fire(this, value);
        }
    }

    public final void setValue(DATA_TYPE value, boolean fireEvent, boolean populate) {
        //In case of CComponent model represented by IEntity, disable check for equality because value may be the same instance that is returned by getValue()
        if (value instanceof IEntity || !isValuesEquals(getValue(), value)) {
            this.value = preprocessValue(value, fireEvent, populate);
            setEditorValue(this.value);
            revalidate();
            if (getParent() != null) {
                getParent().updateContainer(this);
            }
            if (fireEvent) {
                ValueChangeEvent.fire(this, this.value);
            }
        }
        onValuePropagation(this.value, fireEvent, populate);
        onValueSet(populate);
        if (populate) {
            setVisited(false);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.repopulated);
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

    public final void refresh(boolean fireEvent) {
        this.value = preprocessValue(this.value, fireEvent, false);
        setEditorValue(this.value);
        revalidate();
        if (getParent() != null) {
            getParent().updateContainer(this);
        }
        if (fireEvent) {
            ValueChangeEvent.fire(this, this.value);
        }
        onValuePropagation(this.value, fireEvent, false);
        onValueSet(false);
    }

    protected void onValueSet(boolean populate) {
    }

    protected void onValuePropagation(DATA_TYPE value, boolean fireEvent, boolean populate) {
    }

    protected void onReset() {
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
        if (validationError != null && !isValid()) {
            results.appendValidationError(this, validationError.getMessage(), getLocationHint());
        }
        return results;
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

    public void addValueValidator(EditableValueValidator<DATA_TYPE> validator) {
        if (validators == null) {
            validators = new Vector<EditableValueValidator<DATA_TYPE>>();
        }
        validators.add(validator);
    }

    public boolean removeValueValidator(EditableValueValidator<DATA_TYPE> validator) {
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
            adaptersReport.append("isEnabled ").append(adapter.isEnabled()).append(" ");
            adaptersReport.append("isEditable ").append(adapter.isEditable()).append(" ");
        }

        return "Type:" + this.getClass() + ";\n Title: " + getTitle() + ";\n value:" + getValue() + "; isMandatory=" + isMandatory() + ";\n isEnabled="
                + isEnabled() + "; isEditable=" + isEditable() + "; isVisible=" + isVisible() + "; isVisited=" + isVisited() + "; isValid=" + isValid()
                + "; toolTip=" + getTooltip() + "; adapters=[" + adaptersReport.toString() + "]";
    }

    public String shortDebugInfo() {
        return GWTJava5Helper.getSimpleName(this.getClass()) + ((debugId != null) ? " " + debugId : "");
    }

    public boolean isEditingInProgress() {
        return editingInProgress;
    }

    public void onEditingStart() {
        if (isEnabled() && isVisible() && isEditable() && !isViewable()) {
            editingInProgress = true;
        }
    }

    public void onEditingStop() {
        if (isEnabled() && isVisible() && isEditable() && !isViewable()) {
            boolean isOrigEmpty = isValueEmpty();
            editingInProgress = false;
            try {
                update(getEditorValue());
            } catch (ParseException e) {
                update(null);
            }

            if (!isOrigEmpty || (isOrigEmpty && !isValueEmpty())) {
                setVisited(true);
            }
        }
    }

    protected abstract void setEditorValue(DATA_TYPE value);

    protected abstract DATA_TYPE getEditorValue() throws ParseException;

    public IDecorator getDecorator() {
        return decorator;
    }

    public void setDecorator(IDecorator decorator) {
        this.decorator = decorator;
        decorator.setComponent(this);
    }

    public void setUnconditionalValidationErrorRendering(boolean flag) {
        if (flag != unconditionalValidationErrorRendering) {
            unconditionalValidationErrorRendering = flag;
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.showErrorsUnconditional);
        }
    }

    public boolean isUnconditionalValidationErrorRendering() {
        return unconditionalValidationErrorRendering;
    }

}
