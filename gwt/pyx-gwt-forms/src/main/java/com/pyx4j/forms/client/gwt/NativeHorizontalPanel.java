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
package com.pyx4j.forms.client.gwt;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CLayoutConstraints;
import com.pyx4j.forms.client.ui.CPanel;
import com.pyx4j.forms.client.ui.INativeComponent;
import com.pyx4j.forms.client.ui.INativeSimplePanel;

public class NativeHorizontalPanel extends HorizontalPanel implements INativeSimplePanel {

    private final CPanel panel;

    public NativeHorizontalPanel(CPanel panel) {
        super();
        this.panel = panel;
        setWidth(panel.getWidth());
        setHeight(panel.getHeight());
    }

    public void setEnabled(boolean enabled) {
    }

    public void add(INativeComponent nativeWidget, CLayoutConstraints layoutConstraints) {
        super.add((Widget) nativeWidget);
        GWTStyleAdapter.setLayoutConstraints((Widget) nativeWidget, layoutConstraints);
    }

    public void updateLookAndFeel() {
    }

    public CPanel getCComponent() {
        return panel;
    }

    public boolean isEnabled() {
        return true;
    }

}
