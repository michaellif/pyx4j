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

import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.deferred.DeferredProcessRegistry;
import com.pyx4j.essentials.server.report.ReportServiceImpl;

import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.domain.financial.billing.Bill;

public class BillPrintServiceImpl extends ReportServiceImpl<Bill> {

    @SuppressWarnings("unchecked")
    @Override
    public void createDownload(AsyncCallback<String> callback, ReportRequest reportRequest) {
        callback.onSuccess(DeferredProcessRegistry.fork(new BillPrintDeferredProcess((EntityQueryCriteria<Bill>) reportRequest.getCriteria(),
                JasperFileFormat.PDF), ThreadPoolNames.DOWNLOADS));
    }

}
