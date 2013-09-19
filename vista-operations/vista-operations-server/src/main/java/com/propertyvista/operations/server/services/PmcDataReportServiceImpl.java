/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 14, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.report.ReportServiceImpl;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;

import com.propertyvista.operations.rpc.dto.PmcDTO;
import com.propertyvista.operations.rpc.services.PmcDataReportService;
import com.propertyvista.operations.server.qa.PmcDataBuildingsReportDeferredProcess;
import com.propertyvista.operations.server.qa.PmcDataImagesReportDeferredProcess;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.domain.pmc.Pmc;

public class PmcDataReportServiceImpl extends ReportServiceImpl<PmcDTO> implements PmcDataReportService {

    @Override
    public void createDownload(AsyncCallback<String> callback, ReportRequest reportRequest) {
        ReportRequest reportdbo = new ReportRequest();
        // TODO covert PmcDTO criteria to Pmc
        reportdbo.setCriteria(EntityQueryCriteria.create(Pmc.class));

        if (reportRequest.getParameters() != null && Boolean.TRUE.equals(reportRequest.getParameters().get(PmcDataReportService.LoadImagesParameter))) {
            callback.onSuccess(DeferredProcessRegistry.fork(new PmcDataImagesReportDeferredProcess(reportdbo), ThreadPoolNames.DOWNLOADS));
        } else {
            callback.onSuccess(DeferredProcessRegistry.fork(new PmcDataBuildingsReportDeferredProcess(reportdbo), ThreadPoolNames.DOWNLOADS));
        }
    }

}
