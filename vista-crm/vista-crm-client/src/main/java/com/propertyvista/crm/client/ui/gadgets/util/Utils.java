/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.util;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme;

public class Utils {

    public static Widget label(String caption) {

        HTML label = new HTML(new SafeHtmlBuilder().appendEscaped(caption).toSafeHtml());
        label.setStyleName(WidgetDecoratorTheme.StyleName.WidgetDecoratorLabel.name());

        FlowPanel labelHolder = new FlowPanel();
        labelHolder.add(label);
        labelHolder.setStyleName(WidgetDecoratorTheme.StyleName.WidgetDecorator.name());
        labelHolder.addStyleDependentName(WidgetDecoratorTheme.StyleDependent.readOnly.name());

        return labelHolder;
    }

    public static FlexTable createTable(String[] columnNames, String[] columnWidths, String[] rowNames, IsWidget[][] widgets) {
        FlexTable table = new FlexTable();

        int col = 0;
        for (String columnWidth : columnWidths) {
            table.getColumnFormatter().setWidth(col, columnWidth);
            col++;
        }

        col = 0;
        for (String columnName : columnNames) {
            table.setWidget(0, col++, label(columnName));
        }

        int row = 1;

        table.getRowFormatter().getElement(0).getStyle().setProperty("textAlign", "right");
        for (String rowName : rowNames) {
            col = 1;
            table.setWidget(row, 0, label(rowName));
            for (IsWidget widget : widgets[row - 1]) {
                table.setWidget(row, col++, widget);
            }
            table.getRowFormatter().getElement(row).getStyle().setProperty("textAlign", "right");
            ++row;
        }

        return table;

    }
}
