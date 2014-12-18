/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 14, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.gadgets.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewForm;
import com.propertyvista.crm.rpc.dto.gadgets.CollectionsGadgetDataDTO;

public class CollectionsSummaryForm extends ZoomableViewForm<CollectionsGadgetDataDTO> {

    public CollectionsSummaryForm() {
        super(CollectionsGadgetDataDTO.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().leasesPaidThisMonth()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().fundsCollectedThisMonth()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().fundsInProcessing()).decorate().componentWidth(120);
        return formPanel;
    }

}
