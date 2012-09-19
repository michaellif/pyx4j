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
package com.propertyvista.crm.client.ui.gadgets.applications;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.crm.client.ui.gadgets.common.CounterGadgetSummaryForm;
import com.propertyvista.crm.rpc.dto.gadgets.ApplicationsGadgetDataDTO;

public class ApplicationsGadgetSummaryForm extends CounterGadgetSummaryForm<ApplicationsGadgetDataDTO> {

    public ApplicationsGadgetSummaryForm() {
        super(ApplicationsGadgetDataDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().applications())).componentWidth(10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().pending())).componentWidth(10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().approved())).componentWidth(10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().declined())).componentWidth(10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().cancelled())).componentWidth(10).build());

        return content;
    }

}
