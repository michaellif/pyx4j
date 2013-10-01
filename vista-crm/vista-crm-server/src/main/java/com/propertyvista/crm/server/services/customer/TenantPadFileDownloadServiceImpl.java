/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-01
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
import com.propertyvista.crm.rpc.services.customer.TenantPadFileDownloadService;

public class TenantPadFileDownloadServiceImpl extends ReportServiceImpl<IEntity> implements TenantPadFileDownloadService {

    @Override
    public void createDownload(AsyncCallback<String> callback, ReportRequest reportRequest) {
        callback.onSuccess(DeferredProcessRegistry.fork(new TenantPadFileDownloadDeferredProcess(), ThreadPoolNames.DOWNLOADS));
    }

}
