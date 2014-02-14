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
package com.propertyvista.crm.client.ui.reports.availability;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.components.c.CEnumSubsetSelector;
import com.propertyvista.common.client.ui.components.c.SubsetSelector.Layout;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.reports.NotEmptySetValidator;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;

public class AvailabilityReportSettingsSimpleForm extends CEntityForm<AvailabilityReportMetadata> {

    public AvailabilityReportSettingsSimpleForm() {
        super(AvailabilityReportMetadata.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IsWidget createContent() {
        int row = -1;
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().asOf())).labelWidth(10).componentWidth(10).build());
        panel.setWidget(
                ++row,
                0,
                new FormDecoratorBuilder(inject(proto().vacancyStatus(), new CEnumSubsetSelector<UnitAvailabilityStatus.Vacancy>(
                        UnitAvailabilityStatus.Vacancy.class, Layout.Horizontal))).labelWidth(10).componentWidth(10).build());
        get(proto().vacancyStatus()).addComponentValidator(new NotEmptySetValidator());
        panel.setWidget(
                ++row,
                0,
                new FormDecoratorBuilder(inject(proto().rentedStatus(), new CEnumSubsetSelector<UnitAvailabilityStatus.RentedStatus>(
                        UnitAvailabilityStatus.RentedStatus.class, Layout.Vertical))).labelWidth(10).componentWidth(10).build());
        get(proto().rentedStatus()).addComponentValidator(new NotEmptySetValidator());
        panel.setWidget(
                row,
                1,
                new FormDecoratorBuilder(inject(proto().rentReadinessStatus(), new CEnumSubsetSelector<UnitAvailabilityStatus.RentReadiness>(
                        UnitAvailabilityStatus.RentReadiness.class, Layout.Vertical))).labelWidth(10).componentWidth(15).build());
        get(proto().rentReadinessStatus()).addComponentValidator(new NotEmptySetValidator());
        return panel;
    }
}
