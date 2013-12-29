/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.report.ReportServiceImpl;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;

import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.services.reports.DashboardReportService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class DashboardReportServiceImpl extends ReportServiceImpl<DashboardMetadata> implements DashboardReportService {

    @SuppressWarnings("unchecked")
    @Override
    public void createDownload(AsyncCallback<String> callback, ReportRequest reportRequest) {
        callback.onSuccess(DeferredProcessRegistry.fork(new ReportsDeferredProcess((EntityQueryCriteria<DashboardMetadata>) reportRequest.getCriteria(),
                (Vector<Building>) reportRequest.getParameters().get(DashboardReportService.PARAM_SELECTED_BUILDINGS), JasperFileFormat.PDF),
                ThreadPoolNames.DOWNLOADS));
    }

}
