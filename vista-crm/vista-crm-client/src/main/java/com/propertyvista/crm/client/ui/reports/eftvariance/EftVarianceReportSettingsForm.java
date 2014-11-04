/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-21
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.eftvariance;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.crm.client.ui.reports.SelectPortfolioFolder;
import com.propertyvista.crm.client.ui.reports.SelectedBuildingsFolder;
import com.propertyvista.domain.reports.EftVarianceReportMetadata;

public class EftVarianceReportSettingsForm extends CForm<EftVarianceReportMetadata> {

    public EftVarianceReportSettingsForm() {
        super(EftVarianceReportMetadata.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().billingCycleStartDate()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().filterByPortfolio()).decorate();
        formPanel.append(Location.Right, proto().selectedPortfolios(), new SelectPortfolioFolder());

        formPanel.append(Location.Right, proto().filterByBuildings()).decorate();
        formPanel.append(Location.Right, proto().selectedBuildings(), new SelectedBuildingsFolder());

        // tweaks:
        get(proto().filterByPortfolio()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().selectedPortfolios()).setVisible(event.getValue());
            }
        });
        get(proto().filterByBuildings()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().selectedBuildings()).setVisible(event.getValue());
            }
        });

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().selectedPortfolios()).setVisible((getValue().filterByPortfolio().getValue(false)));
        get(proto().selectedBuildings()).setVisible((getValue().filterByBuildings().getValue(false)));
    }
}
