/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-01-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.report.ReportServiceImpl;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;

import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.services.lease.BlankApplicationDocumentDownloadService;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.common.lease.BlankLeaseApplicationDocumentCreatorDeferredProcess;

public class BlankApplicationDocumentDownloadServiceImpl extends ReportServiceImpl<IEntity> implements BlankApplicationDocumentDownloadService {

    @Override
    public void createDownload(AsyncCallback<String> callback, ReportRequest reportRequest) {
        Lease leaseId = EntityFactory.createIdentityStub(Lease.class, (Key) reportRequest.getParameters().get(LEASE_ID_PARAM_KEY));
        Customer customerId = EntityFactory.createIdentityStub(Customer.class, (Key) reportRequest.getParameters().get(CUSTOMER_ID_PARAM_KEY));
        callback.onSuccess(DeferredProcessRegistry
                .fork(new BlankLeaseApplicationDocumentCreatorDeferredProcess(leaseId, customerId), ThreadPoolNames.DOWNLOADS));
    }
}
