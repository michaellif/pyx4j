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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewForm;
import com.propertyvista.crm.rpc.dto.gadgets.ArrearsGadgetDataDTO;

public class ArrearsGadgetSummaryForm extends ZoomableViewForm<ArrearsGadgetDataDTO> {

    private static final I18n i18n = I18n.get(ArrearsGadgetSummaryForm.class);

    public enum Styles implements IStyleName {

        ArrearsSummaryPanel;

    }

    public ArrearsGadgetSummaryForm() {
        super(ArrearsGadgetDataDTO.class);
    }

    @Override
    protected IsWidget createContent() {
        SimplePanel panel = new SimplePanel();
        panel.addStyleName(Styles.ArrearsSummaryPanel.name());
        FlexTable content = new FlexTable();
        int row = -1;
        content.setWidget(++row, 0, new Label(i18n.tr("This Month:")));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 1, inject(proto().outstandingThisMonthCount()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 2, inject(proto().buckets().bucketThisMonth()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT);

        content.setWidget(++row, 0, new Label(i18n.tr("1-30:")));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 1, inject(proto().outstanding1to30DaysCount()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 2, inject(proto().buckets().bucket30()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT);

        content.setWidget(++row, 0, new Label(i18n.tr("31-60:")));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 1, inject(proto().outstanding31to60DaysCount()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 2, inject(proto().buckets().bucket60()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT);

        content.setWidget(++row, 0, new Label(i18n.tr("61-90:")));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 1, inject(proto().outstanding61to90DaysCount()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 2, inject(proto().buckets().bucket90()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT);

        content.setWidget(++row, 0, new Label(i18n.tr("91+:")));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 1, inject(proto().outstanding91andMoreDaysCount()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 2, inject(proto().buckets().bucketOver90()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT);

        content.setWidget(++row, 0, new Label(i18n.tr("Total:")));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        content.getFlexCellFormatter().getElement(row, 0).getStyle().setFontWeight(FontWeight.BOLD);
        content.setWidget(row, 1, inject(proto().delinquentLeases()));
        content.getFlexCellFormatter().getElement(row, 1).getStyle().setFontWeight(FontWeight.BOLD);
        content.getFlexCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        content.setWidget(row, 2, inject(proto().buckets().totalBalance()));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT);
        content.getFlexCellFormatter().getElement(row, 2).getStyle().setFontWeight(FontWeight.BOLD);

        content.getColumnFormatter().setWidth(1, "100px");
        content.getColumnFormatter().setWidth(2, "150px");
        panel.setWidget(content);
        return panel;
    }

}
