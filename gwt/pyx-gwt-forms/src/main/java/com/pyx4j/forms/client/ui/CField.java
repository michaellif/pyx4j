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
 * Created on Jul 8, 2013
 * @author michaellif
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.ui.decorators.IFieldDecorator;

public abstract class CField<DATA_TYPE, WIDGET_TYPE extends INativeField<DATA_TYPE>> extends
        CComponent<CField<DATA_TYPE, WIDGET_TYPE>, DATA_TYPE, WIDGET_TYPE, IFieldDecorator> {

    private Command navigationCommand;

    public CField() {
        super();

        if (ApplicationMode.isDevelopment()) {
            addValueChangeHandler(new ValueChangeHandler<DATA_TYPE>() {

                @Override
                public void onValueChange(ValueChangeEvent<DATA_TYPE> event) {
                    if (asWidget() != null) {
                        asWidget().getElement().setAttribute(DEV_ATTR, CField.this.getDebugInfo());
                    }
                }
            });
        }
    }

    @Override
    protected void setEditorValue(DATA_TYPE value) {
        if (getNativeComponent() != null) {
            getNativeComponent().setNativeValue(value);
        }
    }

    @Override
    protected DATA_TYPE getEditorValue() throws ParseException {
        if (getNativeComponent() != null) {
            return getNativeComponent().getNativeValue();
        } else {
            return null;
        }
    }

    public void setNavigationCommand(Command navigationCommand) {
        this.navigationCommand = navigationCommand;
        getNativeComponent().setNavigationCommand(navigationCommand);

    }

    public Command getNavigationCommand() {
        return navigationCommand;
    }

    @Override
    protected final void setNativeComponent(WIDGET_TYPE nativeComponent) {
        super.setNativeComponent(nativeComponent);
        nativeComponent.init();
        applyAccessibilityRules();

        nativeComponent.setNavigationCommand(navigationCommand);

        onNativeComponentSet();
    }

    protected void onNativeComponentSet() {
        if (getDebugId() != null) {
            getNativeComponent().setDebugId(getDebugId());
        }
    }

    @Override
    public boolean isValidatable() {
        return isVisible() && isEditable() && isEnabled() && isPopulated() && !isViewable() && (isVisited() || !isValueEmpty() || isEditingInProgress());
    }

    @Override
    public void applyVisibilityRules() {
        super.applyVisibilityRules();
        boolean visible = isVisible();
        if (getNativeComponent().isVisible() != visible) {
            getNativeComponent().setVisible(visible);
        }
        asWidget().setVisible(isVisible());
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.visible);
    }

    @Override
    public void applyEnablingRules() {
        super.applyEnablingRules();
        boolean enabled = isEnabled();
        if (getNativeComponent().isEnabled() != enabled) {
            getNativeComponent().setEnabled(enabled);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.enabled);
        }
    }

    @Override
    public void applyEditabilityRules() {
        super.applyEditabilityRules();
        boolean editable = isEditable();
        if (getNativeComponent().isEditable() != editable) {
            getNativeComponent().setEditable(editable);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.editable);
        }

    }

    @Override
    public void applyViewabilityRules() {
        super.applyViewabilityRules();
        boolean viewable = isViewable();
        if (getNativeComponent().isViewable() != viewable) {
            getNativeComponent().setViewable(viewable);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.viewable);
        }
    }

    @Override
    protected void setDebugId(IDebugId debugId) {
        getNativeComponent().setDebugId(debugId);
    }

    @Override
    protected String getDebugInfo() {
        StringBuilder info = new StringBuilder(super.getDebugInfo());
        info.append("value");
        if (isPopulated()) {
            info.append("=").append(getValue());
        } else {
            info.append(" not populated");
        }
        info.append(";");
        return info.toString();
    }

}
