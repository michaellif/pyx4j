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

import java.util.Arrays;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComboBox.NotInOptionsPolicy;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;

import com.propertyvista.common.client.ui.components.c.CEnumSubsetSelector;
import com.propertyvista.common.client.ui.components.c.SubsetSelector.Layout;
import com.propertyvista.crm.client.ui.reports.NotEmptySetValidator;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;
import com.propertyvista.domain.reports.AvailabilityReportMetadata.RentReadinessStatusPreset;
import com.propertyvista.domain.reports.AvailabilityReportMetadata.RentedStatusPreset;

public class AvailabilityReportSettingsSimpleForm extends CForm<AvailabilityReportMetadata> {

    private CComboBox<RentedStatusPreset> rentedStatusPreset;

    private CComboBox<RentReadinessStatusPreset> rentReadinessStatusPreset;

    public AvailabilityReportSettingsSimpleForm() {
        super(AvailabilityReportMetadata.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().asOf()).decorate().labelWidth("10em").componentWidth("10em");
        formPanel
                .append(Location.Left, proto().vacancyStatus(),
                        new CEnumSubsetSelector<UnitAvailabilityStatus.Vacancy>(UnitAvailabilityStatus.Vacancy.class, Layout.Horizontal)).decorate()
                .labelWidth("10em").componentWidth("10em");

        rentedStatusPreset = new CComboBox<RentedStatusPreset>(NotInOptionsPolicy.DISCARD);
        rentedStatusPreset.setOptions(Arrays.asList(RentedStatusPreset.values()));
        formPanel.append(Location.Left, proto().rentedStatus(), rentedStatusPreset).decorate().labelWidth("10em").componentWidth("10em");

        rentReadinessStatusPreset = new CComboBox<RentReadinessStatusPreset>(NotInOptionsPolicy.DISCARD);
        rentReadinessStatusPreset.setOptions(Arrays.asList(RentReadinessStatusPreset.values()));
        formPanel.append(Location.Left, proto().rentReadinessStatus(), rentReadinessStatusPreset).decorate().labelWidth("10em").componentWidth("15em");

        return formPanel;
    }

    @Override
    public void addValidations() {
        get(proto().vacancyStatus()).addComponentValidator(new NotEmptySetValidator<UnitAvailabilityStatus.Vacancy>());
    }
}
