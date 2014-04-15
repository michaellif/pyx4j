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
import com.pyx4j.site.client.ui.prime.form.FormDecoratorBuilder;

import com.propertyvista.crm.client.ui.gadgets.common.ZoomableViewForm;
import com.propertyvista.crm.rpc.dto.gadgets.ApplicationsGadgetDataDTO;

public class ApplicationsGadgetSummaryForm extends ZoomableViewForm<ApplicationsGadgetDataDTO> {

    public ApplicationsGadgetSummaryForm() {
        super(ApplicationsGadgetDataDTO.class);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, inject(proto().applications(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().pending(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().approved(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().declined(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().cancelled(), new FormDecoratorBuilder(10).build()));

        return content;
    }

}
