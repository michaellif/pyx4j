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
 */
package com.propertyvista.crm.client.activity.reports;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.site.rpc.ReportsAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.reports.availability.AvailabilityReportView;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.Vacancy;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;
import com.propertyvista.domain.reports.AvailabilityReportMetadata.RentReadinessStatusPreset;
import com.propertyvista.domain.reports.AvailabilityReportMetadata.RentedStatusPreset;

public class AvailabilityReportActivity extends CrmReportsActivity<AvailabilityReportMetadata> {

    public AvailabilityReportActivity(ReportsAppPlace<AvailabilityReportMetadata> reportPlace) {
        super(AvailabilityReportMetadata.class, reportPlace, CrmSite.getViewFactory().getView(AvailabilityReportView.class));
    }

    @Override
    protected AvailabilityReportMetadata createDefaultReportMetadata() {
        AvailabilityReportMetadata metadata = EntityFactory.create(AvailabilityReportMetadata.class);
        metadata.asOf().setValue(new LogicalDate());
        metadata.vacancyStatus().setArrayValue(Vacancy.values());
        metadata.rentedStatus().setValue(RentedStatusPreset.All);
        metadata.rentReadinessStatus().setValue(RentReadinessStatusPreset.All);
        return metadata;
    }
}
