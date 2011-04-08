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

import java.util.List;
import java.util.Vector;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.ICloneable;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

public abstract class CEditableComponent<DATA_TYPE, WIDGET_TYPE extends Widget & INativeEditableComponent<DATA_TYPE>> extends CFocusComponent<WIDGET_TYPE>
        implements HasValueChangeHandlers<DATA_TYPE> {

    private String mandatoryValidationMessage = "This field is Mandatory";

    private DATA_TYPE value = null;

    private List<EditableValueValidator<? super DATA_TYPE>> validators;

    private boolean mandatory = false;

    // Have been changed after population
    private boolean visited = false;

    private boolean editing = false;

    private boolean valid = true;

    public CEditableComponent() {
        this(null);
    }

    public CEditableComponent(String title) {
        super(title);
    }

    public boolean isValid() {
        return valid;
    }

    public void revalidate() {

        boolean newValid = !isVisible() ||

        !isEditable() ||

        !isEnabled() ||

        !isVisited() ||

        (isMandatoryConditionMet() && (isValueEmpty() || isValidationConditionMet()));

        if (!newValid || (newValid != valid)) {
            valid = newValid;
            asWidget().setValid(valid);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.VALIDITY);
        }

    }

    void update(DATA_TYPE value) {
        if (!isValuesEquals(getValue(), value)) {
            this.value = value;
            revalidate();
            ValueChangeEvent.fire(this, value);
        }
    }

    public void setValue(DATA_TYPE value) {
        if (!isValuesEquals(getValue(), value)) {
            this.value = value;
            setNativeValue(value);
            //TODO Evaluate if revalidate is needed here
            //revalidate();
            ValueChangeEvent.fire(this, value);
        }
    }

    public DATA_TYPE getValue() {
        return value;
    }

    /*
     * Call populate on init of component
     */
    public void populate(DATA_TYPE value) {

        this.value = value;
        this.visited = !isValueEmpty();

        setNativeValue(value);

        revalidate();

    }

    @SuppressWarnings("unchecked")
    public void populateMutable(ICloneable<DATA_TYPE> value) {
        populate((DATA_TYPE) value);
    }

    public boolean isValueEmpty() {
        return (getValue() == null);
    }

    public boolean isValuesEquals(DATA_TYPE value1, DATA_TYPE value2) {
        return EqualsHelper.equals(value1, value2);
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
            setTabIndex(editable ? 0 : -2); // enable/disable focus navigation
        }
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        if (this.mandatory != mandatory) {
            this.mandatory = mandatory;
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.MANDATORY_PROPERTY);
        }
    }

    public boolean isMandatoryConditionMet() {
        return !isEnabled() || !isEditable() || !isMandatory() || !isVisited() || !isValueEmpty();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public String getValidationMessage() {
        if (!isValid()) {
            if (!isMandatoryConditionMet()) {
                return getMandatoryValidationMessage();
            } else {
                if (validators != null) {
                    for (EditableValueValidator<? super DATA_TYPE> validator : validators) {
                        if (!validator.isValid((CEditableComponent) this, getValue())) {
                            return validator.getValidationMessage((CEditableComponent) this, getValue());
                        }
                    }
                }
            }
        }
        return null;
    }

    public String getMandatoryValidationMessage() {
        return mandatoryValidationMessage;
    }

    public void setMandatoryValidationMessage(String message) {
        mandatoryValidationMessage = message;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
        revalidate();
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
    protected boolean isValidationConditionMet() {
        if (validators == null) {
            return true;
        }
        for (EditableValueValidator<? super DATA_TYPE> validator : validators) {
            if (!validator.isValid((CEditableComponent) this, getValue())) {
                return false;
            }
        }
        return true;
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
                + isEnabled() + "; isEditable=" + isEditable() + "; isVisible=" + isVisible() + "; isValid=" + isValid() + "; toolTip=" + getToolTip()
                + "; size=" + getWidth() + ":" + getHeight() + "; adapters=[" + adaptersReport.toString() + "]";
    }

    @Override
    protected void onWidgetCreated() {
        super.onWidgetCreated();
        setNativeValue(getValue());
        WIDGET_TYPE widget = super.asWidget();
        widget.addFocusHandler(new FocusHandler() {

            @Override
            public void onFocus(FocusEvent event) {
                onEditingStart();
            }
        });

        widget.addBlurHandler(new BlurHandler() {

            @Override
            public void onBlur(BlurEvent event) {
                onEditingStop();
            }
        });
    }

    protected void setNativeValue(DATA_TYPE value) {
        if (isWidgetCreated()) {
            asWidget().setNativeValue(value);
        }
    }

    public void applyEditabilityRules() {
        boolean editable = isEditable();
        if (isWidgetCreated() && asWidget().isEditable() != editable) {
            asWidget().setEditable(editable);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.READONLY_PROPERTY);
        }
    }

    @Override
    public void applyAccessibilityRules() {
        super.applyAccessibilityRules();
        applyEditabilityRules();
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
            setVisited(true);
            editing = false;
            update(asWidget().getNativeValue());
        }
    }

    @Override
    protected WIDGET_TYPE createWidget() {
        // TODO Auto-generated method stub
        return null;
    }

}
