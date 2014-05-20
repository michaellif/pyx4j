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

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.site.client.ui.reports.PropertyCriteriaFolder;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;

public class AvailabilityReportSettingsAdvancedForm extends CForm<AvailabilityReportMetadata> {

    public AvailabilityReportSettingsAdvancedForm() {
        super(AvailabilityReportMetadata.class);
    }

    @Override
    protected IsWidget createContent() {
        DualColumnForm formPanel = new DualColumnForm(this);
        formPanel.append(Location.Left, proto().asOf()).decorate().labelWidth("120px").componentWidth("120px");
        formPanel.append(Location.Left, proto().availbilityTableCriteria(), new PropertyCriteriaFolder(VistaImages.INSTANCE, UnitAvailabilityStatus.class,
                Arrays.asList(AvailabilityReportTableColumnsHolder.AVAILABILITY_TABLE_COLUMNS)));
        return formPanel;
    }
}
