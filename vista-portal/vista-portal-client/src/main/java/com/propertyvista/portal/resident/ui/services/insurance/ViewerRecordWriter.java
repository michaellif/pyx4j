/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 22, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.services.insurance;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;

import com.propertyvista.common.client.theme.BillingTheme;

public class ViewerRecordWriter {

    private final NumberFormat currencyFormat;

    public ViewerRecordWriter(NumberFormat currencyFormat) {
        this.currencyFormat = currencyFormat;
    }

    public void addDetailRecord(FlexTable table, int row, String description, BigDecimal amount) {
        BasicFlexFormPanel record = new BasicFlexFormPanel();

        record.setWidget(row, 0, new HTML(description));
        record.setWidget(row, 1, new HTML(currencyFormat.format(amount)));

        // styling:
        record.getColumnFormatter().setWidth(1, "100px");

        record.getRowFormatter().setStyleName(row, BillingTheme.StyleName.BillingDetailItem.name());
        record.getWidget(row, 0).setStyleName(BillingTheme.StyleName.BillingDetailItemTitle.name());
        record.getWidget(row, 1).setStyleName(BillingTheme.StyleName.BillingDetailItemAmount.name());

        table.setWidget(row, 0, record);
    }

    public void addTotalRecord(FlexTable table, int row, String description, BigDecimal amount) {
        BasicFlexFormPanel record = new BasicFlexFormPanel();

        record.setWidget(row, 0, new HTML(description));
        record.setWidget(row, 1, new HTML(currencyFormat.format(amount)));

        // styling:
        record.getColumnFormatter().setWidth(1, "100px");

        record.getRowFormatter().setStyleName(row, BillingTheme.StyleName.BillingDetailTotal.name());
        record.getWidget(row, 0).setStyleName(BillingTheme.StyleName.BillingDetailTotalTitle.name());
        record.getWidget(row, 1).setStyleName(BillingTheme.StyleName.BillingDetailTotalAmount.name());

        record.getWidget(row, 1).getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        record.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_LEFT);

        table.setWidget(row, 0, record);
    }
}
