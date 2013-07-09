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
package com.propertyvista.crm.client.ui.gadgets.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewForm;
import com.propertyvista.crm.rpc.dto.gadgets.MaintenanceGadgetDataDTO;

public class MaintenanceGadgetSummaryForm extends ZoomableViewForm<MaintenanceGadgetDataDTO> {

    private static final I18n i18n = I18n.get(MaintenanceGadgetSummaryForm.class);

    public MaintenanceGadgetSummaryForm() {
        super(MaintenanceGadgetDataDTO.class);
    }

    @Override
    public IsWidget createContent() {

        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setH2(++row, 0, 1, i18n.tr("Open:"));
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().openWorkOrders())).customLabel("").useLabelSemicolon(false).componentWidth(5).build());

        content.setH2(++row, 0, 1, i18n.tr("Urgent:"));
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().urgentWorkOrders())).customLabel("").useLabelSemicolon(false).componentWidth(5).build());

        content.setH2(++row, 0, 1, i18n.tr("Outstanding:"));
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().outstandingWorkOrders1to2days())).customLabel(i18n.tr("1 to 2 days")).componentWidth(5)
                .build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().outstandingWorkOrders2to3days())).customLabel(i18n.tr("2 to 3 days")).componentWidth(5)
                .build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().outstandingWorkOrdersMoreThan3days())).customLabel(i18n.tr("3 and more"))
                .componentWidth(5).build());
        return content;

    }

}
