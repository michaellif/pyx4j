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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.forms.client.events.PropertyChangeEvent;

public abstract class CField<DATA_TYPE, WIDGET_TYPE extends INativeComponent<DATA_TYPE>> extends CComponent<DATA_TYPE> {

    private WIDGET_TYPE nativeWidget;

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
        if (nativeWidget != null) {
            nativeWidget.setNativeValue(value);
        }
    }

    @Override
    protected DATA_TYPE getEditorValue() throws ParseException {
        if (nativeWidget != null) {
            return nativeWidget.getNativeValue();
        } else {
            return null;
        }
    }

    public void setNavigationCommand(Command navigationCommand) {
        this.navigationCommand = navigationCommand;
        nativeWidget.setNavigationCommand(navigationCommand);

    }

    public Command getNavigationCommand() {
        return navigationCommand;
    }

    protected void setNativeWidget(WIDGET_TYPE widget) {
        this.nativeWidget = widget;
        widget.init();
        applyAccessibilityRules();

        widget.setNavigationCommand(navigationCommand);
        if (getDebugId() != null) {
            widget.setDebugId(getDebugId());
        }

    }

    @Override
    public Widget asWidget() {
        if (nativeWidget == null) {
            return null;
        } else {
            return nativeWidget.asWidget();
        }
    }

    public final WIDGET_TYPE getWidget() {
        return nativeWidget;
    }

    @Override
    public void revalidate() {
        if (isVisible() && isEditable() && isEnabled() && !isViewable() && (isVisited() || !isValueEmpty() || isEditingInProgress())) {
            super.revalidate();
        }
    }

    @Override
    public void applyVisibilityRules() {
        super.applyVisibilityRules();
        boolean visible = isVisible();
        if (asWidget().isVisible() != visible) {
            asWidget().setVisible(visible);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.visible);
        }
    }

    @Override
    public void applyEnablingRules() {
        super.applyEnablingRules();
        boolean enabled = isEnabled();
        if (nativeWidget.isEnabled() != enabled) {
            nativeWidget.setEnabled(enabled);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.enabled);
        }
    }

    @Override
    public void applyEditabilityRules() {
        super.applyEditabilityRules();
        boolean editable = isEditable();
        if (nativeWidget.isEditable() != editable) {
            nativeWidget.setEditable(editable);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.editable);
        }

    }

    @Override
    public void applyViewabilityRules() {
        super.applyViewabilityRules();
        boolean viewable = isViewable();
        if (nativeWidget.isViewable() != viewable) {
            nativeWidget.setViewable(viewable);
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.viewable);
        }
    }

    @Override
    protected void setDebugId(IDebugId debugId) {
        nativeWidget.setDebugId(debugId);
    }

    @Override
    protected String getDebugInfo() {
        StringBuilder info = new StringBuilder(super.getDebugInfo());
        info.append("value").append("=").append(getValue()).append(";");
        return info.toString();
    }
}
