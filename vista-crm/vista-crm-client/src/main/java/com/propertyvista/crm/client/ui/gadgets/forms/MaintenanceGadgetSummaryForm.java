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

import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewForm;
import com.propertyvista.crm.rpc.dto.gadgets.MaintenanceGadgetDataDTO;

public class MaintenanceGadgetSummaryForm extends ZoomableViewForm<MaintenanceGadgetDataDTO> {

    private static final I18n i18n = I18n.get(MaintenanceGadgetSummaryForm.class);

    public MaintenanceGadgetSummaryForm() {
        super(MaintenanceGadgetDataDTO.class);
    }

    @Override
    protected IsWidget createContent() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);
        formPanel.h2(i18n.tr("Open:"));
        formPanel.append(Location.Left, proto().openWorkOrders()).decorate().customLabel("").useLabelSemicolon(false).componentWidth(50);
        formPanel.h2(i18n.tr("Urgent:"));
        formPanel.append(Location.Left, proto().urgentWorkOrders()).decorate().customLabel("").useLabelSemicolon(false).componentWidth(50);
        formPanel.h2(i18n.tr("Outstanding:"));
        formPanel.append(Location.Left, proto().outstandingWorkOrders1to2days()).decorate().customLabel(i18n.tr("1 to 2 days")).componentWidth(50);
        formPanel.append(Location.Left, proto().outstandingWorkOrders2to3days()).decorate().componentWidth(50);
        formPanel.append(Location.Left, proto().outstandingWorkOrdersMoreThan3days()).decorate().componentWidth(50);
        return formPanel;
    }
}
