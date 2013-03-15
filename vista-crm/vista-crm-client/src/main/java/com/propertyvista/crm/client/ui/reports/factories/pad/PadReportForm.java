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
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().from())).componentWidth(10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().until())).componentWidth(10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().selectBuildings())).build());
        get(proto().selectBuildings()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().selectedBuildings()).setVisible(event.getValue());
            }
        });
        panel.setWidget(++row, 0, inject(proto().selectedBuildings(), new SelectedBuildingsFolder()));
        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().selectedBuildings()).setVisible((getValue().selectBuildings().isBooleanTrue()));
    }
}
