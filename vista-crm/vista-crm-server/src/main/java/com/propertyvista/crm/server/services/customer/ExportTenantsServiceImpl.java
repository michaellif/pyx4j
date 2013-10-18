/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-16
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.report.ReportServiceImpl;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;

import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.services.customer.ExportTenantsService;
import com.propertyvista.domain.tenant.access.PortalAccessSecutiryCodeReportType;

public class ExportTenantsServiceImpl extends ReportServiceImpl<IEntity> implements ExportTenantsService {

    @Override
    public void createDownload(AsyncCallback<String> callback, ReportRequest reportRequest) {

        PortalAccessSecutiryCodeReportType type = (PortalAccessSecutiryCodeReportType) reportRequest.getParameters()
                .get(ExportTenantsService.PARAM_REPORT_TYPE);

        callback.onSuccess(DeferredProcessRegistry.fork(new ExportTenantsPortalSecretsDeferredProcess(type), ThreadPoolNames.DOWNLOADS));
    }

}
