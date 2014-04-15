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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewForm;
import com.propertyvista.crm.rpc.dto.gadgets.CollectionsGadgetDataDTO;

public class CollectionsSummaryForm extends ZoomableViewForm<CollectionsGadgetDataDTO> {

    public CollectionsSummaryForm() {
        super(CollectionsGadgetDataDTO.class);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;
        content.setWidget(++row, 0, inject(proto().leasesPaidThisMonth(), new FormDecoratorBuilder().componentWidth(10).build()));
        content.setWidget(++row, 0, inject(proto().fundsCollectedThisMonth(), new FormDecoratorBuilder().componentWidth(10).build()));
        content.setWidget(++row, 0, inject(proto().fundsInProcessing(), new FormDecoratorBuilder().componentWidth(10).build()));
        return content;
    }

}
