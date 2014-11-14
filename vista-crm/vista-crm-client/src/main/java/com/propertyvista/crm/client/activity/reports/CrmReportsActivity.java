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

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeReportActivity;
import com.pyx4j.site.client.backoffice.ui.prime.report.IPrimeReportView;
import com.pyx4j.site.rpc.ReportsAppPlace;
import com.pyx4j.site.shared.domain.reports.ReportTemplate;

import com.propertyvista.crm.client.event.CrudNavigateEvent;
import com.propertyvista.crm.rpc.services.reports.CrmReportsService;
import com.propertyvista.crm.rpc.services.reports.CrmReportsSettingsPersistenceService;
import com.propertyvista.portal.rpc.DeploymentConsts;

public abstract class CrmReportsActivity<R extends ReportTemplate> extends AbstractPrimeReportActivity<R> {

    public <RPlace extends ReportsAppPlace<R>> CrmReportsActivity(Class<R> reportMetadataClass, RPlace reportPlace, IPrimeReportView<R> view) {
        super(//@formatter:off
                reportMetadataClass,
                reportPlace,                
                GWT.<CrmReportsService>create(CrmReportsService.class),
                GWT.<CrmReportsSettingsPersistenceService>create(CrmReportsSettingsPersistenceService.class),
                view,                
                GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping
        );//@formatter:on
    }

    @Override
    protected void onReportMetadataSet(R reportMetadata) {
        AppSite.getEventBus().fireEvent(new CrudNavigateEvent(getPlace(), getPlace().getReportMetadata()));
    };
}
