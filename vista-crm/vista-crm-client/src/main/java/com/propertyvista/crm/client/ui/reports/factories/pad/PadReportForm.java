/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-14
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.factories.pad;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.reports.PapReportMetadata;

public class PadReportForm extends CEntityDecoratableForm<PapReportMetadata> {

    public PadReportForm() {
        super(PapReportMetadata.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel panel = new FormFlexPanel();
        int row = -1;
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().onlyWithNotice())).componentWidth(10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().filterByBillingCycle())).componentWidth(10).build());
        get(proto().filterByBillingCycle()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().billingPeriod()).setVisible(event.getValue());
                get(proto().billingCycleStartDate()).setVisible(event.getValue());
            }
        });
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingPeriod())).componentWidth(10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingCycleStartDate())).componentWidth(10).build());

        row = 0;
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().filterByBuildings())).build());
        get(proto().filterByBuildings()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().selectedBuildings()).setVisible(event.getValue());
            }
        });
        panel.setWidget(++row, 1, inject(proto().selectedBuildings(), new SelectedBuildingsFolder()));
        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().billingPeriod()).setVisible(getValue().filterByBillingCycle().isBooleanTrue());
        get(proto().billingCycleStartDate()).setVisible(getValue().filterByBillingCycle().isBooleanTrue());

        get(proto().selectedBuildings()).setVisible((getValue().filterByBuildings().isBooleanTrue()));
    }
}
