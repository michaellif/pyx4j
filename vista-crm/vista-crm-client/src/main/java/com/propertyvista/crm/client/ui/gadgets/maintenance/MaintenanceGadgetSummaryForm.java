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
package com.propertyvista.crm.client.ui.gadgets.maintenance;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetSummaryForm;
import com.propertyvista.crm.client.ui.gadgets.util.Utils;
import com.propertyvista.crm.rpc.dto.gadgets.MaintenanceGadgetDataDTO;

public class MaintenanceGadgetSummaryForm extends CounterGadgetSummaryForm<MaintenanceGadgetDataDTO> {

    private static final I18n i18n = I18n.get(MaintenanceGadgetSummaryForm.class);

    public MaintenanceGadgetSummaryForm() {
        super(MaintenanceGadgetDataDTO.class);
    }

    @Override
    public IsWidget createContent() {
        VerticalPanel content = new VerticalPanel();
        content.setWidth("100%");
        content.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        content.getElement().getStyle().setPaddingBottom(1, Unit.EM);

        FlexTable workOrders = Utils.createTable(//@formatter:off
                new String[]{
                        "",
                        i18n.tr("Open"),
                        i18n.tr("Urgent"),
                        i18n.tr("Outstanding 1 to 2 days"),
                        i18n.tr("Outstanding 2 to 3 days"),
                        i18n.tr("Outstanding 3 and more days")},
                new String[]{"200", "100", "100", "100", "100", "100"},
                new String[]{i18n.tr("# of Work Orders:")},
                new IsWidget[][] {{
                        inject(proto().openWorkOrders()),
                        inject(proto().urgentWorkOrders()),
                        inject(proto().outstandingWorkOrders1to2days()),
                        inject(proto().outstandingWorkOrders2to3days()),
                        inject(proto().outstandingWorkOrdersMoreThan3days())
                }}
        );//@formatter:on
        content.add(workOrders);

        return content;
    }

}
