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
package com.propertyvista.crm.server.services.billing;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.services.billing.BillPrintService;
import com.propertyvista.domain.financial.billing.Bill;

public class BillPrintServiceImpl implements BillPrintService {

    @SuppressWarnings("unchecked")
    @Override
    public void createDownload(AsyncCallback<String> callback, ReportRequest reportRequest) {
        Bill bill = Persistence.secureRetrieve((EntityQueryCriteria<Bill>) reportRequest.getCriteria());
        callback.onSuccess(DeferredProcessRegistry.fork(new BillPrintDeferredProcess(bill), ThreadPoolNames.DOWNLOADS));
    }

    @Override
    public void cancelDownload(AsyncCallback<VoidSerializable> callback, String downloadUrl) {
        String fileName = Downloadable.getDownloadableFileName(downloadUrl);
        if (fileName != null) {
            Downloadable.cancel(fileName);
        }
        callback.onSuccess(null);
    }

}
