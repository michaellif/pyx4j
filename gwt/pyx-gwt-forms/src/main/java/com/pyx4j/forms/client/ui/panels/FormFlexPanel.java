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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;

public class FormFlexPanel extends FlexTable {

    public static enum StyleName implements IStyleName {
        FormFlexPanel, FormFlexPanelHR, FormFlexPanelH1, FormFlexPanelH1Label, FormFlexPanelH2, FormFlexPanelH2Label, FormFlexPanelH3, FormFlexPanelH3Label
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

    public void setHR(int row, int col, int span) {
        getFlexCellFormatter().setColSpan(row, col, span);
        HTML header = new HTML("&nbsp;");
        header.setStyleName(StyleName.FormFlexPanelHR.name());
        super.setWidget(row, 0, header);
    }

    public void setH1(int row, int col, int span, String caption) {
        setHX(row, col, span, caption, StyleName.FormFlexPanelH1Label, StyleName.FormFlexPanelH1);
    }

    public void setH2(int row, int col, int span, String caption) {
        setHX(row, col, span, caption, StyleName.FormFlexPanelH2Label, StyleName.FormFlexPanelH2);
    }

    public void setH3(int row, int col, int span, String caption) {
        setHX(row, col, span, caption, StyleName.FormFlexPanelH3Label, StyleName.FormFlexPanelH3);
    }

    private void setHX(int row, int col, int span, String caption, StyleName labelStyle, StyleName headerStyle) {
        getFlexCellFormatter().setColSpan(row, col, span);
        Label label = new Label(caption);
        label.setStyleName(labelStyle.name());
        SimplePanel header = new SimplePanel();
        header.setWidget(label);
        header.setStyleName(headerStyle.name());
        super.setWidget(row, 0, header);
    }

}
