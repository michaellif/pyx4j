/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.reports;

import com.google.gwt.core.client.GWT;

import com.pyx4j.site.client.activity.AbstractReportActivity;
import com.pyx4j.site.rpc.ReportsAppPlace;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

import com.propertyvista.crm.client.ui.reports.CrmReportsView;
import com.propertyvista.crm.client.ui.viewfactories.ReportsViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.reports.CrmReportsService;
import com.propertyvista.crm.rpc.services.reports.CrmReportsSettingsPersistenceService;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class CrmReportsActivity extends AbstractReportActivity {

    public CrmReportsActivity(ReportsAppPlace reportsPlace) {
        super(//@formatter:off
                GWT.<CrmReportsService>create(CrmReportsService.class),
                GWT.<CrmReportsSettingsPersistenceService>create(CrmReportsSettingsPersistenceService.class),
                ReportsViewFactory.instance(CrmReportsView.class),
                reportsPlace,
                GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping
        );//@formatter:on

    }

    @Override
    protected ReportsAppPlace createReportsPlace(ReportMetadata reportMetadata) {
        return new CrmSiteMap.Reports(reportMetadata);
    }

}
