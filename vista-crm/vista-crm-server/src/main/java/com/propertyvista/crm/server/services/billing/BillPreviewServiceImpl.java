/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-06
 * @author VladL
 */
package com.propertyvista.crm.server.services.billing;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.crm.rpc.services.billing.BillPreviewService;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.BillDTO;

public class BillPreviewServiceImpl implements BillPreviewService {

    @Override
    public void getPreview(AsyncCallback<BillDTO> callback, Lease leaseId) {
        callback.onSuccess(BillingUtils.createBillPreviewDto(ServerSideFactory.create(BillingFacade.class).runBillingPreview(leaseId)));
    }
}
