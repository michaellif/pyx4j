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
package com.propertyvista.crm.client.ui.reports.autopay;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.client.ui.reports.eft.SelectedBuildingsFolder;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;

public class AutoPayChangesReportSettingsForm extends CEntityDecoratableForm<AutoPayChangesReportMetadata> {

    public AutoPayChangesReportSettingsForm() {
        super(AutoPayChangesReportMetadata.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel panel = new FormFlexPanel();
        int row = -1;
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leasesOnNoticeOnly())).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().filterByBuildings())).build());
        get(proto().filterByBuildings()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().buildings()).setVisible(event.getValue());
            }
        });
        panel.setWidget(++row, 0, inject(proto().buildings(), new SelectedBuildingsFolder()));

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().filterByExpectedMoveOut())).build());
        get(proto().filterByExpectedMoveOut()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().minimum()).setVisible(event.getValue() == true);
                get(proto().maximum()).setVisible(event.getValue() == true);
            }
        });
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().minimum())).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().maximum())).build());
        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().buildings()).setVisible(getValue().filterByBuildings().isBooleanTrue());
        get(proto().minimum()).setVisible(getValue().filterByExpectedMoveOut().isBooleanTrue());
        get(proto().maximum()).setVisible(getValue().filterByExpectedMoveOut().isBooleanTrue());
    }

}
