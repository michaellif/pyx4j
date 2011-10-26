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
 * Created on Oct 20, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.panels;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;

public class FormFlexPanel extends FlexTable {

    public static enum StyleName implements IStyleName {
        FormFlexPanel, FormFlexPanelHeader, FormFlexPanelHeaderLabel
    }

    public static enum StyleDependent implements IStyleDependent {

    }

    public FormFlexPanel() {
        setStyleName(StyleName.FormFlexPanel.name());
    }

    @Override
    public void setWidget(int row, int column, Widget widget) {
        super.setWidget(row, column, widget);
        getCellFormatter().setWidth(row, column, "100%");
    }

    @Override
    public void setWidget(int row, int column, IsWidget widget) {
        super.setWidget(row, column, widget);
        getCellFormatter().setWidth(row, column, "100%");
    }

    public void setHeader(int row, int col, int span, String caption) {
        getFlexCellFormatter().setColSpan(row, col, span);
        Label label = new Label(caption);
        label.setStyleName(StyleName.FormFlexPanelHeaderLabel.name());
        SimplePanel header = new SimplePanel();
        header.setWidget(label);
        header.setStyleName(StyleName.FormFlexPanelHeader.name());
        super.setWidget(row, 0, header);
    }
}
