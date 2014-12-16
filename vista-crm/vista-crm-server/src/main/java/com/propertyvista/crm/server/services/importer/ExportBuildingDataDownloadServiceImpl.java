/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.importer;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.docs.sheet.ReportServiceImpl;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;

import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.services.importer.ExportBuildingDataDownloadService;
import com.propertyvista.domain.property.asset.building.Building;

public class ExportBuildingDataDownloadServiceImpl extends ReportServiceImpl<Building> implements ExportBuildingDataDownloadService {

    @Override
    public void createDownload(AsyncCallback<String> callback, ReportRequest reportRequest) {
        @SuppressWarnings("unchecked")
        EntityQueryCriteria<Building> criteria = (EntityQueryCriteria<Building>) reportRequest.getCriteria();
        callback.onSuccess(DeferredProcessRegistry.fork(new ExportBuildingDataDeferredProcess(criteria), ThreadPoolNames.DOWNLOADS));
    }
}
