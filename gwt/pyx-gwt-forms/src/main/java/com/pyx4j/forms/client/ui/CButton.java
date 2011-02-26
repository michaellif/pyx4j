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


public class CButton extends CFocusComponent<NativeButton> {

    private String label;

    private final Command command;

    private String popupTooltipText;

    public CButton(String label, Command command) {
        this.label = label;
        this.command = command;
    }

    @Override
    protected NativeButton initWidget() {
        NativeButton nativeButton = new NativeButton(this, label, command);
        nativeButton.setTitle(popupTooltipText);
        return nativeButton;
    }

    public void setLabel(String label) {
        this.label = label;
        if (isWidgetCreated()) {
            asWidget().setText(label);
        }
    }

    public String getLabel() {
        return label;
    }

    public Command getCommand() {
        return command;
    }

    public String getPopupTooltip() {
        return popupTooltipText;
    }

    public void setPopupTooltip(String popupTooltipText) {
        if (isWidgetCreated()) {
            asWidget().setTitle(popupTooltipText);
        }
    }

}
