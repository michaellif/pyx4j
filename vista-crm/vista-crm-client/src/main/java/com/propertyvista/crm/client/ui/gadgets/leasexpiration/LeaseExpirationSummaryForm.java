/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.leasexpiration;

import static com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme.StyleName.WidgetDecoratorLabel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ui.Cursor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetSummaryForm;
import com.propertyvista.crm.rpc.dto.gadgets.LeaseExpirationGadgetDataDTO;

final class LeaseExpirationSummaryForm extends CounterGadgetSummaryForm<LeaseExpirationGadgetDataDTO> {

    private static final I18n i18n = I18n.get(LeaseExpirationSummaryForm.class);

    public LeaseExpirationSummaryForm() {
        super(LeaseExpirationGadgetDataDTO.class);
    }

    @Override
    public IsWidget createContent() {
        VerticalPanel panel = new VerticalPanel();
        panel.setWidth("100%");
        panel.getElement().getStyle().setPaddingLeft(1, Unit.EM);

        Grid occupancyTable = new Grid(2, 3);
        panel.add(occupancyTable);

        occupancyTable.setWidget(0, 1, label("#"));
        occupancyTable.setWidget(0, 2, label("%"));
        occupancyTable.setWidget(1, 0, label(i18n.tr("Unit Occupancy")));
        occupancyTable.setWidget(1, 1, inject(proto().unitsOccupied()));
        occupancyTable.setWidget(1, 2, inject(proto().unitOccupancy()));

        Grid leaseExpriationTable = new Grid(2, 4);
        leaseExpriationTable.getElement().getStyle().setMarginTop(1, Unit.EM);

        leaseExpriationTable.setWidget(0, 1, label(i18n.tr("This Month")));
        leaseExpriationTable.setWidget(0, 2, label(i18n.tr("Next Month")));
        leaseExpriationTable.setWidget(0, 3, label(i18n.tr("90+")));
        leaseExpriationTable.setWidget(1, 0, label(i18n.tr("Leases Ending")));
        leaseExpriationTable.setWidget(1, 1, inject(proto().numOfLeasesEndingThisMonth()));
        leaseExpriationTable.setWidget(1, 2, inject(proto().numOfLeasesEndingNextMonth()));
        leaseExpriationTable.setWidget(1, 3, inject(proto().numOfLeasesEndingOver90Days()));

        panel.add(leaseExpriationTable);

        Grid leasesOnMonthToMonth = new Grid(1, 2);
        leasesOnMonthToMonth.getElement().getStyle().setMarginTop(1, Unit.EM);
        leasesOnMonthToMonth.setWidget(0, 0, label(i18n.tr("Leases on Month to Month")));
        leasesOnMonthToMonth.setWidget(0, 1, inject(proto().numOfLeasesOnMonthToMonth()));

        panel.add(leasesOnMonthToMonth);

        return panel;
    }

    private static HTML label(String caption) {
        HTML label = new HTML(new SafeHtmlBuilder().appendEscaped(caption).toSafeHtml());
        label.setStyleName(WidgetDecoratorLabel.name());

        Cursor.setDefault(label.getElement());
        return label;
    }
}