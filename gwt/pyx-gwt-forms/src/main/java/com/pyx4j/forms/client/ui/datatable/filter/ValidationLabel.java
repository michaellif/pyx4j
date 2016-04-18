/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jan 9, 2015
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.pyx4j.gwt.commons.ui.HTML;
import com.pyx4j.gwt.commons.ui.SimplePanel;

import com.pyx4j.widgets.client.IFocusWidget;

public class ValidationLabel extends SimplePanel {

    private final HTML label;

    public ValidationLabel(IFocusWidget widget) {

        label = new HTML();
        setWidget(label);

        widget.addBlurHandler(new BlurHandler() {

            @Override
            public void onBlur(BlurEvent event) {
                label.setVisible(true);
            }
        });

        widget.addFocusHandler(new FocusHandler() {

            @Override
            public void onFocus(FocusEvent event) {
                label.setVisible(false);
            }
        });
    }

    public void setMessage(String message) {
        label.setText(message);
    }
}
