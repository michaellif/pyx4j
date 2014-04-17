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

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewForm;
import com.propertyvista.crm.rpc.dto.gadgets.MaintenanceGadgetDataDTO;

public class MaintenanceGadgetSummaryForm extends ZoomableViewForm<MaintenanceGadgetDataDTO> {

    private static final I18n i18n = I18n.get(MaintenanceGadgetSummaryForm.class);

    public MaintenanceGadgetSummaryForm() {
        super(MaintenanceGadgetDataDTO.class);
    }

    @Override
    protected IsWidget createContent() {

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;
        content.setH2(++row, 0, 1, i18n.tr("Open:"));
        content.setWidget(++row, 0,
                inject(proto().openWorkOrders(), new FieldDecoratorBuilder().customLabel("").useLabelSemicolon(false).componentWidth(5).build()));

        content.setH2(++row, 0, 1, i18n.tr("Urgent:"));
        content.setWidget(++row, 0,
                inject(proto().urgentWorkOrders(), new FieldDecoratorBuilder().customLabel("").useLabelSemicolon(false).componentWidth(5).build()));

        content.setH2(++row, 0, 1, i18n.tr("Outstanding:"));
        content.setWidget(++row, 0,
                inject(proto().outstandingWorkOrders1to2days(), new FieldDecoratorBuilder().customLabel(i18n.tr("1 to 2 days")).componentWidth(5).build()));
        content.setWidget(++row, 0,
                inject(proto().outstandingWorkOrders2to3days(), new FieldDecoratorBuilder().customLabel(i18n.tr("2 to 3 days")).componentWidth(5).build()));
        content.setWidget(++row, 0,
                inject(proto().outstandingWorkOrdersMoreThan3days(), new FieldDecoratorBuilder().customLabel(i18n.tr("3 and more")).componentWidth(5).build()));
        return content;

    }

}
