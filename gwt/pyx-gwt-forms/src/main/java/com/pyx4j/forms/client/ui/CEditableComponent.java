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

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.ICloneable;
import com.pyx4j.commons.IEqual;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

public abstract class CEditableComponent<E> extends CFocusComponent<INativeEditableComponent<E>> implements HasValueChangeHandlers<E> {

    private boolean mandatory = false;

    private String mandatoryValidationMessage = "This field is Mandatory";

    protected E initValue = null;

    private E currentValue = null;

    private List<EditableValueValidator<E>> validators;

    public CEditableComponent() {
        this(null);
    }

    public CEditableComponent(String title) {
        super(title);
    }

    public boolean isValid() {
        return !isVisible() || isReadOnly() || !isEnabled() || (isMandatoryConditionMet() && isValidationConditionMet());
    }

    public E getValue() {
        return currentValue;
    }

    public E getInitValue() {
        return initValue;
    }

    public void setValue(E value) {
        if (getValue() == null ? value == null : getValue().equals(value)) {
            return;
        }
        currentValue = value;
        setNativeComponentValue(value);
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.TOOLTIP_PROPERTY);
        ValueChangeEvent.fire(this, currentValue);
    }

    public void populate(E value) {
        initValue = value;
        currentValue = value;
        setNativeComponentValue(value);
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.TOOLTIP_PROPERTY);
    }

    @SuppressWarnings("unchecked")
    public void populateMutable(ICloneable<E> value) {
        populate((E) value);
        if (value != null) {
            initValue = value.iclone();
        }
    }

    @SuppressWarnings("unchecked")
    public boolean isDirty() {
        if (initValue instanceof IEqual) {
            return !EqualsHelper.iequals((IEqual<E>) initValue, getValue());
        } else {
            // We have more control over initValue, so equals of initValue should be used.
            // Example is the Mutable List, initValue is FullyEqualArrayList, and the Value can be any Vector constructed by Editor.
            return !EqualsHelper.equals(initValue, getValue());
        }
    }

    public boolean isValueEmpty() {
        return (getValue() == null);
    }

    public boolean isReadOnly() {
        for (IAccessAdapter adapter : getAccessAdapters()) {
            if (adapter.isReadOnly(this)) {
                return true;
            }
        }
        return false;
    }

    public void setReadOnly(boolean readOnly) {
        defaultAccessAdapter.setReadOnly(readOnly);
        applyEditabilityRules();
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

    @Override
    public String getToolTip() {
        String tooltip = super.getToolTip();
        if (isValid()) {
            return tooltip;
        } else {
            return getValidationMessage() + ((tooltip == null) || tooltip.trim().equals("") ? "" : "<p>" + tooltip);
        }
    }

    public void fireValidation() {
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.TITLE_PROPERTY);
    }

    protected boolean isMandatoryConditionMet() {
        return !isMandatory() || (!isValueEmpty());
    }

    public String getValidationMessage() {
        if (!isValid()) {
            if (!isMandatoryConditionMet()) {
                return getMandatoryValidationMessage();
            }
            if (validators != null) {
                for (EditableValueValidator<E> validator : validators) {
                    if (!validator.isValid(this, getValue())) {
                        return validator.getValidationMessage(this, getValue());
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

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<E> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void addValueValidator(EditableValueValidator<E> validator) {
        if (validators == null) {
            validators = new Vector<EditableValueValidator<E>>();
        }
        validators.add(validator);
    }

    public boolean removeValueValidator(EditableValueValidator<E> validator) {
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

    protected boolean isValidationConditionMet() {
        if (validators == null) {
            return true;
        }
        for (EditableValueValidator<E> validator : validators) {
            if (!validator.isValid(this, getValue())) {
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
            adaptersReport.append("isReadOnly ").append(adapter.isReadOnly(this)).append(" ");
        }

        return "Title: " + getTitle() + ";\n value:" + getValue() + "; isMandatory=" + isMandatory() + ";\n isEnabled=" + isEnabled() + "; isReadOnly="
                + isReadOnly() + "; isVisible=" + isVisible() + "; isDirty=" + isDirty() + "; isValid=" + isValid() + "; toolTip=" + getToolTip() + "; size="
                + getWidth() + ":" + getHeight() + "; adapters=[" + adaptersReport.toString() + "]";
    }

    protected void setNativeComponentValue(E value) {
        if (getNativeComponent() == null) {
            //do nothing
        } else if (getNativeComponent() instanceof INativeEditableComponent) {
            (getNativeComponent()).setNativeValue(value);
        } else {
            throw new Error("CEditableComponent should have native component of " + "type INativeEditableComponent but has " + getNativeComponent());
        }
    }

    protected void applyEditabilityRules() {
        boolean readOnly = isReadOnly();
        if (getNativeComponent() != null && getNativeComponent().isReadOnly() != readOnly) {
            getNativeComponent().setReadOnly(readOnly);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.READONLY_PROPERTY);
        }
    }

    @Override
    protected void applyAccessibilityRules() {
        super.applyAccessibilityRules();
        applyEditabilityRules();
    }

}
