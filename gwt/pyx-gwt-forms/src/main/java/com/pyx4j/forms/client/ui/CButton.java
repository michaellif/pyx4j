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

import com.google.gwt.user.client.Command;

import com.pyx4j.forms.client.gwt.NativeButton;
import com.pyx4j.widgets.client.Tooltip;

public class CButton extends CFocusComponent<INativeFocusComponent> {

    private NativeButton nativeButton;

    private String label;

    private final Command command;

    private String popupTooltipText;

    private Tooltip popupTooltip;

    public CButton(String label, Command command) {
        this.label = label;
        this.command = command;
        addAccessAdapter(new CommandAccessAdapter(command));
    }

    @Override
    public NativeButton getNativeComponent() {
        return nativeButton;
    }

    @Override
    public NativeButton initNativeComponent() {
        if (nativeButton == null) {
            nativeButton = new NativeButton(this, label, command);
            applyAccessibilityRules();
            if (popupTooltipText != null) {
                popupTooltip = Tooltip.tooltip(nativeButton, popupTooltipText);
            }
        }
        return nativeButton;
    }

    public void setLabel(String label) {
        this.label = label;
        if (nativeButton != null) {
            nativeButton.setText(label);
        }
    }

    public String getLabel() {
        return label;
    }

    @Override
    public void applyEnablingRules() {
        if (command != null) {
            command.revalidate();
        }
        super.applyEnablingRules();
    }

    public Command getCommand() {
        return command;
    }

    public String getPopupTooltip() {
        return popupTooltipText;
    }

    public void setPopupTooltip(String popupTooltipText) {
        this.popupTooltipText = popupTooltipText;
        if (nativeButton != null) {
            if (popupTooltip != null) {
                popupTooltip.setTooltipText(this.popupTooltipText);
            } else {
                popupTooltip = Tooltip.tooltip(nativeButton, popupTooltipText);
            }
        }
    }

}
