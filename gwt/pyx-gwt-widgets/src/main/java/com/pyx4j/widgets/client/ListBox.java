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
 */
package com.pyx4j.widgets.client;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.gwt.commons.ui.HasStyle;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class ListBox extends com.google.gwt.user.client.ui.ListBox implements IFocusWidget, HasStyle {

    private boolean enabled = true;

    private boolean editable = true;

    public ListBox() {
        this(false);
    }

    public ListBox(boolean multipleSelect) {
        super(multipleSelect);
        setStyleName(WidgetsTheme.StyleName.ListBox.name());

        if (!multipleSelect) {
            addStyleDependentName(WidgetsTheme.StyleDependent.singleLine.name());
        }
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
        String readOnlyStyle = WidgetsTheme.StyleDependent.readonly.name();
        if (!editable) {
            addStyleDependentName(readOnlyStyle);
        } else {
            removeStyleDependentName(readOnlyStyle);
        }
        super.setEnabled(editable && this.isEnabled());
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        ensureDebugId(debugId.debugId());
    }
}
