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

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.Widget;

public abstract class CFocusComponent<DATA_TYPE, WIDGET_TYPE extends Widget & INativeFocusComponent<DATA_TYPE>> extends
        CEditableComponent<DATA_TYPE, WIDGET_TYPE> {

    private int tabIndex = 0;

    public CFocusComponent() {
        this(null);
    }

    public CFocusComponent(String title) {
        super(title);
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
        if (isWidgetCreated()) {
            asWidget().setTabIndex(tabIndex);
        }
    }

    public void setFocus(boolean focused) {
        if (isWidgetCreated()) {
            asWidget().setFocus(focused);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setTabIndex(enabled ? 0 : -2); // enable/disable focus navigation 
    }

    @Override
    public void setEditable(boolean editable) {
        super.setEditable(editable);
        if (editable != isEditable()) {
            setTabIndex(editable ? 0 : -2); // enable/disable focus navigation
        }
    }

    @Override
    protected void onWidgetCreated() {
        super.onWidgetCreated();
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

}
