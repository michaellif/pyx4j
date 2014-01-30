/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.customercreditcheck;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.reports.CustomerCreditCheckReportMetadata;

public class CustomerCreditCheckReportSettingsForm extends CEntityForm<CustomerCreditCheckReportMetadata> {

    public CustomerCreditCheckReportSettingsForm() {
        super(CustomerCreditCheckReportMetadata.class);
    }

    @Override
    public IsWidget createContent() {

        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        panel.setWidget(0, 0, new FormDecoratorBuilder(inject(proto().minAmountChecked())).labelWidth(10).componentWidth(10).build());
        panel.setWidget(1, 0, new FormDecoratorBuilder(inject(proto().maxAmountChecked())).labelWidth(10).componentWidth(10).build());

        panel.setWidget(0, 1, new FormDecoratorBuilder(inject(proto().minCreditCheckDate())).labelWidth(10).componentWidth(10).build());
        panel.setWidget(1, 1, new FormDecoratorBuilder(inject(proto().maxCreditCheckDate())).labelWidth(10).componentWidth(10).build());

        return panel;
    }

}
