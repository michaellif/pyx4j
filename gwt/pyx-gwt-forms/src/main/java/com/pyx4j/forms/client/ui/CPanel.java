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

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.gwt.NativeHorizontalPanel;
import com.pyx4j.forms.client.gwt.NativeVerticalPanel;

public class CPanel extends CContainer {

    public enum Layout {
        VERTICAL, HORISONTAL;
    }

    private final Collection<CComponent<?>> components = new ArrayList<CComponent<?>>();

    private final Layout layout;

    private INativeSimplePanel nativePanel;

    public CPanel(Layout layout) {
        this.layout = layout;
    }

    @Override
    public INativeComponent getNativeComponent() {
        return nativePanel;
    }

    @Override
    public INativeComponent initNativeComponent() {
        if (nativePanel == null) {
            if (Layout.VERTICAL.equals(layout)) {
                nativePanel = new NativeVerticalPanel(this);
            } else {
                nativePanel = new NativeHorizontalPanel(this);
            }
            for (CComponent<?> component : components) {
                INativeComponent nativeComponent = component.initNativeComponent();
                // TODO move ensureDebugId GWT call to proper place e.g. NativeComponent creation
                if (component.getComponentDebugID() != null) {
                    ((Widget) nativeComponent).ensureDebugId(component.getComponentDebugID());
                }

                nativePanel.add(nativeComponent, component.getConstraints());
            }
            applyAccessibilityRules();
        }
        return nativePanel;
    }

    @Override
    public void addComponent(CComponent<?> component) {
        components.add(component);
        component.setParent(this);
    }

    @Override
    public Collection<CComponent<?>> getComponents() {
        return components;
    }

}
