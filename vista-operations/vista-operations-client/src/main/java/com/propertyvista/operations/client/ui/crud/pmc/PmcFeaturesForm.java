/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-01-15
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.pmc;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.settings.PmcVistaFeatures;

public class PmcFeaturesForm extends CEntityForm<PmcVistaFeatures> {

    public PmcFeaturesForm() {
        super(PmcVistaFeatures.class);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().countryOfOperation()), 25).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leases()), 5).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().onlineApplication()), 5).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().yardiIntegration()), 5).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().yardiMaintenance()), 5).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().tenantSureIntegration()), 5).build());

        final CComponent<Boolean> yardiIntegrationSwitch = get(proto().yardiIntegration());
        final CComponent<Boolean> yardiMaintenanceSwitch = get(proto().yardiMaintenance());
        yardiIntegrationSwitch.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                yardiMaintenanceSwitch.setEnabled(Boolean.TRUE.equals(event.getValue()));
            }
        });
        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().yardiMaintenance()).setEnabled(getValue() != null && getValue().yardiIntegration().isBooleanTrue());
    }
}
