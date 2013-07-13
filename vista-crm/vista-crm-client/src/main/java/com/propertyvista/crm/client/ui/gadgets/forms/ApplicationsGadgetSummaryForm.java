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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewForm;
import com.propertyvista.crm.rpc.dto.gadgets.ApplicationsGadgetDataDTO;

public class ApplicationsGadgetSummaryForm extends ZoomableViewForm<ApplicationsGadgetDataDTO> {

    public ApplicationsGadgetSummaryForm() {
        super(ApplicationsGadgetDataDTO.class);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().applications()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().pending()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().approved()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().declined()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().cancelled()), 10).build());

        return content;
    }

}
