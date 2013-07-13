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

import com.google.gwt.user.client.ui.Widget;

public class TwoColumnFlexFormPanel extends BasicFlexFormPanel {

    public TwoColumnFlexFormPanel(String title) {
        super(title);
        getColumnFormatter().setStyleName(0, FlexFormPanelTheme.StyleName.FormFlexPanelLeftColumn.name());
        getColumnFormatter().setStyleName(1, FlexFormPanelTheme.StyleName.FormFlexPanelRightColumn.name());
    }

    public TwoColumnFlexFormPanel() {
        this(null);
    }

    @Override
    public void setWidget(int row, int column, int span, Widget widget) {
        super.setWidget(row, column, span, widget);
        if (span == 1) {
            if (column == 0) {
                getFlexCellFormatter().setStyleName(row, column, FlexFormPanelTheme.StyleName.FormFlexPanelLeftCell.name());
            } else if (column == 1) {
                getFlexCellFormatter().setStyleName(row, column, FlexFormPanelTheme.StyleName.FormFlexPanelRightCell.name());
            }
        } else if (span == 2) {
            getFlexCellFormatter().setStyleName(row, column, FlexFormPanelTheme.StyleName.FormFlexPanelTwoRows.name());
        }

    }

}
