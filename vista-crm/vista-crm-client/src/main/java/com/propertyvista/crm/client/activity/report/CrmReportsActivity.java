/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.report;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.reports.ReportMetadata;
import com.pyx4j.site.client.ui.reports.AbstractReportsActivity;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.services.reports.IReportsService;

import com.propertyvista.crm.client.ui.reports.CrmReportsView;
import com.propertyvista.crm.client.ui.viewfactories.DashboardViewFactory;
import com.propertyvista.crm.rpc.services.reports.CrmReportsService;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;

public class CrmReportsActivity extends AbstractReportsActivity {

    public CrmReportsActivity(AppPlace place) {
        super(GWT.<IReportsService> create(CrmReportsService.class), DashboardViewFactory.instance(CrmReportsView.class), place);
    }

    @Override
    protected ReportMetadata retrieveReportSettings(AppPlace place) {
        AvailabilityReportMetadata metadata = EntityFactory.create(AvailabilityReportMetadata.class);
        metadata.asOf().setValue(new LogicalDate());
        return metadata;
    }

}
