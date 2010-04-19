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
 * Created on Apr 16, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.gwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CFlexForm;
import com.pyx4j.forms.client.ui.INativeComponent;

public class NativeFlexForm extends LayoutPanel implements INativeComponent {

    private static final Logger log = LoggerFactory.getLogger(NativeFlexForm.class);

    private final FlexTable flexTable;

    private final int numOfColumns = 3;

    private final CFlexForm form;

    public NativeFlexForm(CFlexForm form) {
        this.form = form;

        flexTable = new FlexTable();
        add(flexTable);

    }

    @Override
    public CComponent<?> getCComponent() {
        return form;
    }

    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    public void layout() {
        flexTable.clear();
        int row = 0;
        int column = 5;
        FlexCellFormatter cellFormatter = flexTable.getFlexCellFormatter();

        for (CComponent<?> component : form.getComponents()) {
            Widget nativeComponent = (Widget) component.initNativeComponent();

            flexTable.setWidget(row, column, nativeComponent);
            if (component.getConstraints() != null) {
                cellFormatter.setColSpan(row, column, component.getConstraints().colSpan);
                cellFormatter.setRowSpan(row, column, component.getConstraints().rowSpan);
            }
            cellFormatter.setHeight(row, column, "100px");
            cellFormatter.setWidth(row, column, "100px");
            if (++column == numOfColumns + 5) {
                row++;
                column = 5;
            }
        }
    }

}
