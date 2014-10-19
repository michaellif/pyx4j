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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.site.client.backoffice.ui.IPane;
import com.pyx4j.site.client.backoffice.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.crm.client.ui.reports.SelectPortfolioFolder;
import com.propertyvista.crm.client.ui.reports.SelectedBuildingsFolder;
import com.propertyvista.domain.reports.EftVarianceReportMetadata;

public class EftVarianceReportSettingsForm extends CForm<EftVarianceReportMetadata> {

    private final IPane parentView;

    public EftVarianceReportSettingsForm(IPane parentView) {
        super(EftVarianceReportMetadata.class);
        this.parentView = parentView;
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().billingCycleStartDate()).decorate().componentWidth(120);

        FlowPanel buildingFilterPanel = new FlowPanel();
        buildingFilterPanel.add(inject(proto().filterByPortfolio(), new FieldDecoratorBuilder().build()));
        buildingFilterPanel.add(inject(proto().selectedPortfolios(), new SelectPortfolioFolder(parentView)));
        get(proto().filterByPortfolio()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().selectedPortfolios()).setVisible(event.getValue());
            }
        });

        buildingFilterPanel.add(inject(proto().filterByBuildings(), new FieldDecoratorBuilder().build()));
        get(proto().filterByBuildings()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().selectedBuildings()).setVisible(event.getValue());
            }
        });
        buildingFilterPanel.add(inject(proto().selectedBuildings(), new SelectedBuildingsFolder(parentView)));
        formPanel.append(Location.Right, buildingFilterPanel);

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().selectedPortfolios()).setVisible((getValue().filterByPortfolio().getValue(false)));
        get(proto().selectedBuildings()).setVisible((getValue().filterByBuildings().getValue(false)));
    }
}
