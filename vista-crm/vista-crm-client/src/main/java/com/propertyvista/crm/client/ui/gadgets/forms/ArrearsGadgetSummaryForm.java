/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.forms;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewForm;
import com.propertyvista.crm.rpc.dto.gadgets.ArrearsGadgetDataDTO;

public class ArrearsGadgetSummaryForm extends ZoomableViewForm<ArrearsGadgetDataDTO> {

    private static final I18n i18n = I18n.get(ArrearsGadgetSummaryForm.class);

    private final NumberFormat currencyFormat;

    public ArrearsGadgetSummaryForm(NumberFormat currencyFormat) {
        super(ArrearsGadgetDataDTO.class);
        this.currencyFormat = currencyFormat;
    }

    public ArrearsGadgetSummaryForm() {
        this(NumberFormat.getFormat(CMoneyField.symbol + "#,##0.00"));
    }

    @Override
    public IsWidget createContent() {
        FlexTable content = new FlexTable();
        int row = -1;
        content.setWidget(++row, 0, new Label(i18n.tr("This Month:")));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 1, inject(proto().outstandingThisMonthCount()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 2, inject(proto().outstandingThisMonth()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT);

        content.setWidget(++row, 0, new Label(i18n.tr("1-30:")));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 1, inject(proto().outstanding1to30DaysCount()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 2, inject(proto().outstanding1to30Days()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT);

        content.setWidget(++row, 0, new Label(i18n.tr("31-60:")));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 1, inject(proto().outstanding31to60DaysCount()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 2, inject(proto().outstanding31to60Days()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT);

        content.setWidget(++row, 0, new Label(i18n.tr("61-90:")));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 1, inject(proto().outstanding61to90DaysCount()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 2, inject(proto().outstanding61to90Days()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT);

        content.setWidget(++row, 0, new Label(i18n.tr("91+:")));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 1, inject(proto().outstanding91andMoreDaysCount()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 2, inject(proto().outstanding91andMoreDays()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT);

        content.setWidget(++row, 0, new Label(i18n.tr("Total:")));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        content.getFlexCellFormatter().getElement(row, 0).getStyle().setFontWeight(FontWeight.BOLD);
        content.setWidget(row, 1, inject(proto().delinquentLeases()));
        content.getFlexCellFormatter().getElement(row, 1).getStyle().setFontWeight(FontWeight.BOLD);
        content.getFlexCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 2, inject(proto().outstandingTotal()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT);
        content.getFlexCellFormatter().getElement(row, 2).getStyle().setFontWeight(FontWeight.BOLD);

        content.getColumnFormatter().setWidth(1, "100px");
        content.getColumnFormatter().setWidth(2, "150px");
        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        // WE DO MUST DO THIS BECAUSE CHYPERLINKS sued in the form do not support formatting
        get(proto().outstandingThisMonth()).setValue(currencyFormat.format(getValue().buckets().bucketThisMonth().getValue()));
        get(proto().outstanding1to30Days()).setValue(currencyFormat.format(getValue().buckets().bucket30().getValue()));
        get(proto().outstanding31to60Days()).setValue(currencyFormat.format(getValue().buckets().bucket60().getValue()));
        get(proto().outstanding61to90Days()).setValue(currencyFormat.format(getValue().buckets().bucket90().getValue()));
        get(proto().outstanding91andMoreDays()).setValue(currencyFormat.format(getValue().buckets().bucketOver90().getValue()));
        get(proto().outstandingTotal()).setValue(currencyFormat.format(getValue().buckets().arrearsAmount().getValue()));
    }

}
