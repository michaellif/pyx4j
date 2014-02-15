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
 * Created on Jan 28, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LabelElement;

public class CheckBox extends com.google.gwt.user.client.ui.CheckBox implements IFocusWidget {

    private boolean enabled = true;

    private boolean editable = true;

    public CheckBox() {
        super();
        setStyleName(DefaultWidgetsTheme.StyleName.CheckBox.name());
    }

    public CheckBox(String label) {
        this();
        this.setText(label);
    }

    /**
     * Change the Debug Id to avoid a special cases for CheckBox in selenium.
     */
    @Override
    protected void onEnsureDebugId(String baseID) {
        InputElement inputElem = getElement().getChild(0).cast();
        LabelElement labelElem = getElement().getChild(1).cast();
        ensureDebugId(labelElem, baseID, "label");
        ensureDebugId(inputElem, baseID);
        labelElem.setHtmlFor(inputElem.getId());

    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        super.setEnabled(enabled && this.isEditable());
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        super.setEnabled(editable && this.isEnabled());
    }

    @Override
    public boolean isEditable() {
        return editable;
    }
}
