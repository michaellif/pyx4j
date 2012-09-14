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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetSummaryForm;
import com.propertyvista.crm.client.ui.gadgets.util.Utils;
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
        panel.getElement().getStyle().setPaddingBottom(1, Unit.EM);

        final String FIRST_COL_WIDTH = "100";
        final double MARGIN = 1.5;

        FlexTable unitOccupancy = Utils.createTable(//@formatter:off
                new String[] {"", ""},
                new String[] {FIRST_COL_WIDTH, "100", "100"},
                new String[] {i18n.tr("Units Occupied:")},
                new Widget[][] {{
                    inject(proto().unitOccupancy()).asWidget()
                }}
        );//@formatter:on
        unitOccupancy.getElement().getStyle().setMarginBottom(MARGIN, Unit.EM);
        panel.add(unitOccupancy);

        FlexTable leasesOnMonthToMonth = Utils.createTable(//@formatter:off
                new String[] {"", ""},
                new String[] {FIRST_COL_WIDTH, "100"},
                new String[] {i18n.tr("Leases on Month to Month:")},
                new Widget[][] {{
                    inject(proto().numOfLeasesOnMonthToMonth()).asWidget()
                }}
        );//@formatter:on
        leasesOnMonthToMonth.getElement().getStyle().setMarginBottom(MARGIN, Unit.EM);
        panel.add(leasesOnMonthToMonth);

        FlexTable leaseExpiration = Utils.createTable(//@formatter:off
                new String[] {"", i18n.tr("This Month"), i18n.tr("Next Month"), i18n.tr("90+")},
                new String[] {FIRST_COL_WIDTH, "100", "100", "100"},
                new String[] {i18n.tr("Leases Ending:")},
                new Widget[][] {{
                    inject(proto().numOfLeasesEndingThisMonth()).asWidget(),
                    inject(proto().numOfLeasesEndingNextMonth()).asWidget(),
                    inject(proto().numOfLeasesEndingOver90Days()).asWidget()
                }}
        );//formatter:on
        panel.add(leaseExpiration);

        return panel;
    }
}