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
package com.propertyvista.crm.client.ui.reports.eft;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.organisation.common.PortfolioFolder;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.reports.EftReportMetadata;
import com.propertyvista.shared.config.VistaFeatures;

public class EftReportSettingsForm extends CEntityDecoratableForm<EftReportMetadata> {

    public EftReportSettingsForm() {
        super(EftReportMetadata.class);
    }

    @Override
    public IsWidget createContent() {
        FlexTable panel = new FlexTable();

        FlowPanel column1 = new FlowPanel();
        final String CHECKBOX_WIDTH = "100px";
        final String INPUT_FIELD_WIDTH = "150px";
        final String CONTENT_WIDTH = "180px";
        column1.add(new FormDecoratorBuilder(inject(proto().leasesOnNoticeOnly())).contentWidth(CONTENT_WIDTH).build());
        column1.add(new FormDecoratorBuilder(inject(proto().forthcomingEft())).contentWidth(CONTENT_WIDTH).build());
        get(proto().forthcomingEft()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().filterByBillingCycle()).setValue(event.getValue() == true, true);
                get(proto().filterByBillingCycle()).setEditable(event.getValue() != true);
                get(proto().paymentStatus()).setVisible(event.getValue() != true);
                get(proto().onlyWithNotice()).setVisible(event.getValue() != true);
            }
        });
        column1.add(new FormDecoratorBuilder(inject(proto().onlyWithNotice())).contentWidth(CONTENT_WIDTH).build());
        column1.add(new FormDecoratorBuilder(inject(proto().paymentStatus())).componentWidth(INPUT_FIELD_WIDTH).contentWidth(CONTENT_WIDTH).build());

        FlowPanel column2 = new FlowPanel();
        column2.add(new FormDecoratorBuilder(inject(proto().filterByBillingCycle())).componentWidth(CHECKBOX_WIDTH).contentWidth(CONTENT_WIDTH).build());
        get(proto().filterByBillingCycle()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().billingPeriod()).setVisible(event.getValue());
                if (VistaFeatures.instance().yardiIntegration()) {
                    get(proto().billingPeriod()).setValue(BillingPeriod.Monthly);
                }
                get(proto().billingCycleStartDate()).setVisible(event.getValue());
            }
        });

        FlowPanel billingCycleFilterPanel = new FlowPanel();
        billingCycleFilterPanel.getElement().getStyle().setPaddingLeft(3, Unit.EM);
        billingCycleFilterPanel.getElement().getStyle().setPaddingBottom(1, Unit.EM);
        billingCycleFilterPanel.add(new FormDecoratorBuilder(inject(proto().billingPeriod())).componentWidth(INPUT_FIELD_WIDTH).contentWidth(CONTENT_WIDTH)
                .build());
        billingCycleFilterPanel.add(new FormDecoratorBuilder(inject(proto().billingCycleStartDate())).componentWidth(INPUT_FIELD_WIDTH)
                .contentWidth(CONTENT_WIDTH).build());
        column2.add(billingCycleFilterPanel);

        column2.add(new FormDecoratorBuilder(inject(proto().filterByExpectedMoveOut())).componentWidth(CHECKBOX_WIDTH).contentWidth(CONTENT_WIDTH).build());
        get(proto().filterByExpectedMoveOut()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().minimum()).setVisible(event.getValue());
                get(proto().maximum()).setVisible(event.getValue());
            }
        });
        FlowPanel expectedMoveOutFilterPanel = new FlowPanel();
        expectedMoveOutFilterPanel.getElement().getStyle().setPaddingLeft(3, Unit.EM);
        expectedMoveOutFilterPanel.add(new FormDecoratorBuilder(inject(proto().minimum())).componentWidth(INPUT_FIELD_WIDTH).contentWidth(CONTENT_WIDTH)
                .build());
        expectedMoveOutFilterPanel.add(new FormDecoratorBuilder(inject(proto().maximum())).componentWidth(INPUT_FIELD_WIDTH).contentWidth(CONTENT_WIDTH)
                .build());
        column2.add(expectedMoveOutFilterPanel);

        FlowPanel buildingFilterPanel = new FlowPanel();
        buildingFilterPanel.add(new FormDecoratorBuilder(inject(proto().filterByPortfolio())).build());
        buildingFilterPanel.add(inject(proto().selectedPortfolios(), new PortfolioFolder(true)));
        get(proto().filterByPortfolio()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().selectedPortfolios()).setVisible(event.getValue());
            }
        });

        buildingFilterPanel.add(new FormDecoratorBuilder(inject(proto().filterByBuildings())).build());
        get(proto().filterByBuildings()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().selectedBuildings()).setVisible(event.getValue());
            }
        });
        buildingFilterPanel.add(inject(proto().selectedBuildings(), new SelectedBuildingsFolder()));

        panel.setWidget(0, 0, column1);
        panel.setWidget(0, 1, column2);
        panel.setWidget(0, 2, buildingFilterPanel);
        panel.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        panel.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
        panel.getFlexCellFormatter().setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);
        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().billingPeriod()).setVisible(getValue().filterByBillingCycle().isBooleanTrue());
        get(proto().billingCycleStartDate()).setVisible(getValue().filterByBillingCycle().isBooleanTrue());

        get(proto().selectedPortfolios()).setVisible((getValue().filterByPortfolio().isBooleanTrue()));
        get(proto().selectedBuildings()).setVisible((getValue().filterByBuildings().isBooleanTrue()));

        get(proto().minimum()).setVisible(getValue().filterByExpectedMoveOut().isBooleanTrue());
        get(proto().maximum()).setVisible(getValue().filterByExpectedMoveOut().isBooleanTrue());
    }
}
