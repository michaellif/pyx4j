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

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.reports.PropertyCriteriaFolder;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;

public class AvailabilityReportSettingsAdvancedForm extends CEntityForm<AvailabilityReportMetadata> {

    public AvailabilityReportSettingsAdvancedForm() {
        super(AvailabilityReportMetadata.class);
    }

    @Override
    protected IsWidget createContent() {
        int row = -1;
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        panel.setWidget(++row, 0, inject(proto().asOf(), new FieldDecoratorBuilder().labelWidth(10).componentWidth(10).build()));
        panel.setWidget(
                ++row,
                0,
                inject(proto().availbilityTableCriteria(),
                        new PropertyCriteriaFolder(VistaImages.INSTANCE, UnitAvailabilityStatus.class, Arrays
                                .asList(AvailabilityReportTableColumnsHolder.AVAILABILITY_TABLE_COLUMNS))));
        return panel;
    }
}
