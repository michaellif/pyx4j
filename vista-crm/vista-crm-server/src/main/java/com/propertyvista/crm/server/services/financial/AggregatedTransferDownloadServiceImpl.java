/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 4, 2014
 * @author ernestog
 */
package com.propertyvista.crm.server.services.financial;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.docs.sheet.ReportServiceImpl;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;

import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.services.financial.AggregatedTransferDownloadService;
import com.propertyvista.domain.financial.AggregatedTransfer;

public class AggregatedTransferDownloadServiceImpl extends ReportServiceImpl<IEntity> implements AggregatedTransferDownloadService {

    @SuppressWarnings("unchecked")
    @Override
    public void createDownload(AsyncCallback<String> callback, ReportRequest reportRequest) {
        callback.onSuccess(DeferredProcessRegistry.fork(
                new ExportAggregatedTransferDeferredProcess((EntityQueryCriteria<AggregatedTransfer>) reportRequest.getCriteria()), ThreadPoolNames.DOWNLOADS));
    }

}
