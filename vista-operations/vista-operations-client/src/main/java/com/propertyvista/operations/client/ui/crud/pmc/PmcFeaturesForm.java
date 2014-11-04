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
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.domain.settings.PmcVistaFeatures;

public class PmcFeaturesForm extends CForm<PmcVistaFeatures> {

    public PmcFeaturesForm() {
        super(PmcVistaFeatures.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().countryOfOperation()).decorate();
        formPanel.append(Location.Left, proto().onlineApplication()).decorate().componentWidth(90);
        formPanel.append(Location.Left, proto().tenantEmailEnabled()).decorate().componentWidth(90);
        formPanel.append(Location.Left, proto().whiteLabelPortal()).decorate().componentWidth(90);
        formPanel.append(Location.Left, proto().yardiIntegration()).decorate().componentWidth(90);
        formPanel.append(Location.Left, proto().yardiMaintenance()).decorate().componentWidth(90);
        formPanel.append(Location.Left, proto().tenantSureIntegration()).decorate().componentWidth(90);

        final CComponent<?, Boolean, ?, ?> yardiIntegrationSwitch = get(proto().yardiIntegration());
        final CComponent<?, Boolean, ?, ?> yardiMaintenanceSwitch = get(proto().yardiMaintenance());
        yardiIntegrationSwitch.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                yardiMaintenanceSwitch.setEnabled(Boolean.TRUE.equals(event.getValue()));
            }
        });
        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().yardiMaintenance()).setEnabled(getValue() != null && getValue().yardiIntegration().getValue(false));
    }
}
